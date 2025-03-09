// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/persist.sql as psql;
import ballerinax/postgresql;
import ballerinax/postgresql.driver as _;

const ALL_TYPES = "alltypes";
const STRING_ID_RECORD = "stringidrecords";
const INT_ID_RECORD = "intidrecords";
const FLOAT_ID_RECORD = "floatidrecords";
const DECIMAL_ID_RECORD = "decimalidrecords";
const BOOLEAN_ID_RECORD = "booleanidrecords";
const COMPOSITE_ASSOCIATION_RECORD = "compositeassociationrecords";
const ALL_TYPES_ID_RECORD = "alltypesidrecords";
const USER = "users";
const POST = "posts";
const FOLLOW = "follows";
const COMMENT = "comments";
const EMPLOYEE = "employees";
const WORKSPACE = "workspaces";
const BUILDING = "buildings";
const DEPARTMENT = "departments";
const ORDER_ITEM = "orderitems";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final postgresql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} metadata = {
        [ALL_TYPES]: {
            entityName: "AllTypes",
            tableName: "AllTypes",
            fieldMetadata: {
                id: {columnName: "id"},
                booleanType: {columnName: "booleanType"},
                intType: {columnName: "intType"},
                floatType: {columnName: "floatType"},
                decimalType: {columnName: "decimalType"},
                stringType: {columnName: "stringType"},
                byteArrayType: {columnName: "byteArrayType"},
                dateType: {columnName: "dateType"},
                timeOfDayType: {columnName: "timeOfDayType"},
                utcType: {columnName: "utcType"},
                civilType: {columnName: "civilType"},
                booleanTypeOptional: {columnName: "booleanTypeOptional"},
                intTypeOptional: {columnName: "intTypeOptional"},
                floatTypeOptional: {columnName: "floatTypeOptional"},
                decimalTypeOptional: {columnName: "decimalTypeOptional"},
                stringTypeOptional: {columnName: "stringTypeOptional"},
                byteArrayTypeOptional: {columnName: "byteArrayTypeOptional"},
                dateTypeOptional: {columnName: "dateTypeOptional"},
                timeOfDayTypeOptional: {columnName: "timeOfDayTypeOptional"},
                utcTypeOptional: {columnName: "utcTypeOptional"},
                civilTypeOptional: {columnName: "civilTypeOptional"},
                enumType: {columnName: "enumType"},
                enumTypeOptional: {columnName: "enumTypeOptional"}
            },
            keyFields: ["id"]
        },
        [STRING_ID_RECORD]: {
            entityName: "StringIdRecord",
            tableName: "StringIdRecord",
            fieldMetadata: {
                id: {columnName: "id"},
                randomField: {columnName: "randomField"}
            },
            keyFields: ["id"]
        },
        [INT_ID_RECORD]: {
            entityName: "IntIdRecord",
            tableName: "IntIdRecord",
            fieldMetadata: {
                id: {columnName: "id"},
                randomField: {columnName: "randomField"}
            },
            keyFields: ["id"]
        },
        [FLOAT_ID_RECORD]: {
            entityName: "FloatIdRecord",
            tableName: "FloatIdRecord",
            fieldMetadata: {
                id: {columnName: "id"},
                randomField: {columnName: "randomField"}
            },
            keyFields: ["id"]
        },
        [DECIMAL_ID_RECORD]: {
            entityName: "DecimalIdRecord",
            tableName: "DecimalIdRecord",
            fieldMetadata: {
                id: {columnName: "id"},
                randomField: {columnName: "randomField"}
            },
            keyFields: ["id"]
        },
        [BOOLEAN_ID_RECORD]: {
            entityName: "BooleanIdRecord",
            tableName: "BooleanIdRecord",
            fieldMetadata: {
                id: {columnName: "id"},
                randomField: {columnName: "randomField"}
            },
            keyFields: ["id"]
        },
        [COMPOSITE_ASSOCIATION_RECORD]: {
            entityName: "CompositeAssociationRecord",
            tableName: "CompositeAssociationRecord",
            fieldMetadata: {
                id: {columnName: "id"},
                randomField: {columnName: "randomField"},
                alltypesidrecordBooleanType: {columnName: "alltypesidrecordBooleanType"},
                alltypesidrecordIntType: {columnName: "alltypesidrecordIntType"},
                alltypesidrecordFloatType: {columnName: "alltypesidrecordFloatType"},
                alltypesidrecordDecimalType: {columnName: "alltypesidrecordDecimalType"},
                alltypesidrecordStringType: {columnName: "alltypesidrecordStringType"},
                "allTypesIdRecord.booleanType": {relation: {entityName: "allTypesIdRecord", refField: "booleanType"}},
                "allTypesIdRecord.intType": {relation: {entityName: "allTypesIdRecord", refField: "intType"}},
                "allTypesIdRecord.floatType": {relation: {entityName: "allTypesIdRecord", refField: "floatType"}},
                "allTypesIdRecord.decimalType": {relation: {entityName: "allTypesIdRecord", refField: "decimalType"}},
                "allTypesIdRecord.stringType": {relation: {entityName: "allTypesIdRecord", refField: "stringType"}},
                "allTypesIdRecord.randomField": {relation: {entityName: "allTypesIdRecord", refField: "randomField"}}
            },
            keyFields: ["id"],
            joinMetadata: {allTypesIdRecord: {entity: AllTypesIdRecord, fieldName: "allTypesIdRecord", refTable: "AllTypesIdRecord", refColumns: ["booleanType", "intType", "floatType", "decimalType", "stringType"], joinColumns: ["alltypesidrecordBooleanType", "alltypesidrecordIntType", "alltypesidrecordFloatType", "alltypesidrecordDecimalType", "alltypesidrecordStringType"], 'type: psql:ONE_TO_ONE}}
        },
        [ALL_TYPES_ID_RECORD]: {
            entityName: "AllTypesIdRecord",
            tableName: "AllTypesIdRecord",
            fieldMetadata: {
                booleanType: {columnName: "booleanType"},
                intType: {columnName: "intType"},
                floatType: {columnName: "floatType"},
                decimalType: {columnName: "decimalType"},
                stringType: {columnName: "stringType"},
                randomField: {columnName: "randomField"},
                "compositeAssociationRecord.id": {relation: {entityName: "compositeAssociationRecord", refField: "id"}},
                "compositeAssociationRecord.randomField": {relation: {entityName: "compositeAssociationRecord", refField: "randomField"}},
                "compositeAssociationRecord.alltypesidrecordBooleanType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordBooleanType"}},
                "compositeAssociationRecord.alltypesidrecordIntType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordIntType"}},
                "compositeAssociationRecord.alltypesidrecordFloatType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordFloatType"}},
                "compositeAssociationRecord.alltypesidrecordDecimalType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordDecimalType"}},
                "compositeAssociationRecord.alltypesidrecordStringType": {relation: {entityName: "compositeAssociationRecord", refField: "alltypesidrecordStringType"}}
            },
            keyFields: ["booleanType", "intType", "floatType", "decimalType", "stringType"],
            joinMetadata: {compositeAssociationRecord: {entity: CompositeAssociationRecord, fieldName: "compositeAssociationRecord", refTable: "CompositeAssociationRecord", refColumns: ["alltypesidrecordBooleanType", "alltypesidrecordIntType", "alltypesidrecordFloatType", "alltypesidrecordDecimalType", "alltypesidrecordStringType"], joinColumns: ["booleanType", "intType", "floatType", "decimalType", "stringType"], 'type: psql:ONE_TO_ONE}}
        },
        [USER]: {
            entityName: "User",
            tableName: "User",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                birthDate: {columnName: "birthDate"},
                mobileNumber: {columnName: "mobileNumber"},
                "posts[].id": {relation: {entityName: "posts", refField: "id"}},
                "posts[].description": {relation: {entityName: "posts", refField: "description"}},
                "posts[].tags": {relation: {entityName: "posts", refField: "tags"}},
                "posts[].category": {relation: {entityName: "posts", refField: "category"}},
                "posts[].timestamp": {relation: {entityName: "posts", refField: "timestamp"}},
                "posts[].userId": {relation: {entityName: "posts", refField: "userId"}},
                "comments[].id": {relation: {entityName: "comments", refField: "id"}},
                "comments[].comment": {relation: {entityName: "comments", refField: "comment"}},
                "comments[].timesteamp": {relation: {entityName: "comments", refField: "timesteamp"}},
                "comments[].userId": {relation: {entityName: "comments", refField: "userId"}},
                "comments[].postId": {relation: {entityName: "comments", refField: "postId"}},
                "followers[].id": {relation: {entityName: "followers", refField: "id"}},
                "followers[].leaderId": {relation: {entityName: "followers", refField: "leaderId"}},
                "followers[].followerId": {relation: {entityName: "followers", refField: "followerId"}},
                "followers[].timestamp": {relation: {entityName: "followers", refField: "timestamp"}},
                "following[].id": {relation: {entityName: "following", refField: "id"}},
                "following[].leaderId": {relation: {entityName: "following", refField: "leaderId"}},
                "following[].followerId": {relation: {entityName: "following", refField: "followerId"}},
                "following[].timestamp": {relation: {entityName: "following", refField: "timestamp"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                posts: {entity: Post, fieldName: "posts", refTable: "Post", refColumns: ["userId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE},
                comments: {entity: Comment, fieldName: "comments", refTable: "Comment", refColumns: ["userId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE},
                followers: {entity: Follow, fieldName: "followers", refTable: "Follow", refColumns: ["leaderId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE},
                following: {entity: Follow, fieldName: "following", refTable: "Follow", refColumns: ["followerId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}
            }
        },
        [POST]: {
            entityName: "Post",
            tableName: "Post",
            fieldMetadata: {
                id: {columnName: "id"},
                description: {columnName: "description"},
                tags: {columnName: "tags"},
                category: {columnName: "category"},
                timestamp: {columnName: "timestamp"},
                userId: {columnName: "userId"},
                "user.id": {relation: {entityName: "user", refField: "id"}},
                "user.name": {relation: {entityName: "user", refField: "name"}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate"}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber"}},
                "comments[].id": {relation: {entityName: "comments", refField: "id"}},
                "comments[].comment": {relation: {entityName: "comments", refField: "comment"}},
                "comments[].timesteamp": {relation: {entityName: "comments", refField: "timesteamp"}},
                "comments[].userId": {relation: {entityName: "comments", refField: "userId"}},
                "comments[].postId": {relation: {entityName: "comments", refField: "postId"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                user: {entity: User, fieldName: "user", refTable: "User", refColumns: ["id"], joinColumns: ["userId"], 'type: psql:ONE_TO_MANY},
                comments: {entity: Comment, fieldName: "comments", refTable: "Comment", refColumns: ["postId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}
            }
        },
        [FOLLOW]: {
            entityName: "Follow",
            tableName: "Follow",
            fieldMetadata: {
                id: {columnName: "id"},
                leaderId: {columnName: "leaderId"},
                followerId: {columnName: "followerId"},
                timestamp: {columnName: "timestamp"},
                "leader.id": {relation: {entityName: "leader", refField: "id"}},
                "leader.name": {relation: {entityName: "leader", refField: "name"}},
                "leader.birthDate": {relation: {entityName: "leader", refField: "birthDate"}},
                "leader.mobileNumber": {relation: {entityName: "leader", refField: "mobileNumber"}},
                "follower.id": {relation: {entityName: "follower", refField: "id"}},
                "follower.name": {relation: {entityName: "follower", refField: "name"}},
                "follower.birthDate": {relation: {entityName: "follower", refField: "birthDate"}},
                "follower.mobileNumber": {relation: {entityName: "follower", refField: "mobileNumber"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                leader: {entity: User, fieldName: "leader", refTable: "User", refColumns: ["id"], joinColumns: ["leaderId"], 'type: psql:ONE_TO_MANY},
                follower: {entity: User, fieldName: "follower", refTable: "User", refColumns: ["id"], joinColumns: ["followerId"], 'type: psql:ONE_TO_MANY}
            }
        },
        [COMMENT]: {
            entityName: "Comment",
            tableName: "Comment",
            fieldMetadata: {
                id: {columnName: "id"},
                comment: {columnName: "comment"},
                timesteamp: {columnName: "timesteamp"},
                userId: {columnName: "userId"},
                postId: {columnName: "postId"},
                "user.id": {relation: {entityName: "user", refField: "id"}},
                "user.name": {relation: {entityName: "user", refField: "name"}},
                "user.birthDate": {relation: {entityName: "user", refField: "birthDate"}},
                "user.mobileNumber": {relation: {entityName: "user", refField: "mobileNumber"}},
                "post.id": {relation: {entityName: "post", refField: "id"}},
                "post.description": {relation: {entityName: "post", refField: "description"}},
                "post.tags": {relation: {entityName: "post", refField: "tags"}},
                "post.category": {relation: {entityName: "post", refField: "category"}},
                "post.timestamp": {relation: {entityName: "post", refField: "timestamp"}},
                "post.userId": {relation: {entityName: "post", refField: "userId"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                user: {entity: User, fieldName: "user", refTable: "User", refColumns: ["id"], joinColumns: ["userId"], 'type: psql:ONE_TO_MANY},
                post: {entity: Post, fieldName: "post", refTable: "Post", refColumns: ["id"], joinColumns: ["postId"], 'type: psql:ONE_TO_MANY}
            }
        },
        [EMPLOYEE]: {
            entityName: "Employee",
            tableName: "Employee",
            fieldMetadata: {
                empNo: {columnName: "empNo"},
                firstName: {columnName: "firstName"},
                lastName: {columnName: "lastName"},
                birthDate: {columnName: "birthDate"},
                gender: {columnName: "gender"},
                hireDate: {columnName: "hireDate"},
                departmentDeptNo: {columnName: "departmentDeptNo"},
                workspaceWorkspaceId: {columnName: "workspaceWorkspaceId"},
                "department.deptNo": {relation: {entityName: "department", refField: "deptNo"}},
                "department.deptName": {relation: {entityName: "department", refField: "deptName"}},
                "workspace.workspaceId": {relation: {entityName: "workspace", refField: "workspaceId"}},
                "workspace.workspaceType": {relation: {entityName: "workspace", refField: "workspaceType"}},
                "workspace.locationBuildingCode": {relation: {entityName: "workspace", refField: "locationBuildingCode"}}
            },
            keyFields: ["empNo"],
            joinMetadata: {
                department: {entity: Department, fieldName: "department", refTable: "Department", refColumns: ["deptNo"], joinColumns: ["departmentDeptNo"], 'type: psql:ONE_TO_MANY},
                workspace: {entity: Workspace, fieldName: "workspace", refTable: "Workspace", refColumns: ["workspaceId"], joinColumns: ["workspaceWorkspaceId"], 'type: psql:ONE_TO_MANY}
            }
        },
        [WORKSPACE]: {
            entityName: "Workspace",
            tableName: "Workspace",
            fieldMetadata: {
                workspaceId: {columnName: "workspaceId"},
                workspaceType: {columnName: "workspaceType"},
                locationBuildingCode: {columnName: "locationBuildingCode"},
                "location.buildingCode": {relation: {entityName: "location", refField: "buildingCode"}},
                "location.city": {relation: {entityName: "location", refField: "city"}},
                "location.state": {relation: {entityName: "location", refField: "state"}},
                "location.country": {relation: {entityName: "location", refField: "country"}},
                "location.postalCode": {relation: {entityName: "location", refField: "postalCode"}},
                "location.type": {relation: {entityName: "location", refField: "type"}},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo"}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName"}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName"}},
                "employees[].birthDate": {relation: {entityName: "employees", refField: "birthDate"}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender"}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate"}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo"}},
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId"}}
            },
            keyFields: ["workspaceId"],
            joinMetadata: {
                location: {entity: Building, fieldName: "location", refTable: "Building", refColumns: ["buildingCode"], joinColumns: ["locationBuildingCode"], 'type: psql:ONE_TO_MANY},
                employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["workspaceWorkspaceId"], joinColumns: ["workspaceId"], 'type: psql:MANY_TO_ONE}
            }
        },
        [BUILDING]: {
            entityName: "Building",
            tableName: "Building",
            fieldMetadata: {
                buildingCode: {columnName: "buildingCode"},
                city: {columnName: "city"},
                state: {columnName: "state"},
                country: {columnName: "country"},
                postalCode: {columnName: "postalCode"},
                'type: {columnName: "type"},
                "workspaces[].workspaceId": {relation: {entityName: "workspaces", refField: "workspaceId"}},
                "workspaces[].workspaceType": {relation: {entityName: "workspaces", refField: "workspaceType"}},
                "workspaces[].locationBuildingCode": {relation: {entityName: "workspaces", refField: "locationBuildingCode"}}
            },
            keyFields: ["buildingCode"],
            joinMetadata: {workspaces: {entity: Workspace, fieldName: "workspaces", refTable: "Workspace", refColumns: ["locationBuildingCode"], joinColumns: ["buildingCode"], 'type: psql:MANY_TO_ONE}}
        },
        [DEPARTMENT]: {
            entityName: "Department",
            tableName: "Department",
            fieldMetadata: {
                deptNo: {columnName: "deptNo"},
                deptName: {columnName: "deptName"},
                "employees[].empNo": {relation: {entityName: "employees", refField: "empNo"}},
                "employees[].firstName": {relation: {entityName: "employees", refField: "firstName"}},
                "employees[].lastName": {relation: {entityName: "employees", refField: "lastName"}},
                "employees[].birthDate": {relation: {entityName: "employees", refField: "birthDate"}},
                "employees[].gender": {relation: {entityName: "employees", refField: "gender"}},
                "employees[].hireDate": {relation: {entityName: "employees", refField: "hireDate"}},
                "employees[].departmentDeptNo": {relation: {entityName: "employees", refField: "departmentDeptNo"}},
                "employees[].workspaceWorkspaceId": {relation: {entityName: "employees", refField: "workspaceWorkspaceId"}}
            },
            keyFields: ["deptNo"],
            joinMetadata: {employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["departmentDeptNo"], joinColumns: ["deptNo"], 'type: psql:MANY_TO_ONE}}
        },
        [ORDER_ITEM]: {
            entityName: "OrderItem",
            tableName: "OrderItem",
            fieldMetadata: {
                orderId: {columnName: "orderId"},
                itemId: {columnName: "itemId"},
                quantity: {columnName: "quantity"},
                notes: {columnName: "notes"}
            },
            keyFields: ["orderId", "itemId"]
        }
    };

    public isolated function init() returns persist:Error? {
        postgresql:Client|error dbClient = new (host = host, username = user, password = password, database = database, port = port, options = connectionOptions);
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
                    if joinMetadataMap != () {
                        foreach string joinKey in joinMetadataMap.keys() {
                            psql:JoinMetadata joinMetadata = joinMetadataMap.get(joinKey);
                            if joinMetadata.refSchema == () {
                                joinMetadata.refSchema = defaultSchema;
                            }
                        }
                    }
                }
            }
        }
        self.persistClients = {
            [ALL_TYPES]: check new (dbClient, self.metadata.get(ALL_TYPES).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [STRING_ID_RECORD]: check new (dbClient, self.metadata.get(STRING_ID_RECORD).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [INT_ID_RECORD]: check new (dbClient, self.metadata.get(INT_ID_RECORD).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [FLOAT_ID_RECORD]: check new (dbClient, self.metadata.get(FLOAT_ID_RECORD).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [DECIMAL_ID_RECORD]: check new (dbClient, self.metadata.get(DECIMAL_ID_RECORD).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [BOOLEAN_ID_RECORD]: check new (dbClient, self.metadata.get(BOOLEAN_ID_RECORD).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [COMPOSITE_ASSOCIATION_RECORD]: check new (dbClient, self.metadata.get(COMPOSITE_ASSOCIATION_RECORD).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [ALL_TYPES_ID_RECORD]: check new (dbClient, self.metadata.get(ALL_TYPES_ID_RECORD).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [USER]: check new (dbClient, self.metadata.get(USER).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [POST]: check new (dbClient, self.metadata.get(POST).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [FOLLOW]: check new (dbClient, self.metadata.get(FOLLOW).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [COMMENT]: check new (dbClient, self.metadata.get(COMMENT).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [EMPLOYEE]: check new (dbClient, self.metadata.get(EMPLOYEE).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [WORKSPACE]: check new (dbClient, self.metadata.get(WORKSPACE).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [BUILDING]: check new (dbClient, self.metadata.get(BUILDING).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [DEPARTMENT]: check new (dbClient, self.metadata.get(DEPARTMENT).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS),
            [ORDER_ITEM]: check new (dbClient, self.metadata.get(ORDER_ITEM).cloneReadOnly(), psql:POSTGRESQL_SPECIFICS)
        };
    }

    isolated resource function get alltypes(AllTypesTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get alltypes/[int id](AllTypesTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post alltypes(AllTypesInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ALL_TYPES);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from AllTypesInsert inserted in data
            select inserted.id;
    }

    isolated resource function put alltypes/[int id](AllTypesUpdate value) returns AllTypes|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ALL_TYPES);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/alltypes/[id].get();
    }

    isolated resource function delete alltypes/[int id]() returns AllTypes|persist:Error {
        AllTypes result = check self->/alltypes/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ALL_TYPES);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get stringidrecords(StringIdRecordTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get stringidrecords/[string id](StringIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post stringidrecords(StringIdRecordInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(STRING_ID_RECORD);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from StringIdRecordInsert inserted in data
            select inserted.id;
    }

    isolated resource function put stringidrecords/[string id](StringIdRecordUpdate value) returns StringIdRecord|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(STRING_ID_RECORD);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/stringidrecords/[id].get();
    }

    isolated resource function delete stringidrecords/[string id]() returns StringIdRecord|persist:Error {
        StringIdRecord result = check self->/stringidrecords/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(STRING_ID_RECORD);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get intidrecords(IntIdRecordTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get intidrecords/[int id](IntIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post intidrecords(IntIdRecordInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(INT_ID_RECORD);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from IntIdRecordInsert inserted in data
            select inserted.id;
    }

    isolated resource function put intidrecords/[int id](IntIdRecordUpdate value) returns IntIdRecord|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(INT_ID_RECORD);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/intidrecords/[id].get();
    }

    isolated resource function delete intidrecords/[int id]() returns IntIdRecord|persist:Error {
        IntIdRecord result = check self->/intidrecords/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(INT_ID_RECORD);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get floatidrecords(FloatIdRecordTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get floatidrecords/[float id](FloatIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post floatidrecords(FloatIdRecordInsert[] data) returns float[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FLOAT_ID_RECORD);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from FloatIdRecordInsert inserted in data
            select inserted.id;
    }

    isolated resource function put floatidrecords/[float id](FloatIdRecordUpdate value) returns FloatIdRecord|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FLOAT_ID_RECORD);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/floatidrecords/[id].get();
    }

    isolated resource function delete floatidrecords/[float id]() returns FloatIdRecord|persist:Error {
        FloatIdRecord result = check self->/floatidrecords/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FLOAT_ID_RECORD);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get decimalidrecords(DecimalIdRecordTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get decimalidrecords/[decimal id](DecimalIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post decimalidrecords(DecimalIdRecordInsert[] data) returns decimal[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DECIMAL_ID_RECORD);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from DecimalIdRecordInsert inserted in data
            select inserted.id;
    }

    isolated resource function put decimalidrecords/[decimal id](DecimalIdRecordUpdate value) returns DecimalIdRecord|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DECIMAL_ID_RECORD);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/decimalidrecords/[id].get();
    }

    isolated resource function delete decimalidrecords/[decimal id]() returns DecimalIdRecord|persist:Error {
        DecimalIdRecord result = check self->/decimalidrecords/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DECIMAL_ID_RECORD);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get booleanidrecords(BooleanIdRecordTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get booleanidrecords/[boolean id](BooleanIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post booleanidrecords(BooleanIdRecordInsert[] data) returns boolean[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOLEAN_ID_RECORD);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from BooleanIdRecordInsert inserted in data
            select inserted.id;
    }

    isolated resource function put booleanidrecords/[boolean id](BooleanIdRecordUpdate value) returns BooleanIdRecord|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOLEAN_ID_RECORD);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/booleanidrecords/[id].get();
    }

    isolated resource function delete booleanidrecords/[boolean id]() returns BooleanIdRecord|persist:Error {
        BooleanIdRecord result = check self->/booleanidrecords/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BOOLEAN_ID_RECORD);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get compositeassociationrecords(CompositeAssociationRecordTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get compositeassociationrecords/[string id](CompositeAssociationRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post compositeassociationrecords(CompositeAssociationRecordInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMPOSITE_ASSOCIATION_RECORD);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from CompositeAssociationRecordInsert inserted in data
            select inserted.id;
    }

    isolated resource function put compositeassociationrecords/[string id](CompositeAssociationRecordUpdate value) returns CompositeAssociationRecord|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMPOSITE_ASSOCIATION_RECORD);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/compositeassociationrecords/[id].get();
    }

    isolated resource function delete compositeassociationrecords/[string id]() returns CompositeAssociationRecord|persist:Error {
        CompositeAssociationRecord result = check self->/compositeassociationrecords/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMPOSITE_ASSOCIATION_RECORD);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get alltypesidrecords(AllTypesIdRecordTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get alltypesidrecords/[boolean booleanType]/[int intType]/[float floatType]/[decimal decimalType]/[string stringType](AllTypesIdRecordTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post alltypesidrecords(AllTypesIdRecordInsert[] data) returns [boolean, int, float, decimal, string][]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ALL_TYPES_ID_RECORD);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from AllTypesIdRecordInsert inserted in data
            select [inserted.booleanType, inserted.intType, inserted.floatType, inserted.decimalType, inserted.stringType];
    }

    isolated resource function put alltypesidrecords/[boolean booleanType]/[int intType]/[float floatType]/[decimal decimalType]/[string stringType](AllTypesIdRecordUpdate value) returns AllTypesIdRecord|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ALL_TYPES_ID_RECORD);
        }
        _ = check sqlClient.runUpdateQuery({"booleanType": booleanType, "intType": intType, "floatType": floatType, "decimalType": decimalType, "stringType": stringType}, value);
        return self->/alltypesidrecords/[booleanType]/[intType]/[floatType]/[decimalType]/[stringType].get();
    }

    isolated resource function delete alltypesidrecords/[boolean booleanType]/[int intType]/[float floatType]/[decimal decimalType]/[string stringType]() returns AllTypesIdRecord|persist:Error {
        AllTypesIdRecord result = check self->/alltypesidrecords/[booleanType]/[intType]/[floatType]/[decimalType]/[stringType].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ALL_TYPES_ID_RECORD);
        }
        _ = check sqlClient.runDeleteQuery({"booleanType": booleanType, "intType": intType, "floatType": floatType, "decimalType": decimalType, "stringType": stringType});
        return result;
    }

    isolated resource function get users(UserTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post users(UserInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    isolated resource function put users/[int id](UserUpdate value) returns User|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/users/[id].get();
    }

    isolated resource function delete users/[int id]() returns User|persist:Error {
        User result = check self->/users/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(USER);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get posts(PostTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get posts/[int id](PostTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post posts(PostInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from PostInsert inserted in data
            select inserted.id;
    }

    isolated resource function put posts/[int id](PostUpdate value) returns Post|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/posts/[id].get();
    }

    isolated resource function delete posts/[int id]() returns Post|persist:Error {
        Post result = check self->/posts/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(POST);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get follows(FollowTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get follows/[int id](FollowTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post follows(FollowInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from FollowInsert inserted in data
            select inserted.id;
    }

    isolated resource function put follows/[int id](FollowUpdate value) returns Follow|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/follows/[id].get();
    }

    isolated resource function delete follows/[int id]() returns Follow|persist:Error {
        Follow result = check self->/follows/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(FOLLOW);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get comments(CommentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get comments/[int id](CommentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post comments(CommentInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from CommentInsert inserted in data
            select inserted.id;
    }

    isolated resource function put comments/[int id](CommentUpdate value) returns Comment|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMMENT);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/comments/[id].get();
    }

    isolated resource function delete comments/[int id]() returns Comment|persist:Error {
        Comment result = check self->/comments/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(COMMENT);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get employees(EmployeeTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get employees/[string empNo](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post employees(EmployeeInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.empNo;
    }

    isolated resource function put employees/[string empNo](EmployeeUpdate value) returns Employee|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runUpdateQuery(empNo, value);
        return self->/employees/[empNo].get();
    }

    isolated resource function delete employees/[string empNo]() returns Employee|persist:Error {
        Employee result = check self->/employees/[empNo].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(EMPLOYEE);
        }
        _ = check sqlClient.runDeleteQuery(empNo);
        return result;
    }

    isolated resource function get workspaces(WorkspaceTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get workspaces/[string workspaceId](WorkspaceTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post workspaces(WorkspaceInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from WorkspaceInsert inserted in data
            select inserted.workspaceId;
    }

    isolated resource function put workspaces/[string workspaceId](WorkspaceUpdate value) returns Workspace|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runUpdateQuery(workspaceId, value);
        return self->/workspaces/[workspaceId].get();
    }

    isolated resource function delete workspaces/[string workspaceId]() returns Workspace|persist:Error {
        Workspace result = check self->/workspaces/[workspaceId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(WORKSPACE);
        }
        _ = check sqlClient.runDeleteQuery(workspaceId);
        return result;
    }

    isolated resource function get buildings(BuildingTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get buildings/[string buildingCode](BuildingTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post buildings(BuildingInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from BuildingInsert inserted in data
            select inserted.buildingCode;
    }

    isolated resource function put buildings/[string buildingCode](BuildingUpdate value) returns Building|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runUpdateQuery(buildingCode, value);
        return self->/buildings/[buildingCode].get();
    }

    isolated resource function delete buildings/[string buildingCode]() returns Building|persist:Error {
        Building result = check self->/buildings/[buildingCode].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(BUILDING);
        }
        _ = check sqlClient.runDeleteQuery(buildingCode);
        return result;
    }

    isolated resource function get departments(DepartmentTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get departments/[string deptNo](DepartmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post departments(DepartmentInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from DepartmentInsert inserted in data
            select inserted.deptNo;
    }

    isolated resource function put departments/[string deptNo](DepartmentUpdate value) returns Department|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runUpdateQuery(deptNo, value);
        return self->/departments/[deptNo].get();
    }

    isolated resource function delete departments/[string deptNo]() returns Department|persist:Error {
        Department result = check self->/departments/[deptNo].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(DEPARTMENT);
        }
        _ = check sqlClient.runDeleteQuery(deptNo);
        return result;
    }

    isolated resource function get orderitems(OrderItemTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "query"
    } external;

    isolated resource function get orderitems/[string orderId]/[string itemId](OrderItemTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post orderitems(OrderItemInsert[] data) returns [string, string][]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from OrderItemInsert inserted in data
            select [inserted.orderId, inserted.itemId];
    }

    isolated resource function put orderitems/[string orderId]/[string itemId](OrderItemUpdate value) returns OrderItem|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runUpdateQuery({"orderId": orderId, "itemId": itemId}, value);
        return self->/orderitems/[orderId]/[itemId].get();
    }

    isolated resource function delete orderitems/[string orderId]/[string itemId]() returns OrderItem|persist:Error {
        OrderItem result = check self->/orderitems/[orderId]/[itemId].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(ORDER_ITEM);
        }
        _ = check sqlClient.runDeleteQuery({"orderId": orderId, "itemId": itemId});
        return result;
    }

    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor"
    } external;

    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.PostgreSQLProcessor"
    } external;

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

