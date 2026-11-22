
package tn.vulntrack.scan_service.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tn.vulntrack.scan.v1.Finding;
import tn.vulntrack.scan.v1.GetScanStatusRequest;
import tn.vulntrack.scan.v1.GetScanStatusResponse;
import tn.vulntrack.scan.v1.ScanServiceGrpc;
import tn.vulntrack.scan.v1.ScanStatus;
import tn.vulntrack.scan.v1.Severity;
import tn.vulntrack.scan.v1.StartScanRequest;
import tn.vulntrack.scan.v1.StartScanResponse;
import tn.vulntrack.scan.v1.StreamFindingsRequest;

import tn.vulntrack.scan_service.exception.InvalidScanRequestException;
import tn.vulntrack.scan_service.exception.ScanNotFoundException;
import tn.vulntrack.scan_service.model.Scan;
import tn.vulntrack.scan_service.model.SimulatedFinding;
import tn.vulntrack.scan_service.service.ScanService;

@Service
public class ScanGrpcService extends ScanServiceGrpc.ScanServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(ScanGrpcService.class);

    private final ScanService scanService;

    public ScanGrpcService(ScanService scanService) {
        this.scanService = scanService;
    }

    @Override
    public void startScan(StartScanRequest request, StreamObserver<StartScanResponse> out) {
        try {
            Scan scan = scanService.start(
                    request.getAssetId(),
                    request.getAssetName(),
                    request.getAssetType()
            );

            out.onNext(StartScanResponse.newBuilder()
                    .setScanId(scan.getScanId())
                    .setStatus(ScanStatus.valueOf(scan.getStatus()))
                    .build());

            out.onCompleted();

        } catch (InvalidScanRequestException e) {
            out.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getScanStatus(
            GetScanStatusRequest request,
            StreamObserver<GetScanStatusResponse> out) {
        try {
            Scan scan = scanService.getById(request.getScanId());

            out.onNext(GetScanStatusResponse.newBuilder()
                    .setScanId(scan.getScanId())
                    .setStatus(ScanStatus.valueOf(scan.getStatus()))
                    .setFindingsCount(scan.getFindingsCount())
                    .build());

            out.onCompleted();

        } catch (InvalidScanRequestException e) {
            out.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());

        } catch (ScanNotFoundException e) {
            out.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void streamFindings(
            StreamFindingsRequest request,
            StreamObserver<Finding> out) {
        try {
            scanService.streamFindings(
                    request.getScanId(),
                    finding -> out.onNext(toProto(finding))
            );

            out.onCompleted();

        } catch (InvalidScanRequestException e) {
            out.onError(Status.INVALID_ARGUMENT
                    .withDescription(e.getMessage())
                    .asRuntimeException());

        } catch (ScanNotFoundException e) {
            out.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());

        } catch (RuntimeException e) {
            log.warn("Streaming failed for scan {}", request.getScanId(), e);

            out.onError(Status.INTERNAL
                    .withDescription("Scan failed")
                    .asRuntimeException());
        }
    }

    private static Finding toProto(SimulatedFinding f) {
        return Finding.newBuilder()
                .setCveId(f.cveId())
                .setTitle(f.title())
                .setDescription(f.description())
                .setSeverity(Severity.valueOf(f.severity()))
                .build();
    }
}
