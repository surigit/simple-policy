package com.mbr.examples;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

public class TestSampleYML {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testYMLSample() {
		InputStream is = is = Thread.currentThread().getContextClassLoader().getResourceAsStream("jfppolicy.yml");
		
		Yaml yml = new Yaml();
		Map<String, Object> map = (Map<String,Object>)yml.load(is);
		
		for (String key: map.keySet()){
			System.out.println(key+"--");
			List<Map<String,Object>> roleList = (List<Map<String,Object>>)map.get(key); 

			// -- at top level 
			for (Map<String,Object> rmap: roleList){
				System.out.println(" >"+rmap.get("type"));
				System.out.println(" >"+rmap.get("dn"));

				// Now get Attrs 
				
				List<Map<String,Object>> attrList = (List<Map<String,Object>>)rmap.get("attrs"); 
				
				for(Map<String,Object> attmap: attrList){
					
					System.out.println("  >pr#"+attmap.get("pr"));
					System.out.println("  >sc#"+attmap.get("sc"));
					System.out.println("  >scf#"+attmap.get("scf"));
					
				}
				
			}
			
			
		}
		
	}

}
