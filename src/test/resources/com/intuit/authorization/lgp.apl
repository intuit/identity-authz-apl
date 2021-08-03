#version:4
rule: Ticket validity, CBT, Exchange CBT
description: Permit if the ticket is valid
salience: 1
when:
  - action["id"] == "access"
  - sub["min_aal"] == null       #  empty check for min AAL
  - sub["authnLevel"] != null
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=permit
  - |-
    newObligation()
    .put("rule", "Allow valid ticket calls")
    .put("decision", "permit")
    .put("permission", action["id"])
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: Ticket validity, CBT, Exchange CBT
description: Deny if the ticket is not valid
salience: 1
when:
  - action["id"] == "access"
  - sub["authnLevel"] == null         #  empty check for authnLevel
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=deny
  - |-
    newObligation()
    .put("rule", "Disallow invalid ticket calls")
    .put("decision", "deny")
    .put("reason", "INVALID_IAM_TICKET")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: AAL Validity
description: Permit if the AAL is valid
salience: 1
when:
  - action["id"] == "access" # Not null and empty check
  - sub["min_aal"] != null          #  non empty check for min AAL
  - sub["authnLevel"] != null       #  non empty check for authnLevel
  - parseInt(sub["authnLevel"]) >= parseInt(sub["min_aal"])
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=permit
  - |-
    newObligation()
    .put("rule", "Allow valid AAL calls")
    .put("decision", "permit")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: AAL Validity
description: Deny if the AAL is invalid
salience: 1
when:
  - action["id"] == "access" # Not null and empty check
  - sub["min_aal"] != null           #  non empty check for min AAL
  - sub["authnLevel"] != null      #  non empty check for authnLevel
  - parseInt(sub["authnLevel"]) < parseInt(sub["min_aal"])
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=deny
  - |-
    newObligation()
    .put("rule", "Do not allow invalid AAL calls")
    .put("decision", "deny")
    .put("reason", "INSUFFICIENT_AUTHENTICATION_LEVEL")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: System User Detection
description: Permit if the system user is valid and min_aal is not set
salience: 1
when:
  - action["id"] == "offline_access"
  - sub["namespaceId"] == "50000001"
  - sub["authnLevel"] != null
  - sub["min_aal"] == null           #   empty check for min AAL
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=permit
  - |-
    newObligation()
    .put("decision", "permit")
    .put("rule", "Allow valid system user")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: System User Detection
description: Permit if the system user is valid and min_aal is satisfied
salience: 1
when:
  - action["id"] == "offline_access"
  - sub["namespaceId"] == "50000001"
  - sub["min_aal"] != null           #  non empty check for min AAL
  - sub["authnLevel"] != null
  - parseInt(sub["authnLevel"]) >= parseInt(sub["min_aal"])
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=permit
  - |-
    newObligation()
    .put("decision", "permit")
    .put("rule", "Allow valid system user")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: System User Detection for Incorrect NamespaceId
description: Deny if the system user is not valid
salience: 1
when:
  - action["id"] == "offline_access"
  - sub["namespaceId"] != "50000001"
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=deny
  - |-
    newObligation()
    .put("decision", "deny")
    .put("rule", "Invalid system user")
    .put("reason", "INVALID_IAM_TICKET")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: System User Detection for Incorrect NamespaceId
description: Deny if the system user is not valid
salience: 1
when:
  - action["id"] == "offline_access"
  - sub["namespaceId"] == "50000001"
  - sub["authnLevel"] == null
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=deny
  - |-
    newObligation()
    .put("decision", "deny")
    .put("rule", "Invalid system user")
    .put("reason", "INVALID_IAM_TICKET")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

rule: AAL Validity
description: Deny if the AAL is invalid
salience: 1
when:
  - action["id"] == "offline_access" # Not null and empty check
  - sub["namespaceId"] == "50000001"
  - sub["min_aal"] != null           #  non empty check for min AAL
  - sub["authnLevel"] != null      #  non empty check for authnLevel
  - parseInt(sub["authnLevel"]) < parseInt(sub["min_aal"])
  - res["id"] == "Intuit.iam.identity.account"
then:
  - decision=deny
  - |-
    newObligation()
    .put("rule", "Do not allow invalid AAL calls")
    .put("decision", "deny")
    .put("reason", "INSUFFICIENT_AUTHENTICATION_LEVEL")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])
---

#rule for exclusion list - permit
rule: Checking RBAC Permission
description: Check if valid RBAC permissions
salience: 1
when:
  - action["id"] != null
  - sub[permissions] != null
  - res["id"] != "Intuit.iam.identity.account"
  - sub["authnLevel"] != null      #  non empty check for authnLevel
  - matchesAnyIgnoreCase({"^(?!Intuit\.|intuit\.)\S+"}, res["id"])
  - containsAnyIgnoreCase(sub[permissions] , res["id"] + "." + action["id"])
then:
  - decision=permit
  - |-
    newObligation()
    .put("decision", "permit")
    .put("rule", "Allow user with valid RBAC permissions")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])

---
#rule for exclusion list - deny
rule: Checking invalid RBAC Permission
description: Check if invalid RBAC permissions
salience: 1
when:
  - action["id"] != null
  - sub[permissions] != null
  - res["id"] != "Intuit.iam.identity.account"
  - matchesAnyIgnoreCase({"^(?!Intuit\.|intuit\.)\S+"}, res["id"])
  - containsAnyIgnoreCase(sub[permissions] , res["id"] + "." + action["id"]) == false
then:
  - decision=deny
  - |-
    newObligation()
    .put("decision", "deny")
    .put("rule", "do not allow user with insufficient RBAC permissions")
    .put("reason", "Insufficient privileges")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])

---
#rule for inclusion list - permit
rule: Checking RBAC Permission
description: Check if valid RBAC permissions
salience: 1
when:
  - action["id"] != null
  - sub[permissions] != null
  - sub["authnLevel"] != null      #  non empty check for authnLevel
  - matchesAnyIgnoreCase({"resource1","resource2"}, res["id"])
  - containsAnyIgnoreCase(sub[permissions] , res["id"] + "." + action["id"])
then:
  - decision=permit
  - |-
    newObligation()
    .put("decision", "permit")
    .put("rule", "Allow user in inclusion list with valid RBAC permissions")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])

---
#rule for inclusion list - deny
rule: Checking RBAC Permission
description: Check if valid RBAC permissions
salience: 1
when:
  - action["id"] != null
  - sub[permissions] != null
  - matchesAnyIgnoreCase({"resource1","resource2"}, res["id"])
  - containsAnyIgnoreCase(sub[permissions] , res["id"] + "." + action["id"]) == false
then:
  - decision=deny
  - |-
    newObligation()
    .put("decision", "deny")
    .put("rule", "Do not allow user in inclusion list with invalid RBAC permissions")
    .put("reason", "Insufficient privileges")
    .put("resourceId", res["id"])
    .put("actionId", action["id"])

