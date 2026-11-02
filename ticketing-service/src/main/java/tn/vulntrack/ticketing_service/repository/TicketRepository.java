package tn.vulntrack.ticketing_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.vulntrack.ticketing_service.model.Ticket;

public interface TicketRepository extends JpaRepository<Ticket, String> {
}