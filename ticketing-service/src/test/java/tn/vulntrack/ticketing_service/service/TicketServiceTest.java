package tn.vulntrack.ticketing_service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tn.vulntrack.ticketing_service.exception.TicketNotFoundException;
import tn.vulntrack.ticketing_service.model.Ticket;
import tn.vulntrack.ticketing_service.repository.TicketRepository;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    TicketRepository repository;

    @InjectMocks
    TicketService service;

    @Test
    void create_setsOpenStatusGeneratedIdAndDate() {
        when(repository.save(any(Ticket.class))).thenAnswer(inv -> inv.getArgument(0));

        Ticket ticket = service.create("vuln-001", "Critical flaw", "CRITICAL", "web-server-01");

        assertThat(ticket.getTicketId()).startsWith("TCK-");
        assertThat(ticket.getStatus()).isEqualTo("OPEN");
        assertThat(ticket.getSeverity()).isEqualTo("CRITICAL");
        assertThat(ticket.getCreatedAt()).isNotNull();
    }

    @Test
    void getById_unknownTicket_throwsNotFound() {
        when(repository.findById("TCK-unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById("TCK-unknown"))
                .isInstanceOf(TicketNotFoundException.class);
    }
}