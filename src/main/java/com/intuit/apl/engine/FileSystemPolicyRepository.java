package com.intuit.apl.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
public class FileSystemPolicyRepository extends PolicyRepository{
  
  private static Logger logger = LoggerFactory.getLogger(FileSystemPolicyRepository.class);

  @Override
  public InputStream getPolicyAsInputStream(String fileName) {
    InputStream inStream = null;
    try {
      inStream = new FileInputStream(fileName);
    } catch (FileNotFoundException e) {
      logger.error("Could not read file : "+fileName, e);
    }
    if (null == inStream) {
      logger.warn("Could not load policy: " + fileName);
    }
    return inStream;
  }

}
