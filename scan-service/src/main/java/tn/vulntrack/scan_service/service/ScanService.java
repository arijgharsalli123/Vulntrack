package tn.vulntrack.scan_service.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tn.vulntrack.scan_service.exception.InvalidScanRequestException;
import tn.vulntrack.scan_service.exception.ScanNotFoundException;
import tn.vulntrack.scan_service.model.Scan;
import tn.vulntrack.scan_service.model.SimulatedFinding;
import tn.vulntrack.scan_service.repository.ScanRepository;

@Service
public class ScanService {

    private static final Set<String> ASSET_TYPES = Set.of("SERVER", "WEB_APP", "DOCKER_IMAGE");

    private final ScanRepository repository;
    private final FindingSimulator simulator;
    private final long delayMs;

    public ScanService(ScanRepository repository,
                       FindingSimulator simulator,
                       @Value("${scan.findings.delay-ms:500}") long delayMs) {
        this.repository = repository;
        this.simulator = simulator;
        this.delayMs = delayMs;
    }

    public Scan start(String assetId, String assetName, String assetType) {
        if (isBlank(assetId) || isBlank(assetName)) {
            throw new InvalidScanRequestException("assetId and assetName are required");
        }
        if (!ASSET_TYPES.contains(assetType)) {
            throw new InvalidScanRequestException("assetType must be SERVER, WEB_APP or DOCKER_IMAGE");
        }
        Scan scan = new Scan();
        scan.setScanId("SCAN-" + UUID.randomUUID());
        scan.setAssetId(assetId);
        scan.setAssetName(assetName);
        scan.setAssetType(assetType);
        scan.setStatus("RUNNING");
        scan.setStartedAt(OffsetDateTime.now());
        return repository.save(scan);
    }

    public Scan getById(String scanId) {
        if (isBlank(scanId)) {
            throw new InvalidScanRequestException("scanId is required");
        }
        return repository.findById(scanId).orElseThrow(() -> new ScanNotFoundException(scanId));
    }

    /** Emits the findings one by one, then marks the scan COMPLETED (or FAILED on error). */
    public void streamFindings(String scanId, Consumer<SimulatedFinding> sink) {
        Scan scan = getById(scanId);
        List<SimulatedFinding> findings = simulator.findingsFor(scan.getAssetId());
        int sent = 0;
        try {
            for (int i = 0; i < findings.size(); i++) {
                sink.accept(findings.get(i));
                sent++;
                if (i < findings.size() - 1) {
                    pause();
                }
            }
            finish(scan, "COMPLETED", sent);
        } catch (RuntimeException e) {
            finish(scan, "FAILED", sent);
            throw e;
        }
    }

    private void finish(Scan scan, String status, int count) {
        scan.setStatus(status);
        scan.setFindingsCount(count);
        scan.setEndedAt(OffsetDateTime.now());
        repository.save(scan);
    }

    private void pause() {
        if (delayMs <= 0) {
            return;
        }
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Scan interrupted", e);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}