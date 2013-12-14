package com.intuit.services.common;

import java.util.Date;

import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.intuit.services.utils.Utils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Service
public class DataServiceClientImpl implements DataServiceClient {
	
	@Autowired
	private Utils utils;
	
	@Value("${filingBaseURL}")
	public String filingBaseURL;
	
	@Value("${datasvcBaseURL}")
	public String dataSvcBaseURL;
	
	@Value("${indClientRequest}")
	public String indClientRequest;
	
	@Value("${createReturnRequest}")
	public String createReturnRequest;
	
	@Value("${postV1Firm}")
	public String postFirm;
	
	@Value("${postV1Clients}")
	public String postClients;
	
	@Value("${postV1Returns}")
	public String postReturns;
	
	//private static Logger logger = Logger.getLogger(DataServiceClientImpl.class);
	
	public JSONObject createNewReturn(Credentials credentials) throws Exception{
		
		JSONObject firmData = getFirm(credentials);
		firmData = updateFirm(credentials, firmData);
		JSONObject clientData = getNewClient(credentials);
		JSONObject returnData = getNewReturn(credentials, clientData.getString("entityId"), firmData.getString("id_uuid"));

		return returnData;
	}

	public JSONObject getFirm(Credentials credentials) throws Exception {
		Client client = Client.create();

		String domain = utils.getDomainName(dataSvcBaseURL);
		WebResource webResource = client.resource(dataSvcBaseURL + postFirm);
		Builder builder = utils.addAuthCookies(webResource, domain, credentials);
		
		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		} else {
			//logger.info("Sucess : " + response);
		}
		String entity = response.getEntity(String.class);
		return new JSONObject(entity);
	}
	
	public JSONObject updateFirm(Credentials credentials, JSONObject firmData) throws Exception {
		Client client = Client.create();

		String domain = utils.getDomainName(dataSvcBaseURL);
		WebResource webResource = client.resource(dataSvcBaseURL + postFirm);
		Builder builder = utils.addAuthCookies(webResource, domain, credentials);
		
		firmData.put("zip", "75024");
		firmData.put("phone", "(111)-111-1111");
		firmData.put("address", "test");
		firmData.put("city", "plano");
		JSONObject contact = new JSONObject();
		contact.put("lastName", "test");
		contact.put("firstName", "test");
		contact.put("phoneNumber", "(111)-111-1111");
		firmData.put("contact", contact);
		
		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).put(ClientResponse.class, firmData.toString());
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		} else {
			//logger.info("Sucess : " + response);
		}
		String entity = response.getEntity(String.class);
		return new JSONObject(entity);
	}
	
	public JSONObject getNewClient(Credentials credentials) throws Exception {
		Client client = Client.create();

		String domain = utils.getDomainName(dataSvcBaseURL);
		WebResource webResource = client.resource(dataSvcBaseURL + postClients);
		Builder builder = utils.addAuthCookies(webResource, domain, credentials);
		
		String requestString = utils.convertFileTOString(indClientRequest);
		JSONObject requestJson = new JSONObject(requestString);
		JSONObject requestJsonGetPerson = requestJson.getJSONObject("person");
		JSONArray requestJsonGetNames = requestJsonGetPerson.getJSONArray("names");
		JSONObject requestArrayToJson = (JSONObject) requestJsonGetNames.get(0);
		requestArrayToJson.put("lastName", "Test " + new Date().getTime());
		requestArrayToJson.put("firstName", "Automation");
		requestJsonGetPerson.remove("names");
		JSONArray requestJsonArray = new JSONArray();
		requestJsonArray.put(requestArrayToJson);
		requestJsonGetPerson.put("names", requestJsonArray);
		
		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, requestJson.toString());
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		} else {
			//logger.info("Sucess : " + response);
		}
		String entity = response.getEntity(String.class);
		return new JSONObject(entity);
	}
	
	public JSONObject getNewReturn(Credentials credentials, String clientId, String firmId) throws Exception {
		Client client = Client.create();

		String domain = utils.getDomainName(filingBaseURL);
		WebResource webResource = client.resource(dataSvcBaseURL + postReturns);
		Builder builder = utils.addAuthCookies(webResource, domain, credentials);
		
		String requestString = utils.convertFileTOString(createReturnRequest);
		JSONObject requestJson = new JSONObject(requestString);
		requestJson.put("id_client", clientId);
		requestJson.put("id_firm", firmId);
		requestJson.remove("id_uuid");
		
		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, requestJson.toString());
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		} else {
			//logger.info("Sucess : " + response);
		}
		String entity = response.getEntity(String.class);
		return new JSONObject(entity);
	}
	
}
