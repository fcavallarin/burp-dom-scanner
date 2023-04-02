package org.fcvl.domdig.burp;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

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
	public String createdAt;
	private URL parsedURL;

	public DomdigRequest(int id,  String type, String method, String url, String headers, String data, String trigger, String createdAt) {
		super();
		this.id = id;
		this.type = type;
		this.method = method;
		this.url = url;
		this.headers = headers;
		this.data = data;
		this.trigger = trigger.equals("") ? null : trigger;
		this.createdAt = createdAt;
		try {
			this.parsedURL = new URL(this.url);
		} catch (MalformedURLException e1) {
			this.parsedURL = null;
		}
		
		try {
			JSONObject json = new JSONObject(trigger);
			this.triggerElement = json.getString("element");
			this.triggerEvent = json.getString("event");
		} catch(Exception e) {
			this.trigger = null;
		}
	}
	
	public String getRaw() throws MalformedURLException{
		if(parsedURL == null) {
			throw new MalformedURLException();
		}
		int port = parsedURL.getPort();
		String raw = this.method.toUpperCase() + " " + getRelativeURL() + " HTTP/1.1\r\n";
		raw += "host: " + parsedURL.getHost() + (port != -1 ? ":" + port : "") + "\r\n";
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

	public String getHost(){
		if(parsedURL == null) {
			return "";
		}

		return parsedURL.getHost();
	}

	public String getRelativeURL(){
		if(parsedURL == null) {
			return url;
		}
		String path = parsedURL.getFile();
		return path.equals("") ? "/" : path;
	}

	public String getProtocol(){
		if(parsedURL == null) {
			return url;
		}
		return parsedURL.getProtocol();
	}

	public String getTime() {
		SimpleDateFormat inFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat outFmt = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy");
		try {
			return outFmt.format(inFmt.parse(createdAt));
		} catch (ParseException e) {
			return null;
		}
	}
}
