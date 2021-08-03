rule: Allow CustomDetailedReport for QBO Plus
description: Allow CustomDetailedReport for QBO Plus
salience: 11
when:
  - env["product"] == "QBO"
then:
  - decision=permit

---

rule: Deny CustomDetailedReport for QBO Basic
description: Deny CustomDetailedReport for QBO Basic
salience: 10
when:
  - "true"
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