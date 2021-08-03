package: com.intuit.authorization
rule: permit based on app id
description: Permit if app id is in allowed list
salience: 10
when:
  - containsAnyIgnoreCase(environment[action], "household.data.sharing+personalInfo")
then:
  - decision=permit
