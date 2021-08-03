package com.intuit.apl.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Result from the policy.
 * 
 * @author bdutt
 *
 */
public class Result {
  private List<String> result = new ArrayList<>();

  /**
   * Get results.
   * 
   * @return result
   */
  public List<String> getResult() {
    return result;
  }

  /**
   * Set results.
   * 
   * @param result result to set
   */
  public void setResult(List<String> result) {
    this.result = result;
  }

  /**
   * Add to results.
   * 
   * @param str string to add to result
   */
  public void add(String str) {
    result.add(str);
  }
}
