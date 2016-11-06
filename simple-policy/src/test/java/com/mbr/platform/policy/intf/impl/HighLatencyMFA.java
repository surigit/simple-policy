package com.mbr.platform.policy.intf.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mbr.platform.policy.intf.FunctionCall;
import com.mbr.platform.policy.intf.FunctionResponse;
import com.mbr.platform.policy.intf.PolicyContext;
import com.mbr.platform.policy.intf.PolicySubject;

public class HighLatencyMFA implements FunctionCall {
	private static Log log = LogFactory.getLog(HighLatencyMFA.class.getName());
	private static boolean DEBUG = false;

	static {
		DEBUG = log.isDebugEnabled();
	}

	@Override
	public Object call(PolicyContext plCtx) throws Exception {

		log.info("----------------------------------");
		log.info("Started in Function...");
		
		Thread.sleep(2000);
		
		PolicySubject sub = plCtx.getPolicySubject();
		log.info(" > Primary attrs from Outside ");		
		log.info("   >> "+sub.getPrimaryAttrs());		
		log.info(" > Other data from Outside ");		
		log.info("   >> "+sub.getOtherData());		
		log.info(" > Secondary Attrs from Policy ");		
		log.info("   >> "+plCtx.getSecondaryAttrs());		
		log.info("Finished in Function...");
		log.info("----------------------------------");

		return "SUCCESS";
	}

}
