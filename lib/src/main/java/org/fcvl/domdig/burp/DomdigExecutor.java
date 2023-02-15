package org.fcvl.domdig.burp;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class DomdigExecutor {
	private ThreadExecutor thExecutor = null;
	public String nodePath;
	public String domdigPath;
	public String targetUrl;
	public String cookies;
	public String credentials;
	public int timeout;
	public String proxy;
	public String headers;
	public String loginSequence;
	public String payloadsFile;
	public String modes;
	public String excludeRegex;
	public String localStorage;
	public String dbFilePath;
	public boolean checkTemplateInjection;
	public boolean checkStored;
	public boolean singleBrowser;

	public DomdigExecutor(String nodePath, String domdigPath, String targetUrl, String cookies, String credentials, int timeout, String proxy,
			String headers, String loginSequence, String payloadsFile, String modes, String excludeRegex,
			String localStorage, boolean checkTemplateInjection, boolean checkStored, boolean singleBrowser) {
		super();
		this.nodePath = nodePath;
		this.domdigPath = domdigPath;
		this.targetUrl = targetUrl;
		this.cookies = cookies;
		this.credentials = credentials;
		this.timeout = timeout;
		this.proxy = proxy;
		this.headers = headers;
		this.loginSequence = loginSequence;
		this.payloadsFile = payloadsFile;
		this.modes = modes;
		this.excludeRegex = excludeRegex;
		this.localStorage = localStorage;
		this.dbFilePath = Paths.get(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + ".db").toAbsolutePath().toString();
		this.checkTemplateInjection = checkTemplateInjection;
		this.checkStored = checkStored;
		this.singleBrowser = singleBrowser;
		//System.out.println(this.dbFilePath);
	}

	public boolean isRunning() {
		if(thExecutor == null) {
			return false;
		}
		return thExecutor.isRunning();
	}

	public void requestStopScan() {
		if(thExecutor != null) {
			thExecutor.stop();
			thExecutor = null;
		}
	}

	private String stripJson(String json) {
		ArrayList<String> ls = new ArrayList<String>();
		json = json.trim();
		for(String l : json.split("\n")) {
			ls.add(l.trim());
		}
		return String.join("", ls);
	}

	public ArrayList<String> getCommand() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(this.nodePath);
		list.add(this.domdigPath);
		list.add("-l");
		list.add("-m");
		list.add(modes);
		list.add("-d");
		list.add(dbFilePath);
		list.add("-x");
		list.add("" + timeout);
		if(!cookies.equals("[]")) {
			list.add("-c");
			list.add(cookies);
		}
		if(!headers.equals("{}")) {
			list.add("-E");
			list.add(headers);
		}
		if(!localStorage.equals("{}")) {
			list.add("-g");
			list.add(localStorage);
		}
		if(!credentials.equals(":")) {
			list.add("-A");
			list.add(credentials);
		}
		if(!loginSequence.equals("")) {
			list.add("-s");
			list.add(stripJson(loginSequence));
		}
		if(!payloadsFile.equals("")) {
			list.add("-P");
			list.add(stripJson(payloadsFile));
		}
		if(!excludeRegex.equals("")) {
			list.add("-X");
			list.add(stripJson(excludeRegex));
		}
		if(!proxy.equals("")) {
			list.add("-p");
			list.add(proxy);
		}
		if(!checkTemplateInjection) {
			list.add("-T");
		}
		if(!checkStored) {
			list.add("-S");
		}
		if(!singleBrowser) {
			list.add("-B");
		}

		return list;
	}

	public String getCommandAsString() {
		return String.join(" ", getCommand());
	}

	public void runCrawler(DomdigEvents events) {
		System.out.println(getCommandAsString());

		ArrayList<String> list = getCommand();
		list.add("-D"); // dry-run
		list.add(this.targetUrl);
		//System.out.println(getCommandAsString());
		thExecutor = new ThreadExecutor();
		thExecutor.start(list, events, dbFilePath);
	}

	public void runXSSScanner(DomdigEvents events) {
		System.out.println(getCommandAsString());
		ArrayList<String> list = getCommand();
		list.add(this.targetUrl);

		thExecutor = new ThreadExecutor();
		thExecutor.start(list, events, dbFilePath);
	}


}


class ThreadExecutor implements Runnable {
	private Thread th = null;
	Process process = null;
	private boolean exitRequested = false;
	private ArrayList<String> command;
	private DomdigEvents events;
	private String dbFilePath;
	private int lastStatusID = 0;
	private int lastRequestID = 0;
	private int lastVulnerabilityID = 0;
	private Boolean error = false; 

	public void start(ArrayList<String> command, DomdigEvents events, String dbFilePath) {
		this.command = command;
		this.events = events;
		this.dbFilePath = dbFilePath;
		deleteDBFile();
		th = new Thread(this);
		th.start();
	}

	private void handleError(String message, Boolean fatal) {
		error = true;
		events.onError(message);
		if(fatal) {
			events.onScanCompleted(error);
		}
	}
	
	private String getStatus() {
		DomdigDB db = new DomdigDB(dbFilePath);
		return db.getStatus();
	}


	private ArrayList<DomdigRequest> getNewRequests() {
		DomdigDB db = new DomdigDB(dbFilePath);
		ArrayList<DomdigRequest> reqList = db.getRequests(lastRequestID);
		if(!reqList.isEmpty()) {
			lastRequestID = reqList.get(reqList.size() - 1).id;
		}
		return reqList;
	}

	private ArrayList<DomdigVulnerability> getNewVulnerabilities() {
		DomdigDB db = new DomdigDB(dbFilePath);
		ArrayList<DomdigVulnerability> vulnList = db.getVulnerabilities(lastVulnerabilityID);
		if(!vulnList.isEmpty()) {
			lastVulnerabilityID = vulnList.get(vulnList.size() - 1).id;
		}
		return vulnList;
	}


	public void stop() {
		exitRequested = true;
	}

	public boolean isRunning() {
		if(th == null || process == null) {
			return false;
		}
		try {
			process.exitValue();
			return false;
		}catch(IllegalThreadStateException e) {	

		}
		return true;
	}

	private void deleteDBFile() {
		File f = new File(dbFilePath);
		f.delete();
	}

	public String getStderr() {
		ArrayList<String> err = new ArrayList<>();
		BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		String s = null;
		try {
			while ((s = stderr.readLine()) != null) {
				err.add(s);
			}
		} catch (IOException e) {
			return "";
		}
		
		return String.join("\n", err);

	}
	
	@Override
	public void run() {
		ProcessBuilder pb = new ProcessBuilder(command);
		try {
			process = pb.start();
		} catch (IOException e1) {
			handleError("Error starting node", true);
			return;
		}

		while(!exitRequested && isRunning()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			try {
				events.onStatusChange(getStatus());
				events.onNewRequests(getNewRequests());
				events.onNewVulnerabilities(getNewVulnerabilities());
			} catch(Exception e) {
				handleError("Database Error", true);
			}
		}

		if(exitRequested) {
			System.out.println("Exit requested");
			process.destroy();
		}
		try {
			process.waitFor();
		} catch (InterruptedException e) {}
		deleteDBFile();
		
		if(process.exitValue() != 0 && !exitRequested) {
			handleError("exit code not zero: " + getStderr(), true);
		} else {
			events.onScanCompleted(false);	
		}
	}
}
