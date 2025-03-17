rule: Custom Function permit
description: Allow CustomDetailedReport for QBO Plus
salience: 11
when:
  - cf.isValid(resource)
then:
  - decision=permit

---

rule: Custom Function deny
description: Allow CustomDetailedReport for QBO Plus
salience: 11
when:
  - cf.isValid(resource) == false
then:
  - decision=deny
