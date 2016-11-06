package com.mbr.platform.policy.intf;

import java.util.Map;

import javax.management.relation.RoleNotFoundException;

public interface PolicySentry {

	@Deprecated
	public PolicyResult execute(String userRole, String attrDomain, Map<String,String> roleAttrs) throws RoleNotFoundException;

	public PolicyResult execute(PolicySubject pSubject) throws RoleNotFoundException;

}
