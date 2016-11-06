package com.mbr.platform;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.management.ObjectName;
import javax.management.relation.RoleList;

import org.junit.Before;
import org.junit.Test;

public class TestRoleList {

	private enum Role{
		Visitor,Customer,Demoted_Customer,CitiBlue
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws Exception{
//		{"url":"/security/*"},
//		{"url":"/private/auth/profile/deviceIds/*", "httpMethod":"GET"},

		List<ObjectName> objNList = new ArrayList();

		Hashtable<String,String> table = new Hashtable();
		
		table.put("url", "/communications/*");
		table.put("httpMethod", "P*T");
		
		ObjectName objN1 = new ObjectName("api",table);
		
		Hashtable<String,String> table2 = new Hashtable();
		
		table2.put("url", "/auth/biometrics/deviceIds/{123456}/modalities/{CRS}/signatures");
		table2.put("httpMethod", "GET");
		ObjectName  objN2 = new ObjectName("api",table2);
		
		System.out.println(objN1.apply(getNewRole().getRoleValue().get(0)));
		
		objNList.add(objN1);

		table = new Hashtable();
		
		table.put("url", "/private/auth/profile/deviceIds/*");
		table.put("httpMethod", "POST");
		objN1 = new ObjectName("api",table);
		objNList.add(objN2);

		
		// set Role Object now 
		javax.management.relation.Role role = new javax.management.relation.Role("VISITOR", objNList);
		RoleList rl = new RoleList();
		rl.add(role);
		
		// Now search if the Role has a particular role 
		
		javax.management.relation.Role myRole = getNewRole();

		System.out.println(myRole.getRoleValue().get(0));
		System.out.println(role.getRoleValue());
		System.out.println(role.getRoleValue().contains(myRole.getRoleValue().get(0)));
	}

	
	private javax.management.relation.Role getNewRole() throws Exception {
		List<ObjectName> objNList = new ArrayList();

		Hashtable<String,String> table = new Hashtable();
		
		table.put("url", "/communications/fusion");
		table.put("httpMethod", "POST");

		ObjectName objN1 = new ObjectName("api",table);
		objNList.add(objN1);

		javax.management.relation.Role role = new javax.management.relation.Role("VISITOR", objNList);
		return role;
	}
}
