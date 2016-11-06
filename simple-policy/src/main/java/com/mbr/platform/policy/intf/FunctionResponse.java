package com.mbr.platform.policy.intf;

public interface FunctionResponse {

	public boolean passed();
	
	public boolean hasErrors();
	
	public String getErrorDesc();
	
	public Object getData();
}
