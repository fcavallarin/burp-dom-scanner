package org.fcvl.domdig.burp;

import java.util.ArrayList;

abstract public class DomdigEvents {

	public void onError(String message) {
		
	}
	
	public void onStatusChange(String message) {
		
	}
	
	public void onScanCompleted(Boolean error) {
		
	}
	
	public void onNewRequests(ArrayList<DomdigRequest> reqList) {
		
	}
	
	public void onNewVulnerabilities(ArrayList<DomdigVulnerability> vulnList) {
		
	}
}
