rule: Policy with bean resolver
description: Policy with bean resolver
when:
  - dummyFunction("test")
then:
  - decision=deny

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