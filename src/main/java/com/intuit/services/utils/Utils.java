package com.intuit.services.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.Cookie;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.intuit.services.common.Credentials;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;

@Component
public class Utils {

	public Utils(){
		
		System.out.println("initialized");
	}
	
	@Value("${env}")
	public String env;
	
	private ApplicationContext applicationContext;

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;

	}

	public Builder addAuthCookies(WebResource webResource, String domain, Credentials credentials) {

		if (env.equals("qa")) {
			Builder builder = webResource.cookie(getNewCookie("qbn.ptc.ticket", credentials.getTicket(), domain));
			builder = builder.cookie(getNewCookie("qbn.ptc.parentid", credentials.getRealmId(), domain));
			builder = builder.cookie(getNewCookie("qbn.ptc.authid", credentials.getAuthId(), domain));
			return builder;

		}
		if (env.equals("perf")) {
			Builder builder = webResource.cookie(getNewCookie("qbn.dtc.ticket", credentials.getTicket(), domain));
			builder = builder.cookie(getNewCookie("qbn.dtc.parentid", credentials.getRealmId(), domain));
			builder = builder.cookie(getNewCookie("qbn.dtc.authid", credentials.getAuthId(), domain));
			return builder;

		}
		return null;
	}

	private Cookie getNewCookie(String key, String value, String domain) {
		return new Cookie(key, value, "/", domain, Cookie.DEFAULT_VERSION);
	}
	
	public String getDomainName(String url) throws URISyntaxException {
		URI uri = new URI(url);
		String domain = uri.getHost();
		return domain.startsWith("www.") ? domain.substring(4) : domain;
	}
	
	public String convertFileTOString(String request) throws IOException {
		String nextFileName = request;
		Resource res = applicationContext.getResource("json/" + nextFileName);
		InputStream fstream = res.getInputStream();
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		String jsonTxt = "";
		while ((strLine = br.readLine()) != null) {
			jsonTxt = jsonTxt + strLine;
		}
		System.out.println("Utils"+jsonTxt);
		return jsonTxt;
	}

}
