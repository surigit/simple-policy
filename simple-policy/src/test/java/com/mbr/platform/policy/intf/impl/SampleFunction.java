package com.mbr.platform.policy.intf.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mbr.platform.policy.intf.FunctionCall;
import com.mbr.platform.policy.intf.FunctionResponse;
import com.mbr.platform.policy.intf.PolicyContext;
import com.mbr.platform.policy.intf.PolicySubject;

public class SampleFunction implements FunctionCall {
	private static Log log = LogFactory.getLog(SampleFunction.class.getName());

	/**
	 * NOTE: 
	 * This function is a Singleton. Care should be taken on Class Level Variables.
	 */
	
	@Override
	public Object call(PolicyContext plCtx) throws Exception {
		log.info("----------------------------------");
		log.info("Started in Function...");
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
