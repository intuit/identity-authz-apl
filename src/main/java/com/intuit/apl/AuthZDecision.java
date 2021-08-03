package com.intuit.apl;

/**
 * The decision from policy engine
 * 
 * @author bdutt
 *
 */
public enum AuthZDecision {
	PERMIT("PERMIT"), DENY("DENY"), NOT_APPLICABLE("NOT_APPLICABLE"), INDETERMINATE("INDETERMINATE");

	private String decision;

	AuthZDecision(String decision) {
		this.decision = decision;
	}

	public static AuthZDecision fromString(String str) {
		for (AuthZDecision val : AuthZDecision.values()) {
			if (val.decision.equalsIgnoreCase(str)) {
				return val;
			}
		}
		return null;
	}
}