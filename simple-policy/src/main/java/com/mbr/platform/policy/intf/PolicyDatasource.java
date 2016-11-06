package com.mbr.platform.policy.intf;

import java.util.List;
import java.util.Map;

import com.mbr.platform.policy.data.PlatformRole;
import com.mbr.platform.policy.ex.PolicyDatasourceException;

public interface PolicyDatasource {

	public Map<String,List<PlatformRole>> getPolicyMap() throws PolicyDatasourceException;
}
