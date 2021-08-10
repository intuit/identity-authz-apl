# Demo AI capabilities of APL

rule: A rule
description: A simple rule
when:
  - res["r2"].equals("r2")
  - res["r3"].equals("r3")
  - res["r1"].equals("r1")
then:
  - decision=deny

---


rule: A rule
description: A simple rule
when:
  - res["r1"].equals("r1")
then:
  - decision=deny

---


rule: A rule
description: A simple rule
when:
  - res["r3"].equals("r3")
then:
  - decision=deny

---

rule: A rule
description: A simple rule
when:
  - res["r4"].equals("r4")
then:
  - decision=deny

---

rule: A rule
description: A simple rule
when:
  - res["r6"].equals("r6")
then:
  - decision=deny

---

rule: A rule
description: A simple rule
when:
  - res["r7"].equals("r7")
then:
  - decision=deny

---

rule: A rule
description: A simple rule
when:
  - res["r5"].equals("r5")
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
