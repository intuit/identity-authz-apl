rule: r1
description: r1
salience: 1
when:
  - sub["idp.fake"] == sub["id"]
  - res["idp.metaTags"] == sub["id"]
then:
  - decision=permit

---

rule: r2
description: r2
salience: 1
when:
  - res["idp.metaTags"] == sub["id"]
  - res["ownerId"] == sub["id"]
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