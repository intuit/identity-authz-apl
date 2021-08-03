package: com.intuit.authorization.constants

rule: Allow User to access own data
description: Allow User to access own data
salience: 1
when:
  - sub["id"] != null
  - sub["id"] == res["resOwnerId"]
then:
  - decision=permit

---

rule: Default Rule
description: default rule execution
salience: 0
when:
then:
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")
