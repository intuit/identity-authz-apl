package: com.intuit.authorization

rule: "IEPContext_1"
description: "Tax pro access to widgets provided to action us.tax.exp"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "us.tax.exp"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]"})

then:
  - decision=permit
---
rule: "IEPContext_2"
description: "Tax pro access to widgets provided to action us.tax.adm"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "us.tax.adm"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]"})

then:
  - decision=permit
---
rule: "IEPContext_3"
description: "Tax pro access to widgets provided to action ca.tax.exp"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "ca.tax.exp"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"iep[ca.tax.exp]"})

then:
  - decision=permit
---
rule: "IEPContext_4"
description: "Tax pro access to widgets provided to action ca.tax.adm"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "ca.tax.adm"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"iep[ca.tax.adm]"})

then:
  - decision=permit
---
rule: "IEPContext_5"
description: "Tax pro access to widgets provided to action us.bk.exp"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "us.bk.exp"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Bookkeeper Expert][US Bk Pro]"})

then:
  - decision=permit
---
rule: "IEPContext_6"
description: "Tax pro access to widgets provided to action us.bk.adm"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "us.bk.adm"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Bookkeeper Admin][US Bk Pro]"})

then:
  - decision=permit
---
rule: "IEPContext_7"
description: "Tax pro access to widgets provided to action us.tax.rev"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "us.tax.rev"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Peer Review][CG Tax Pro]"})

then:
  - decision=permit
---
rule: "IEPContext_9"
description: "Tax pro access to widgets provided to action us.bk.rev"
salience: 1
when:
  - sub["nsId"] == "50000000"
  - res["ownerId"] == "50000000"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Tax Pro Expert][CG Tax Pro]", "sfap[Tax Pro Lead][CG Tax Pro]", "sfap[Tax Pro Admin][CG Tax Pro]", "sfap[Tax Pro Peer Review][CG Tax Pro]", "iep[ca.tax.exp]", "iep[ca.tax.adm]", "sfap[Bookkeeper Expert][US Bk Pro]", "sfap[Bookkeeper Admin][US Bk Pro]", "sfap[Bookkeeper Reviewer][US Bk Pro]"})
  - action["id"] == "us.bk.rev"
  - sub["assetId"] == "8359824400620181958" || sub["assetId"]=="8503939588691291606" || sub["assetId"] == "5840060409105315285"
  - containsAnyIgnoreCase(sub["ldapGrps"], {"sfap[Bookkeeper Reviewer][US Bk Pro]"})

then:
  - decision=permit
---

rule: Default Rule
description: default rule execution
salience: 0
when:
then:
  - decision=deny
