package com.intuit.services.rest;

import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.intuit.services.common.Credentials;
import com.intuit.services.common.CredentialsService;
import com.intuit.services.utils.Utils;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

/**
 * @author udasari
 */
@Component
public class BaseRestApi {

	public Logger logger = Logger.getLogger(this.getClass());

	@Value("${env}")
	public String env;
	@Value("${filingBaseURL}")
	public String filingBaseURL;
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

	@Autowired
	private CredentialsService credentialsService;
	
	@Autowired
	protected Utils utils;

	protected Credentials credentials;

	@Before
	public void setUp() throws Exception {
		System.out.println("cretials start");
		credentials = credentialsService.getCredentials();
		System.out.println("cretials end");

	}

//	public String convertFileTOString(String request) throws IOException {
//		String nextFileName = request;
//		Resource res = context.getResource("json/" + nextFileName);
//		InputStream fstream = res.getInputStream();
//		DataInputStream in = new DataInputStream(fstream);
//		BufferedReader br = new BufferedReader(new InputStreamReader(in));
//		String strLine;
//		String jsonTxt = "";
//		while ((strLine = br.readLine()) != null) {
//			jsonTxt = jsonTxt + strLine;
//		}
//		return jsonTxt;
//	}

	public String postRestCall(String absoluteUrl, String json)
			throws URISyntaxException {
		Client client = Client.create();
		String domain = utils.getDomainName(filingBaseURL);

		WebResource webResource = client.resource(filingBaseURL + absoluteUrl);
		Builder builder = utils.addAuthCookies(webResource, domain, credentials);

		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
		if (response.getStatus() != 200) {
			throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		} else {
			logger.info("Sucess : " + response);
		}
		String entity = response.getEntity(String.class);
		return entity;
	}

	public ClientResponse postRestCallAndGetResponse(String absoluteUrl, String json) throws Exception {
		Client client = Client.create();
		String domain = utils.getDomainName(filingBaseURL);

		WebResource webResource = client.resource(filingBaseURL + absoluteUrl);
		Builder builder = utils.addAuthCookies(webResource, domain, credentials);

		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, json);
		return response;
	}

	public ClientResponse getRestCall(String url) throws Exception {
		Client client = Client.create();
		String domain = utils.getDomainName(filingBaseURL);
		WebResource webResource = client.resource(url);
		Builder builder = utils.addAuthCookies(webResource, domain, credentials);
		ClientResponse response = builder.type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
		return response;
	}
	
}
