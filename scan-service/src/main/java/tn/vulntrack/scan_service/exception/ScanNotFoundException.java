package tn.vulntrack.scan_service.exception;

public class ScanNotFoundException extends RuntimeException {
    public ScanNotFoundException(String scanId) {
        super("Scan not found: " + scanId);
    }
}