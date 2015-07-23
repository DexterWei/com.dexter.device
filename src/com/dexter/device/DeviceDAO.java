package com.dexter.device;

import java.net.UnknownHostException;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import java.util.Date;

public class DeviceDAO {
	private static MongoClient client=null;
	
	public static MongoClient Connect(){
		if(client!=null)
			return client;
		try{
		client = new MongoClient("localhost" , 27017);
		}catch(UnknownHostException ex){
			System.err.println(ex);
		}
		return client;
	}
	
	public static DBObject getDevice(BasicDBObject dbj){
		DB db = client.getDB("Client");
		DBCollection clnt = db.getCollection("Device");
		//BasicDBObject dbj =  (BasicDBObject) JSON.parse(obj.toString());
		DBCursor rst = clnt.find();
		return rst.next();
	}
	
	public static void updateResource(String src,String val){
		DB db = client.getDB("Client");
		DBCollection clnt = db.getCollection("Device");
		BasicDBObject query = (BasicDBObject) new BasicDBObject("Resource.Name", src);
		BasicDBObject update = (BasicDBObject) new BasicDBObject("$set", new BasicDBObject("Resource.$.Value", val));
		clnt.update(query, update);
	}
	
	public static void startObserve(String src,String url){
		DB db = client.getDB("Client");
		DBCollection clnt = db.getCollection("Device");
		BasicDBObject query = (BasicDBObject) new BasicDBObject("Resource.Name", src);
		BasicDBObject update = (BasicDBObject) new BasicDBObject("$set", new BasicDBObject("Resource.$.Observed", "Y"));
		clnt.update(query, update);
		BasicDBObject update2 = (BasicDBObject) new BasicDBObject("$set", new BasicDBObject("Resource.$.Observer", url));
		clnt.update(query, update2);
	}
	
	public static void stopObserve(String src){
		DB db = client.getDB("Client");
		DBCollection clnt = db.getCollection("Device");
		BasicDBObject query = (BasicDBObject) new BasicDBObject("Resource.Name", src);
		BasicDBObject update = (BasicDBObject) new BasicDBObject("$set", new BasicDBObject("Resource.$.Observed", "N"));
		clnt.update(query, update);
		BasicDBObject update2 = (BasicDBObject) new BasicDBObject("$set", new BasicDBObject("Resource.$.Observer", ""));
		clnt.update(query, update2);
	}
	
	public static void IceOn(){
		updateResource("IceMaker","On");
	}
	public static void IceOff(){
		updateResource("IceMaker","Off");
	}
	
	public static void createResource(BasicDBObject newEntry){
		DB db = client.getDB("Client");
		DBCollection clnt = db.getCollection("Device");
		BasicDBObject query = (BasicDBObject) new BasicDBObject("SN", "cmpe273");
		BasicDBObject update = (BasicDBObject) new BasicDBObject("$addToSet", new BasicDBObject("Resource",newEntry));
		clnt.update(query, update);
	}
	
	public static void deleteResource(String Name){
		DB db = client.getDB("Client");
		DBCollection clnt = db.getCollection("Device");
		BasicDBObject query = (BasicDBObject) new BasicDBObject("SN", "cmpe273");
		BasicDBObject update = (BasicDBObject) new BasicDBObject("$pull", new BasicDBObject("Resource", new BasicDBObject("Name",Name)));
		clnt.update(query, update);
	}
	
	public static String setDevice(BasicDBObject target,BasicDBObject newval){
		DB db = client.getDB("Client");
		DBCollection clnt = db.getCollection("Device");
		//BasicDBObject dbj =  (BasicDBObject) JSON.parse(obj.toString());
		BasicDBObject update = new BasicDBObject();
		update.put("$set", newval);
		
		WriteResult rst= clnt.update(target,update);
		if(rst.getN()==1)
			return "Update Done";
		else
			return "Update aborted";
	}
	
	public static void main(String[] args) throws JSONException{
		Connect();
		BasicDBObject myself = (BasicDBObject) new BasicDBObject().put("SN", "cmpe273");
		System.out.println(getDevice(myself).toString());
		updateResource("Thermometer","4");
		System.out.println(getDevice(myself).toString());
		
		JSONObject jbj = new JSONObject().put("Resource", new JSONObject().put("Name","Light").put("Value", "On"));
		BasicDBObject nrsc = (BasicDBObject) JSON.parse(jbj.toString());
		//createResource(nrsc);
		System.out.println(getDevice(myself).toString());
		deleteResource("Light");
		System.out.println(getDevice(myself).toString());
		DisConnect();
	}
/*
	public static void InsertSubscriber(JSONObject obj) throws JSONException{
		DB db = client.getDB("Client");
		try{
			String maker = (String)obj.get("Manufacturer");
			DBCollection clnt=db.getCollection(maker);
			BasicDBObject dbj = (BasicDBObject)JSON.parse(obj.toString());
			
			dbj.append("StartTime", new Date().toString()).append("EndTime", "");
			clnt.insert(dbj);
		}catch(JSONException ex){
			System.out.println("unknown manufacturer");
		}
	}
	
	public static boolean FindDevice(String manufacturer,String model){
		DB db=client.getDB("RegServer1");
		DBCollection clnt = db.getCollection("inventory");
		DBCursor rst = clnt.find(new BasicDBObject().append("Manufacturer", manufacturer).append("Model",model));
		if(rst.count()==1){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean FindSubscriber(JSONObject device) throws JSONException{
		DB db=client.getDB("RegServer1");
		String collection=(String) device.get("Manufacturer");
		
		DBCollection clnt= db.getCollection(collection);
		try{
			DBCursor rst= clnt.find(new BasicDBObject()
										.append("Manufacturer", device.get("Manufacturer"))
										.append("Model",device.get("Model"))
										.append("SN",device.get("SN")));
			if(rst.count()==1) {
				return true;
			}else{
				return false;
			}
		}catch(JSONException ex){
			return false;
		}
	}
	
	public static String UpdateSubscriber(JSONObject device) throws JSONException{
		DB db=client.getDB("RegServer1");
		String collection=(String) device.get("Manufacturer");
		
		DBCollection clnt= db.getCollection(collection);
		try{
			BasicDBObject query = new BasicDBObject()
										.append("Manufacturer", device.get("Manufacturer"))
										.append("Model",device.get("Model"))
										.append("SN",device.get("SN"));
	        BasicDBObject update = new BasicDBObject();
	        update.put("$set", new BasicDBObject("Resources",(BasicDBObject)JSON.parse(device.get("Resources").toString())));
			
	        WriteResult rst= clnt.update(query,update);
	        if(rst.getN()==1)
	        	return "Update Done";
	        else
	        	return "Update aborted";
		}catch(JSONException ex){
			return ex.toString();
		}
	}
	
	public static String DeregisterSubscriber(JSONObject device) throws JSONException{
		DB db=client.getDB("RegServer1");
		String collection=(String) device.get("Manufacturer");
		
		DBCollection clnt= db.getCollection(collection);
		try{
			BasicDBObject query = new BasicDBObject()
										.append("Manufacturer", device.get("Manufacturer"))
										.append("Model",device.get("Model"))
										.append("SN",device.get("SN"));
	        BasicDBObject update = new BasicDBObject();
	        update.put("$set", new BasicDBObject("EndTime",new Date().toString()));
			
	        WriteResult rst= clnt.update(query,update);
	        if(rst.getN()==1)
	        	return "De-register Done";
	        else
	        	return "De-register failed";
		}catch(JSONException ex){
			return ex.toString();
		}
	}
	*/
	public static void DisConnect(){
		client.close();
	}
}
