package tn.vulntrack.scan_service.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import tn.vulntrack.scan.v1.Finding;
import tn.vulntrack.scan.v1.GetScanStatusRequest;
import tn.vulntrack.scan.v1.GetScanStatusResponse;
import tn.vulntrack.scan.v1.ScanServiceGrpc;
import tn.vulntrack.scan.v1.ScanStatus;
import tn.vulntrack.scan.v1.Severity;
import tn.vulntrack.scan.v1.StartScanRequest;
import tn.vulntrack.scan.v1.StartScanResponse;
import tn.vulntrack.scan.v1.StreamFindingsRequest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:scantest",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "server.port=0",
        "spring.grpc.server.port=9191",
        "scan.findings.delay-ms=0"
})
class ScanGrpcIntegrationTest {

    private static ManagedChannel channel;

    @BeforeAll
    static void connect() {
        channel = ManagedChannelBuilder.forAddress("localhost", 9191).usePlaintext().build();
    }

    @AfterAll
    static void disconnect() {
        channel.shutdownNow();
    }

    private static ScanServiceGrpc.ScanServiceBlockingStub stub() {
        return ScanServiceGrpc.newBlockingStub(channel).withDeadlineAfter(5, TimeUnit.SECONDS);
    }

    private static StartScanRequest validRequest() {
        return StartScanRequest.newBuilder()
                .setAssetId("asset-1")
                .setAssetName("web-server-01")
                .setAssetType("SERVER")
                .build();
    }

    @Test
    void startScan_valid_returnsRunning() {
        StartScanResponse response = stub().startScan(validRequest());

        assertThat(response.getScanId()).startsWith("SCAN-");
        assertThat(response.getStatus()).isEqualTo(ScanStatus.RUNNING);
    }

    @Test
    void startScan_blankName_returnsInvalidArgument() {
        StartScanRequest bad = validRequest().toBuilder().setAssetName("").build();

        assertThatThrownBy(() -> stub().startScan(bad))
                .isInstanceOfSatisfying(StatusRuntimeException.class,
                        e -> assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.INVALID_ARGUMENT));
    }

    @Test
    void getScanStatus_unknownScan_returnsNotFound() {
        GetScanStatusRequest request = GetScanStatusRequest.newBuilder().setScanId("SCAN-unknown").build();

        assertThatThrownBy(() -> stub().getScanStatus(request))
                .isInstanceOfSatisfying(StatusRuntimeException.class,
                        e -> assertThat(e.getStatus().getCode()).isEqualTo(Status.Code.NOT_FOUND));
    }

    @Test
    void streamFindings_sendsFindingsThenScanIsCompleted() {
        String scanId = stub().startScan(validRequest()).getScanId();

        List<Finding> findings = new ArrayList<>();
        stub().streamFindings(StreamFindingsRequest.newBuilder().setScanId(scanId).build())
                .forEachRemaining(findings::add);

        assertThat(findings).hasSizeBetween(3, 10);
        assertThat(findings).anyMatch(f -> f.getSeverity() == Severity.CRITICAL);

        GetScanStatusResponse status = stub().getScanStatus(
                GetScanStatusRequest.newBuilder().setScanId(scanId).build());
        assertThat(status.getStatus()).isEqualTo(ScanStatus.COMPLETED);
        assertThat(status.getFindingsCount()).isEqualTo(findings.size());
    }
}