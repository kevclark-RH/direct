package org.payment.service;

import javax.ws.rs.*;
import com.sun.mdm.index.webservice.AddOrUpdateSystemRecord;
import com.sun.mdm.index.webservice.AddSystemRecord;
import com.sun.mdm.index.webservice.Search;
import com.sun.mdm.index.webservice.UpdateSystemRecord;
import com.sun.mdm.index.webservice.AddOrUpdateSystemRecord;
import com.sun.mdm.index.webservice.AddOrUpdateSystemRecord;
import com.sun.mdm.index.webservice.AddOrUpdateSystemRecord;


@Path("/PersonEJB/")
public interface CustomerRest {

	 @POST @Path("/addSystemRecordRequest") @Consumes("text/xml; charset=utf-8")
	String add(AddSystemRecord updateRequest);

	 @POST @Path("/updateSystemRecordRequest") @Consumes("text/xml; charset=utf-8")
	String update(UpdateSystemRecord updateRequest);
	 
	 @POST @Path("/searchRequest") @Consumes("text/xml; charset=utf-8")
	String search(Search searchRequest);
}
