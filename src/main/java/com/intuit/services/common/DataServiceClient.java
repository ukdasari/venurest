package com.intuit.services.common;

import org.json.JSONObject;

public interface DataServiceClient {
	
	public JSONObject createNewReturn(Credentials credentials) throws Exception;
	
	public JSONObject getNewClient(Credentials credentials) throws Exception;
	
	public JSONObject getNewReturn(Credentials credentials, String clientId, String firmId) throws Exception;
	
	public JSONObject getFirm(Credentials credentials) throws Exception;
	
	public JSONObject updateFirm(Credentials credentials, JSONObject firmData) throws Exception;

}
