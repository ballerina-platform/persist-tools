// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

const COMPANY = "companies";
const EMPLOYEE = "employees";

public client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:SQLMetadata...;|} metadata = {
        [COMPANY] : {
            entityName: "Company",
            tableName: `Company`,
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                "employees[].id": {relation: {entityName: "employees", refField: "id"}},
                "employees[].name": {relation: {entityName: "employees", refField: "name"}},
                "employees[].companyId": {relation: {entityName: "employees", refField: "companyId"}}
            },
            keyFields: ["id"],
            joinMetadata: {employees: {entity: Employee, fieldName: "employees", refTable: "Employee", refColumns: ["companyId"], joinColumns: ["id"], 'type: persist:MANY_TO_ONE}}
        },
        [EMPLOYEE] : {
            entityName: "Employee",
            tableName: `Employee`,
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                companyId: {columnName: "companyId"},
                "company.id": {relation: {entityName: "company", refField: "id"}},
                "company.name": {relation: {entityName: "company", refField: "name"}}
            },
            keyFields: ["id"],
            joinMetadata: {company: {entity: Company, fieldName: "company", refTable: "Company", refColumns: ["id"], joinColumns: ["companyId"], 'type: persist:ONE_TO_MANY}}
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port, options = connectionOptions);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [COMPANY] : check new (self.dbClient, self.metadata.get(COMPANY)),
            [EMPLOYEE] : check new (self.dbClient, self.metadata.get(EMPLOYEE))
        };
    }

    isolated resource function get companies(CompanyTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get companies/[int id](CompanyTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post companies(CompanyInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(COMPANY).runBatchInsertQuery(data);
        return from CompanyInsert inserted in data
            select inserted.id;
    }

    isolated resource function put companies/[int id](CompanyUpdate value) returns Company|persist:Error {
        _ = check self.persistClients.get(COMPANY).runUpdateQuery(id, value);
        return self->/companies/[id].get();
    }

    isolated resource function delete companies/[int id]() returns Company|persist:Error {
        Company result = check self->/companies/[id].get();
        _ = check self.persistClients.get(COMPANY).runDeleteQuery(id);
        return result;
    }

    isolated resource function get employees(EmployeeTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get employees/[int id](EmployeeTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post employees(EmployeeInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(EMPLOYEE).runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.id;
    }

    isolated resource function put employees/[int id](EmployeeUpdate value) returns Employee|persist:Error {
        _ = check self.persistClients.get(EMPLOYEE).runUpdateQuery(id, value);
        return self->/employees/[id].get();
    }

    isolated resource function delete employees/[int id]() returns Employee|persist:Error {
        Employee result = check self->/employees/[id].get();
        _ = check self.persistClients.get(EMPLOYEE).runDeleteQuery(id);
        return result;
    }

    public function close() returns persist:Error? {
        error? result = self.dbClient.close();
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }
}

