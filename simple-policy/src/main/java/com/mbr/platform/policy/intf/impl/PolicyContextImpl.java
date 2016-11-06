package com.mbr.platform.policy.intf.impl;

import java.util.HashMap;
import java.util.Map;

import com.mbr.platform.policy.intf.PolicyContext;
import com.mbr.platform.policy.intf.PolicySubject;

public class PolicyContextImpl implements PolicyContext {

	private PolicySubject pSubj;
	private Map<String, String> secondAttrs;

	public PolicyContextImpl(PolicySubject pSubj, Map<String, String> secondAttrs) {
		super();
		this.pSubj = pSubj;
		this.secondAttrs = new HashMap(secondAttrs);
	}

	@Override
	public PolicySubject getPolicySubject() {
		return this.pSubj;
	}

	@Override
	public Map<String, String> getSecondaryAttrs() {
		return this.secondAttrs;
	}

}
