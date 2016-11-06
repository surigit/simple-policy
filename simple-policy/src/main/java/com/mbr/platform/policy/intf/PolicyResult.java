package com.mbr.platform.policy.intf;

import java.util.Map;

public interface PolicyResult {

	public boolean allow();
	public boolean reqSecondaryCheck();
	public Map<String,String> getSecondaryPolicyAttrs();
	public boolean hasErrors();
	public String errorDesc();
	public FunctionResponse getFunctionResult();
}
