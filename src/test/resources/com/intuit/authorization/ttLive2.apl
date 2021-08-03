package: com.intuit.authorization
#Policy file for CTG Pro Delegation TY17
rule: CTGProDelegationTY17_v1_2.1
description: Taxpro write access to Taxpayer's data managed by CTG services
salience: 1
when:
  - action["role"] == "role:PG.consumer.tax.ctg.write"
  - sub["nsId"] == "50000003"
  - containsAnyIgnoreCase({"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]", "sfap[tax pro peer review][cg tax pro]","sfap[tax reviewer][ca tax pro]"}, sub["ldapGrps"])
  - res[ownerId] != "50000000"
  - containsAnyIgnoreCase( {"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]"},sub["ldapGrps"])
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[nsId], res[ownerId], "Delegate"), {'Intuit.consumer.tax.prep'})
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[id], sub[nsId], 'Perform'), {'Intuit.consumer.tax.prep'})
then:
  - decision=permit
  - |-
          newObligation()
          .put("decision", "permit")
---
rule: CTGProDelegationTY17_v1_2.2
description: Taxpro read access to Taxpayer's data managed by CTG services
salience: 1
when:
  - action["role"] == "role:PG.consumer.tax.ctg.read"
  - sub["nsId"] == "50000003"
  - containsAnyIgnoreCase( {"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]", "sfap[tax pro peer review][cg tax pro]","sfap[tax reviewer][ca tax pro]"},sub["ldapGrps"])
  - res[ownerId] != "50000000"
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[nsId], res[ownerId], 'Delegate'), {'Intuit.consumer.tax.prep', 'Intuit.consumer.tax.review'})
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[id], sub[nsId], 'Perform'), {'Intuit.consumer.tax.prep', 'Intuit.consumer.tax.review'})
then:
  - decision=permit
  - |-
          newObligation()
          .put("decision", "permit")
---
rule: CTGProDelegationTY17_v1_3.1
description: Taxpro write access to IDP entities
salience: 1
when:
  - action["role"] == "role:PG.consumer.tax.idp.write"
  - sub["nsId"] == "50000003"
  - containsAnyIgnoreCase( {"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]", "sfap[tax pro peer review][cg tax pro]","sfap[tax reviewer][ca tax pro]"},sub["ldapGrps"])
  - res["ownerId"] != "50000000"
  - containsAnyIgnoreCase( {"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]"} , sub["ldapGrps"])
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[nsId], res[ownerId], 'Delegate'), {'Intuit.consumer.tax.prep'})
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[id], sub[nsId], 'Perform'), {'Intuit.consumer.tax.prep'})
  - containsAnyIgnoreCase( {"tax-docs","fin-docs"}, res["idp.metaTags"])
then:
  - decision=permit
  - |-
          newObligation()
          .put("decision", "permit")
---
rule: CTGProDelegationTY17_v1_3.2
description: Taxpro read access to IDP entities
salience: 1
when:
  - action["role"] == "role:PG.consumer.tax.idp.read"
  - sub["nsId"] == "50000003"
  - containsAnyIgnoreCase( {"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]", "sfap[tax pro peer review][cg tax pro]","sfap[tax reviewer][ca tax pro]"},sub["ldapGrps"])
  - res["ownerId"] != "50000000"
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[nsId], res[ownerId], 'Delegate'), {'Intuit.consumer.tax.prep', 'Intuit.consumer.tax.review'})
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[id], sub[nsId], 'Perform'), {'Intuit.consumer.tax.prep', 'Intuit.consumer.tax.review'})
  - containsAnyIgnoreCase( {"tax-docs","fin-docs"} , res["idp.metaTags"])
then:
  - decision=permit
  - |-
          newObligation()
          .put("decision", "permit")
---
rule: CTGProDelegationTY17_v1_3.3
description: Taxpro delete access to IDP entities
salience: 1
when:
  - action["role"] == "role:PG.consumer.tax.idp.delete"
  - sub["nsId"] == "50000003"
  - containsAnyIgnoreCase( {"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]", "sfap[tax pro peer review][cg tax pro]","sfap[tax reviewer][ca tax pro]"},sub["ldapGrps"],)
  - res[ownerId] != "50000000"
  - containsAnyIgnoreCase( {"sfap[tax pro lead][cg tax pro]","sfap[tax admin][ca tax pro]", "sfap[tax pro expert][cg tax pro]","sfap[glance video][cg glance video agent]"},sub["ldapGrps"])
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[nsId], res[ownerId], 'Delegate'), {'Intuit.consumer.tax.prep'})
  - containsAnyIgnoreCase(@identityHelper.delegatedActionsAuthorized(res[ownerId], sub[id], sub[nsId], 'Perform'), {'Intuit.consumer.tax.prep'})
  - containsAnyIgnoreCase( {"fin-docs"},res["idp.metaTags"])
then:
  - decision=permit
  - |-
          newObligation()
          .put("decision", "permit")
---
