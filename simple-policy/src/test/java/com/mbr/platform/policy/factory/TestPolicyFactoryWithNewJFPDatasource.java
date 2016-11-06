package com.mbr.platform.policy.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.management.relation.RoleNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mbr.examples.YamlDatasource;
import com.mbr.platform.policy.ex.PlatformPolicyException;
import com.mbr.platform.policy.factory.PolicyFactory;
import com.mbr.platform.policy.intf.PolicyResult;
import com.mbr.platform.policy.intf.PolicySentry;
import com.mbr.platform.policy.intf.PolicySubject;
import com.mbr.platform.policy.intf.impl.PolicySubjectImpl;

public class TestPolicyFactoryWithNewJFPDatasource {
	private static Log log = LogFactory.getLog(TestPolicyFactoryWithNewJFPDatasource.class.getName());
	PolicySentry sentry = null;

	
	@Before
	public void setUp() throws Exception {
		try {
			sentry = PolicyFactory.getPlatformPolicy(new YamlDatasource("JfpPolicy.yml"));
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
	}

	@Test
	public void testGrant_SingleWithFile() {

		/**
		 * jfp json entry : {"url":"/user/*"},
		 * Allowed Roles : VISITOR 
		 * Allowed httpMethods : POST, PUT, DELETE, GET
		 */

		
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
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}


	@Test
	public void testGrant_SingleWithFile_Case1() {

		//{url:/accounts/*/add/myacc*/*},
		
		String userRole = "CUSTOMER";
		String domain = "nam"; 
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/accounts/543534534534/add/myacc656565/");  
		roleAttrs.put("httpMethod", "PUT");  
		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		

		try {
			PolicyResult result = sentry.execute(pSubject);
			Assert.assertSame(true, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}

	@Test
	public void testGrant_SingleWithFile_Case2() {

		//{"url":"/user/registration/createprofile/*", "httpMethod":"POST"}
	
		
		String userRole = "DEMOTED_CUSTOMER";
		String domain = "nam"; // This was hardcoded in the Datasource- because JFP has no mechanism to identify by domain.
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/user/registration/createprofile/A"); //    
		roleAttrs.put("httpMethod", "POST"); //    
		
		Map<String,Object> otherData = new HashMap();
		otherData.put("clientId", "11234567890");
		otherData.put("siteId","PLCR_HOMEDEPOT");
		
		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs,otherData);
		
		

		try {
			PolicyResult result = sentry.execute(pSubject);
			// Primary Policy allow 
			Assert.assertSame(true, result.allow());
			// Secondary Check True 
			Assert.assertSame(true, result.reqSecondaryCheck());
			// Get Secondary Policy Attrs
			Map<String,String> seconAttrs = result.getSecondaryPolicyAttrs();
			
			// size of secondary attrs - 3 
			Assert.assertEquals(3, seconAttrs.size());

//			Set<Entry<String, String>> keys = seconAttrs.entrySet();
//			keys.forEach(entry -> {log.info(entry.getKey()+"="+entry.getValue());});
			
			return;
		} catch (RoleNotFoundException e) {
			Assert.assertTrue(false);
		} 
		
	}
	
	@Test
	public void testDeny_SingleWithFile_Case1() {

		/**
		 * jfp json entry : {"url":"/user/*"},
		 * Allowed Roles : VISITOR 
		 * Allowed httpMethods : POST, PUT, DELETE, GET
		 * 
		 * CASE 1: Only URL attribute
		 * 
		 */
		
		String userRole = "VISITOR";
		String domain = "nam"; // This was hardcoded in the Datasource- because JFP has no mechanism to identify by domain.
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/user/123/helloworld"); //   - /user/* - We will supply wildcards for this 
		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		
	
		try {
			PolicyResult result = sentry.execute(pSubject);
			Assert.assertSame(false, result.allow());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}


	@Test
	public void testDeny_SingleWithFile_Case2() {

		/**
		 * jfp json entry : {"url":"/user/*"},
		 * Allowed Roles : VISITOR 
		 * Allowed httpMethods : POST, PUT, DELETE, GET
		 * 
		 * CASE 2: Provide both attributes - but modify a Value on httpMethod - give junk  
		 * 
		 */
		
		String userRole = "VISITOR";
		String domain = "nam"; // This was hardcoded in the Datasource- because JFP has no mechanism to identify by domain.
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/user/123/helloworld"); //   - /user/* - We will supply wildcards for this 
		roleAttrs.put("httpMethod", "PUTS"); //   - PUTS is not a valid HTTP Verb 
		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		

		try {
			PolicyResult result = sentry.execute(pSubject);
			Assert.assertSame(false, result.allow());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}

	
	@Test
	public void testDeny_SingleWithFile_Case3() {

		/**
		 * jfp json entry : {"url":"/user/*"},
		 * Allowed Roles : VISITOR 
		 * Allowed httpMethods : POST, PUT, DELETE, GET
		 * 
		 * CASE 3: Add a new Attribute - like hello=world  
		 * 
		 */
		
		String userRole = "VISITOR";
		String domain = "nam"; // This was hardcoded in the Datasource- because JFP has no mechanism to identify by domain.
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/user/123/helloworld"); //   - /user/* - We will supply wildcards for this 
		roleAttrs.put("httpMethod", "PUT"); //   -  
		roleAttrs.put("hello", "world"); //   -  This attrs DOES not exist in the policy file
		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		

		try {
			PolicyResult result = sentry.execute(pSubject);
			Assert.assertSame(false, result.allow());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}


	@Test
	public void testDeny_SingleWithFile_Case4() {

		/**
		 * jfp json entry : {"url":"/user/*"},
		 * Allowed Roles : VISITOR 
		 * Allowed httpMethods : POST, PUT, DELETE, GET
		 * 
		 * CASE 4: Provide a role which does not exist in the policy file   
		 * 
		 */
		
		String userRole = "ADMIN"; // Provide a Role which does not exists 
		String domain = "nam"; // This was hardcoded in the Datasource- because JFP has no mechanism to identify by domain.
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/user/123/helloworld"); //   - /user/* - We will supply wildcards for this 
		roleAttrs.put("httpMethod", "PUT"); //   -  
		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		
	
		try {
			PolicyResult result = sentry.execute(pSubject);
			Assert.assertTrue(false); // Control should NEVER come here
			
		} catch (RoleNotFoundException e) {
			//log.error("RoleNotFoundException",e);
			Assert.assertTrue(true);;
		} 
		
	}

	@Test
	public void testDeny_SingleWithFile_Case5() {

		//{"url":"/user/registration/createprofile/B", "httpMethod":"POST"}
	
		
		String userRole = "DEMOTED_CUSTOMER";
		String domain = "nam"; // This was hardcoded in the Datasource- because JFP has no mechanism to identify by domain.
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("url", "/user/registration/createprofile/B"); //    
		roleAttrs.put("httpMethod", "GET"); //    
		
		Map<String,Object> otherData = new HashMap();
		otherData.put("clientId", "11234567890");
		otherData.put("siteId","PLCR_HOMEDEPOT");
		
		PolicySubject pSubject = new PolicySubjectImpl(userRole, domain, roleAttrs,otherData);

		try {
			PolicyResult result = sentry.execute(pSubject);
			// Primary Policy allow 
			Assert.assertSame(false, result.allow());
			// Secondary Check True 
			Assert.assertSame(true, result.reqSecondaryCheck());
			// Get Secondary Policy Attrs
			Map<String,String> seconAttrs = result.getSecondaryPolicyAttrs();
			
			//Errors should be there 
			Assert.assertSame(true, result.hasErrors());
			
			//Errors desc should be there  
			Assert.assertNotNull(result.errorDesc());;
			
			return;
		} catch (RoleNotFoundException e) {
			Assert.assertTrue(false);
		} 
		
	}

	
}
