package: com.intuit.authorization
import: 
  - com.intuit.authorization.common.user

#This was created by Bala on 15/05/2019
#This rule file is for a sample policy of my-product


rule: Allow CustomDetailedReport for my-product Plus #first rule
description: Allow CustomDetailedReport for my-product Plus
salience: 1
when:
  - containsAnyIgnoreCase({"admin", "user", "bookkeeper"}, sub["role"])
  - env["product"] == "my-product"
  - env["sku"] == "PLUS"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=permit

---

rule: Deny CustomDetailedReport for my-product Basic
description: Deny CustomDetailedReport for my-product Basic
salience: 0
when:
  - containsAnyIgnoreCase({"admin", "user", "bookkeeper"}, sub["role"])
  - env["product"] == "my-product"
  - env["sku"] == "BASIC"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for my-product Basic")
    .put("decision", "deny")

---

rule: Half baked rule
description: Not finished yet
salience: 0
when:
  - "false"
  - env["product"] == "my-product"
  - env["sku"] == "ADVANCED"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for my-product Basic")
    .put("decision", "deny")
    
---

rule: Time range rule
description: effective between two ranges
salience: 0
when:
  - (new java.util.Date()).compareTo( (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse("2019-05-15") ) > 0
  - (new java.util.Date()).compareTo( (new java.text.SimpleDateFormat("yyyy-MM-dd")).parse("2019-05-17") ) < 0
  - env["product"] == "my-product"
  - env["sku"] == "ADVANCED"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=deny
  - |-
    newObligation()
    .put("id", "audit")
    .put("rule", "Deny CustomDetailedReport for my-product Basic")
    .put("decision", "deny")
    
