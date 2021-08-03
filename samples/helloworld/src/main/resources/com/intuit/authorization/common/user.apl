package: com.intuit.authorization.common

rule: Allow User to access own data
description: Allow User to access own data
salience: 1
when:
  - sub["id"] != null
  - sub["id"] == res["resOwnerId"]
then:
  - decision=permit
  