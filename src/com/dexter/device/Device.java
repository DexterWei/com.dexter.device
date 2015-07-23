package com.dexter.device;

import java.util.Date;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class Device {
	private static String Name = "Default";
	public static boolean observe = false;
	public static int count = 0;
	public static double temperature;
	
	public static JSONObject resource;
	// the observation object takes the form of
	/*
	 * {"Resource1":"Url1","Resource2":"Url2"}]
	 */
	public static JSONObject observation;
	
	public static JSONObject getResource(){
		return resource;
	}
	
	public static JSONObject getObservation(){
		return observation;
	}
	
	public static void addResource(String key,Object val){
		if(resource == null)
			resource = new JSONObject();
		try {
			resource.put(key, val);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean setResource(String key,Object val) throws JSONException{
		if(resource.has(key) == false)
			return false;
		if(observation.has(key)){
			try {
				Client client = Client.create();
				WebResource r = client.resource((String)observation.get(key));
				ClientResponse response = r.type("application/json")
						.post(ClientResponse.class, new JSONObject().put(key, val));
				if (response.getStatus() > 202) {
				   throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
				}
				String result = response.getEntity(String.class);
				if("stop".equals(result))
					delObserve(key);
				
			  } catch (Exception e) {
				e.printStackTrace();
			  }
			resource.put(key, val);
		}
		if(resource.get(key) == val)
			return true;
		else
			return false;
	}
	
	public static boolean delResource(String key) throws JSONException{
		if(resource.has(key) == false)
			return false;
		resource.put(key, JSONObject.NULL);
		if(resource.has(key) )
			return false;
		else
			return true;
	}
	
	public static void addObserve(String rsc, String reportUrl){
		if(observation == null)
			observation = new JSONObject();
		try {
			observation.put(rsc, reportUrl);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static boolean delObserve(String rsc){
		try {
			if(observation.get(rsc) == null)
				return false;
			observation.put(rsc, JSONObject.NULL);
			return true;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public static double getTemp(){
		return temperature;
	}
	
	public static void setTemp(double tmp){
		while(count > 0){
			String url = "http://localhost:8080/com.dexter.manager/notification";
			try {
				Client client = Client.create();
				WebResource r = client.resource(url);
				ClientResponse response = r.type("application/json")
						.post(ClientResponse.class, new JSONObject().put("Temperature", ""+count));
		
				if (response.getStatus() > 202) {
				   throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
				}
			  } catch (Exception e) {
				e.printStackTrace();
			  }
			count--;
		}
	}
	
	public static String getName(){
		return Name;
	}
	public static void setName(String nm){
		Name = nm;
	}
}
