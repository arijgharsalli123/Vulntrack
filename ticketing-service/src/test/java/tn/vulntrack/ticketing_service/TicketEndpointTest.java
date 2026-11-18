package tn.vulntrack.ticketing_service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.ws.test.server.ResponseMatcher;
import org.springframework.xml.transform.StringSource;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:tickettest",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TicketEndpointTest {

    private static final Map<String, String> NS = Map.of("t", "http://vulntrack.tn/ticketing");

    @Autowired
    ApplicationContext context;

    MockWebServiceClient client;

    @BeforeEach
    void setUp() {
        client = MockWebServiceClient.createClient(context);
    }

    private static String createRequest(String severity) {
        return """
            <t:CreateTicketRequest xmlns:t="http://vulntrack.tn/ticketing">
              <t:vulnerabilityId>vuln-001</t:vulnerabilityId>
              <t:title>Critical flaw on web-server-01</t:title>
              <t:severity>%s</t:severity>
              <t:assetName>web-server-01</t:assetName>
            </t:CreateTicketRequest>
            """.formatted(severity);
    }

    private static ResponseMatcher faultCodeIs(String localPart) {
        return (request, response) -> {
            SoapFault fault = ((SoapMessage) response).getSoapBody().getFault();
            assertThat(fault).as("a SOAP Fault was expected").isNotNull();
            assertThat(fault.getFaultCode().getLocalPart()).isEqualTo(localPart);
        };
    }

    @Test
    void createTicket_validRequest_returnsOpenTicket() {
        client.sendRequest(withPayload(new StringSource(createRequest("CRITICAL"))))
              .andExpect(noFault())
              .andExpect(xpath("//t:status", NS).evaluatesTo("OPEN"));
    }

    @Test
    void createTicket_invalidSeverity_returnsInvalidSeverityFault() {
        client.sendRequest(withPayload(new StringSource(createRequest("BANANA"))))
              .andExpect(faultCodeIs("InvalidSeverity"));
    }

    @Test
    void getTicketStatus_unknownId_returnsTicketNotFoundFault() {
        String request = """
            <t:GetTicketStatusRequest xmlns:t="http://vulntrack.tn/ticketing">
              <t:ticketId>TCK-does-not-exist</t:ticketId>
            </t:GetTicketStatusRequest>
            """;
        client.sendRequest(withPayload(new StringSource(request)))
              .andExpect(faultCodeIs("TicketNotFound"));
    }
}
