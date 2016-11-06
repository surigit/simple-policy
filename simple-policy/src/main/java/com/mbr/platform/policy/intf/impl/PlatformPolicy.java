package com.mbr.platform.policy.intf.impl;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.relation.RoleNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mbr.platform.policy.data.PlatformRole;
import com.mbr.platform.policy.ex.PolicyDatasourceException;
import com.mbr.platform.policy.intf.FunctionCall;
import com.mbr.platform.policy.intf.FunctionResponse;
import com.mbr.platform.policy.intf.PolicyContext;
import com.mbr.platform.policy.intf.PolicyDatasource;
import com.mbr.platform.policy.intf.PolicyResult;
import com.mbr.platform.policy.intf.PolicySentry;
import com.mbr.platform.policy.intf.PolicySubject;

public class PlatformPolicy implements PolicySentry {

	private static Log log = LogFactory.getLog(PlatformPolicy.class.getName());
	private static boolean DEBUG = false;
	private Lock loadLock = new ReentrantLock(true);
	private Lock policyLock = new ReentrantLock(true);


	static {
		DEBUG = log.isDebugEnabled();
	}

	private PolicyDatasource polDS = null;
	private ConcurrentMap<String, List<PlatformRole>> policyMap = null;

	public PlatformPolicy(PolicyDatasource polDS) throws Exception {
		super();

		if (polDS == null) {
			log.error("PolicyDatasource is NULL. Datasource Cannot be NULL.");
			throw new Exception("PolicyDatasource is NULL. Datasource Cannot be NULL.");
		}
		this.polDS = polDS;
	}

	
	
	@Override
	public PolicyResult execute(PolicySubject pSubject) throws RoleNotFoundException {
		// TODO Auto-generated method stub
		return checkPolicy(pSubject);
	}


	@Override
	@Deprecated
	public PolicyResult execute(String userRole, String attrDomain, Map<String, String> roleAttrs)
			throws RoleNotFoundException {

		return checkPolicy(new PolicySubjectImpl(userRole, attrDomain, roleAttrs));

	}

