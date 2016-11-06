package com.mbr.platform.policy.intf;

import java.util.Map;

public interface PolicyContext {

	public PolicySubject getPolicySubject();
	
	public Map<String, String> getSecondaryAttrs();


}
