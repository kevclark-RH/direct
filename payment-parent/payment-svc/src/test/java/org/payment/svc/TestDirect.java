package org.payment.svc;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import com.sun.mdm.index.webservice.AddSystemRecord;
import com.sun.mdm.index.webservice.PersonBean;
import com.sun.mdm.index.webservice.Search;
import com.sun.mdm.index.webservice.SystemPerson;
import com.sun.mdm.index.webservice.UpdateSystemRecord;

import org.junit.Before;
import org.junit.Test;


public class TestDirect extends CamelSpringTestSupport{

	private AddSystemRecord addRequest;
	private UpdateSystemRecord updateRequest;
	private Search searchRequest;
	
	protected Exchange exchange;
	private byte[] bytes;
	
	//@EndpointInject(uri = "direct:callRestEndpoint") private MockEndpoint resultEndpoint;

    @Produce(uri = "direct:callRestEndpoint") private ProducerTemplate restFactory;
    
	
    @Before
    public void setup() throws Exception {
    	
    	//craft add request
    	SystemPerson addReqSys = new SystemPerson();
    	PersonBean addReqPerson = new PersonBean();
    	
    	addReqPerson.setLastName("Williams");
    	addReqPerson.setFirstName("Mark");
    	addReqPerson.setVetStatus("yes");
    	addReqPerson.setBirthOrder("2");
    	addReqPerson.setAuthFlag("no");
    	addReqPerson.setDegree("bachelors");
    	addReqPerson.setGender("male");
    	
    	addReqSys.setPerson(addReqPerson);
    	addReqSys.setSystemCode("LZJFDK");
    	addReqSys.setCreateUser("yes");
    	addReqSys.setLocalId("1238DFT324");
    	

    	addRequest = new AddSystemRecord();
    	addRequest.setEuid("1242");
    	addRequest.setSysObjBean(addReqSys);
    	
    	//craft update request
    	SystemPerson updateReqSys = new SystemPerson();
    	PersonBean updateReqPerson = new PersonBean();
    	
    	updateReqPerson.setLastName("Johnson");
    	updateReqPerson.setFirstName("Kelly");
    	updateReqPerson.setSSN("334-65-7443");
    	updateReqPerson.setVetStatus("no");
    	updateReqPerson.setBirthOrder("3");
    	updateReqPerson.setAuthFlag("yes");
    	updateReqPerson.setDegree("master");
    	updateReqPerson.setGender("female");
    	
    	updateReqSys.setPerson(updateReqPerson);
    	updateReqSys.setSystemCode("LZJFDK");
    	updateReqSys.setCreateUser("yes");
    	
    	updateRequest = new UpdateSystemRecord();
    	updateRequest.setSysObjBean(updateReqSys);
    	
    	//craft search request

    	PersonBean searchReqPerson = new PersonBean();
    	searchReqPerson.setLastName("Johnson");
    	searchReqPerson.setFirstName("Kelly");
    	searchReqPerson.setSSN("334-65-7443");
    	searchReqPerson.setGender("female");
    	
    	searchRequest = new Search();
    	searchRequest.setObjBean(searchReqPerson);
    	
    	//craft malformed bean
    	PersonBean malFormedReqPerson = new PersonBean();
    	malFormedReqPerson.setLastName("</person>");
    	malFormedReqPerson.setFirstName("Kelly");
    	malFormedReqPerson.setSSN("334-65-7443");
    	malFormedReqPerson.setGender("female");
    	
        //get default exchange
        exchange = new DefaultExchange(context);
        

        
    }
    
    
	@Test
	public void testCamelRoute() throws Exception {
		
		
		System.out.print("\n\n\n\nTESTING\n\n\n\n\n");
		
		restFactory.sendBodyAndHeader(addRequest, "type", "add");

		Thread.sleep(10000);
		
		
		assertTrue(true);
	
		
	}



	@Override
	protected AbstractApplicationContext createApplicationContext() {
		// TODO Auto-generated method stub
		AbstractXmlApplicationContext springCtx	=
				new ClassPathXmlApplicationContext("META-INF/spring/bundle-context.xml");
		return springCtx;
	}

	
}
