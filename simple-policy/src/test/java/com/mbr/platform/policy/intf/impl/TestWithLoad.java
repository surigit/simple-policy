package com.mbr.platform.policy.intf.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestWithLoad {
	private static Log log = LogFactory.getLog(TestWithLoad.class.getName());
	ExecutorService service = Executors.newFixedThreadPool(5);
	List<Future<JobResult>> futures = null;
	List<LoadCallable> loadList = new ArrayList(10);;

	final int TOTALS_CALLS = 5;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		service = null;
		futures = null;
		loadList = null;
	}


	@Test
	public void test_LoadWithInvoke() {
		

		for (int i=0;i<TOTALS_CALLS;i++){
			// add new Loads here 

			// You can send the JobObject - if you want to use that In the Callable
			loadList.add(new LoadCallable(new JobContext("J"+i, null)));
			
		}

		List<Future<JobResult>> res = null;
		try {
			res = service.invokeAll(loadList);
		} catch (InterruptedException e) {
			log.error("InterruptedException",e);
		}

		for(Future<JobResult> fut: res){
			JobResult jR = null;
			try {
				jR = fut.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} 
			log.info(jR);
		}
		
		
		// run parallel for all the tasks 
		// set concurrency level to 16 
		
		// if you came here - then you finished all
		Assert.assertTrue(true);
		
	}

	
	
	/**
	 * Since jUnit will fire all Cases Concurrent - try that concurrency as well
	 */
	//@Test
	public void test_Load() {
		

		for (int i=0;i<TOTALS_CALLS;i++){
			// add new Loads here 

			// You can send the JobObject - if you want to use that In the Callable
			loadList.add(new LoadCallable(new JobContext("J"+i, null)));
			
		}

		
		// run parallel for all the tasks 
		// set concurrency level to 16 
		
		
		loadList.parallelStream().forEach(call -> {
			
			long tid = Thread.currentThread().getId();
			Future<JobResult> res = service.submit(call);
			
			try {
				JobResult jRes = res.get(5, TimeUnit.SECONDS);
				log.info(jRes);
			} catch (InterruptedException e) {
				log.error("Fut["+tid+"]:InterruptedException ",e);
			} catch (ExecutionException e) {
				log.error("Fut["+tid+"]:ExecutionException ",e);
			} catch (TimeoutException e) {
				log.error("Fut["+tid+"]:TimeoutException ",e);
			}
			
		});
		
		// if you came here - then you finished all
		Assert.assertTrue(true);
		
	}

	
	
	/**
	 * 
	 * @author sm58496
	 *
	 */
	private class LoadCallable implements Callable<JobResult>{

		JobContext jCtx = null;
		LoadCallable(JobContext ctx){
			this.jCtx =ctx;
		}
		
		@Override
		public JobResult call() throws Exception {

			long sT = System.currentTimeMillis();
			long tid = Thread.currentThread().getId();
			try {

				// run the Job Here
				runJob(jCtx);
				
			} catch (Exception e) {
				log.error("TID["+tid+"Error in checking policy:",e);
				return new JobResult(jCtx.getJobCode(),e.getLocalizedMessage(),(System.currentTimeMillis() - sT));
			}
			
			log.info("Finished Job for TID ["+tid+"]");			
			return new JobResult(jCtx.getJobCode(),null,(System.currentTimeMillis() - sT));
		}
		
	}
	
	
	/**
	 * 
	 * @param jCtx
	 * @throws Exception
	 */
	private void runJob(JobContext jCtx) throws Exception {
		
		
		// TODO - RUN YOUR STUFF HERE
		
		// sample code 
		if("J2".equals(jCtx.getJobCode())){
			Thread.sleep(100);
		}
		
	}
	
	
	/**
	 * 
	 * @author sm58496
	 *
	 */
	public class JobResult{
		
		String jobCode;
		String errorDesc;
		long latency;
		public JobResult(String jobCode, String errorDesc, long latency) {
			super();
			this.jobCode = jobCode;
			this.errorDesc = errorDesc;
			this.latency = latency;
		}
		public String getJobCode() {
			return jobCode;
		}
		public String getErrorDesc() {
			return errorDesc;
		}
		public long getLatency() {
			return latency;
		}
		@Override
		public String toString() {
			return "JobResult [jobCode=" + jobCode + ", errorDesc=" + errorDesc + ", latency=" + latency + "]";
		}
		
		
	}
	
	/**
	 * 
	 * @author sm58496
	 *
	 */
	private class JobContext{
		
		String jobCode;
		Object jobObject;
		public JobContext(String jobCode, Object jobObject) {
			super();
			this.jobCode = jobCode;
			this.jobObject = jobObject;
		}
		public String getJobCode() {
			return jobCode;
		}
		public Object getJobObject() {
			return jobObject;
		}
		@Override
		public String toString() {
			return "JobContext [jobCode=" + jobCode + ", jobObject=" + jobObject + "]";
		}

		
	}
}


