package tn.vulntrack.ticketing_service.endpoint;

import java.util.GregorianCalendar;
//import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;


import tn.vulntrack.ticketing_service.gen.CreateTicketRequest;
import tn.vulntrack.ticketing_service.gen.CreateTicketResponse;
import tn.vulntrack.ticketing_service.gen.GetTicketStatusRequest;
import tn.vulntrack.ticketing_service.gen.GetTicketStatusResponse;
import tn.vulntrack.ticketing_service.gen.TicketStatus;
//import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
//import javax.xml.datatype.DatatypeFactory;
import tn.vulntrack.ticketing_service.model.Ticket;
import tn.vulntrack.ticketing_service.service.TicketService;

@Endpoint
public class TicketEndpoint {

    private static final String NAMESPACE = "http://vulntrack.tn/ticketing";

    private final TicketService ticketService;

    public TicketEndpoint(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreateTicketRequest")
    @ResponsePayload
    public CreateTicketResponse createTicket(@RequestPayload CreateTicketRequest request)
            throws DatatypeConfigurationException {
      String severity = request.getSeverity() == null ? null : request.getSeverity().value();
     Ticket ticket = ticketService.create(
        request.getVulnerabilityId(),
        request.getTitle(),
        severity,
        request.getAssetName());

        CreateTicketResponse response = new CreateTicketResponse();
        response.setTicketId(ticket.getTicketId());
        response.setStatus(TicketStatus.fromValue(ticket.getStatus()));
        response.setCreatedAt(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                GregorianCalendar.from(ticket.getCreatedAt().toZonedDateTime())));
        return response;
    }
    @PayloadRoot(namespace = NAMESPACE, localPart = "GetTicketStatusRequest")
    @ResponsePayload
    public GetTicketStatusResponse getTicketStatus(@RequestPayload GetTicketStatusRequest request) {
        Ticket ticket = ticketService.getById(request.getTicketId());

        GetTicketStatusResponse response = new GetTicketStatusResponse();
        response.setTicketId(ticket.getTicketId());
        response.setStatus(TicketStatus.fromValue(ticket.getStatus()));
        return response;
    }
}
