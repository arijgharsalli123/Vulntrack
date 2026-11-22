package tn.vulntrack.scan_service.exception;

public class InvalidScanRequestException extends RuntimeException {
    public InvalidScanRequestException(String message) {
        super(message);
    }
}