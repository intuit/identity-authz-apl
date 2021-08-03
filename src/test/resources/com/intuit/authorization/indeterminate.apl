rule: Taxpro access to Taxpayer's data
description: Taxpro access to Taxpayer's data
salience: 1
when:
  - sub["nsid"]=="50000000"
  - dummyFunction("test")
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