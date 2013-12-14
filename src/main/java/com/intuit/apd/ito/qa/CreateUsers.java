package com.intuit.apd.ito.qa;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intuit.services.common.Credentials;
import com.intuit.services.common.CredentialsService;
import com.intuit.services.utils.Utils;
 
@Path("/hello")
@Component
public class CreateUsers{
 
	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired
	protected Utils utils;
	
	protected Credentials credentials;
	
	public CredentialsService getCredentialsService() {
		return credentialsService;
	}

	public void setCredentialsService(CredentialsService credentialsService) {
		this.credentialsService = credentialsService;
	}

	public Utils getUtils() {
		return utils;
	}

	public void setUtils(Utils utils) {
		this.utils = utils;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	@GET
	@Path("/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMsg(@PathParam("param") String msg) throws Exception {
		JSONObject output = new JSONObject();
		output.put("key1", "value1");
		output.put("key2", "value2");
		output.put("key3", "value3");
		
		System.out.println("cretials start");
		utils.toString();
		
		credentialsService.getCredentials();
		System.out.println("cretials end");
		
		System.out.println("++++++++++++++++++++++++++++++");
		System.out.println(credentials);
		System.out.println("++++++++++++++++++++++++++++++");
		//Credentials output = getCredentials();
		String output1 = "Jersey say : " + msg;
		return Response.status(200).entity(output1).build();
	}
}