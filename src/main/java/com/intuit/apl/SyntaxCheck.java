package com.intuit.apl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.intuit.apl.engine.FileSystemPolicyRepository;
import com.intuit.apl.model.Result;


/**
 * Check Syntax of APL policy file. Checks basic syntax.
 * 
 * @author bdutt
 *
 */
public class SyntaxCheck <
	Subject extends Map<String, String>, 
	Resource extends Map<String, String>, 
	Action extends Map<String, String>, 
	Environment extends Map<String, String>, 
	Obligation extends Map<String, String>>
{
  private static Logger logger = LoggerFactory.getLogger(SyntaxCheck.class);

  public static void main(String[] args) {
	SyntaxCheck syntaxCheck = new SyntaxCheck<Map<String,String>, Map<String,String>, Map<String,String>, Map<String,String>, Map<String,String>>();
    for (String arg : args) {
      String[] ruleFiles = {arg};
      logger.info("parsing: "+arg);
      syntaxCheck.run(ruleFiles);
    }

  }
  
  private void run(String[] ruleFiles) {

      Map<String, String> environment = new HashMap<String, String>();
      Map<String, String> resource = new HashMap<String, String>();
      Map<String, String> subject = new HashMap<String, String>();
      Map<String, String> action = new HashMap<String, String>();

      try {
        PolicyEngine policyEngine = (new PolicyEngineFactory(ruleFiles, new FileSystemPolicyRepository())).createNewEngine();
        List<Obligation> obligationList = new ArrayList<Obligation>();
        String explainOutput = policyEngine.explain(subject, resource, action, environment,
            obligationList, new ArrayList<Result>());
        logger.info("All good!");
      } catch (Exception e) {
        logger.error("Error in file : "+Arrays.deepToString(ruleFiles), e);
      }
  }

}
