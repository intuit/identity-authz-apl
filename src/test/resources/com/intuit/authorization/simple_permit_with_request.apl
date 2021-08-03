rule: A simple policy file with permit
description: A simple policy file with permit
salience: 1
when:
  - res["ownerId"] == sub["id"]
  - a = 3
  # Ternary Expression ( you have to write it this way, not 4 : 5) It treats : as equal to then
  - b = ($a == 3 ? 4:5)
  - $b == 4
  - request["dynamicVar1"] == "value1"
then:
  - decision=permit

