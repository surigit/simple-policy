package com.mbr.platform.policy.intf;

import java.util.Map;

public interface PolicySubject {

	public String getUserRole();
	public String getDomain();
	public Map<String, String> getPrimaryAttrs();
	public Map<String, Object> getOtherData();
	
}
