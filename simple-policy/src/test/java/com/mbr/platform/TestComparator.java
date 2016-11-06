package com.mbr.platform;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestComparator {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

	
		List<Customer> list = new ArrayList();
		
		list.add(new Customer("Ve","Max"));
		list.add(new Customer("My","Su"));
		list.add(new Customer("Jor","Pike"));
		
		list.sort((a,b) -> {return a.fName.compareTo(b.fName);});

		list.forEach(a -> System.out.println(a));
		
	}
	
	
	class Customer{
		
		String fName = null;
		String LName = null;
		public Customer(String fName, String lName) {
			super();
			this.fName = fName;
			LName = lName;
		}
		public String getfName() {
			return fName;
		}
		public String getLName() {
			return LName;
		}
		@Override
		public String toString() {
			return "Customer [fName=" + fName + ", LName=" + LName + "]";
		}
		
	}

}
