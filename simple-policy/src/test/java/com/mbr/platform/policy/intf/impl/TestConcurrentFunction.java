package com.mbr.platform.policy.intf.impl;

import java.util.Map;

import com.mbr.platform.policy.intf.FunctionCall;
import com.mbr.platform.policy.intf.FunctionResponse;
import com.mbr.platform.policy.intf.PolicyContext;

public class TestConcurrentFunction implements FunctionCall {

	@Override
	public Object call(PolicyContext plCtx) throws Exception {
		
		
//		System.out.println(plCtx.getPolicySubject());
//		System.out.println(plCtx.getSecondaryAttrs());
//		System.out.println(plCtx.getPolicySubject().getOtherData());
		Map<String,Object> otherData = plCtx.getPolicySubject().getOtherData();
		System.out.println("TestConcurrentFunction - Executed");

		if(plCtx.getPolicySubject().getOtherData()  != null){
			return plCtx.getPolicySubject().getOtherData().get("FUNC");
		}
		
		return "FAILED";
	}
	
}
