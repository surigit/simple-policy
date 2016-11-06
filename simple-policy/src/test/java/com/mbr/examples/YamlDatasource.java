package com.mbr.examples;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

import com.mbr.platform.policy.data.PlatformRole;
import com.mbr.platform.policy.ex.PolicyDatasourceException;
import com.mbr.platform.policy.ex.PolicyLoadException;
import com.mbr.platform.policy.intf.PolicyDatasource;

public class YamlDatasource implements PolicyDatasource {

	private static Log log = LogFactory.getLog(YamlDatasource.class.getName());
	private static boolean DEBUG = false;
	private ConcurrentMap<String, List<PlatformRole>> policyMap = null;

	static {
		DEBUG = log.isDebugEnabled();
	}

	private String policyFile = null;
	private InputStream inputStream = null;

	public YamlDatasource() {
		super();
	}

	public YamlDatasource(String policyFile) {
		super();
		this.policyFile = policyFile;
	}

	public YamlDatasource(InputStream inputStream) {
		super();
		this.inputStream = inputStream;
	}

	/**
	 * 
	 * @throws PolicyLoadException
	 */
	private void loadPolicy() throws PolicyLoadException {

		boolean fileMissing = false;
		if (policyFile == null) {
			log.debug("YamlDatasource:init()-> policyFile was NULL. Attempting InputStream");
			fileMissing = true;
		}

		if (fileMissing && inputStream == null) {
			log.error("YamlDatasource:init()-> policyFile was NULL and InputStream also NULL. Cannot continue.");
			throw new PolicyLoadException(
					"YamlDatasource:init()-> policyFile was NULL and InputStream also NULL. Cannot continue.");
		}

		InputStream is;

		if (!fileMissing) {
			is = Thread.currentThread().getContextClassLoader().getResourceAsStream(policyFile);
		} else {
			is = this.inputStream;
		}

		// Prepare Policy Map here
		// parse Yaml here
		Yaml yml = null;
		try {
			ConcurrentMap<String, List<PlatformRole>> rlMap = new ConcurrentHashMap<>();

			yml = new Yaml();
			Map<String, Object> map = (Map<String, Object>) yml.load(is);

			// role level
			for (String key : map.keySet()) {

				List<Map<String, Object>> roleList = (List<Map<String, Object>>) map.get(key);
				// domain level
				for (Map<String, Object> rmap : roleList) {

					String roleType = (String)rmap.get("type");
					log.info("Loading ROLE["+roleType+"]");
					
					List<PlatformRole> rList = new ArrayList();
					rlMap.putIfAbsent(roleType, rList);

					//System.out.println(" >" + rmap.get("type"));
					//System.out.println(" >" + rmap.get("dn"));

					// Now get Attrs

					List<Map<String, Object>> attrList = (List<Map<String, Object>>) rmap.get("attrs");

					// Attrs Level
					for (Map<String, Object> attmap : attrList) {

						String prRaw = (String) attmap.get("pr");
						String scRaw = (String) attmap.get("sc");
						String scfRaw = (String) attmap.get("scf");

						//System.out.println("  >pr#" + attmap.get("pr"));
						//System.out.println("  >sc#" + attmap.get("sc"));
						//System.out.println("  >scf#" + attmap.get("scf"));

						PlatformRole pRole = new PlatformRole((String) rmap.get("type"), (String) rmap.get("dn"),
								processToMap(prRaw), processToMap(scRaw), scfRaw);

						rlMap.get(roleType).add(pRole);

					}

				}

			}
			
			if(!rlMap.isEmpty()) policyMap = new ConcurrentHashMap<>(rlMap);

		} catch (Exception e) {
			log.error("JFPYamlDatasourcee:init()->Error while retrieving PolicyMap", e);
			throw new PolicyLoadException("YamlDatasource:init()->Error while retrieving PolicyMap", e);
		}
	}

	@Override
	public Map<String, List<PlatformRole>> getPolicyMap() throws PolicyDatasourceException {

		if (policyMap != null)
			return policyMap;

		try {
			loadPolicy();
			return policyMap;
		} catch (PolicyLoadException e) {
			log.error("Error while Loading Policy ", e);
			throw new PolicyDatasourceException("PolicyMap is NULL. Something Wrong");
		}
	}

	/**
	 * 
	 * @param rawStr
	 * @return
	 */
	private Map<String, String> processToMap(String rawStr) {

		if(rawStr == null) return null;
		String[] spltString = rawStr.split(",");
		Map<String, String> attMap = new HashMap();
		for (String s : spltString) {

			//System.out.println("f:"+s);
			String[] values = s.split("=");
			attMap.put(values[0], values[1]);

		}

		return attMap;
	}

}
