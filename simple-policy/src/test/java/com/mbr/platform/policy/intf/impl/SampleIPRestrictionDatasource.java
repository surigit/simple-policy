package com.mbr.platform.policy.intf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mbr.platform.policy.data.PlatformRole;
import com.mbr.platform.policy.ex.PolicyDatasourceException;
import com.mbr.platform.policy.intf.PolicyDatasource;

public class SampleIPRestrictionDatasource implements PolicyDatasource {

	
	/**
	 * THIS IS A SAMPLE IP RESTRICTION DATASource 
	 * Format 
	 * 
	 * Role
	 * 	attributes 
	 */
	
	@Override
	public Map<String, List<PlatformRole>> getPolicyMap() throws PolicyDatasourceException {

		
		Map<String, List<PlatformRole>> policyMap = new HashMap();

		String role = "ADMIN";
		Map<String,String> primAttrs_a = new HashMap();
		primAttrs_a.put("ipAddr", "10.120.1*.66");
		//primAttrs_a.put("ipAddr", "10.120.18.242");
		
		PlatformRole role1 = new PlatformRole("ADMIN","sa",primAttrs_a,null);
		
		List<PlatformRole> roleList = new ArrayList();
		roleList.add(role1);
		// 1 role is added 
		policyMap.put(role, roleList);
		
		// add 2nd role 
		role = "DBA";
		primAttrs_a = new HashMap();
		primAttrs_a.put("ipAddr", "101.1?0.1*.66");
		//primAttrs_a.put("ipAddr", "101.120.18.242");
		
		role1 = new PlatformRole("DBA","dbo",primAttrs_a,null);
		
		roleList = new ArrayList();
		roleList.add(role1);
		policyMap.put(role, roleList);
		
		return policyMap;
	}

}
