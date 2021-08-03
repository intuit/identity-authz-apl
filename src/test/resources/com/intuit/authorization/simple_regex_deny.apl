rule: A simple policy file with deny
description: A simple policy file with deny
salience: 1
when:
  - res["ownerId"] == sub["id"]
  - matchesAnyIgnoreCase({"a*"}, sub["role"])
then:
  - decision=deny

---

rule: Default Rule
description: default rule execution
salience: 0
when:
then:
  - decision=permit
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")
