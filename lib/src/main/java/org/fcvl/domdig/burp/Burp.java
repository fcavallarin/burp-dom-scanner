package org.fcvl.domdig.burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;

public class Burp implements BurpExtension{
	 private MontoyaApi api;

	  @Override
	    public void initialize(MontoyaApi api)
	    {
	        this.api = api;
	        api.extension().setName("DOM Scanner");
	        api.userInterface().registerSuiteTab("DOM Scanner", new DomdigUI(api));
	    }
}
