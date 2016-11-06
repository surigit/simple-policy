package com.mbr.platform;

import java.util.Arrays;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public final class JsonPolicy {

	@Override
	public String toString() {
		return "JsonPolicy [accessPolicies=" + Arrays.toString(accessPolicies) + "]";
	}

	private final AccessPolicy accessPolicies[];

	@JsonCreator
	public JsonPolicy(@JsonProperty("accessPolicies") AccessPolicy[] accessPolicies) {

		this.accessPolicies = accessPolicies;
		// this.appCodes = appCodes;
	}

	public AccessPolicy[] getAccessPolicies() {
		return accessPolicies;
	}

	public static final class AccessPolicy {
/*		@Override
		public String toString() {
			return "AccessPolicy [api=" + Arrays.toString(api) + "]";
		}*/

		public final Api[] api;
		public final String requiredMFA;
		public final String mfaScope;
		public final String executionType;
		public final Unhandled unhandled;
		public final String allowedRoles[];
		public final AdvancedGrantConfig advancedGrantConfig;
		public final AdvancedDenyConfig advancedDenyConfig;

		@JsonCreator
		public AccessPolicy(@JsonProperty("api") Api[] api, @JsonProperty("requiredMFA") String requiredMFA,
				@JsonProperty("mfaScope") String mfaScope, @JsonProperty("executionType") String executionType,
				@JsonProperty("unhandled") Unhandled unhandled, @JsonProperty("allowedRoles") String[] allowedRoles,
				@JsonProperty("advancedGrantConfig") AdvancedGrantConfig advancedGrantConfig,
				@JsonProperty("advancedDenyConfig") AdvancedDenyConfig advancedDenyConfig) {
			this.api = api;
			this.requiredMFA = requiredMFA;
			this.mfaScope = mfaScope;
			this.executionType = executionType;
			this.unhandled = unhandled;
			this.allowedRoles = allowedRoles;
			this.advancedGrantConfig = advancedGrantConfig;
			this.advancedDenyConfig = advancedDenyConfig;
		}

		@Override
		public String toString() {
			return "AccessPolicy [api=" + Arrays.toString(api) + ", requiredMFA=" + requiredMFA + ", mfaScope="
					+ mfaScope + ", executionType=" + executionType + ", unhandled=" + unhandled + ", allowedRoles="
					+ Arrays.toString(allowedRoles) + ", advancedGrantConfig=" + advancedGrantConfig
					+ ", advancedDenyConfig=" + advancedDenyConfig + "]";
		}
		
		
	}

	public static final class Api {
		public final String url;
		public final String httpMethod;
		public final String httpProtocol;

		@JsonCreator
		public Api(@JsonProperty("url") String url, @JsonProperty("httpMethod") String httpMethod,
				@JsonProperty("httpProtocol") String httpProtocol) {
			this.url = url;
			this.httpMethod = httpMethod;
			this.httpProtocol = httpProtocol;
		}

		@Override
		public String toString() {
			return "Api [url=" + url + ", httpMethod=" + httpMethod + ", httpProtocol=" + httpProtocol + "]";
		}

	}

	public static final class AdvancedDenyConfig {
		public final String rolesDenied;
		public final String violationAction;
		public final String additionalEntryCriterion;
		public final String action_success;
		public final String action_failure;
		public final String action_named;

		@JsonCreator
		public AdvancedDenyConfig(@JsonProperty("rolesDenied") String rolesDenied,
				@JsonProperty("violationAction") String violationAction,
				@JsonProperty("additionalEntryCriterion") String additionalEntryCriterion,
				@JsonProperty("action_success") String action_success,
				@JsonProperty("action_failure") String action_failure,
				@JsonProperty("action_named") String action_named) {
			this.rolesDenied = rolesDenied;
			this.violationAction = violationAction;
			this.additionalEntryCriterion = additionalEntryCriterion;
			this.action_success = action_success;
			this.action_failure = action_failure;
			this.action_named = action_named;
		}
	}

	public static final class AdvancedGrantConfig {
		public final String additionalEntryCriterion;

		@JsonCreator
		public AdvancedGrantConfig(@JsonProperty("additionalEntryCriterion") String additionalEntryCriterion) {
			this.additionalEntryCriterion = additionalEntryCriterion;
		}
	}

	public static final class PolicyViolationAction {
		public final String successCase;
		public final String failCase;
		public final String namedCase;

		public PolicyViolationAction(@JsonProperty("successCase") String successCase,
				@JsonProperty("failCase") String failCase, @JsonProperty("namedCase") String namedCase) {
			this.successCase = successCase;
			this.failCase = failCase;
			this.namedCase = namedCase;
		}
	}

	public static final class PolicyViolationObject {

		public final String type;
		public final String code;
		public final String details;
		public final String location;
		public final String moreinfo;
		public final String successCase;
		public final String failCase;
		public final String namedCase;

		@JsonCreator
		public PolicyViolationObject(@JsonProperty("type") String type, @JsonProperty("code") String code,
				@JsonProperty("details") String details, @JsonProperty("location") String location,
				@JsonProperty("moreinfo") String moreinfo, @JsonProperty("successCase") String successCase,
				@JsonProperty("failCase") String failCase, @JsonProperty("namedCase") String namedCase) {

			this.type = type;
			this.code = code;
			this.details = details;
			this.location = location;
			this.moreinfo = moreinfo;
			this.successCase = successCase;
			this.failCase = failCase;
			this.namedCase = namedCase;
		}

	}

	public static final class Deny {
		public final String attributes;
		public final PolicyViolationObject policyViolationObject;
		public final String[] roles;

		@JsonCreator
		public Deny(@JsonProperty("attributes") String attributes,
				@JsonProperty("policyViolationObject") PolicyViolationObject policyViolationObject,
				@JsonProperty("roles") String[] roles) {
			this.attributes = attributes;
			this.policyViolationObject = policyViolationObject;
			this.roles = roles;
		}
	}

	public static final class Grant {
		public final String attributes;
		public final String updatePrincipal;
		public final String[] roles;

		@JsonCreator
		public Grant(@JsonProperty("attributes") String attributes,
				@JsonProperty("updatePrincipal") String updatePrincipal, @JsonProperty("roles") String[] roles) {
			this.attributes = attributes;
			this.updatePrincipal = updatePrincipal;
			this.roles = roles;
		}
	}

	public static final class Unhandled {
		public final PolicyViolationObject policyViolationObject;

		@JsonCreator
		public Unhandled(@JsonProperty("policyViolationObject") PolicyViolationObject policyViolationObject) {
			this.policyViolationObject = policyViolationObject;

		}
	}

}