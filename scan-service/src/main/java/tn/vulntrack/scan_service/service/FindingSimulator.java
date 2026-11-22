
package tn.vulntrack.scan_service.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Component;

import tn.vulntrack.scan_service.model.SimulatedFinding;

@Component
public class FindingSimulator {

    // Fake CVE identifiers (CVE-2099-xxxx)
    private static final List<SimulatedFinding> POOL = List.of(

        new SimulatedFinding(
            "CVE-2099-0001",
            "Remote code execution in web framework",
            "Unauthenticated attacker can execute arbitrary code.",
            "CRITICAL"
        ),

        new SimulatedFinding(
            "CVE-2099-0002",
            "Authentication bypass in admin panel",
            "Admin endpoints are reachable without credentials.",
            "CRITICAL"
        ),

        new SimulatedFinding(
            "CVE-2099-0003",
            "Deserialization flaw in message handler",
            "Crafted payload leads to code execution.",
            "CRITICAL"
        ),

        new SimulatedFinding(
            "CVE-2099-0004",
            "SQL injection in search endpoint",
            "User input is concatenated into a SQL query.",
            "HIGH"
        ),

        new SimulatedFinding(
            "CVE-2099-0005",
            "Path traversal in file download",
            "Files outside the web root can be read.",
            "HIGH"
        ),

        new SimulatedFinding(
            "CVE-2099-0006",
            "Privilege escalation in service account",
            "A low-privilege account can obtain admin rights.",
            "HIGH"
        ),

        new SimulatedFinding(
            "CVE-2099-0007",
            "Stored XSS in comment field",
            "Script content is saved and replayed to other users.",
            "MEDIUM"
        ),

        new SimulatedFinding(
            "CVE-2099-0008",
            "Missing rate limiting on login",
            "Passwords can be guessed without throttling.",
            "MEDIUM"
        ),

        new SimulatedFinding(
            "CVE-2099-0009",
            "Outdated TLS configuration",
            "Weak protocol versions are still accepted.",
            "MEDIUM"
        ),

        new SimulatedFinding(
            "CVE-2099-0010",
            "Verbose error messages",
            "Stack traces are returned to clients.",
            "LOW"
        ),

        new SimulatedFinding(
            "CVE-2099-0011",
            "Missing security headers",
            "Clickjacking protection headers are absent.",
            "LOW"
        ),

        new SimulatedFinding(
            "CVE-2099-0012",
            "Server version disclosure",
            "The server banner reveals its exact version.",
            "LOW"
        )
    );

    /**
     * Same asset always gives the same findings:
     * 3 to 10 findings, at least one CRITICAL.
     */
    public List<SimulatedFinding> findingsFor(String assetId) {

        Random random = new Random(assetId.hashCode());

        List<SimulatedFinding> shuffled = new ArrayList<>(POOL);

        Collections.shuffle(shuffled, random);

        int count = 3 + random.nextInt(8);

        List<SimulatedFinding> selected =
                new ArrayList<>(shuffled.subList(0, count));

        boolean hasCritical = selected.stream()
                .anyMatch(f -> "CRITICAL".equals(f.severity()));

        if (!hasCritical) {
            selected.set(count - 1, POOL.get(0));
        }

        return selected;
    }
}
