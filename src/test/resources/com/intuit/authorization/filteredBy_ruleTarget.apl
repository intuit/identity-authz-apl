rule: A policy with rule level target
description: A policy with rule level target
salience: 1
when:
  - res["ownerId"] == sub["id"]
then:
  - decision=permit

---

rule: Default Rule
description: default rule execution
salience: 0
when:
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")