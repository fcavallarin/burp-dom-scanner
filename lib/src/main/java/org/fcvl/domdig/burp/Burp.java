package org.fcvl.domdig.burp;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import burp.api.montoya.extension.ExtensionUnloadingHandler;

public class Burp implements BurpExtension{
	private MontoyaApi api;
	private DomdigUI ui;

	@Override
	public void initialize(MontoyaApi api)
	{
		this.api = api;
		this.ui = new DomdigUI(api);
		api.extension().setName("DOM Scanner");
		api.extension().registerUnloadingHandler(new BurpUnloadHandler());
		api.userInterface().registerSuiteTab("DOM Scanner", ui);
	}

	public class BurpUnloadHandler implements ExtensionUnloadingHandler {
		@Override
		public void extensionUnloaded() {
			Burp.this.ui.stopScan();
		}
	}
}
