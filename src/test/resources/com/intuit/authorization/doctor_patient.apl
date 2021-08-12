rule: Doctor Patient Access
description: Allow doctor to access patient details
when:
  - containsAnyIgnoreCase({"doctor"}, sub["role"])
  - res["id"] == "PatientRecord"
  - action["id"] == "read" || action["id"] == "update"
  - res["doctorOfThePatient"] == sub["id"]
  - env["skuOfTheProduct"] == "PLUS"
then:
  - decision=permit
