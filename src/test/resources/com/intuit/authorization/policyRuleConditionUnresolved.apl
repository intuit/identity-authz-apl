rule: policy target and condition have unresolved attributes
description: policy target and condition have unresolved attributes
salience: 1
when:
  - res["idp.metaTags"] == sub["id"]
  - sub["idp.fake"] == sub["id"]
  - res["idp.bogus"] == sub["id"]
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