	/**
	 * 
	 * @param pSubject
	 * @return
	 * @throws RoleNotFoundException
	 */
	private PolicyResult checkPolicy(PolicySubject pSubject) throws RoleNotFoundException{
		
		/**
		 * INVOKED ONLY ONCE AND ONLY ONCE
		 */
		try {
			init();
		} catch (Exception e) {
			log.error("Error while Loading the Policy - the first time");
			// return deny now
			return denyWhenError("Error while Loading the Policy - the first time");
		}

		// run the check against the policy

		// Check if the role exists
		if (!policyMap.containsKey(pSubject.getUserRole())) {
			throw new RoleNotFoundException("*POLICY- RoleNotFoundException. Role was [" + pSubject.getUserRole() + "]");
		}

		// ** L O C K ***

		// Create a new ObjectName
		PolicyResult endResult = null;
		List<PlatformRole> resultList = null;
		long tid = Thread.currentThread().getId();
		try {
			policyLock.tryLock(1, TimeUnit.SECONDS);
			ObjectName subject = null;
			try {
				subject = new ObjectName(pSubject.getDomain(), new Hashtable<String, String>(pSubject.getPrimaryAttrs()));
			} catch (MalformedObjectNameException e) {
				log.error("*POLICY- Unable to create ObjectName with inbound Params.MalformedObjectNameException:", e);
				return denyWhenError(
						"*POLICY- Unable to create ObjectName with inbound Params.MalformedObjectNameException-"
								+ e.getLocalizedMessage());
			}

			log.debug("TID["+tid+"] PolAttrs="+subject+";PolSub="+pSubject);
			
			resultList = check(subject, pSubject.getUserRole());
		} catch (Exception e) {
			log.error("*POLICY - Error while checking against POlicy Data. Also the tryLock could have been timedout", e);
			// RETURN DENY
			return denyWhenError("*POLICY- Error while checking against POlicy Data. Also the tryLock could have been timedout-" + e.getLocalizedMessage());

		} finally {
			// *** UNLOCK THE LOCK HERE
			policyLock.unlock();
		}

		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// PENDING - TEST FOR THREAD ISSUES ON THE BOTTOM CODE. RUN HIGH CONCURRENCY CHECKS 
		//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		
		
		// CHECK FOR DENY
		if (resultList == null || resultList.isEmpty()) {
			// deny it
			return new PlatformPolicyResult(false, false, false, null, null);

			// TODO - Save to Deny Cache - SimpleCache : Key is Subject, Value
			// is PlatformPOlicyResult

		}
		
		PlatformRole finalResult = null;
		if (resultList.size() > 1) {
			// whoa - multiple matched here
			log.warn("*POLICY - Multiple ObjectNames matched the subject. Attempting to narrow the match using Hash TID ["+tid+"]");
				int index = resultList.indexOf(new PlatformRole("Search",pSubject.getDomain(), new Hashtable<String, String>(pSubject.getPrimaryAttrs()),null));
				log.info(">> INDEX ="+index);;
				if (index >=0){
					finalResult = resultList.get(index);
				}
		}else{
			finalResult = resultList.get(0);
		}

		log.info("FinalResult="+finalResult);

		//Case NO Secondary Attributes EXIST. Just return 
		if (finalResult.getSecondaryPolicyAttrs() == null) {
			return new PlatformPolicyResult(true, false, false, null, null);
		}
		
		//Case When NO Function call Exists - but Only Secondary Attributes exist 
		if (finalResult.getSecondaryPolicyAttrs() != null && finalResult.getSecondaryPolicyAttrs() == null) {
			// No function Call here 
			return new PlatformPolicyResult(true, false, true, finalResult.getSecondaryPolicyAttrs(), null,null);
		}
		
		
//		if (finalResult.getSecondaryPolicyAttrs() != null) {
//
//			if (finalResult.getFunctionCall() != null) {

				//Prepare the POlicy Context Here
				PolicyContext pCtx = new PolicyContextImpl(pSubject, finalResult.getSecondaryPolicyAttrs());
				log.debug("*POLICY - Invoking Function");
				// run the executor here 
				ExecutorService service = Executors.newFixedThreadPool(1);	
				Future<CallResult> funFuture = service.submit(new FunctionCallable(finalResult.getFunctionCall(), pCtx));
				service.shutdown();
				
				// wait for 1 sec only
				CallResult cResult = null;

				try {
					cResult = funFuture.get(1, TimeUnit.SECONDS);
				} catch (InterruptedException e1) {
					log.error("FuncCallBack: InterruptedException:",e1);
					FunctionResponse fResp = new FunctionResponseImpl(false, true,"FuncCallBack: InterruptedException:"+e1.getLocalizedMessage() , null);
					return new PlatformPolicyResult(false, true, true, finalResult.getSecondaryPolicyAttrs(), "FuncCallBack: InterruptedException:"+e1.getLocalizedMessage(),fResp);
				} catch (ExecutionException e1) {
					log.error("FuncCallBack: ExecutionException:",e1);
					FunctionResponse fResp = new FunctionResponseImpl(false, true,"FuncCallBack: ExecutionException:"+e1.getLocalizedMessage() , null);
					return new PlatformPolicyResult(false, true, true, finalResult.getSecondaryPolicyAttrs(), "FuncCallBack: ExecutionException:"+e1.getLocalizedMessage(),fResp);
				} catch (TimeoutException e1) {
					log.error("FuncCallBack: TimeoutException:",e1);
					FunctionResponse fResp = new FunctionResponseImpl(false, true,"FuncCallBack: TimeoutException:"+e1.getLocalizedMessage() , null);
					return new PlatformPolicyResult(false, true, true, finalResult.getSecondaryPolicyAttrs(), "FuncCallBack: TimeoutException:"+e1.getLocalizedMessage(),fResp);
				}finally{
					service = null;
				}

				if(cResult== null||cResult.getStatus() <0){
					log.error("FuncCallBack: Function Exceuted - but Response was NULL:");
					FunctionResponse fResp = new FunctionResponseImpl(false, true,"FuncCallBack: Function Exceuted - but Response was NULL:", null);
					return new PlatformPolicyResult(false, true, true, finalResult.getSecondaryPolicyAttrs(), "FuncCallBack: Function Exceuted - but Response was NULL:",fResp);
				}
				
				FunctionResponse fResp = new FunctionResponseImpl(true, false,null , cResult.getfResp());
				return new PlatformPolicyResult(true, false, true, finalResult.getSecondaryPolicyAttrs(), null,fResp);
				
//			}
//			
//			// No function Call here 
//			return new PlatformPolicyResult(true, false, true, finalResult.getSecondaryPolicyAttrs(), null,null);
//		}
//
//		// TODO - Save to Grant Cache - SimpleCache : Key is Subject, Value is
//		// PlatformPOlicyResult
//		endResult = new PlatformPolicyResult(true, false, false, null, null);
		
	}
	
	
	/**
	 * 
	 * @author sm58496
	 *
	 */
	private class FunctionCallable implements Callable<CallResult>{

