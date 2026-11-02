package tn.vulntrack.ticketing_service.endpoint;

import java.util.GregorianCalendar;
import java.util.UUID;
import javax.xml.datatype.DatatypeFactory;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import tn.vulntrack.ticketing_service.gen.CreateTicketRequest;
import tn.vulntrack.ticketing_service.gen.CreateTicketResponse;
import tn.vulntrack.ticketing_service.gen.TicketStatus;

@Endpoint
public class TicketEndpoint {

    private static final String NAMESPACE = "http://vulntrack.tn/ticketing";

    @PayloadRoot(namespace = NAMESPACE, localPart = "CreateTicketRequest")
    @ResponsePayload
    public CreateTicketResponse createTicket(@RequestPayload CreateTicketRequest request) throws Exception {
        CreateTicketResponse response = new CreateTicketResponse();
        response.setTicketId("TCK-" + UUID.randomUUID());
        response.setStatus(TicketStatus.OPEN);
        response.setCreatedAt(DatatypeFactory.newInstance()
                .newXMLGregorianCalendar(new GregorianCalendar()));
        return response;
    }
}