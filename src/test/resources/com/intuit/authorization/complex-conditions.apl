rule: Allow CustomDetailedReport for QBO Plus
description: Allow CustomDetailedReport for QBO Plus
salience: 3
when:
  - env["product"] == "QBO"
  - env["sku"] == "PLUS"
then:
  - decision=deny

---

rule: Deny CustomDetailedReport for QBO Basic
description: Deny CustomDetailedReport for QBO Basic
salience: 2
when:
  - env["product"] == "QBO"
then:
  - decision=permit

---

rule: temp rule
description: temp rule
salience: 1
when:
  - env["sku"] == "PLUS"
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