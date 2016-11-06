package com.mbr.platform;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.management.ObjectName;
import javax.management.relation.Role;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestPolicyFileRead {

	private static Log log = LogFactory.getLog(TestPolicyFileRead.class.getName());

	@Before
	public void setUp() throws Exception {
	}

	//@Test
	public void testJsonPolicyLoad(){
		String filePath = "JfpPolicy_CCP.json";
		try {
			JsonPolicy.AccessPolicy[] acls  = getAccessPolicies(filePath);
			if(acls == null) {
				Assert.assertFalse(true);
				return;
			}
			Assert.assertTrue(true);
		} catch (Exception e) {
			log.fatal("Failed in testJsonPolicyLoad ",e);
			Assert.assertFalse(true);
		}
		
	}
	
	//@Test
	public void testPrepareRoleList(){
		String filePath = "JfpPolicy.json";
		try {
			JsonPolicy.AccessPolicy[] acls  = getAccessPolicies(filePath);
			if(acls == null) {
				Assert.assertFalse(true);
				return;
			}
			ConcurrentMap<String,List<ObjectName>> roleMap = getRoleList(acls);
			if(roleMap == null){
				Assert.assertFalse(true);
				return;
			}
			Set<String> s = roleMap.keySet();
			s.forEach(k -> {
				List<ObjectName> objList = roleMap.get(k);
				
				log.info(">> "+k );
				
				objList.forEach(o -> {
					
					log.info("	>> "+o);
				});
				
			});
			
		} catch (Exception e) {
			log.fatal("Failed in testJsonPolicyLoad ",e);
			Assert.assertFalse(true);
		}
		
	}


	@Test
	public void testASampleRole(){
		
		// get the Roles First 
		String filePath = "JfpPolicy.json";
		try {
			JsonPolicy.AccessPolicy[] acls  = getAccessPolicies(filePath);
			if(acls == null) {
				Assert.assertFalse(true);
				return;
			}
			ConcurrentMap<String,List<ObjectName>> roleMap = getRoleList(acls);
			if(roleMap == null){
				Assert.assertFalse(true);
				return;
			}

			// Prepare a Sample Role. 
			
			Hashtable<String,String> sampleTable = new Hashtable();
			sampleTable.put("url", "/accounts/myaccount");
			sampleTable.put("httpMethod", "GET");

			ObjectName sampObj = new ObjectName("fcom",sampleTable);
			// Try all Three Roles 
			List<ObjectName> rList= null;

			
			// VISITOR
			rList = roleMap.get("VISITOR");
			Assert.assertTrue(isAllowed(sampObj,rList));
			
			rList= roleMap.get("CUSTOMER");
			Assert.assertFalse(isAllowed(sampObj,rList));
			
			rList= roleMap.get("DEMOTED_CUSTOMER");
			Assert.assertFalse(isAllowed(sampObj,rList));
		
		
		} catch (Exception e) {
			log.fatal("Failed in testASampleRole ",e);
			Assert.assertFalse(true);
		}
	}
	
	
	//@Test
	public void test() {

		ObjectMapper mapper = new ObjectMapper();
		JsonPolicy jsonPolicy;

		String filePath = "JfpPolicy_CCP.json";
		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
				BufferedInputStream bis = new BufferedInputStream(is)) {

			jsonPolicy = mapper.readValue(bis, JsonPolicy.class);

			if (jsonPolicy == null) {
				log.fatal("JsonPOlicy Unable to load");
			}

			JsonPolicy.AccessPolicy[] aclArr = jsonPolicy.getAccessPolicies();
			for (JsonPolicy.AccessPolicy acl : aclArr) {
				log.info("--------------");
				log.info("requiredMFA=" + acl.requiredMFA);
				log.info("mfaScope=" + acl.mfaScope);
				JsonPolicy.Api[] apiArr = acl.api;
				if (apiArr != null) {
					log.info("	> API Details ");
					for (JsonPolicy.Api api : apiArr) {
						log.info("	> api: " + api);
					}
					log.info("		> ROLES ");
					String[] roles = acl.allowedRoles;
					for (String role : roles) {
						log.info("		> role: " + role);
					}
				}

			}

		} catch (Exception e) {
			log.fatal("Failed ", e);
		}

	}

	
	/**
	 * 
	 * @param aclArray
	 * @return
	 * @throws Exception
	 */
	private ConcurrentMap<String,List<ObjectName>> getRoleList(JsonPolicy.AccessPolicy[] aclArray) throws Exception{
		
		ConcurrentMap<String,List<ObjectName>> roleMap = new ConcurrentHashMap();
		
		if(aclArray == null) {
			log.fatal("*URLP:20 - Access Policies Array was NULL. Cannot Proceed");
			return null;
		}
		if(aclArray.length==0) {
			log.fatal("*URLP:22 - Access Policies Array is EMPTY. Cannot Proceed"); 
			return null;
		}
		log.debug("*URLP:22 - Total Access Policies are ["+aclArray.length+"]");
		
		for (JsonPolicy.AccessPolicy acl : aclArray) {
			List<ObjectName> objNameList = new ArrayList(); 
			
			JsonPolicy.Api[] apiArr = acl.api;
			if (apiArr != null) {
				log.info("	> API Details ");
				for (JsonPolicy.Api api : apiArr) {
					
					String[] httpMArr = null;
					if(api.httpMethod != null){
						httpMArr =api.httpMethod.split(","); 
					}
					
					
					if(httpMArr != null && httpMArr.length>0){
	
						for(String httpM : httpMArr){
							// Create the ObjectName here 
							//ObjectName objName = new ObjectName("fcom",table);
							ObjectName objName = new ObjectName("fcom",buildAttributes(api.url,httpM,api.httpProtocol,acl.requiredMFA,acl.mfaScope));
							
							// add the Object Name to the List 
							objNameList.add(objName);
							log.info("	> objName: " + objName);
						}
						continue;
					}

					// Create the ObjectName here 
					ObjectName objName = new ObjectName("fcom",buildAttributes(api.url,api.httpMethod,api.httpProtocol,acl.requiredMFA,acl.mfaScope));
					
					// add the Object Name to the List 
					objNameList.add(objName);
					log.info("	> objName: " + objName);
				}

				log.info("		> ROLES ");
				String[] roles = acl.allowedRoles;
				if(roles == null) {
					log.fatal("*URLP:23 - Roles Array is NULL. Cannot Proceed");
					return null;
				}
				if(roles.length==0) {
					log.fatal("*URLP:24 - Roles Array is EMPTY. Cannot Proceed"); 
					return null;
				}
				
				for (String role : roles) {
					log.info(">>> ["+role+"]");
					// check if the role already exists
					if(roleMap.containsKey(role)){
						log.info("ROLE alredy exists ["+roleMap.containsKey(role)+"]");
						(roleMap.get(role)).addAll(objNameList);
					}else{
						log.info("		> Adding role to Map: " + role);
						roleMap.put(role, objNameList);
					}
				}

			}
			
			log.info("--------------");
			log.info("requiredMFA=" + acl.requiredMFA);
			log.info("mfaScope=" + acl.mfaScope);

		}
		
		return roleMap;
	}
	
	
	/**
	 * 
	 * @param aclFile
	 * @return
	 * @throws Exception
	 */
	private JsonPolicy.AccessPolicy[] getAccessPolicies(String aclFile) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		JsonPolicy jsonPolicy;
		
		log.info("*URLP:10 - Starting to read JsonPOlicy file ["+aclFile+"]");
		
		if(aclFile == null || "".equals(aclFile.trim()) ) {
			log.fatal("*URLP:11 - Policy filep path is NULL or Empty");
			return null;
		}
		
		if(!aclFile.endsWith("json")) {
			log.fatal("*URLP:12 - Policy file path MUST be a json file and end with .json extn");
			return null;
		}

		try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(aclFile);
				BufferedInputStream bis = new BufferedInputStream(is)) {

			jsonPolicy = mapper.readValue(bis, JsonPolicy.class);

			if (jsonPolicy == null) {
				log.fatal("*URLP:13 - JsonPOlicy Unable to load");
				return null;
			}
			log.info("*URLP:14 - Finished Reading JsonPolicy file ["+aclFile+"]");
			return jsonPolicy.getAccessPolicies();
		} catch (Exception e) {
			log.fatal("*URLP:15 - Reading data from JsonPolicy file ["+aclFile+"] encountered Exception", e);
		}
		return null;
	}
	
	
	/**
	 * 
	 * @param url
	 * @param httpM
	 * @param httpPro
	 * @param mfaReq
	 * @param mfaScope
	 */
	private Hashtable buildAttributes(String url, String httpM, String httpPro, String mfaReq, String mfaScope){

		Hashtable<String,String> table = new Hashtable();
		if(url != null){
			table.put("url", url);
		}
		if(httpM != null){
			table.put("httpMethod", httpM.toUpperCase());
		}

		if(httpPro != null){
			table.put("httpProtocol", httpPro);
		}
		if(mfaReq!= null){
			table.put("requiredMFA", mfaReq);
		}
		if(mfaScope != null){
			table.put("mfaScope", mfaScope);
		}
		
		return table;
	}
	
	
	/**
	 * 
	 * @param subject
	 * @param roleList
	 * @return
	 */
	private boolean isAllowed(ObjectName subject, List<ObjectName> roleList){
		
		boolean matched = roleList.parallelStream().anyMatch(p -> p.apply(subject));

		List<ObjectName> matchedObj = roleList.parallelStream().filter(p -> p.apply(subject)).collect(Collectors.toList());

		if(matchedObj!= null){
			matchedObj.forEach(o -> System.out.println("Matched is :"+o.toString()));
		}
		
		return matched;
	}
}
