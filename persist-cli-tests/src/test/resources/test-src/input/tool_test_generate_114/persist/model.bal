// Copyright (c) 2025 WSO2 LLC. (http://www.wso2.org).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/persist as _;
import ballerinax/persist.sql;

@sql:Name {value: "codesystems"}
public type CodeSystem record {|
    @sql:Generated
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
    Concept[] concepts;
|};

@sql:Name {value: "concepts"}
public type Concept record {|
    @sql:Generated
    readonly int conceptId;
    string code;
    byte[] concept;
    int? parentConceptId;
    CodeSystem codeSystem;
	ValueSetComposeIncludeConcept[] valuesetcomposeincludeconcept;
|};

@sql:Name {value: "valuesets"}
public type ValueSet record {|
    @sql:Generated
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
    ValueSetComposeInclude[] composes;
    ValueSetComposeIncludeValueSet[] conceptsInValueSetConcepts;
|};

@sql:Name {value: "valueset_compose_includes"}
public type ValueSetComposeInclude record {|
    @sql:Generated
    readonly int valueSetComposeIncludeId;
    boolean systemFlag;
    boolean valueSetFlag;
    boolean conceptFlag;
    ValueSet valueSet;
    ValueSetComposeIncludeValueSet[] valuesetcomposeincludevalueset;
	ValueSetComposeIncludeConcept[] valuesetcomposeincludeconcept;
    int? codeSystemId;
|};

@sql:Name {value: "valueset_compose_include_value_sets"}
public type ValueSetComposeIncludeValueSet record {|
    @sql:Generated
    readonly int valueSetComposeIncludeValueSetId;
	ValueSetComposeInclude valuesetCompose;
	ValueSet valueset;
|};

@sql:Name {value: "valueset_compose_include_concepts"}
public type ValueSetComposeIncludeConcept record {|
    @sql:Generated
    readonly int valueSetComposeIncludeConceptId;
    ValueSetComposeInclude valuesetCompose;
    Concept concept;
|};
