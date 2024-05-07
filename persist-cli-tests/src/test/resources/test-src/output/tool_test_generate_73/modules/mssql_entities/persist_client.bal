// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerina/sql;
import ballerinax/mssql;
import ballerinax/mssql.driver as _;
import ballerinax/persist.sql as psql;

const USER = "users";
const CAR = "cars";
const PERSON = "people";
const PERSON2 = "person2s";

public isolated client class Client {
    *persist:AbstractPersistClient;

    private final mssql:Client dbClient;

    private final map<psql:SQLClient> persistClients;

    private final record {|psql:SQLMetadata...;|} & readonly metadata = {
        [USER]: {
            entityName: "User",
            tableName: "User",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                gender: {columnName: "gender"},
                nic: {columnName: "nic"},
                salary: {columnName: "salary"},
                "cars[].id": {relation: {entityName: "cars", refField: "id"}},
                "cars[].name": {relation: {entityName: "cars", refField: "name"}},
                "cars[].model": {relation: {entityName: "cars", refField: "model", refColumn: "MODEL"}},
                "cars[].ownerId": {relation: {entityName: "cars", refField: "ownerId"}}
            },
            keyFields: ["id"],
            joinMetadata: {cars: {entity: Car, fieldName: "cars", refTable: "cars", refColumns: ["ownerId"], joinColumns: ["id"], 'type: psql:MANY_TO_ONE}}
        },
        [CAR]: {
            entityName: "Car",
            tableName: "cars",
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                model: {columnName: "MODEL"},
                ownerId: {columnName: "ownerId"},
                "owner.id": {relation: {entityName: "owner", refField: "id"}},
                "owner.name": {relation: {entityName: "owner", refField: "name"}},
                "owner.gender": {relation: {entityName: "owner", refField: "gender"}},
                "owner.nic": {relation: {entityName: "owner", refField: "nic"}},
                "owner.salary": {relation: {entityName: "owner", refField: "salary"}}
            },
            keyFields: ["id"],
            joinMetadata: {owner: {entity: User, fieldName: "owner", refTable: "User", refColumns: ["id"], joinColumns: ["ownerId"], 'type: psql:ONE_TO_MANY}}
        },
        [PERSON]: {
            entityName: "Person",
            tableName: "Person",
            fieldMetadata: {
                name: {columnName: "name"},
                age: {columnName: "age"},
                nic: {columnName: "nic"},
                salary: {columnName: "salary"}
            },
            keyFields: ["name"]
        },
        [PERSON2]: {
            entityName: "Person2",
            tableName: "people2",
            fieldMetadata: {
                name: {columnName: "name"},
                age: {columnName: "age"},
                nic: {columnName: "nic"},
                salary: {columnName: "salary"}
            },
            keyFields: ["name"]
        }
    };

    public isolated function init() returns persist:Error? {
        mssql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [USER]: check new (dbClient, self.metadata.get(USER), psql:MSSQL_SPECIFICS),
            [CAR]: check new (dbClient, self.metadata.get(CAR), psql:MSSQL_SPECIFICS),
            [PERSON]: check new (dbClient, self.metadata.get(PERSON), psql:MSSQL_SPECIFICS),
            [PERSON2]: check new (dbClient, self.metadata.get(PERSON2), psql:MSSQL_SPECIFICS)
        };
    }

    isolated resource function get users(UserTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    isolated resource function get users/[int id](UserTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
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

    isolated resource function get cars(CarTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    isolated resource function get cars/[int id](CarTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post cars(CarInsert[] data) returns int[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from CarInsert inserted in data
            select inserted.id;
    }

    isolated resource function put cars/[int id](CarUpdate value) returns Car|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR);
        }
        _ = check sqlClient.runUpdateQuery(id, value);
        return self->/cars/[id].get();
    }

    isolated resource function delete cars/[int id]() returns Car|persist:Error {
        Car result = check self->/cars/[id].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(CAR);
        }
        _ = check sqlClient.runDeleteQuery(id);
        return result;
    }

    isolated resource function get people(PersonTargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    isolated resource function get people/[string name](PersonTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post people(PersonInsert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PERSON);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from PersonInsert inserted in data
            select inserted.name;
    }

    isolated resource function put people/[string name](PersonUpdate value) returns Person|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PERSON);
        }
        _ = check sqlClient.runUpdateQuery(name, value);
        return self->/people/[name].get();
    }

    isolated resource function delete people/[string name]() returns Person|persist:Error {
        Person result = check self->/people/[name].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PERSON);
        }
        _ = check sqlClient.runDeleteQuery(name);
        return result;
    }

    isolated resource function get person2s(Person2TargetType targetType = <>, sql:ParameterizedQuery whereClause = ``, sql:ParameterizedQuery orderByClause = ``, sql:ParameterizedQuery limitClause = ``, sql:ParameterizedQuery groupByClause = ``) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "query"
    } external;

    isolated resource function get person2s/[string name](Person2TargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post person2s(Person2Insert[] data) returns string[]|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PERSON2);
        }
        _ = check sqlClient.runBatchInsertQuery(data);
        return from Person2Insert inserted in data
            select inserted.name;
    }

    isolated resource function put person2s/[string name](Person2Update value) returns Person2|persist:Error {
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PERSON2);
        }
        _ = check sqlClient.runUpdateQuery(name, value);
        return self->/person2s/[name].get();
    }

    isolated resource function delete person2s/[string name]() returns Person2|persist:Error {
        Person2 result = check self->/person2s/[name].get();
        psql:SQLClient sqlClient;
        lock {
            sqlClient = self.persistClients.get(PERSON2);
        }
        _ = check sqlClient.runDeleteQuery(name);
        return result;
    }

    remote isolated function queryNativeSQL(sql:ParameterizedQuery sqlQuery, typedesc<record {}> rowType = <>) returns stream<rowType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    remote isolated function executeNativeSQL(sql:ParameterizedQuery sqlQuery) returns psql:ExecutionResult|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.sql.datastore.MSSQLProcessor"
    } external;

    public isolated function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

