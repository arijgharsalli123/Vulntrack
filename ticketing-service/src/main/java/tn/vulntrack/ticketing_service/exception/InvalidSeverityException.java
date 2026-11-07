package tn.vulntrack.ticketing_service.exception;

import org.springframework.ws.soap.server.endpoint.annotation.FaultCode;
import org.springframework.ws.soap.server.endpoint.annotation.SoapFault;

@SoapFault(faultCode = FaultCode.CUSTOM,
        customFaultCode = "{http://vulntrack.tn/ticketing}InvalidSeverity")
public class InvalidSeverityException extends RuntimeException {

    public InvalidSeverityException(String severity) {
        super("Tickets can only be created for HIGH or CRITICAL severity, received: " + severity);
    }
}