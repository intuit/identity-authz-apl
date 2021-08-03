rule: Check if user can view the file
description: Check if user can view the file
salience: 1
when:
  - viewFileIds = getAllViewIds(sub["documentId"])
  - folderId = getFolderId(sub["documentId"])
  - viewFolderIds = getAllViewFolderIds($folderId)
  - action["view"] == "yes"
  - containsAnyIgnoreCase($viewFileIds, sub["userId"]) == true || containsAnyIgnoreCase($viewFolderIds, sub["userId"]) == true
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