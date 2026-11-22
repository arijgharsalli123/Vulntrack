package tn.vulntrack.scan_service.grpc;

import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import tn.vulntrack.scan.v1.ScanServiceGrpc;
import tn.vulntrack.scan.v1.ScanStatus;
import tn.vulntrack.scan.v1.StartScanRequest;
import tn.vulntrack.scan.v1.StartScanResponse;

@Service
public class ScanGrpcService extends ScanServiceGrpc.ScanServiceImplBase {

    @Override
    public void startScan(StartScanRequest request, StreamObserver<StartScanResponse> out) {
        out.onNext(StartScanResponse.newBuilder()
                .setScanId("test-scan")
                .setStatus(ScanStatus.RUNNING)
                .build());
        out.onCompleted();
    }
}