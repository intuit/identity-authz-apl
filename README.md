# ABAC Policy Language [![Build Status](https://build.intuit.com/identity/buildStatus/buildIcon?job=AplBuildJob/master)](https://build.intuit.com/identity/job/AplBuildjob/job/master/)

   **_Secure, fast and reliable policies are protecting your resources_**
   
   <img src="apl-logo.png">

Attribute-based access control (ABAC), also known as policy-based access control, defines an access control paradigm whereby access rights are granted to users through the use of policies which combine attributes together. The policies can use any type of attributes (user attributes, resource attributes, object, environment attributes etc.). Read more here - https://en.wikipedia.org/wiki/Attribute-based_access_control

ABAC Policy Language is used by ABAC to author policies. A policy consists of rules, which have "when" conditions and "then" actions. Policies are executed in a bounded time, goaled to reach a decision as quickly as possible in deterministic, fast and reliable way. Further light-weight execution consumes minimal resources.

# Hello World

Can a user (Subject) access a report in a given flavour of product?

Let us use ABAC to decide that.

A simple rule allowing admin, standard user or bookkeeper to access the report in a particular flavour of product could be written like this,

```
rule: CustomDetailedReportQBOPlus #comment ...
description: Allow CustomDetailedReport for QBO Plus
salience: 1
when:
  - containsAnyIgnoreCase({"admin", "user", "bookkeeper"}, sub["role"])
  - env["product"] == "QBO"
  - env["sku"] == "PLUS"
  - res["id"] == "CustomDetailedReport"
then:
  - decision=permit
```

This rule has multiple parts. It has a name and description. Salience lets it run at priority. Two conditions in "when" check whether suk of the product is PLUS and if resource id is "CustomDetailedReport", i.e. the resource on which access is requested.

When both the conditions are true, rule is said to fire. On firing of rule, action statements in rule are executed. Here action is to set a deny decision. Once a decision is reached APL engine aborts further rule evaluation.


This project demonstrates how the clients can use standalone policy engine.

# How to run

```
cd samples/helloworld
```
Build it
```
mvn clean install
```
Run it
```
mvn exec:java -Dexec.mainClass=com.sample.apl.HelloWorld
```

# Features

* Domain Specific Language for policies for ABAC. Conditions are based on attributes, actions can provide decisions and obligations only. No arbitrary logic allowed like network calls, DB access, complex computation.
* Policies take micro-seconds to give answers and consume less than a kb of heap.
* Support for functions, attributes
* Optimized execution, evaluates repeating conditions only once per execution. RETE based algorithm. Stops when a decision is reached.
* Supports salience in rules. This allows higher priority rules to first get a chance to fire.
* Ability to explain an execution. You wrote a policy, want to know how a certain decision was made, how a rule got fired, explain can give insights into execution and good troubleshooting information on rules. 
* Quota for policies. Lengthy and slow policies can hog resources. Quota based system ensures that this is not allowed and execution is bounded.
* Support modularity. Define policies in packages and import them.
* Supports multiple policy files for a single resource decision making. Easy to combine policies as they are rule based. ABAC allows resource hierarchies where policies for parent resource are also included in decision making.
* Rules that always evaluate to true or false are detected and highlighted.
* Inconsistent rules are detected and highlighted.
* Variables can be used in conditions after values are assigned. Variables are accessed by using $ before a variable. 
* 3 kinds of rules - deny, permit and default rules. Deny rules should have salience higher than permit, which should be higher than default. Also, only one default rule expected. Engine warns on violations.

||Drools|APL|
|----|---------|------|
|Type|General purpose, feature rich|Domain specific for ABAC|
|Execution time|~1ms|~20 μs|
|Heap per execution|130 KB|<1KB|
|Jar size|>5MB|45KB (+280KB SPEL, +250KB YAML)|


## Upcoming features

* Compile the policies for even faster execution.


# Details

[Sample rule file](samples/helloworld/src/main/resources/com/intuit/authorization/qbo-rules.apl)

[Another sample rule file](samples/helloworld/src/main/resources/com/intuit/authorization/common/user.apl)

[Source code for main Java class](samples/helloworld/src/main/java/com/intuit/authorization/HelloWorld.java)

## Explain
    APL engine can be asked to explain a policy execution. This explanation provides insights into the policy. Below is a sample explaination.
    
------------------
APL Explain :
------------------

    Inputs:

         Subject:                role -> admin,        
         Resource:               id -> CustomDetailedReport,        
         Action:                 name -> execute,       
         Environment:            product -> QBO, sku -> BASIC,       


    Variables:
       
        Number of varaibles: 16
        
        product
        role
        bookkeeper
        CustomDetailedReport
        admin
        BASIC
        ADVANCED
        2019-05-15
        2019-05-17
        resOwnerId
        yyyy-MM-dd
        id
        sku
        user
        QBO
        PLUS

    Stats:

         Rules:                 4
         Rules Fired:           1
         Conditions:            15
         UniqueConditions:      10
         Actions:               6
         ExecutedActions:       2
         HalfBakedRules:        1
         Parsing(µs):           12924
         Execution(µs):         133
         ConditionExecution(µs):109
         RuleExecution(µs):     23


    Execution:

        
|EXECUTION STEP|CONDITION|OUTPUT|
|----|---------|------|
|0|containsAnyIgnoreCase({"admin", "user", "bookkeeper"}, sub["role"])|T|
|1|env["product"] == "QBO" |T|
|3|res["id"] == "CustomDetailedReport"|T|
|5|env["sku"] == "BASIC"|T|
|2|env["sku"] == "PLUS"|F|
|4|sub["id"] != null|F|


        Rules fired:

                                 Name : Deny CustomDetailedReport for QBO Basic
                                 Description : rule description

        Decision:                DENY


	Rules having always false conditions:

                                 Half baked rule



        Rules having always true conditions :



        Rete Execute Process:

# Build

https://build.intuit.com/identity/job/AplBuildjob/job/master/

# Design of APL

* [ABAC Policy Language (APL) Design](https://wiki.intuit.com/pages/viewpage.action?pageId=493493276)
* [ABAC Policy Language implementation](src/)
