rule: res.attr[...] causes PolicyEvaluationException
description: res.attr[...] causes PolicyEvaluationException
salience: 1
when:
  - sub["idp.fake"] == sub["id"]
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
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")