package com.viewfunction.participantManagementService;

import com.viewfunction.participantManagement.authentication.restful.ParticipantAuthenticateService;
import com.viewfunction.participantManagement.operation.restful.ParticipantOperationService;

import com.viewfunction.participantManagementService.util.ApplicationLauncherUtil;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;

@SpringBootApplication
@ServletComponentScan(basePackages = { "com.viewfunction.participantManagement"})
@ComponentScan(basePackages = { "com.viewfunction.participantManagement"})
@EnableDiscoveryClient
public class ParticipantManagementServiceApplication {

    @Autowired
    private Bus cxfBus;

    @Autowired
    private ParticipantOperationService participantOperationService;

    @Autowired
    private ParticipantAuthenticateService participantAuthenticateService;

    @Bean
    public JacksonJaxbJsonProvider jacksonJaxbJsonProvider() {
        return new JacksonJaxbJsonProvider();
    }

    @Bean
    public JAXBElementProvider jaxbElementProvider() {
        return new JAXBElementProvider();
    }

    @Bean
    public Server cxfRestFulServer() {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(cxfBus);
        endpoint.setAddress("/participantsManagement");
        endpoint.setServiceBeans(Arrays.asList(participantOperationService,participantAuthenticateService));
        endpoint.setProviders(Arrays.asList(jacksonJaxbJsonProvider(), jaxbElementProvider()));
        endpoint.setFeatures(Arrays.asList(new Swagger2Feature()));
        return endpoint.create();
    }

	public static void main(String[] args) {
		SpringApplication.run(ParticipantManagementServiceApplication.class, args);
        ApplicationLauncherUtil.printApplicationConsoleBanner();
	}
}
