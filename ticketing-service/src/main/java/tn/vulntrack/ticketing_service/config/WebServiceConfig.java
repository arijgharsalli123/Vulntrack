package tn.vulntrack.ticketing_service.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.soap.server.endpoint.SoapFaultAnnotationExceptionResolver;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext context) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(context);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "tickets")
    public DefaultWsdl11Definition ticketsWsdl(XsdSchema ticketsSchema) {
        DefaultWsdl11Definition wsdl = new DefaultWsdl11Definition();
        wsdl.setPortTypeName("TicketsPort");
        wsdl.setLocationUri("/ws");
        wsdl.setTargetNamespace("http://vulntrack.tn/ticketing");
        wsdl.setSchema(ticketsSchema);
        return wsdl;
    }

    @Bean
    public XsdSchema ticketsSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/ticket.xsd"));
    }
    @Bean
    public SoapFaultAnnotationExceptionResolver exceptionResolver() {
        SoapFaultAnnotationExceptionResolver resolver = new SoapFaultAnnotationExceptionResolver();
        SoapFaultDefinition defaultFault = new SoapFaultDefinition();
        defaultFault.setFaultCode(SoapFaultDefinition.SERVER);
        defaultFault.setFaultStringOrReason("Internal server error");
        resolver.setDefaultFault(defaultFault);
        resolver.setOrder(1);
        return resolver;
    }
}