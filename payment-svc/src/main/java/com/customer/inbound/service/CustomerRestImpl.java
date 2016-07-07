package com.customer.inbound.service;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQObjectMessage;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import com.customer.app.Person;
import com.sun.mdm.index.webservice.AddOrUpdateSystemRecord;
import com.sun.mdm.index.webservice.AddSystemRecord;
import com.sun.mdm.index.webservice.Search;
import com.sun.mdm.index.webservice.UpdateSystemRecord;

public class CustomerRestImpl implements CustomerRest{

	
	@Produce(uri = "direct:uploadToQueue")
	private ProducerTemplate template;
	
	@Override
	public String add(AddSystemRecord addRequest) {
		// TODO Auto-generated method stub
		
		
		template.sendBody(addRequest);
		
		
		System.out.println("\n**********\nADD REST METHOD CALLED\n**********\n");
		return "";
	}
	
	@Override
	public String update(UpdateSystemRecord updateRequest) {
		// TODO Auto-generated method stub
		

		template.sendBody(updateRequest);
		
		
		System.out.println("\n**********\nUPDATE REST METHOD CALLED\n**********\n");
		return "";
	}

	@Override
	public String search(Search searchRequest) {
		// TODO Auto-generated method stub
		

		template.sendBody(searchRequest);
		
		System.out.println("\n**********\nSEARCH REST METHOD CALLED\n**********\n");
		return "";
	}
	

}
