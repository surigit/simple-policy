package com.mbr.platform.policy.intf.impl;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.mbr.platform.policy.intf.FunctionCall;

public class TestFunctionCall {

	FunctionCall funcObj;

	@Before
	public void setUp() throws Exception {

		String objClass = "com.mbr.platform.policy.intf.impl.MFAAuthentication";
		Class<?> clazz= Class.forName(objClass);
		try {
			funcObj = (FunctionCall)clazz.newInstance();
		} catch (ClassCastException e) {
			System.out.println("In ClassCast");
			e.printStackTrace();
		}catch (Exception e) {
			System.out.println("In General");
			e.printStackTrace();
		}
		
	}

	@Test
	public void test() throws Exception{
		funcObj.call(null);
	}

}