		private FunctionCall funcCall;
		private PolicyContext pCtx;
		
		FunctionCallable(FunctionCall fCall, PolicyContext ctx){
			this.funcCall = fCall;
			this.pCtx = ctx;
		}
		
		@Override
		public CallResult call() throws Exception {
		
			Object resp;
			try {
				resp = funcCall.call(pCtx);
				
			} catch (Exception e) {
				log.error("Error while executing function Call:",e);
				return new CallResult(null,-1);
			}
			
			return new CallResult(resp,1);
		}
		
	}
	
	/**
	 * 
	 * @author sm58496
	 *
	 */
	private class CallResult{
		
		private Object fResp;
		private int status =-1;
		public CallResult(Object fResp, int status) {
			super();
			this.fResp = fResp;
			this.status = status;
		}
		public Object getfResp() {
			return fResp;
		}
		public void setfResp(FunctionResponse fResp) {
			this.fResp = fResp;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		
	}
	
	
	private List<PlatformRole> check(ObjectName subject, String userRole) {

		// Now check the req against the Role
		List<PlatformRole> resultList = policyMap.get(userRole).parallelStream().filter(pr -> {
			ObjectName on = null;
			try {
				on = pr.getPrimaryObjectName();
				//log.debug(on + " # "+subject);
			} catch (Exception e) {
				log.error("*POLICY: Error while Cross-Checking policy from PlatformRole Object. Return False");
			}

			if (on == null)
				return false;
			return on.apply(subject);

		}).collect(Collectors.toList());

		return resultList;
	}

	private PolicyResult denyWhenError(String error) {
		PolicyResult pr = new PlatformPolicyResult(false, true, false, null, error);
		return pr;
	}

	private void init() throws Exception {

		if (policyMap != null)
			return;
		long tid = Thread.currentThread().getId();
		log.debug("*POLICY: Policy is NULL. Have to Load tid["+tid+"]");
		try {
			loadLock.tryLock(1, TimeUnit.SECONDS);

			if (policyMap != null){
				log.debug("Returning TID["+tid+"]");
				return; // this will return any second thread to get out
			}

			// load policy here
			try {

				log.debug("*POLICY:Attempting to Load Policy ");
				policyMap = new ConcurrentHashMap<>(polDS.getPolicyMap());
				log.debug("*POLICY:Load Complete");

			} catch (PolicyDatasourceException e) {
				log.error(
						"PolicyDatasourceException:Error while retrieving POlicy Data from PolicyDatasource. PolicyDatasource Class["
								+ polDS.getClass().getName() + "]. Cannot Continue",
						e);
				throw new Exception(
						"PolicyDatasourceException: Error while retrieving POlicy Data from PolicyDatasource. PolicyDatasource Class["
								+ polDS.getClass().getName() + "]. Cannot Continue");
			}
		} catch (Exception e) {
			if (policyMap != null) {
				log.warn(
						"Exception: Possible chances of unable to obtain Load Lock after 2 secs. POlicy was Loaded. PolicyDatasource Class["
								+ polDS.getClass().getName() + "]. Cannot Continue",
						e);
				return;
			}
			log.error("Exception: Possible chances of unable to obtain Load Lock after 2 secs. PolicyDatasource Class["
					+ polDS.getClass().getName() + "]. Cannot Continue", e);
			throw new Exception(
					"Exception: Possible chances of unable to obtain Load Lock after 2 secs. PolicyDatasource Class["
							+ polDS.getClass().getName() + "]. Cannot Continue");
		} finally {
			// release the lock here
			loadLock.unlock();
			log.debug("Finally- Load Lock Released");
		}

	}

}
