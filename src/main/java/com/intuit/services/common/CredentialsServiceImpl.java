package com.intuit.services.common;

import java.util.Random;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.intuit.services.utils.Utils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Component
public class CredentialsServiceImpl implements CredentialsService{

	@Value("${qaAuthorization}")
	public String qaAuthorization;
	@Value("${qaContent-Type}")
	public String qaContentType;
	@Value("${qaIntuitOriginatingip}")
	public String qaIntuitOriginatingip;
	@Value("${qaIntuitOfferingId}")
	public String qaIntuitOfferingId;
	@Value("${qaIntuitTid}")
	public String qaIntuitTid;
	@Value("${qaOiiUrl}")
	public String qaOiiUrl;
	@Value("${qaCreateRealmUrl}")
	public String qaCreateRealmUrl;
	@Value("${OIICreateRealmRequest}")
	public String OIICreateRealmRequest;
	@Value("${SignUpUserRequest}")
	public String SignUpUserRequest;
	@Value("${env}")
	public String env;
	@Value("${qaCreateUsers}")
	public String qaCreateUsers;

	private static Logger logger = Logger.getLogger(CredentialsServiceImpl.class);

	@Autowired
	private Utils utils;
	
	private Credentials credentials;	public Credentials getCredentials() {
		return credentials;
	}

	public void createCredentials() throws Exception {
		credentials = new Credentials();
		String oiiSignUpResponseString1 = utils.convertFileTOString(SignUpUserRequest);
		JSONObject signUpUserRequestJson = new JSONObject(oiiSignUpResponseString1);
		String email = "Test." + gen() + "@gmail.com";
		signUpUserRequestJson.put("username", email);

		//sign up
		postRestCallForOii(qaCreateUsers, signUpUserRequestJson);

		//sign in 
		String oiiSignInResponseString = postRestCallForOii(qaOiiUrl, setJSONpayloadForOii(email, signUpUserRequestJson.getString("password")));
		JSONObject oiiSignInResponseJson = new JSONObject(oiiSignInResponseString);
		String ticket = (String) oiiSignInResponseJson.get("ticket");
		String userId = (String) oiiSignInResponseJson.get("userId");

		String OIICreateRealmRequestString = utils.convertFileTOString(OIICreateRealmRequest);
		JSONObject OIICreateRealmRequestJson = new JSONObject(OIICreateRealmRequestString);

		//create realm 
		String realmJsonStr = postRestCallForOiiRealm(qaCreateRealmUrl, OIICreateRealmRequestJson, ticket, userId);

		JSONObject realmJson = new JSONObject(realmJsonStr);
		credentials.setTicket(ticket);
		credentials.setAuthId(userId);
		credentials.setRealmId(realmJson.getString("realmId"));
	}

	public String postRestCallForOiiRealm(String url, JSONObject JSONPayload, String ticket, String userId) throws Exception {
		Client client = Client.create();

		WebResource webResource = client.resource(url);
		Builder builder = setHeadersForOiiCreateRealm(webResource, ticket, userId);

		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, JSONPayload.toString());
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		} else {
			logger.info("Sucess : " + response);
		}
		String entity = response.getEntity(String.class);
		return entity;
	}

	public String postRestCallForOii(String url, JSONObject JSONPayload) throws Exception {
		Client client = Client.create();

		WebResource webResource = client.resource(url);
		Builder builder = setHeadersForOii(webResource);

		String response = builder.type(MediaType.APPLICATION_JSON).post(String.class, JSONPayload.toString());
		return response;
	}

	public int gen() {
		Random r = new Random(System.currentTimeMillis());
		return 100000000 + r.nextInt(20000);
	}

	public Builder setHeadersForOii(WebResource webResource) {
		Builder builder = webResource.header("Authorization", qaAuthorization);
		builder = builder.header("Content-type", qaContentType);
		builder = builder.header("intuit_originatingip", qaIntuitOriginatingip);
		builder = builder.header("intuit_offeringId", qaIntuitOfferingId);
		builder = builder.header("intuit_tid", qaIntuitTid);
		return builder;
	}

	public JSONObject setJSONpayloadForOii(String userName, String password) throws JSONException {
		JSONObject JSONpayload = new JSONObject();
		JSONpayload.put("username", userName);
		JSONpayload.put("password", password);

		return JSONpayload;
	}

	public Builder setHeadersForOiiCreateRealm(WebResource webResource, String ticket, String userId) {
		Builder builder = webResource.header("Authorization", qaAuthorization + ",intuit_token_type=IAM-Ticket, intuit_token=" + ticket
				+ ", intuit_userid=" + userId);
		builder = builder.header("Content-type", qaContentType);
		builder = builder.header("intuit_originatingip", qaIntuitOriginatingip);
		builder = builder.header("intuit_offeringId", qaIntuitOfferingId);
		builder = builder.header("intuit_tid", qaIntuitTid);
		return builder;
	}

}
