package com.mbr.examples;

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

import com.mbr.platform.policy.ex.PlatformPolicyException;
import com.mbr.platform.policy.factory.PolicyFactory;
import com.mbr.platform.policy.intf.PolicyResult;
import com.mbr.platform.policy.intf.PolicySentry;
import com.mbr.platform.policy.intf.PolicySubject;
import com.mbr.platform.policy.intf.impl.PolicySubjectImpl;

public class IPAddressPolicyCheck {
	private static Log log = LogFactory.getLog(IPAddressPolicyCheck.class.getName());
	PolicySentry sentry = null;

	
	@Before
	public void setUp() throws Exception {
		try {
			sentry = PolicyFactory.getPlatformPolicy(new YamlDatasource("ippolicy.yml"));
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
		 *  ip: 10.20.30.40
		 * Allowed Roles : ADMIN 
		 */
		
		String userRole = "ADMIN";
		String domain = "nam";
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ip", "10.20.30.40");  
		PolicySubject subject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		
		try {
			PolicyResult result = sentry.execute(subject);
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

		String userRole = "READ_WRITE";
		String domain = "nam";
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ip", "10.20.30.120");  
		PolicySubject subject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		
		try {
			PolicyResult result = sentry.execute(subject);
			Assert.assertSame(true, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}

	
	@Test
	public void testDeny_SingleWithFile() {

		/**
		 *  ip: 10.20.30.40
		 * Allowed Roles : ADMIN 
		 */
		
		String userRole = "READ_WRITE";
		String domain = "nam";
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ip", "10.20.30.40");  
		PolicySubject subject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		
		try {
			PolicyResult result = sentry.execute(subject);
			Assert.assertSame(false, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}

	@Test
	public void testDeny_SingleWithFile_Case1() {

		/**
		 *  ip: 10.20.30.40
		 * Allowed Roles : ADMIN 
		 */
		
		String userRole = "ADMIN";
		String domain = "nam";
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ip", "10.50.30.40");  
		PolicySubject subject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		
		try {
			PolicyResult result = sentry.execute(subject);
			Assert.assertSame(false, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}
	
	@Test
	public void testDeny_SingleWithFile_Case2() {

		/**
		 *  ip: 10.20.30.40
		 * Allowed Roles : ADMIN 
		 */
		
		String userRole = "SA";
		String domain = "nam";
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ip", "10.50.30.40");  
		PolicySubject subject = new PolicySubjectImpl(userRole, domain, roleAttrs);
		
		try {
			PolicyResult result = sentry.execute(subject);
			Assert.assertSame(false, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(true);
		} 
		
	}	
}
