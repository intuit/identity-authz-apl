package com.intuit.apl;

import com.intuit.apl.engine.APLEngine;
import com.intuit.apl.engine.APLInterpreter;
import com.intuit.apl.engine.ClassPathPolicyRepository;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * PolicyEngineFactory is used to create policy engines.
 * 
 * @author bdutt
 */
public class PolicyEngineFactory {

  private static Logger logger = LoggerFactory.getLogger(PolicyEngineFactory.class);

  private String[] fileNames;

  private APLInterpreter ruleEngine = null;

  private InputStream inputStream;

  private boolean debug = false;

  private boolean justParsingAndExecutionOfEngine = false;

  private PolicyRepository policyRepository = new ClassPathPolicyRepository();

  public PolicyEngineFactory(String[] fileNames) {
    this.fileNames = fileNames;
  }

  public PolicyEngineFactory(String[] fileNames, InputStream inputStream) {
    this(fileNames);
    this.inputStream = inputStream;
  }

  public PolicyEngineFactory(String[] fileNames, PolicyRepository policyRepository) {
    this(fileNames);
    this.policyRepository = policyRepository;
  }

  /**
   * Constructor.
   * 
   * @param fileNames policy file names
   * @param debug whether to generate debug info
   * @param justParsingAndExecutionOfEngine just parse and execute
   */
  public PolicyEngineFactory(String[] fileNames, boolean debug,
      boolean justParsingAndExecutionOfEngine) {
    this(fileNames);
    this.debug = debug;
    this.justParsingAndExecutionOfEngine = justParsingAndExecutionOfEngine;
  }

  /**
   * Setup the underlying APL interpreter for APLEngines.
   * 
   */
  private synchronized void setUpRulesEngine() {
    ruleEngine = new APLInterpreter(this.fileNames, inputStream, debug,
        justParsingAndExecutionOfEngine, this.policyRepository);
  }

  /**
   * Create a new engine.
   * 
   * @return policy engine
   */
  public PolicyEngine createNewEngine() {
    if (ruleEngine == null) {
      setUpRulesEngine();
    }
    logger.debug("Creating new ALPEngine");
    return new APLEngine(ruleEngine);
  }
  
  /**
   * Create a new engine with given configuration.
   * 
   * @param PolicyEngineConfiguration policyEngineConfiguration configuration
   * @return policy engine
   */
  public PolicyEngine createNewEngine(PolicyEngineConfiguration policyEngineConfiguration) {
    if (ruleEngine == null) {
      setUpRulesEngine();
      if(null != policyEngineConfiguration) {
        Object enableAI = policyEngineConfiguration.get(PolicyEngineConfiguration.ENABLE_AI);
        if(null != enableAI && enableAI instanceof Boolean) {
          if((Boolean)enableAI) {
            ruleEngine.enableAI();
          }
        }
      }
    }
    logger.debug("Creating new ALPEngine");
    return new APLEngine(ruleEngine);
  }
}
