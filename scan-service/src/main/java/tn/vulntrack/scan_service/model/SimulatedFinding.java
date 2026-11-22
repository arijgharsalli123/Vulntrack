
package tn.vulntrack.scan_service.model;

public record SimulatedFinding(
        String cveId,
        String title,
        String description,
        String severity
) {
}
