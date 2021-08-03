rule: checkNull
description: Return indeterminate if the resource owner id is missing
salience: 1
when:
  -  res["resourceOwnerId"] == null
then:
  - decision=indeterminate

---

rule: checkNotNull
description: Return deny if the resource owner id is present
salience: 1
when:
  -  res["resourceOwnerId"] != null
then:
  - decision=deny

---
rule: duplicateCheckNotNull
description: Return deny if the resource owner id is present
salience: 1
when:
  -  res["resourceOwnerId"] != null
then:
  - System.out.println("duplicate")
