package: com.intuit.authorization
import: 
  - com.intuit.authorization.common.user

#This rule file is for a sample policy of qbo

rule: Deny CustomDetailedReport for QBO Basic
description: Deny CustomDetailedReport for QBO Basic
salience: 2
when:
  - env["product"] == "QBO"
  - env["sku"] == "BASIC"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")

---

rule: Rule with Obligation, remediation, cause and advice
description: rule with Obligation, remediation, cause and advice
salience: 1
when:
  - env["product"] == "QBO"
  - env["sku"] == "PLUS"
  - res["id"] == "CustomDetailedReportWithRemediation"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")
  - |-
    newRemediation()
    .put("remediation", "invalid role")
  - |-
    newAdvice()
    .put("advice", "role=admin")
  - |-
    newCause()
    .put("cause", "role have no permission")
---
rule: Rule with Multiple Obligation, remediation, cause and advice
description: rule with Multiple Obligation, remediation, cause and advice
salience: 1
when:
  - env["product"] == "QBO"
  - env["sku"] == "PLUS"
  - res["id"] == "CustomDetailedReportWithMultipleRemediation"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")
  - |-
    newObligation()
    .put("id", "audit1")
    .put("rule", "Deny CustomDetailedReport for QBO Basic1")
    .put("decision", "deny")
  - |-
    newRemediation()
    .put("remediation", "invalid role")
  - |-
    newRemediation()
    .put("remediation1", "invalid role1")
  - |-
    newAdvice()
    .put("advice", "role=admin")
  - |-
    newAdvice()
    .put("advice1", "role=admin1")
  - |-
    newCause()
    .put("cause", "role have no permission")
  - |-
    newCause()
    .put("cause1", "role have no permission1")