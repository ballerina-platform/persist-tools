// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

# Represents the `CodeSystem` entity.
public type CodeSystem record {|
    readonly int codeSystemId;
    string id;
    string url;
    string version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] codeSystem;

|};

# Represents `CodeSystem` with every field optional, for projected reads.
public type CodeSystemOptionalized record {|
    int codeSystemId?;
    string id?;
    string url?;
    string version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] codeSystem?;
|};

# Represents `CodeSystem` with its related entities, for projected reads.
public type CodeSystemWithRelations record {|
    *CodeSystemOptionalized;
    ConceptOptionalized[] concepts?;
|};

# The `CodeSystem` type or a projection of it, for reads.
public type CodeSystemTargetType typedesc<CodeSystemWithRelations>;

# The fields to insert a new `CodeSystem`.
public type CodeSystemInsert record {|
    string id;
    string url;
    string version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] codeSystem;
|};

# The fields to update on an existing `CodeSystem`.
public type CodeSystemUpdate record {|
    string id?;
    string url?;
    string version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] codeSystem?;
|};

# Represents the `Concept` entity.
public type Concept record {|
    readonly int conceptId;
    string code;
    byte[] concept;
    int? parentConceptId;
    int codesystemCodeSystemId;

|};

# Represents `Concept` with every field optional, for projected reads.
public type ConceptOptionalized record {|
    int conceptId?;
    string code?;
    byte[] concept?;
    int? parentConceptId?;
    int codesystemCodeSystemId?;
|};

# Represents `Concept` with its related entities, for projected reads.
public type ConceptWithRelations record {|
    *ConceptOptionalized;
    CodeSystemOptionalized codeSystem?;
    ValueSetComposeIncludeConceptOptionalized[] valuesetcomposeincludeconcept?;
|};

# The `Concept` type or a projection of it, for reads.
public type ConceptTargetType typedesc<ConceptWithRelations>;

# The fields to insert a new `Concept`.
public type ConceptInsert record {|
    string code;
    byte[] concept;
    int? parentConceptId;
    int codesystemCodeSystemId;
|};

# The fields to update on an existing `Concept`.
public type ConceptUpdate record {|
    string code?;
    byte[] concept?;
    int? parentConceptId?;
    int codesystemCodeSystemId?;
|};

# Represents the `ValueSet` entity.
public type ValueSet record {|
    readonly int valueSetId;
    string id;
    string url;
    string version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] valueSet;

|};

# Represents `ValueSet` with every field optional, for projected reads.
public type ValueSetOptionalized record {|
    int valueSetId?;
    string id?;
    string url?;
    string version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] valueSet?;
|};

# Represents `ValueSet` with its related entities, for projected reads.
public type ValueSetWithRelations record {|
    *ValueSetOptionalized;
    ValueSetComposeIncludeOptionalized[] composes?;
    ValueSetComposeIncludeValueSetOptionalized[] conceptsInValueSetConcepts?;
|};

# The `ValueSet` type or a projection of it, for reads.
public type ValueSetTargetType typedesc<ValueSetWithRelations>;

# The fields to insert a new `ValueSet`.
public type ValueSetInsert record {|
    string id;
    string url;
    string version;
    string name;
    string title;
    string status;
    string date;
    string publisher;
    byte[] valueSet;
|};

# The fields to update on an existing `ValueSet`.
public type ValueSetUpdate record {|
    string id?;
    string url?;
    string version?;
    string name?;
    string title?;
    string status?;
    string date?;
    string publisher?;
    byte[] valueSet?;
|};

# Represents the `ValueSetComposeInclude` entity.
public type ValueSetComposeInclude record {|
    readonly int valueSetComposeIncludeId;
    boolean systemFlag;
    boolean valueSetFlag;
    boolean conceptFlag;
    int valuesetValueSetId;

    int? codeSystemId;
|};

# Represents `ValueSetComposeInclude` with every field optional, for projected reads.
public type ValueSetComposeIncludeOptionalized record {|
    int valueSetComposeIncludeId?;
    boolean systemFlag?;
    boolean valueSetFlag?;
    boolean conceptFlag?;
    int valuesetValueSetId?;
    int? codeSystemId?;
|};

