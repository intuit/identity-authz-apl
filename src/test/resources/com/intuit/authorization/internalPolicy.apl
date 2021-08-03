rule: A simple internal policy that should be excluded from CBT
description: A simple internal policy that should be excluded from CBT
salience: 1
when:
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp2")
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