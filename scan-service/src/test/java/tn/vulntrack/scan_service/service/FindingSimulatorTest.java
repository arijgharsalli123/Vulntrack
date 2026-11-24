package tn.vulntrack.scan_service.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import tn.vulntrack.scan_service.model.SimulatedFinding;

class FindingSimulatorTest {

    private final FindingSimulator simulator = new FindingSimulator();

    @Test
    void sameAssetAlwaysGivesSameFindings() {
        assertThat(simulator.findingsFor("asset-1")).isEqualTo(simulator.findingsFor("asset-1"));
    }

    @Test
    void findingsRespectTheRules() {
        for (int i = 0; i < 200; i++) {
            List<SimulatedFinding> findings = simulator.findingsFor("asset-" + i);

            assertThat(findings).hasSizeBetween(3, 10);
            assertThat(findings).anyMatch(f -> "CRITICAL".equals(f.severity()));
            assertThat(findings.stream().map(SimulatedFinding::cveId).distinct().count())
                    .isEqualTo(findings.size());
        }
    }
}