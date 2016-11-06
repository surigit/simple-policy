package com.mbr.platform.policy.factory;

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
import com.mbr.platform.policy.intf.PolicyResult;
import com.mbr.platform.policy.intf.PolicySentry;
import com.mbr.platform.policy.intf.impl.SampleIPRestrictionDatasource;

public class TestPolicyforIPRestrictions {
	private static Log log = LogFactory.getLog(TestPolicyforIPRestrictions.class.getName());
	PolicySentry sentry = null;

	@Before
	public void setUp() throws Exception {
		try {
			sentry = PolicyFactory.getPlatformPolicy(new SampleIPRestrictionDatasource());
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
	public void testGrant_Case1() {

		/**
		 * IP Address : 10.120.1*.66 - Test wildcard on this ip
		 * Allowed Roles : ADMIN
		 * Domain : sa
		 */
		
		String userRole = "ADMIN";
		String domain = "sa"; 
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ipAddr", "10.120.101.66");  
		
		try {
			PolicyResult result = sentry.execute(userRole,domain, roleAttrs);
			Assert.assertSame(true, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}

	@Test
	public void testGrant_Case2() {

		/**
		 * IP Address : 101.1?0.1*.66 - Test wildcard on this ip
		 * Allowed Roles : ADMIN
		 * Domain : sa
		 */
		
		String userRole = "DBA";
		String domain = "dbo"; 
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ipAddr", "101.130.1.66");  
		
		try {
			PolicyResult result = sentry.execute(userRole,domain, roleAttrs);
			Assert.assertSame(true, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}

	
	@Test
	public void testDeny_Case1() {

		/**
		 * IP Address : 10.120.1*.66 - Test wildcard on this ip
		 * Allowed Roles : ADMIN
		 * Domain : sa
		 */
		
		String userRole = "DBA";
		String domain = "sa"; 
		Map<String, String> roleAttrs = new HashMap();
		roleAttrs.put("ipAddr", "10.120.101.66");  
		
		try {
			PolicyResult result = sentry.execute(userRole,domain, roleAttrs);
			Assert.assertSame(false, result.allow());
			Assert.assertFalse(result.reqSecondaryCheck());
			return;
		} catch (RoleNotFoundException e) {
			log.error("RoleNotFoundException",e);
			Assert.assertTrue(false);
		} 
		
	}

}
