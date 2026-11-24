package tn.vulntrack.scan_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.vulntrack.scan_service.exception.InvalidScanRequestException;
import tn.vulntrack.scan_service.exception.ScanNotFoundException;
import tn.vulntrack.scan_service.model.Scan;
import tn.vulntrack.scan_service.model.SimulatedFinding;
import tn.vulntrack.scan_service.repository.ScanRepository;

@ExtendWith(MockitoExtension.class)
class ScanServiceTest {

    @Mock
    ScanRepository repository;

    ScanService service;

    @BeforeEach
    void setUp() {
        // delay = 0 so the tests do not wait between findings
        service = new ScanService(repository, new FindingSimulator(), 0);
    }

    private Scan runningScan() {
        Scan scan = new Scan();
        scan.setScanId("SCAN-1");
        scan.setAssetId("asset-1");
        scan.setStatus("RUNNING");
        return scan;
    }

    @Test
    void start_validRequest_returnsRunningScan() {
        when(repository.save(any(Scan.class))).thenAnswer(inv -> inv.getArgument(0));

        Scan scan = service.start("asset-1", "web-server-01", "SERVER");

        assertThat(scan.getScanId()).startsWith("SCAN-");
        assertThat(scan.getStatus()).isEqualTo("RUNNING");
        assertThat(scan.getStartedAt()).isNotNull();
    }

    @Test
    void start_unknownAssetType_isRejected() {
        assertThatThrownBy(() -> service.start("asset-1", "web-server-01", "TOASTER"))
                .isInstanceOf(InvalidScanRequestException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void start_blankAssetName_isRejected() {
        assertThatThrownBy(() -> service.start("asset-1", " ", "SERVER"))
                .isInstanceOf(InvalidScanRequestException.class);
    }

    @Test
    void getById_unknownScan_throwsNotFound() {
        when(repository.findById("SCAN-unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("SCAN-unknown"))
                .isInstanceOf(ScanNotFoundException.class);
    }

    @Test
    void streamFindings_sendsEverythingAndMarksCompleted() {
        Scan scan = runningScan();
        when(repository.findById("SCAN-1")).thenReturn(Optional.of(scan));
        List<SimulatedFinding> received = new ArrayList<>();

        service.streamFindings("SCAN-1", received::add);

        assertThat(received).hasSizeBetween(3, 10);
        assertThat(scan.getStatus()).isEqualTo("COMPLETED");
        assertThat(scan.getFindingsCount()).isEqualTo(received.size());
        assertThat(scan.getEndedAt()).isNotNull();
    }

    @Test
    void streamFindings_whenConsumerFails_marksFailed() {
        Scan scan = runningScan();
        when(repository.findById("SCAN-1")).thenReturn(Optional.of(scan));

        assertThatThrownBy(() -> service.streamFindings("SCAN-1", f -> {
            throw new IllegalStateException("client gone");
        })).isInstanceOf(IllegalStateException.class);

        assertThat(scan.getStatus()).isEqualTo("FAILED");
        assertThat(scan.getFindingsCount()).isZero();
    }
}