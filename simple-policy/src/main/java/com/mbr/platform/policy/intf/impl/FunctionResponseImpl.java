package com.mbr.platform.policy.intf.impl;

import com.mbr.platform.policy.intf.FunctionResponse;

public class FunctionResponseImpl implements FunctionResponse {

	private boolean passed = false;
	private boolean hasErrors = false;
	private String errorDesc = null;
	private Object fData = null;
	
	
	public FunctionResponseImpl(boolean passed, boolean hasErrors, String errorDesc, Object fData) {
		super();
		this.passed = passed;
		this.hasErrors = hasErrors;
		this.errorDesc = errorDesc;
		this.fData = fData;
	}

	@Override
	public boolean passed() {
		// TODO Auto-generated method stub
		return this.passed;
	}

	@Override
	public boolean hasErrors() {
		// TODO Auto-generated method stub
		return this.hasErrors;
	}

	@Override
	public String getErrorDesc() {
		// TODO Auto-generated method stub
		return this.errorDesc;
	}

	@Override
	public Object getData() {
		// TODO Auto-generated method stub
		return this.fData;
	}

	@Override
	public String toString() {
		return "FunctionResponseImpl [passed=" + passed + ", hasErrors=" + hasErrors + ", errorDesc=" + errorDesc
				+ ", fData=" + fData + "]";
	}

}
