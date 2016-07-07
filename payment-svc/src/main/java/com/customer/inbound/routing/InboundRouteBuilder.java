package com.customer.inbound.routing;

import javax.xml.bind.JAXBContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;

import com.customer.app.Person;
import com.sun.mdm.index.webservice.AddSystemRecord;
import com.sun.mdm.index.webservice.PersonBean;
import com.sun.mdm.index.webservice.Search;
import com.sun.mdm.index.webservice.SystemPerson;
import com.sun.mdm.index.webservice.UpdateSystemRecord;

import org.apache.camel.spi.DataFormat;

public class InboundRouteBuilder extends RouteBuilder{

	@Override
	public void configure() throws Exception {
		
		JaxbDataFormat dataFormat = new JaxbDataFormat();
		dataFormat.setContext(JAXBContext.newInstance(AddSystemRecord.class, UpdateSystemRecord.class, Search.class, Person.class));

		
		
	
		//listen on the cxfrs endpoint and forward to the correct implementation method
		from("cxfrs:bean:customerRestService?bindingStyle=SimpleConsumer")
		.to("validator:META-INF/xsd/mdm.xsd")
		.log("\n\tMESSAGE RECEIVED AT CXFRS SERVER ENDPOINT\n\t ${body}\n\n")
		.choice()
			.when(header("operationName").isEqualTo("add"))
				.bean("customerRestImplBean").endChoice()
			.when(header("operationName").isEqualTo("search"))
				.bean("customerRestImplBean").endChoice()
			.when(header("operationName").isEqualTo("update"))
				.bean("customerRestImplBean").end();
		

		
		//marshal & upload to queue from direct endpoint
		from("direct:uploadToQueue")
		.log("\n\n\tExchange received at direct:uploadToQueue\n\n-----\n")
		.marshal(dataFormat)
		.log("\n\n\tLOGGING MARSHALLED FORMAT AND PUSHING TO QUEUE\n\t${body}\n\n-----\n")
		.inOnly("amq-notx:queue:q.empi.deim.in").end();
		
		
		
	}

}
