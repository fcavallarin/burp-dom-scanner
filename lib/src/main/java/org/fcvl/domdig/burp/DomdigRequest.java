package org.fcvl.domdig.burp;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;

public class DomdigRequest {
	public int id;
	public String type;
	public String method;
	public String url;
	public String headers;
	public String data;
	public String trigger;
	public String triggerEvent = "";
	public String triggerElement = "";

	public DomdigRequest(int id,  String type, String method, String url, String headers, String data, String trigger) {
		super();
		this.id = id;
		this.type = type;
		this.method = method;
		this.url = url;
		this.headers = headers;
		this.data = data;
		this.trigger = trigger.equals("") ? null : trigger;
		
		try {
			JSONObject json = new JSONObject(trigger);
			this.triggerElement = json.getString("element");
			this.triggerEvent = json.getString("event");
		} catch(Exception e) {
			this.trigger = null;
		}
	}
	
	public String getRaw() throws MalformedURLException{
		URL url = new URL(this.url);
		String path = url.getFile();
		String raw = this.method.toUpperCase() + " " + (path.equals("") ? "/" : path) + " HTTP/1.1\r\n";
		raw += "host: " + url.getHost() + (url.getPort() != -1 ? ":" + url.getPort() : "") + "\r\n";
		if(headers != null && !headers.equals("")) {
			JSONObject json = new JSONObject(headers);
			for(String k : json.keySet()) {
				raw += k + ": " + json.getString(k) + "\r\n";
			}
		}
		raw += "\r\n";
		if(data != null) {
			raw += data;
		}
		return raw;
		
	}

}
