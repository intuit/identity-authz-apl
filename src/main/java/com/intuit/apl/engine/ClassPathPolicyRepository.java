package com.intuit.apl.engine;

import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.intuit.apl.PolicyRepository;

/**
 * Policy Repository represents a repository of policies. PolicyEngine uses repository to fetch
 * policy files. Typically this repository will fetch the policy from remote location.
 * 
 * 
 * @author bdutt
 *
 */
public class ClassPathPolicyRepository extends PolicyRepository{
  
  private static Logger logger = LoggerFactory.getLogger(ClassPathPolicyRepository.class);

  @Override
  public InputStream getPolicyAsInputStream(String fileName) {
    InputStream inStream = null;
    if (fileName.startsWith("classpath:")) {
      fileName = fileName.substring("classpath:".length());
    }
    inStream = this.getClass().getClassLoader().getResourceAsStream(fileName);
    if (null == inStream) {
      logger.warn("Could not load policy: " + fileName);
    }
    return inStream;
  }

}
