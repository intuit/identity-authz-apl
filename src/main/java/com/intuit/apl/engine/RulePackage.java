package com.intuit.apl.engine;

/**
 * Represents a package for policy file.
 * 
 * @author bdutt
 *
 */
public class RulePackage {
  private String packageName;
  private String[] imports = new String[0];

  /**
   * Get Package name.
   * 
   * @return the packageName
   */
  public String getPackageName() {
    return packageName;
  }

  /**
   * Set Package name.
   * 
   * @param packageName the packageName to set
   */
  void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  /**
   * Get imports.
   * 
   * @return the imports
   */
  String[] getImports() {
    return imports;
  }

  /**
   * Set imports.
   * 
   * @param imports the imports to set
   */
  void setImports(String[] imports) {
    this.imports = imports;
  }
}
