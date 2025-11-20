// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mssql;
import ballerinax/mssql.driver as _;
import ballerinax/persist.sql as psql;

const CODE_SYSTEM = "codesystems";
const CONCEPT = "concepts";
const VALUE_SET = "valuesets";
const VALUE_SET_COMPOSE_INCLUDE = "valuesetcomposeincludes";
const VALUE_SET_COMPOSE_INCLUDE_VALUE_SET = "valuesetcomposeincludevaluesets";
const VALUE_SET_COMPOSE_INCLUDE_CONCEPT = "valuesetcomposeincludeconcepts";

# MSSQL persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mssql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} metadata = {
        [CODE_SYSTEM]: {
            entityName: "CodeSystem",
            tableName: "codesystems",
            fieldMetadata: {
                codeSystemId: {columnName: "codeSystemId", dbGenerated: true},
                id: {columnName: "id"},
                url: {columnName: "url"},
                'version: {columnName: "version"},
                name: {columnName: "name"},
                title: {columnName: "title"},
                status: {columnName: "status"},
                date: {columnName: "date"},
                publisher: {columnName: "publisher"},
                codeSystem: {columnName: "codeSystem"},
                "concepts[].conceptId": {relation: {entityName: "concepts", refField: "conceptId"}},
                "concepts[].code": {relation: {entityName: "concepts", refField: "code"}},
                "concepts[].concept": {relation: {entityName: "concepts", refField: "concept"}},
                "concepts[].parentConceptId": {relation: {entityName: "concepts", refField: "parentConceptId"}},
                "concepts[].codesystemCodeSystemId": {relation: {entityName: "concepts", refField: "codesystemCodeSystemId"}}
            },
            keyFields: ["codeSystemId"],
            joinMetadata: {concepts: {entity: Concept, fieldName: "concepts", refTable: "concepts", refColumns: ["codesystemCodeSystemId"], joinColumns: ["codeSystemId"], 'type: psql:MANY_TO_ONE}}
        },
        [CONCEPT]: {
            entityName: "Concept",
            tableName: "concepts",
            fieldMetadata: {
                conceptId: {columnName: "conceptId", dbGenerated: true},
                code: {columnName: "code"},
                concept: {columnName: "concept"},
                parentConceptId: {columnName: "parentConceptId"},
                codesystemCodeSystemId: {columnName: "codesystemCodeSystemId"},
                "codeSystem.codeSystemId": {relation: {entityName: "codeSystem", refField: "codeSystemId"}},
                "codeSystem.id": {relation: {entityName: "codeSystem", refField: "id"}},
                "codeSystem.url": {relation: {entityName: "codeSystem", refField: "url"}},
                "codeSystem.version": {relation: {entityName: "codeSystem", refField: "version", refColumn: "version"}},
                "codeSystem.name": {relation: {entityName: "codeSystem", refField: "name"}},
                "codeSystem.title": {relation: {entityName: "codeSystem", refField: "title"}},
                "codeSystem.status": {relation: {entityName: "codeSystem", refField: "status"}},
                "codeSystem.date": {relation: {entityName: "codeSystem", refField: "date"}},
                "codeSystem.publisher": {relation: {entityName: "codeSystem", refField: "publisher"}},
                "codeSystem.codeSystem": {relation: {entityName: "codeSystem", refField: "codeSystem"}},
                "valuesetcomposeincludeconcept[].valueSetComposeIncludeConceptId": {relation: {entityName: "valuesetcomposeincludeconcept", refField: "valueSetComposeIncludeConceptId"}},
                "valuesetcomposeincludeconcept[].valuesetcomposeValueSetComposeIncludeId": {relation: {entityName: "valuesetcomposeincludeconcept", refField: "valuesetcomposeValueSetComposeIncludeId"}},
                "valuesetcomposeincludeconcept[].conceptConceptId": {relation: {entityName: "valuesetcomposeincludeconcept", refField: "conceptConceptId"}}
            },
            keyFields: ["conceptId"],
            joinMetadata: {
                codeSystem: {entity: CodeSystem, fieldName: "codeSystem", refTable: "codesystems", refColumns: ["codeSystemId"], joinColumns: ["codesystemCodeSystemId"], 'type: psql:ONE_TO_MANY},
                valuesetcomposeincludeconcept: {entity: ValueSetComposeIncludeConcept, fieldName: "valuesetcomposeincludeconcept", refTable: "valueset_compose_include_concepts", refColumns: ["conceptConceptId"], joinColumns: ["conceptId"], 'type: psql:MANY_TO_ONE}
            }
        },
        [VALUE_SET]: {
            entityName: "ValueSet",
            tableName: "valuesets",
            fieldMetadata: {
                valueSetId: {columnName: "valueSetId", dbGenerated: true},
                id: {columnName: "id"},
                url: {columnName: "url"},
                'version: {columnName: "version"},
                name: {columnName: "name"},
                title: {columnName: "title"},
                status: {columnName: "status"},
                date: {columnName: "date"},
                publisher: {columnName: "publisher"},
                valueSet: {columnName: "valueSet"},
                "composes[].valueSetComposeIncludeId": {relation: {entityName: "composes", refField: "valueSetComposeIncludeId"}},
                "composes[].systemFlag": {relation: {entityName: "composes", refField: "systemFlag"}},
                "composes[].valueSetFlag": {relation: {entityName: "composes", refField: "valueSetFlag"}},
                "composes[].conceptFlag": {relation: {entityName: "composes", refField: "conceptFlag"}},
                "composes[].valuesetValueSetId": {relation: {entityName: "composes", refField: "valuesetValueSetId"}},
                "composes[].codeSystemId": {relation: {entityName: "composes", refField: "codeSystemId"}},
                "conceptsInValueSetConcepts[].valueSetComposeIncludeValueSetId": {relation: {entityName: "conceptsInValueSetConcepts", refField: "valueSetComposeIncludeValueSetId"}},
                "conceptsInValueSetConcepts[].valuesetcomposeValueSetComposeIncludeId": {relation: {entityName: "conceptsInValueSetConcepts", refField: "valuesetcomposeValueSetComposeIncludeId"}},
                "conceptsInValueSetConcepts[].valuesetValueSetId": {relation: {entityName: "conceptsInValueSetConcepts", refField: "valuesetValueSetId"}}
            },
            keyFields: ["valueSetId"],
            joinMetadata: {
                composes: {entity: ValueSetComposeInclude, fieldName: "composes", refTable: "valueset_compose_includes", refColumns: ["valuesetValueSetId"], joinColumns: ["valueSetId"], 'type: psql:MANY_TO_ONE},
                conceptsInValueSetConcepts: {entity: ValueSetComposeIncludeValueSet, fieldName: "conceptsInValueSetConcepts", refTable: "valueset_compose_include_value_sets", refColumns: ["valuesetValueSetId"], joinColumns: ["valueSetId"], 'type: psql:MANY_TO_ONE}
            }
        },
        [VALUE_SET_COMPOSE_INCLUDE]: {
            entityName: "ValueSetComposeInclude",
            tableName: "valueset_compose_includes",
            fieldMetadata: {
                valueSetComposeIncludeId: {columnName: "valueSetComposeIncludeId", dbGenerated: true},
                systemFlag: {columnName: "systemFlag"},
                valueSetFlag: {columnName: "valueSetFlag"},
                conceptFlag: {columnName: "conceptFlag"},
                valuesetValueSetId: {columnName: "valuesetValueSetId"},
                codeSystemId: {columnName: "codeSystemId"},
                "valueSet.valueSetId": {relation: {entityName: "valueSet", refField: "valueSetId"}},
                "valueSet.id": {relation: {entityName: "valueSet", refField: "id"}},
                "valueSet.url": {relation: {entityName: "valueSet", refField: "url"}},
                "valueSet.version": {relation: {entityName: "valueSet", refField: "version", refColumn: "version"}},
                "valueSet.name": {relation: {entityName: "valueSet", refField: "name"}},
                "valueSet.title": {relation: {entityName: "valueSet", refField: "title"}},
                "valueSet.status": {relation: {entityName: "valueSet", refField: "status"}},
                "valueSet.date": {relation: {entityName: "valueSet", refField: "date"}},
                "valueSet.publisher": {relation: {entityName: "valueSet", refField: "publisher"}},
                "valueSet.valueSet": {relation: {entityName: "valueSet", refField: "valueSet"}},
                "valuesetcomposeincludevalueset[].valueSetComposeIncludeValueSetId": {relation: {entityName: "valuesetcomposeincludevalueset", refField: "valueSetComposeIncludeValueSetId"}},
                "valuesetcomposeincludevalueset[].valuesetcomposeValueSetComposeIncludeId": {relation: {entityName: "valuesetcomposeincludevalueset", refField: "valuesetcomposeValueSetComposeIncludeId"}},
                "valuesetcomposeincludevalueset[].valuesetValueSetId": {relation: {entityName: "valuesetcomposeincludevalueset", refField: "valuesetValueSetId"}},
                "valuesetcomposeincludeconcept[].valueSetComposeIncludeConceptId": {relation: {entityName: "valuesetcomposeincludeconcept", refField: "valueSetComposeIncludeConceptId"}},
                "valuesetcomposeincludeconcept[].valuesetcomposeValueSetComposeIncludeId": {relation: {entityName: "valuesetcomposeincludeconcept", refField: "valuesetcomposeValueSetComposeIncludeId"}},
                "valuesetcomposeincludeconcept[].conceptConceptId": {relation: {entityName: "valuesetcomposeincludeconcept", refField: "conceptConceptId"}}
            },
            keyFields: ["valueSetComposeIncludeId"],
            joinMetadata: {
                valueSet: {entity: ValueSet, fieldName: "valueSet", refTable: "valuesets", refColumns: ["valueSetId"], joinColumns: ["valuesetValueSetId"], 'type: psql:ONE_TO_MANY},
                valuesetcomposeincludevalueset: {entity: ValueSetComposeIncludeValueSet, fieldName: "valuesetcomposeincludevalueset", refTable: "valueset_compose_include_value_sets", refColumns: ["valuesetcomposeValueSetComposeIncludeId"], joinColumns: ["valueSetComposeIncludeId"], 'type: psql:MANY_TO_ONE},
                valuesetcomposeincludeconcept: {entity: ValueSetComposeIncludeConcept, fieldName: "valuesetcomposeincludeconcept", refTable: "valueset_compose_include_concepts", refColumns: ["valuesetcomposeValueSetComposeIncludeId"], joinColumns: ["valueSetComposeIncludeId"], 'type: psql:MANY_TO_ONE}
            }
        },
        [VALUE_SET_COMPOSE_INCLUDE_VALUE_SET]: {
            entityName: "ValueSetComposeIncludeValueSet",
            tableName: "valueset_compose_include_value_sets",
            fieldMetadata: {
                valueSetComposeIncludeValueSetId: {columnName: "valueSetComposeIncludeValueSetId", dbGenerated: true},
                valuesetcomposeValueSetComposeIncludeId: {columnName: "valuesetcomposeValueSetComposeIncludeId"},
                valuesetValueSetId: {columnName: "valuesetValueSetId"},
                "valuesetCompose.valueSetComposeIncludeId": {relation: {entityName: "valuesetCompose", refField: "valueSetComposeIncludeId"}},
                "valuesetCompose.systemFlag": {relation: {entityName: "valuesetCompose", refField: "systemFlag"}},
                "valuesetCompose.valueSetFlag": {relation: {entityName: "valuesetCompose", refField: "valueSetFlag"}},
                "valuesetCompose.conceptFlag": {relation: {entityName: "valuesetCompose", refField: "conceptFlag"}},
                "valuesetCompose.valuesetValueSetId": {relation: {entityName: "valuesetCompose", refField: "valuesetValueSetId"}},
                "valuesetCompose.codeSystemId": {relation: {entityName: "valuesetCompose", refField: "codeSystemId"}},
                "valueset.valueSetId": {relation: {entityName: "valueset", refField: "valueSetId"}},
                "valueset.id": {relation: {entityName: "valueset", refField: "id"}},
                "valueset.url": {relation: {entityName: "valueset", refField: "url"}},
                "valueset.version": {relation: {entityName: "valueset", refField: "version", refColumn: "version"}},
                "valueset.name": {relation: {entityName: "valueset", refField: "name"}},
                "valueset.title": {relation: {entityName: "valueset", refField: "title"}},
                "valueset.status": {relation: {entityName: "valueset", refField: "status"}},
                "valueset.date": {relation: {entityName: "valueset", refField: "date"}},
                "valueset.publisher": {relation: {entityName: "valueset", refField: "publisher"}},
                "valueset.valueSet": {relation: {entityName: "valueset", refField: "valueSet"}}
            },
            keyFields: ["valueSetComposeIncludeValueSetId"],
            joinMetadata: {
                valuesetCompose: {entity: ValueSetComposeInclude, fieldName: "valuesetCompose", refTable: "valueset_compose_includes", refColumns: ["valueSetComposeIncludeId"], joinColumns: ["valuesetcomposeValueSetComposeIncludeId"], 'type: psql:ONE_TO_MANY},
                valueset: {entity: ValueSet, fieldName: "valueset", refTable: "valuesets", refColumns: ["valueSetId"], joinColumns: ["valuesetValueSetId"], 'type: psql:ONE_TO_MANY}
            }
        },
        [VALUE_SET_COMPOSE_INCLUDE_CONCEPT]: {
            entityName: "ValueSetComposeIncludeConcept",
            tableName: "valueset_compose_include_concepts",
            fieldMetadata: {
                valueSetComposeIncludeConceptId: {columnName: "valueSetComposeIncludeConceptId", dbGenerated: true},
                valuesetcomposeValueSetComposeIncludeId: {columnName: "valuesetcomposeValueSetComposeIncludeId"},
                conceptConceptId: {columnName: "conceptConceptId"},
                "valuesetCompose.valueSetComposeIncludeId": {relation: {entityName: "valuesetCompose", refField: "valueSetComposeIncludeId"}},
                "valuesetCompose.systemFlag": {relation: {entityName: "valuesetCompose", refField: "systemFlag"}},
                "valuesetCompose.valueSetFlag": {relation: {entityName: "valuesetCompose", refField: "valueSetFlag"}},
                "valuesetCompose.conceptFlag": {relation: {entityName: "valuesetCompose", refField: "conceptFlag"}},
                "valuesetCompose.valuesetValueSetId": {relation: {entityName: "valuesetCompose", refField: "valuesetValueSetId"}},
                "valuesetCompose.codeSystemId": {relation: {entityName: "valuesetCompose", refField: "codeSystemId"}},
                "concept.conceptId": {relation: {entityName: "concept", refField: "conceptId"}},
                "concept.code": {relation: {entityName: "concept", refField: "code"}},
                "concept.concept": {relation: {entityName: "concept", refField: "concept"}},
                "concept.parentConceptId": {relation: {entityName: "concept", refField: "parentConceptId"}},
                "concept.codesystemCodeSystemId": {relation: {entityName: "concept", refField: "codesystemCodeSystemId"}}
            },
            keyFields: ["valueSetComposeIncludeConceptId"],
            joinMetadata: {
                valuesetCompose: {entity: ValueSetComposeInclude, fieldName: "valuesetCompose", refTable: "valueset_compose_includes", refColumns: ["valueSetComposeIncludeId"], joinColumns: ["valuesetcomposeValueSetComposeIncludeId"], 'type: psql:ONE_TO_MANY},
                concept: {entity: Concept, fieldName: "concept", refTable: "concepts", refColumns: ["conceptId"], joinColumns: ["conceptConceptId"], 'type: psql:ONE_TO_MANY}
            }
        }
    };

    public isolated function init() returns persist:Error? {
        mssql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        if defaultSchema != () {
            lock {
                foreach string key in self.metadata.keys() {
                    psql:SQLMetadata metadata = self.metadata.get(key);
                    if metadata.schemaName == () {
                        metadata.schemaName = defaultSchema;
                    }
                    map<psql:JoinMetadata>? joinMetadataMap = metadata.joinMetadata;
                    if joinMetadataMap == () {
                        continue;
                    }
                    foreach [string, psql:JoinMetadata] [_, joinMetadata] in joinMetadataMap.entries() {
                        if joinMetadata.refSchema == () {
                            joinMetadata.refSchema = defaultSchema;
                        }
                    }
                }
            }
        }
        self.persistClients = {
            [CODE_SYSTEM]: check new (dbClient, self.metadata.get(CODE_SYSTEM).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [CONCEPT]: check new (dbClient, self.metadata.get(CONCEPT).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [VALUE_SET]: check new (dbClient, self.metadata.get(VALUE_SET).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [VALUE_SET_COMPOSE_INCLUDE]: check new (dbClient, self.metadata.get(VALUE_SET_COMPOSE_INCLUDE).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [VALUE_SET_COMPOSE_INCLUDE_VALUE_SET]: check new (dbClient, self.metadata.get(VALUE_SET_COMPOSE_INCLUDE_VALUE_SET).cloneReadOnly(), psql:MSSQL_SPECIFICS),
            [VALUE_SET_COMPOSE_INCLUDE_CONCEPT]: check new (dbClient, self.metadata.get(VALUE_SET_COMPOSE_INCLUDE_CONCEPT).cloneReadOnly(), psql:MSSQL_SPECIFICS)
        };
    }

    # Get rows from codesystems table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get codesystems(CodeSystemTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from codesystems table.
    #
    # + codeSystemId - The value of the primary key field codeSystemId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get codesystems/[int codeSystemId](CodeSystemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into codesystems table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post codesystems(CodeSystemInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CODE_SYSTEM);
        }
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
    }

    # Update row in codesystems table.
    #
    # + codeSystemId - The value of the primary key field codeSystemId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put codesystems/[int codeSystemId](CodeSystemUpdate value) returns CodeSystem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CODE_SYSTEM);
        }
        _ = check sqlClient.runUpdateQuery(codeSystemId, value);
        return self->/codesystems/[codeSystemId].get();
    }

    # Delete row from codesystems table.
    #
    # + codeSystemId - The value of the primary key field codeSystemId
    # + return - The deleted record or an error
    isolated resource function delete codesystems/[int codeSystemId]() returns CodeSystem|persist:Error {
        CodeSystem result = check self->/codesystems/[codeSystemId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CODE_SYSTEM);
        }
        _ = check sqlClient.runDeleteQuery(codeSystemId);
        return result;
    }

    # Get rows from concepts table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get concepts(ConceptTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from concepts table.
    #
    # + conceptId - The value of the primary key field conceptId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get concepts/[int conceptId](ConceptTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into concepts table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post concepts(ConceptInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CONCEPT);
        }
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
    }

    # Update row in concepts table.
    #
    # + conceptId - The value of the primary key field conceptId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put concepts/[int conceptId](ConceptUpdate value) returns Concept|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CONCEPT);
        }
        _ = check sqlClient.runUpdateQuery(conceptId, value);
        return self->/concepts/[conceptId].get();
    }

    # Delete row from concepts table.
    #
    # + conceptId - The value of the primary key field conceptId
    # + return - The deleted record or an error
    isolated resource function delete concepts/[int conceptId]() returns Concept|persist:Error {
        Concept result = check self->/concepts/[conceptId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CONCEPT);
        }
        _ = check sqlClient.runDeleteQuery(conceptId);
        return result;
    }

    # Get rows from valuesets table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get valuesets(ValueSetTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from valuesets table.
    #
    # + valueSetId - The value of the primary key field valueSetId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get valuesets/[int valueSetId](ValueSetTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into valuesets table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post valuesets(ValueSetInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET);
        }
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
    }

    # Update row in valuesets table.
    #
    # + valueSetId - The value of the primary key field valueSetId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put valuesets/[int valueSetId](ValueSetUpdate value) returns ValueSet|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET);
        }
        _ = check sqlClient.runUpdateQuery(valueSetId, value);
        return self->/valuesets/[valueSetId].get();
    }

    # Delete row from valuesets table.
    #
    # + valueSetId - The value of the primary key field valueSetId
    # + return - The deleted record or an error
    isolated resource function delete valuesets/[int valueSetId]() returns ValueSet|persist:Error {
        ValueSet result = check self->/valuesets/[valueSetId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET);
        }
        _ = check sqlClient.runDeleteQuery(valueSetId);
        return result;
    }

    # Get rows from valueset_compose_includes table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get valuesetcomposeincludes(ValueSetComposeIncludeTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from valueset_compose_includes table.
    #
    # + valueSetComposeIncludeId - The value of the primary key field valueSetComposeIncludeId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get valuesetcomposeincludes/[int valueSetComposeIncludeId](ValueSetComposeIncludeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into valueset_compose_includes table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post valuesetcomposeincludes(ValueSetComposeIncludeInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE);
        }
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
    }

    # Update row in valueset_compose_includes table.
    #
    # + valueSetComposeIncludeId - The value of the primary key field valueSetComposeIncludeId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put valuesetcomposeincludes/[int valueSetComposeIncludeId](ValueSetComposeIncludeUpdate value) returns ValueSetComposeInclude|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE);
        }
        _ = check sqlClient.runUpdateQuery(valueSetComposeIncludeId, value);
        return self->/valuesetcomposeincludes/[valueSetComposeIncludeId].get();
    }

    # Delete row from valueset_compose_includes table.
    #
    # + valueSetComposeIncludeId - The value of the primary key field valueSetComposeIncludeId
    # + return - The deleted record or an error
    isolated resource function delete valuesetcomposeincludes/[int valueSetComposeIncludeId]() returns ValueSetComposeInclude|persist:Error {
        ValueSetComposeInclude result = check self->/valuesetcomposeincludes/[valueSetComposeIncludeId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE);
        }
        _ = check sqlClient.runDeleteQuery(valueSetComposeIncludeId);
        return result;
    }

    # Get rows from valueset_compose_include_value_sets table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get valuesetcomposeincludevaluesets(ValueSetComposeIncludeValueSetTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from valueset_compose_include_value_sets table.
    #
    # + valueSetComposeIncludeValueSetId - The value of the primary key field valueSetComposeIncludeValueSetId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get valuesetcomposeincludevaluesets/[int valueSetComposeIncludeValueSetId](ValueSetComposeIncludeValueSetTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into valueset_compose_include_value_sets table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post valuesetcomposeincludevaluesets(ValueSetComposeIncludeValueSetInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE_VALUE_SET);
        }
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
    }

    # Update row in valueset_compose_include_value_sets table.
    #
    # + valueSetComposeIncludeValueSetId - The value of the primary key field valueSetComposeIncludeValueSetId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put valuesetcomposeincludevaluesets/[int valueSetComposeIncludeValueSetId](ValueSetComposeIncludeValueSetUpdate value) returns ValueSetComposeIncludeValueSet|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE_VALUE_SET);
        }
        _ = check sqlClient.runUpdateQuery(valueSetComposeIncludeValueSetId, value);
        return self->/valuesetcomposeincludevaluesets/[valueSetComposeIncludeValueSetId].get();
    }

    # Delete row from valueset_compose_include_value_sets table.
    #
    # + valueSetComposeIncludeValueSetId - The value of the primary key field valueSetComposeIncludeValueSetId
    # + return - The deleted record or an error
    isolated resource function delete valuesetcomposeincludevaluesets/[int valueSetComposeIncludeValueSetId]() returns ValueSetComposeIncludeValueSet|persist:Error {
        ValueSetComposeIncludeValueSet result = check self->/valuesetcomposeincludevaluesets/[valueSetComposeIncludeValueSetId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE_VALUE_SET);
        }
        _ = check sqlClient.runDeleteQuery(valueSetComposeIncludeValueSetId);
        return result;
    }

    # Get rows from valueset_compose_include_concepts table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + whereClause - SQL WHERE clause to filter the results (e.g., `column_name = value`)
    # + orderByClause - SQL ORDER BY clause to sort the results (e.g., `column_name ASC`)
    # + limitClause - SQL LIMIT clause to limit the number of results (e.g., `10`)
    # + groupByClause - SQL GROUP BY clause to group the results (e.g., `column_name`)
    # + return - A collection of matching records or an error
    isolated resource function get valuesetcomposeincludeconcepts(ValueSetComposeIncludeConceptTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    # Get row from valueset_compose_include_concepts table.
    #
    # + valueSetComposeIncludeConceptId - The value of the primary key field valueSetComposeIncludeConceptId
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get valuesetcomposeincludeconcepts/[int valueSetComposeIncludeConceptId](ValueSetComposeIncludeConceptTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    # Insert rows into valueset_compose_include_concepts table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post valuesetcomposeincludeconcepts(ValueSetComposeIncludeConceptInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE_CONCEPT);
        }
        sql:ExecutionResult[] result = check sqlClient.runBatchInsertQuery(data);
        return from sql:ExecutionResult inserted in result
            where inserted.lastInsertId != ()
            select <int>inserted.lastInsertId;
    }

    # Update row in valueset_compose_include_concepts table.
    #
    # + valueSetComposeIncludeConceptId - The value of the primary key field valueSetComposeIncludeConceptId
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put valuesetcomposeincludeconcepts/[int valueSetComposeIncludeConceptId](ValueSetComposeIncludeConceptUpdate value) returns ValueSetComposeIncludeConcept|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE_CONCEPT);
        }
        _ = check sqlClient.runUpdateQuery(valueSetComposeIncludeConceptId, value);
        return self->/valuesetcomposeincludeconcepts/[valueSetComposeIncludeConceptId].get();
    }

    # Delete row from valueset_compose_include_concepts table.
    #
    # + valueSetComposeIncludeConceptId - The value of the primary key field valueSetComposeIncludeConceptId
    # + return - The deleted record or an error
    isolated resource function delete valuesetcomposeincludeconcepts/[int valueSetComposeIncludeConceptId]() returns ValueSetComposeIncludeConcept|persist:Error {
        ValueSetComposeIncludeConcept result = check self->/valuesetcomposeincludeconcepts/[valueSetComposeIncludeConceptId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(VALUE_SET_COMPOSE_INCLUDE_CONCEPT);
        }
        _ = check sqlClient.runDeleteQuery(valueSetComposeIncludeConceptId);
        return result;
    }

    # Execute a custom SQL query and return results.
    #
    # + sqlQuery - The SQL query to execute
    # + rowType - Defines the structure of the result rows
    # + return - A collection of result rows or an error
    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    # Execute a custom SQL command (INSERT, UPDATE, DELETE, etc.).
    #
    # + sqlQuery - The SQL command to execute
    # + return - The execution result or an error
    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