# Represents `ValueSetComposeInclude` with its related entities, for projected reads.
public type ValueSetComposeIncludeWithRelations record {|
    *ValueSetComposeIncludeOptionalized;
    ValueSetOptionalized valueSet?;
    ValueSetComposeIncludeValueSetOptionalized[] valuesetcomposeincludevalueset?;
    ValueSetComposeIncludeConceptOptionalized[] valuesetcomposeincludeconcept?;
|};

# The `ValueSetComposeInclude` type or a projection of it, for reads.
public type ValueSetComposeIncludeTargetType typedesc<ValueSetComposeIncludeWithRelations>;

# The fields to insert a new `ValueSetComposeInclude`.
public type ValueSetComposeIncludeInsert record {|
    boolean systemFlag;
    boolean valueSetFlag;
    boolean conceptFlag;
    int valuesetValueSetId;
    int? codeSystemId;
|};

# The fields to update on an existing `ValueSetComposeInclude`.
public type ValueSetComposeIncludeUpdate record {|
    boolean systemFlag?;
    boolean valueSetFlag?;
    boolean conceptFlag?;
    int valuesetValueSetId?;
    int? codeSystemId?;
|};

# Represents the `ValueSetComposeIncludeValueSet` entity.
public type ValueSetComposeIncludeValueSet record {|
    readonly int valueSetComposeIncludeValueSetId;
    int valuesetcomposeValueSetComposeIncludeId;
    int valuesetValueSetId;
|};

# Represents `ValueSetComposeIncludeValueSet` with every field optional, for projected reads.
public type ValueSetComposeIncludeValueSetOptionalized record {|
    int valueSetComposeIncludeValueSetId?;
    int valuesetcomposeValueSetComposeIncludeId?;
    int valuesetValueSetId?;
|};

# Represents `ValueSetComposeIncludeValueSet` with its related entities, for projected reads.
public type ValueSetComposeIncludeValueSetWithRelations record {|
    *ValueSetComposeIncludeValueSetOptionalized;
    ValueSetComposeIncludeOptionalized valuesetCompose?;
    ValueSetOptionalized valueset?;
|};

# The `ValueSetComposeIncludeValueSet` type or a projection of it, for reads.
public type ValueSetComposeIncludeValueSetTargetType typedesc<ValueSetComposeIncludeValueSetWithRelations>;

# The fields to insert a new `ValueSetComposeIncludeValueSet`.
public type ValueSetComposeIncludeValueSetInsert record {|
    int valuesetcomposeValueSetComposeIncludeId;
    int valuesetValueSetId;
|};

# The fields to update on an existing `ValueSetComposeIncludeValueSet`.
public type ValueSetComposeIncludeValueSetUpdate record {|
    int valuesetcomposeValueSetComposeIncludeId?;
    int valuesetValueSetId?;
|};

# Represents the `ValueSetComposeIncludeConcept` entity.
public type ValueSetComposeIncludeConcept record {|
    readonly int valueSetComposeIncludeConceptId;
    int valuesetcomposeValueSetComposeIncludeId;
    int conceptConceptId;
|};

# Represents `ValueSetComposeIncludeConcept` with every field optional, for projected reads.
public type ValueSetComposeIncludeConceptOptionalized record {|
    int valueSetComposeIncludeConceptId?;
    int valuesetcomposeValueSetComposeIncludeId?;
    int conceptConceptId?;
|};

# Represents `ValueSetComposeIncludeConcept` with its related entities, for projected reads.
public type ValueSetComposeIncludeConceptWithRelations record {|
    *ValueSetComposeIncludeConceptOptionalized;
    ValueSetComposeIncludeOptionalized valuesetCompose?;
    ConceptOptionalized concept?;
|};

# The `ValueSetComposeIncludeConcept` type or a projection of it, for reads.
public type ValueSetComposeIncludeConceptTargetType typedesc<ValueSetComposeIncludeConceptWithRelations>;

# The fields to insert a new `ValueSetComposeIncludeConcept`.
public type ValueSetComposeIncludeConceptInsert record {|
    int valuesetcomposeValueSetComposeIncludeId;
    int conceptConceptId;
|};

# The fields to update on an existing `ValueSetComposeIncludeConcept`.
public type ValueSetComposeIncludeConceptUpdate record {|
    int valuesetcomposeValueSetComposeIncludeId?;
    int conceptConceptId?;
|};

