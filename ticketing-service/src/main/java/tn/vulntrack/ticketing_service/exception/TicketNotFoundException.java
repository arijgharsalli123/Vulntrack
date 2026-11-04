package tn.vulntrack.ticketing_service.exception;

public class TicketNotFoundException extends RuntimeException {

    public TicketNotFoundException(String ticketId) {
        super("Ticket not found: " + ticketId);
    }
}