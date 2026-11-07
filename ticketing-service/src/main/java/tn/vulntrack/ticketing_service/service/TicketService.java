package tn.vulntrack.ticketing_service.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;

import tn.vulntrack.ticketing_service.exception.InvalidSeverityException;
import tn.vulntrack.ticketing_service.exception.TicketNotFoundException;
import tn.vulntrack.ticketing_service.model.Ticket;
import tn.vulntrack.ticketing_service.repository.TicketRepository;

@Service
public class TicketService {

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public Ticket create(String vulnerabilityId, String title, String severity, String assetName) {
        if (!"HIGH".equals(severity) && !"CRITICAL".equals(severity)) {
         throw new InvalidSeverityException(severity);
        }        
        Ticket ticket = new Ticket();
        ticket.setTicketId("TCK-" + UUID.randomUUID());
        ticket.setVulnerabilityId(vulnerabilityId);
        ticket.setTitle(title);
        ticket.setAssetName(assetName);
        ticket.setSeverity(severity);
        ticket.setStatus("OPEN");
        ticket.setCreatedAt(OffsetDateTime.now());
        return repository.save(ticket);
    }
    public Ticket getById(String ticketId) {
    return repository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
}
}