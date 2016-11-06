package com.mbr.examples;

import java.util.HashMap;
import java.util.Map;

import javax.management.relation.RoleNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mbr.platform.policy.ex.PlatformPolicyException;
import com.mbr.platform.policy.factory.PolicyFactory;
import com.mbr.platform.policy.intf.FunctionResponse;
import com.mbr.platform.policy.intf.PolicyResult;
import com.mbr.platform.policy.intf.PolicySentry;
import com.mbr.platform.policy.intf.PolicySubject;
import com.mbr.platform.policy.intf.impl.PolicySubjectImpl;

public class APIMAndJFPGroupPolicyCheck {
	private static Log log = LogFactory.getLog(APIMAndJFPGroupPolicyCheck.class.getName());
	PolicySentry sentry = null;
	PolicySentry apimSentry = null;

	
	@Before
	public void setUp() throws Exception {
		try {
			
			// Load Both Policies
			sentry = PolicyFactory.getPlatformPolicy("JFP",new YamlDatasource("jfppolicy.yml"));

			apimSentry = PolicyFactory.getPlatformPolicy("APIM",new YamlDatasource("apim.yml"));
		
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} catch (PlatformPolicyException e) {
			log.error("PlatformPolicyException",e);
			Assert.assertTrue(false);
		}
	}

	@After
	public void tearDown() throws Exception {
		sentry = null;
		apimSentry = null;
	}
	
	
	@Test
	public void testGrant() {
		
		String httpStatusCode = "200";
		//TODO- CHECK FOR HOMEDEPOT ENTRY
		boolean entry = validateEntry();
		
		// ASSUME a hack on the web server and Tomcat is responding with 207  
		if(!entry){
			httpStatusCode = "207";
		}
		
		//TODO- VALIDATE JFP POLICY 
		boolean jfpPass = validateJFP();
		
		//TODO- CHECK EXIT POLICY - 
		// LAST EGRESS FILTER
		boolean goodExit = validateExit(httpStatusCode); 
		
		Assert.assertSame(true, entry);
		Assert.assertSame(true, jfpPass);
		Assert.assertSame(true, goodExit);
		
	}
	
	private boolean validateEntry(){
		String userRole = "ALL";
		String domain = "entry"; 
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("siteid", "PLCR_HOMEDEPOT");  
		roleAttrs.put("clientId", "123456669");  
		roleAttrs.put("secretkey", "NEWBRUNSWICK");  

		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		

		try {
			PolicyResult result = apimSentry.execute(pSubject);
			Assert.assertSame(true, result.allow());
			Assert.assertTrue(result.reqSecondaryCheck());
			
			// Get the Function Result here 
			FunctionResponse resp = result.getFunctionResult();
			Assert.assertSame(true, resp.passed());
			return true;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
		return false;
	}
	
	private boolean validateJFP(){
		String userRole = "VISITOR";
		String domain = "nam"; // This was hardcoded in the Datasource- because JFP has no mechanism to identify by domain.
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/user/123/helloworld"); //   - /user/* - We will supply wildcards for this 
		roleAttrs.put("httpMethod", "PUT"); //   - /user/* - We will supply wildcards for this 

		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		

		try {
			PolicyResult result = sentry.execute(pSubject);
			Assert.assertSame(true, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return true;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
		return false;
	}

	private boolean validateExit(String httpStatusCode){
		String userRole = "ALL";
		String domain = "exit"; 
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("httpCode", httpStatusCode);  

		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		

		try {
			PolicyResult result = apimSentry.execute(pSubject);
			Assert.assertSame(true, result.allow());
			return true;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
		return false;
	}

}
