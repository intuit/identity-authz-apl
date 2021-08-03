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
  - request["dynamicVar1"] == "value1"
then:
  - decision=deny

