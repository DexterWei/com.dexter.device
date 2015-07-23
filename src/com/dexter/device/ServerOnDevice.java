package com.dexter.device;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

@Path("/")
public class ServerOnDevice {
	@Path("/get")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String get(){
		try {
			if(Device.resource == null)
				Device.resource = new JSONObject().put("Light", "On");
			if(Device.observation == null)
				Device.observation = new JSONObject();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Device.getName()+"\n"+Device.getResource().toString()+"\n"+Device.getObservation().toString();
	}
	
	@Path("/set")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setName(String objString){
		//{"Resource":<Name>,"Value":<val>}
		JSONObject obj;
		try {
			obj = new JSONObject(objString);
			String key = (String) obj.get("Resource");
			Object val = obj.get("Value");
			
			return Response.status(201).entity(Device.setResource(key, val)).build();
		} catch (JSONException e) {
			e.printStackTrace();
			return Response.status(202).entity("rename failed").build();
		}
	}
	
	@Path("/observe")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Observe(String objString){
		//{"Resource":<Name>,"Url":<Url>}
		JSONObject obj;
		try {
			obj = new JSONObject(objString);
			String rsc = (String) obj.get("Resource");
			String url = (String) obj.get("Url");
			Device.addObserve(rsc, url);
			return Response.status(201).entity("observation added").build();
		} catch (JSONException e) {
			e.printStackTrace();
			return Response.status(202).entity("observation failed").build();
		}
	}
	
	@Path("/writeAttr")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response WriteAttr(String objString){
		//{"Resource":<Name>,"Cancel":"True/False"}
		JSONObject obj;
		try {
			obj = new JSONObject(objString);
			String rsc = (String) obj.get("Resource");
			String cancel = (String) obj.get("Cancel");
			if("True".equals(cancel))
				Device.observation.remove(rsc);
			return Response.status(201).entity("attribute written").build();
		} catch (JSONException e) {
			e.printStackTrace();
			return Response.status(202).entity("write attribute fail").build();
		}
	}
	
}
