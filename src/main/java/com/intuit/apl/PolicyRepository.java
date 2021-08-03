package com.intuit.apl;

import java.io.InputStream;

/**
 * Policy Repository represents a repository of policies. PolicyEngine uses repository to fetch
 * policy files. Typically this repository will fetch the policy from remote location.
 * 
 * 
 * @author bdutt
 *
 */
public abstract class PolicyRepository {

  public abstract InputStream getPolicyAsInputStream(String fileName);

}
