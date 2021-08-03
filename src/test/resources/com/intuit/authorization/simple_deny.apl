rule: A simple policy file with deny
description: A simple policy file with deny
salience: 1
when:
  - res["ownerId"] == sub["id"]
  - a = containsAnyIgnoreCase({"admin", "user", "bookkeeper"}, sub["role"])
  - $a == true
  - b = "test"
  - dummyFunction($b) == true
  - c = "admin"
  - e = "owner"
  - containsAnyIgnoreCase({$e, "admin"}, sub["role"]) == true
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