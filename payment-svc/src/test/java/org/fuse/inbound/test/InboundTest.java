package org.fuse.inbound.test;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQMessage;
import org.apache.activemq.command.ActiveMQObjectMessage;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


public class InboundTest extends CamelSpringTestSupport{

	private AddSystemRecord addRequest;
	private UpdateSystemRecord updateRequest;
	private Search searchRequest;
	
	protected Exchange exchange;
	private ActiveMQConnectionFactory connectionFactory;
	private byte[] bytes;
	
	//@EndpointInject(uri = "direct:callRestEndpoint") private MockEndpoint resultEndpoint;

    @Produce(uri = "direct:callRestEndpoint") private ProducerTemplate directEndpoint;
    

	
	
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
        
        //establish connection to Active-MQ
        connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
  
        
        
        //MUST BE ADDED OR BROKER WON'T ACCEPT MESSAGE TYPES FOR SECURITY REASONS
        //connectionFactory.setTrustAllPackages(true);
               
        
    }
    
    
	@Test
	public void testCamelRoute() throws Exception {
    
 

        System.out.println("\n\n****\n*******\n**************\nSTARTING INBOUND ROUTE TESTING\nTEST:\t 3 seperate exchanges will be sent to uri=\"direct:callRestEndpoint\" with the header \"type\" specifying which type of request.\nTEST:\t The first has the type header as \"add\", the second has \"update\", and the third has \"search\".\nTEST:\t A camel route listening on this direct endpoint is defined in bundle-context.xml; it packages\nTEST:\t the inbound exchange's request object to be sent to the cxfrs endpoint.\nTEST:\t Another camel route listening at the cxfrs endpoint (org.fuse.inbound.routing.InboundRouteBuilder.class)\nTEST:\t receives the payload, invokes the appropriate method,\nTEST:\t marshals the object to xml and then pushes it to an AM-Q (tcp://localhost:61616) queue named \"q.empi.deim.in\"\nTEST:\t To assert the route logic works correctly, this test opens\nTEST:\t up a connection to the broker, grabs the first three messages as ByteObjects\nTEST:\t and then asserts that the messages have the expected values and are correctly formatted xml \nTEST:\t This ensures the REST calls make it through the inbound application to the queue on the other side intact\nTEST:\t Following the output of this terminal should ilustrate the Route's logic\n\n");
		
    	directEndpoint.sendBodyAndHeader(addRequest, "type", "add");
    
    	directEndpoint.sendBodyAndHeader(updateRequest, "type", "update");
    	
    	directEndpoint.sendBodyAndHeader(searchRequest, "type", "search");
    	
    	//directEndpoint.sendBody(serverEndpoint);
    	
    	
    	
    	////////////////////////////////////////
    	// Open up connection to the queue and make sure that the previous three os
    	// end up on the newRequest queue and are properly formatted
    	//
    	Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination queue = session.createQueue("q.empi.deim.in");
        
        Thread.sleep(1000);
        MessageConsumer consumer = session.createConsumer(queue);
        Message message = consumer.receive(1000);
        
        System.out.println("\n\nTEST:\tPARSING MESSAGES OFF OF THE QUEUE");
        int message_no = 0;
        
    	AddSystemRecord addMessage = null;
    	UpdateSystemRecord updateMessage = null;
    	Search searchMessage = null;
    	
		JAXBContext jc = JAXBContext.newInstance(AddSystemRecord.class, UpdateSystemRecord.class, Search.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		Marshaller marshaller = jc.createMarshaller();
        
        while (message != null) {
        	System.out.println("TEST:\tMESSAGE GRABBED OFF OF THE QUEUE, TYPE: "+message.getClass().toString());
        	
        	
        	if(message instanceof BytesMessage){
        		//read current message on the queue
	        	byte[] bytes = new byte[(int) ((BytesMessage) message).getBodyLength()];
        		((BytesMessage) message).readBytes(bytes);
        		System.out.println("**********Got byte message: \n\""+ new String(bytes)+"\"");
        		

        		
        		if(unmarshaller.unmarshal(new ByteArrayInputStream(bytes)) instanceof AddSystemRecord){
        				
        			addMessage = (AddSystemRecord) unmarshaller.unmarshal(new ByteArrayInputStream(bytes));
        			
        			System.out.println("TEST:\t**********Got add message: \n\""+ addMessage.toString()+"\"\n\n");
        		}else if (unmarshaller.unmarshal(new ByteArrayInputStream(bytes)) instanceof UpdateSystemRecord){
        			
        			updateMessage = (UpdateSystemRecord) unmarshaller.unmarshal(new ByteArrayInputStream(bytes));
        			
        			System.out.println("TEST:\t**********Got update message: \n\""+ updateMessage.toString()+"\"\n\n");
        			
        		}else if (unmarshaller.unmarshal(new ByteArrayInputStream(bytes)) instanceof Search){

        			searchMessage = (Search) unmarshaller.unmarshal(new ByteArrayInputStream(bytes));
        			
        			System.out.println("TEST:\t**********Got search message: \n\""+ searchMessage.toString()+"\"\n\n");
        		}
        		
        	}else{
        		System.out.println("TEST: UNEXPECTED MESSAGE TYPE ("+message.getClass().toString()+"\t**********Message Content: \n\""+ new String(bytes)+"\"");
        		assertTrue(false);
        	}
        	
    		
        	//consume the next message
        	message = consumer.receive(1000);
        	message_no++;
        }

        		/////////////////////////////////
        		//Test all three messages
        
        		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        		
        		marshaller.marshal(addMessage, stream);
        		
        		assertTrue(stream.toString().equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:addSystemRecord xmlns:ns2=\"http://webservice.index.mdm.sun.com/\"><euid>1242</euid><sysObjBean><createUser>yes</createUser><localId>1238DFT324</localId><person><authFlag>no</authFlag><birthOrder>2</birthOrder><degree>bachelors</degree><firstName>Mark</firstName><gender>male</gender><lastName>Williams</lastName><vetStatus>yes</vetStatus></person><systemCode>LZJFDK</systemCode></sysObjBean></ns2:addSystemRecord>"));
        		System.out.println("TEST: ASSERT PASSED!\tThe add request xml pulled from the broker is as expected  \n" + stream.toString()+"\n----\n");
        		stream.reset();
        		
        		
        		marshaller.marshal(updateMessage, stream);
        		
        		assertTrue(stream.toString().equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:updateSystemRecord xmlns:ns2=\"http://webservice.index.mdm.sun.com/\"><sysObjBean><createUser>yes</createUser><person><authFlag>yes</authFlag><birthOrder>3</birthOrder><degree>master</degree><firstName>Kelly</firstName><gender>female</gender><lastName>Johnson</lastName><SSN>334-65-7443</SSN><vetStatus>no</vetStatus></person><systemCode>LZJFDK</systemCode></sysObjBean></ns2:updateSystemRecord>"));
        		System.out.println("TEST: ASSERT PASSED!\tThe update request xml pulled from the broker is as expected  \n" + stream.toString()+"\n----\n");
        		stream.reset();
        		
        		marshaller.marshal(searchMessage, stream);
        		assertTrue(stream.toString().equals("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:search xmlns:ns2=\"http://webservice.index.mdm.sun.com/\"><objBean><firstName>Kelly</firstName><gender>female</gender><lastName>Johnson</lastName><SSN>334-65-7443</SSN></objBean></ns2:search>"));
        		//assertTrue(messageThreeString.contains(\"<patient firstName=\\"John\\" lastName=\\"Smith\\" operation=\\"search\\"/>\n\"));
        		System.out.println("TEST: ASSERT PASSED!\tThe request xml pulled from the broker is as expected\n" + stream.toString() + "\n----\n\nTEST: INBOUND APPLICATION TESTS COMPLETE\n\n**************\n********\n***\n\n");
        		
	}



	@Override
	protected AbstractApplicationContext createApplicationContext() {
		// TODO Auto-generated method stub
		AbstractXmlApplicationContext springCtx	=
				new ClassPathXmlApplicationContext("META-INF/spring/bundle-context.xml");
		return springCtx;
	}

	
}
