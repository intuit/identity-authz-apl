rule: Intuit ProMgr can assign and delete access in 50000000
description: Intuit ProMgr can assign and delete access in 50000000
salience: 1
when:
  - sub["nsId"]=="50000000"
  - res["ownerId"]=="50000000"
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp1")
then:
  - decision=permit

---
rule: Taxpro write access to Taxpayer's data managed by CTG services
description: Taxpro write access to Taxpayer's data managed by CTG services
salience: 1
when:
  - sub["nsId"]=="50000000"
  - res["ownerId"]=="50000000"
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp1")
then:
  - decision=permit

---
rule: Taxpro read access to Taxpayer's data managed by CTG services
description: Taxpro read access to Taxpayer's data managed by CTG services
salience: 1
when:
  - sub["nsId"]=="50000000"
  - res["ownerId"]=="50000000"
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp1")
then:
  - decision=permit

---
rule: Taxpro write access to IDP entities
description: Taxpro write access to IDP entities
salience: 1
when:
  - sub["nsId"]=="50000000"
  - res["ownerId"]=="50000000"
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp2")
then:
  - decision=permit

---
rule: Taxpro read access to IDP entities
description: Taxpro read access to IDP entities
salience: 1
when:
  - sub["nsId"]=="50000000"
  - res["ownerId"]=="50000000"
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp3")
then:
  - decision=permit

---
rule: Taxpro can revoke Taxpayer's authorization to Intuit
description: Taxpro can revoke Taxpayer's authorization to Intuit
salience: 1
when:
  - sub["nsId"]=="50000000"
  - res["ownerId"]=="50000000"
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp2")
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