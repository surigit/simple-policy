package com.mbr.platform.policy.intf.impl;

import java.util.Map;

import com.mbr.platform.policy.intf.FunctionResponse;
import com.mbr.platform.policy.intf.PolicyResult;

public class PlatformPolicyResult implements PolicyResult {

	boolean allow;
	boolean hasErrors;
	boolean reqSecondaryCheck;
	Map<String, String> secondPolicyAttrs;
	String errorDesc;
	FunctionResponse funcResult = null;
	
	public PlatformPolicyResult(boolean allow, boolean hasErrors, boolean reqSecondaryCheck,
			Map<String, String> secondPolicyAttrs, String errorDesc) {
		super();
		this.allow = allow;
		this.hasErrors = hasErrors;
		this.reqSecondaryCheck = reqSecondaryCheck;
		this.secondPolicyAttrs = secondPolicyAttrs;
		this.errorDesc = errorDesc;
	}

	
	public PlatformPolicyResult(boolean allow, boolean hasErrors, boolean reqSecondaryCheck,
			Map<String, String> secondPolicyAttrs, String errorDesc, FunctionResponse funcResult) {
		super();
		this.allow = allow;
		this.hasErrors = hasErrors;
		this.reqSecondaryCheck = reqSecondaryCheck;
		this.secondPolicyAttrs = secondPolicyAttrs;
		this.errorDesc = errorDesc;
		this.funcResult = funcResult;
	}



	@Override
	public boolean allow() {
		// TODO Auto-generated method stub
		return allow;
	}

	@Override
	public boolean reqSecondaryCheck() {
		// TODO Auto-generated method stub
		return reqSecondaryCheck;
	}

	@Override
	public Map<String, String> getSecondaryPolicyAttrs() {
		// TODO Auto-generated method stub
		return secondPolicyAttrs;
	}

	@Override
	public boolean hasErrors() {
		// TODO Auto-generated method stub
		return hasErrors;
	}

	@Override
	public String errorDesc() {
		// TODO Auto-generated method stub
		return errorDesc;
	}

	@Override
	public FunctionResponse getFunctionResult() {
		// TODO Auto-generated method stub
		return this.funcResult;
	}


	@Override
	public String toString() {
		return "PlatformPolicyResult [allow=" + allow + ", hasErrors=" + hasErrors + ", reqSecondaryCheck="
				+ reqSecondaryCheck + ", secondPolicyAttrs=" + secondPolicyAttrs + ", errorDesc=" + errorDesc
				+ ", funcResult=" + funcResult + "]";
	}

	
}
