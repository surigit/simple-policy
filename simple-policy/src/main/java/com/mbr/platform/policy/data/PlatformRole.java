package com.mbr.platform.policy.data;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mbr.platform.policy.intf.FunctionCall;

public class PlatformRole {

	private static Log log = LogFactory.getLog(PlatformRole.class.getName());
	private String userRole;
	private String attrDomain;
	private Map<String,String> primaryPolicyAttrs;
	private Map<String,String> secondaryPolicyAttrs;
	private String functionClass;
	private FunctionCall functionCall;

	public PlatformRole(String userRole, String attrDomain, Map<String, String> primaryPolicyAttrs,
			Map<String, String> secondaryPolicyAttrs) {
		super();
		this.userRole = userRole;
		this.attrDomain = attrDomain;
		this.primaryPolicyAttrs = new ConcurrentHashMap(primaryPolicyAttrs);
		if(secondaryPolicyAttrs != null)this.secondaryPolicyAttrs = new ConcurrentHashMap(secondaryPolicyAttrs);
	}

	public PlatformRole(String userRole, String attrDomain, Map<String, String> primaryPolicyAttrs,
			Map<String, String> secondaryPolicyAttrs, String functionClass) {
		super();
		this.userRole = userRole;
		this.attrDomain = attrDomain;
		this.primaryPolicyAttrs = new ConcurrentHashMap(primaryPolicyAttrs);
		if(secondaryPolicyAttrs != null)this.secondaryPolicyAttrs = new ConcurrentHashMap(secondaryPolicyAttrs);
		if(functionClass != null){
			this.functionClass = functionClass;

			// attempt to create the Function Call Object here 
				try {
					Class<?> clazz = Class.forName(this.functionClass);
					functionCall = (FunctionCall)clazz.newInstance();
				} catch (ClassNotFoundException e) {
					log.error("ClassNotFoundException:Class["+functionClass+"]. Cannot Continue.",e);
					throw new RuntimeException("ClassNotFoundException:Class["+functionClass+"]. Cannot Continue.",e);
				} catch (InstantiationException e) {
					log.error("InstantiationException["+functionClass+"]. Cannot Continue.",e);
					throw new RuntimeException("InstantiationException:Class["+functionClass+"]. Cannot Continue.",e);
				} catch (IllegalAccessException e) {
					log.error("IllegalAccessException["+functionClass+"]. Cannot Continue.",e);
					throw new RuntimeException("IllegalAccessException:Class["+functionClass+"]. Cannot Continue.",e);
				} catch (ClassCastException e) {
					log.error("ClassCastException["+functionClass+"]. Cannot Continue.",e);
					throw new RuntimeException("ClassCastException:Class["+functionClass+"]. Cannot Continue.",e);
				} catch (Exception e) {
					log.error("Exception["+functionClass+"]. Cannot Continue.",e);
					throw new RuntimeException("Exception:Class["+functionClass+"]. Cannot Continue.",e);
				}
			
		}
		
		
	}

	
	public String getUserRole() {
		return userRole;
	}


	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ObjectName getPrimaryObjectName() throws Exception{
		if (null == primaryPolicyAttrs) return null;
		ObjectName primObjName;
		try {
			primObjName = new ObjectName(this.attrDomain,new Hashtable<String,String>(this.primaryPolicyAttrs));
		} catch (MalformedObjectNameException e) {
			log.error("PlatformRole:getPrimaryObjectNames encountered - MalformedObjectNameException",e);
			throw new Exception("PlatformRole:getPrimaryObjectNames encountered - MalformedObjectNameException",e);
		}
		return primObjName;
	}

	@Override
	public String toString() {
		return "PlatformRole [userRole=" + userRole + ", attrDomain=" + attrDomain + ", primaryPolicyAttrs="
				+ primaryPolicyAttrs + ", secondaryPolicyAttrs=" + secondaryPolicyAttrs + "]";
	}


	public Map<String, String> getSecondaryPolicyAttrs() {
		// always return immutable.
		if(secondaryPolicyAttrs!= null) return new ConcurrentHashMap(secondaryPolicyAttrs);
		return null;
	}
	
	public FunctionCall getFunctionCall(){
		return functionCall;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrDomain == null) ? 0 : attrDomain.hashCode());
		result = prime * result + ((primaryPolicyAttrs == null) ? 0 : primaryPolicyAttrs.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlatformRole other = (PlatformRole) obj;
		if (attrDomain == null) {
			if (other.attrDomain != null)
				return false;
		} else if (!attrDomain.equals(other.attrDomain))
			return false;
		if (primaryPolicyAttrs == null) {
			if (other.primaryPolicyAttrs != null)
				return false;
		} else if (!primaryPolicyAttrs.equals(other.primaryPolicyAttrs))
			return false;
		return true;
	}


	
}
