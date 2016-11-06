package com.mbr.platform.policy.intf.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.RuntimeErrorException;

import com.mbr.platform.policy.intf.PolicySubject;

public class PolicySubjectImpl implements PolicySubject {
	
	String userRole =null;
	String domain =null;
	Map<String,String> primaryAttrs;
	Map<String,Object> otherData;
	
	public PolicySubjectImpl(String userRole, String domain, Map<String, String> primaryAttrs) {
		super();
		if(userRole == null || userRole.trim().equals("")) throw new RuntimeException("UserRole Cannot be NULL or Empty.");
		this.userRole = userRole;
		if(domain == null || domain.trim().equals("")) throw new RuntimeException("Domain Cannot be NULL or Empty.");
		this.domain = domain;
		if(primaryAttrs == null || primaryAttrs.isEmpty()) throw new RuntimeException("PrimaryAttrs Cannot be NULL or Empty.");
		this.primaryAttrs = new ConcurrentHashMap(primaryAttrs);
	}
	
	
	
	public PolicySubjectImpl(String userRole, String domain, Map<String, String> primaryAttrs,
			Map<String, Object> otherData) {
		super();
		if(userRole == null || userRole.trim().equals("")) throw new RuntimeException("UserRole Cannot be NULL or Empty.");
		this.userRole = userRole;
		if(domain == null || domain.trim().equals("")) throw new RuntimeException("Domain Cannot be NULL or Empty.");
		this.domain = domain;
		if(primaryAttrs == null || primaryAttrs.isEmpty()) throw new RuntimeException("PrimaryAttrs Cannot be NULL or Empty.");
		this.primaryAttrs = new ConcurrentHashMap(primaryAttrs);
		if(otherData != null ){
			this.otherData = new ConcurrentHashMap(otherData);
		}
	}



	@Override
	public String getUserRole() {
		return this.userRole;
	}

	@Override
	public String getDomain() {
		return this.domain;
	}

	@Override
	public Map<String, String> getPrimaryAttrs() {
		return this.primaryAttrs;
	}

	@Override
	public Map<String, Object> getOtherData() {
		return this.otherData;
	}

	@Override
	public String toString() {
		return "PolicySubjectImpl [userRole=" + userRole + ", domain=" + domain + ", primaryAttrs=" + primaryAttrs
				+ ", otherData=" + otherData + "]";
	}

}
