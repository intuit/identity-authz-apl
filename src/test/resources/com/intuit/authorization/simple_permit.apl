rule: A simple policy file with permit
description: A simple policy file with permit
salience: 1
when:
  - res["ownerId"] == sub["id"]
  - a = 3
  # Ternary Expression ( you have to write it this way, not 4 : 5) It treats : as equal to then
  - b = ($a == 3 ? 4:5)
  - $b == 4
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