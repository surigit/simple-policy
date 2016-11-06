package com.mbr.platform.policy.factory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.management.relation.RoleNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mbr.platform.policy.ex.PlatformPolicyException;
import com.mbr.platform.policy.intf.PolicyDatasource;
import com.mbr.platform.policy.intf.PolicySentry;
import com.mbr.platform.policy.intf.impl.PlatformPolicy;

public final class PolicyFactory {

	private static Log log = LogFactory.getLog(PolicyFactory.class.getName());
	private static boolean DEBUG = false;
	private static ConcurrentMap<String, PolicySentry> groupPolicyMap = null;
	private static PolicySentry singlePolicy = null;
	
	static {
		DEBUG = log.isDebugEnabled();
	}
	
	/**
	 * Assumes no Grouping and loads the specific roles based on the underlying datasource
	 * @param pDatasrc
	 * @return
	 * @throws RoleNotFoundException
	 */
	public static PolicySentry getPlatformPolicy(PolicyDatasource pDatasrc) throws RoleNotFoundException, PlatformPolicyException{
		if(singlePolicy == null) {
			try {
				singlePolicy = new PlatformPolicy(pDatasrc);
			} catch (Exception e) {
				log.error("*POLICY:getPlatformPolicy()#Single - Error when attempting to Create PlatformPOlicy",e);
				throw new PlatformPolicyException("*POLICY:getPlatformPolicy()#Single - Error when attempting to Create PlatformPOlicy",e);
			}
		}
		return singlePolicy;
	}
	

	/**
	 * Maintains policy against a group Name
	 * @param groupName
	 * @param pDatasrc
	 * @return
	 * @throws RoleNotFoundException
	 */
	public static PolicySentry getPlatformPolicy(String groupName, PolicyDatasource pDatasrc) throws RoleNotFoundException, PlatformPolicyException{

		if(groupPolicyMap == null){
			// create a new map . Leave the default capacity as is. Chances of beyond 16 is Rare
			groupPolicyMap = new ConcurrentHashMap<>();
		}
		
		// now check if the group exists 
		if(groupPolicyMap.containsKey(groupName)) return groupPolicyMap.get(groupName);
		
		// create the new group here 
		
		try {
			PolicySentry sentry = new PlatformPolicy(pDatasrc);
			groupPolicyMap.put(groupName, sentry);
			return sentry;
		} catch (Exception e) {
			log.error("*POLICY:getPlatformPolicy#Group - Error when attempting to Create PlatformPOlicy for Group["+groupName+"]",e);
			throw new PlatformPolicyException("*POLICY:getPlatformPolicy#Group - Error when attempting to Create PlatformPOlicy for Group["+groupName+"]",e);
		}
	}

}
