package: com.intuit.authorization


#This was created by Bala on 15/05/2019
#This rule file is for a sample policy of qbo


rule: Allow User to access own data
description: Allow User to access own data
salience: 1
when:
  - sub["id"] != null
  - sub["id"] == res["resOwnerId"]
then:
  - decision=permit

---
rule: PreConditionRule
description: PreConditionRule
salience: 1
when:
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp2")
  - env["product"] == "QBO"
then:
 - |-
       newObligation()
       .put("id", "audit")
       .put("rule", "Deny CustomDetailedReport for QBO Basic")
       .put("decision", "deny")
---

rule: Allow CustomDetailedReport for QBO Plus #first rule
description: Allow CustomDetailedReport for QBO Plus
salience: 1
when:
  - env["sku"] == "PLUS" || env["sku"] == "ADVANCED"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=permit

---

rule: Deny CustomDetailedReport for QBO Basic
description: Deny CustomDetailedReport for QBO Basic
salience: 3
when:
  - containsAnyIgnoreCase({"temp1", "temp2", "temp3"}, "temp2")
  - env["product"] == "QBO"
  - env["sku"] == "BASIC"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")

---

rule: Half baked rule
description: Not finished yet
salience: 0
when:
  - "false"
  - env["product"] == "QBO"
  - env["sku"] == "ADVANCED"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")

---

rule: Time range rule
description: effective between two ranges
salience: 0
when:
  - (new java.util.Date()).compareTo( (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse("2019-05-15") ) > 0
  - (new java.util.Date()).compareTo( (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse("2019-05-17") ) < 0
  - env["product"] == "QBO"
  - env["sku"] == "ADVANCED"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
    .put("decision", "deny")

---

rule: Time range rule
description: effective between two ranges
salience: 0
when:
  - (new java.util.Date()).compareTo( (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse("2019-05-15") ) > 0
  - (new java.util.Date()).compareTo( (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse("2019-05-17") ) < 0
  - env["product"] == "QBO"
  - env["sku"] == "ADVANCED"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for QBO Basic")
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
