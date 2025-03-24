rule: Custom Function permit
description: Test APL Custom Function, return permit if resource.isValid == true
salience: 11
when:
  - cf.isValid(resource)
then:
  - decision=permit

---

rule: Custom Function deny
description: Test APL Custom Function, return deny if resource.isValid == false
salience: 11
when:
  - cf.isValid(resource) == false
then:
  - decision=deny
