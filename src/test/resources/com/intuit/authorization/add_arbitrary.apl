rule: A simple policy file with permit
description: A simple policy file with permit
salience: 1
when:
  - parseInt(res["b"]) > parseInt(request["a"])
then:
  - decision=permit
  - |-
    newObligation()
    .put("id", "sum")
    .put("result", "3")
    .put("decision", "permit")

---

rule: not of add
description: A simple policy file with permit
salience: 2
when:
  - parseInt(res["b"]) < parseInt(request["a"])
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "sum")
    .put("result", "?")
    .put("decision", "deny")

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
