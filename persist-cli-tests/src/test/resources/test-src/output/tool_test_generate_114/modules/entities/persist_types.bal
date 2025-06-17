// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

public type CodeSystem record {|
    readonly int codeSystemId;
    string id;
    string url;
    string 'version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] codeSystem;

|};

public type CodeSystemOptionalized record {|
    int codeSystemId?;
    string id?;
    string url?;
    string 'version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] codeSystem?;
|};

public type CodeSystemWithRelations record {|
    *CodeSystemOptionalized;
    ConceptOptionalized[] concepts?;
|};

public type CodeSystemTargetType typedesc<CodeSystemWithRelations>;

public type CodeSystemInsert record {|
    string id;
    string url;
    string 'version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] codeSystem;
|};

public type CodeSystemUpdate record {|
    string id?;
    string url?;
    string 'version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] codeSystem?;
|};

public type Concept record {|
    readonly int conceptId;
    string code;
    byte[] concept;
    int? parentConceptId;
    int codesystemCodeSystemId;

|};

public type ConceptOptionalized record {|
    int conceptId?;
    string code?;
    byte[] concept?;
    int? parentConceptId?;
    int codesystemCodeSystemId?;
|};

public type ConceptWithRelations record {|
    *ConceptOptionalized;
    CodeSystemOptionalized codeSystem?;
    ValueSetComposeIncludeConceptOptionalized[] valuesetcomposeincludeconcept?;
|};

public type ConceptTargetType typedesc<ConceptWithRelations>;

public type ConceptInsert record {|
    string code;
    byte[] concept;
    int? parentConceptId;
    int codesystemCodeSystemId;
|};

public type ConceptUpdate record {|
    string code?;
    byte[] concept?;
    int? parentConceptId?;
    int codesystemCodeSystemId?;
|};

public type ValueSet record {|
    readonly int valueSetId;
    string id;
    string url;
    string 'version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] valueSet;

|};

public type ValueSetOptionalized record {|
    int valueSetId?;
    string id?;
    string url?;
    string 'version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] valueSet?;
|};

public type ValueSetWithRelations record {|
    *ValueSetOptionalized;
    ValueSetComposeIncludeOptionalized[] composes?;
    ValueSetComposeIncludeValueSetOptionalized[] conceptsInValueSetConcepts?;
|};

public type ValueSetTargetType typedesc<ValueSetWithRelations>;

public type ValueSetInsert record {|
    string id;
    string url;
    string 'version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] valueSet;
|};

public type ValueSetUpdate record {|
    string id?;
    string url?;
    string 'version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] valueSet?;
|};

public type ValueSetComposeInclude record {|
    readonly int valueSetComposeIncludeId;
    boolean systemFlag;
    boolean valueSetFlag;
    boolean conceptFlag;
    int valuesetValueSetId;

    int? codeSystemId;
|};

public type ValueSetComposeIncludeOptionalized record {|
    int valueSetComposeIncludeId?;
    boolean systemFlag?;
    boolean valueSetFlag?;
    boolean conceptFlag?;
    int valuesetValueSetId?;
    int? codeSystemId?;
|};

public type ValueSetComposeIncludeWithRelations record {|
    *ValueSetComposeIncludeOptionalized;
    ValueSetOptionalized valueSet?;
    ValueSetComposeIncludeValueSetOptionalized[] valuesetcomposeincludevalueset?;
    ValueSetComposeIncludeConceptOptionalized[] valuesetcomposeincludeconcept?;
|};

public type ValueSetComposeIncludeTargetType typedesc<ValueSetComposeIncludeWithRelations>;

public type ValueSetComposeIncludeInsert record {|
    boolean systemFlag;
    boolean valueSetFlag;
    boolean conceptFlag;
    int valuesetValueSetId;
    int? codeSystemId;
|};

public type ValueSetComposeIncludeUpdate record {|
    boolean systemFlag?;
    boolean valueSetFlag?;
    boolean conceptFlag?;
    int valuesetValueSetId?;
    int? codeSystemId?;
|};

public type ValueSetComposeIncludeValueSet record {|
    readonly int valueSetComposeIncludeValueSetId;
    int valuesetcomposeValueSetComposeIncludeId;
    int valuesetValueSetId;
|};

public type ValueSetComposeIncludeValueSetOptionalized record {|
    int valueSetComposeIncludeValueSetId?;
    int valuesetcomposeValueSetComposeIncludeId?;
    int valuesetValueSetId?;
|};

public type ValueSetComposeIncludeValueSetWithRelations record {|
    *ValueSetComposeIncludeValueSetOptionalized;
    ValueSetComposeIncludeOptionalized valuesetCompose?;
    ValueSetOptionalized valueset?;
|};

public type ValueSetComposeIncludeValueSetTargetType typedesc<ValueSetComposeIncludeValueSetWithRelations>;

public type ValueSetComposeIncludeValueSetInsert record {|
    int valuesetcomposeValueSetComposeIncludeId;
    int valuesetValueSetId;
|};

public type ValueSetComposeIncludeValueSetUpdate record {|
    int valuesetcomposeValueSetComposeIncludeId?;
    int valuesetValueSetId?;
|};

public type ValueSetComposeIncludeConcept record {|
    readonly int valueSetComposeIncludeConceptId;
    int valuesetcomposeValueSetComposeIncludeId;
    int conceptConceptId;
|};

public type ValueSetComposeIncludeConceptOptionalized record {|
    int valueSetComposeIncludeConceptId?;
    int valuesetcomposeValueSetComposeIncludeId?;
    int conceptConceptId?;
|};

public type ValueSetComposeIncludeConceptWithRelations record {|
    *ValueSetComposeIncludeConceptOptionalized;
    ValueSetComposeIncludeOptionalized valuesetCompose?;
    ConceptOptionalized concept?;
|};

public type ValueSetComposeIncludeConceptTargetType typedesc<ValueSetComposeIncludeConceptWithRelations>;

public type ValueSetComposeIncludeConceptInsert record {|
    int valuesetcomposeValueSetComposeIncludeId;
    int conceptConceptId;
|};

public type ValueSetComposeIncludeConceptUpdate record {|
    int valuesetcomposeValueSetComposeIncludeId?;
    int conceptConceptId?;
|};

