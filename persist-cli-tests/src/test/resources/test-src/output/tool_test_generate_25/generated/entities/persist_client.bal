// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/jballerina.java;
import ballerinax/mysql;

const COMPANY = "companies";
const EMPLOYEE = "employees";
const VEHICLE = "vehicles";

public client class Client {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        [COMPANY] : {
            entityName: "Company",
            tableName: `Company`,
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                "employee[].id": {relation: {entityName: "employee", refField: "id"}},
                "employee[].name": {relation: {entityName: "employee", refField: "name"}},
                "employee[].companyId": {relation: {entityName: "employee", refField: "companyId"}}
            },
            keyFields: ["id"],
            joinMetadata: {employee: {entity: Employee, fieldName: "employee", refTable: "Employee", refColumns: ["companyId"], joinColumns: ["id"], 'type: persist:MANY_TO_ONE}}
        },
        [EMPLOYEE] : {
            entityName: "Employee",
            tableName: `Employee`,
            fieldMetadata: {
                id: {columnName: "id"},
                name: {columnName: "name"},
                companyId: {columnName: "companyId"},
                "company.id": {relation: {entityName: "company", refField: "id"}},
                "company.name": {relation: {entityName: "company", refField: "name"}},
                "vehicles[].model": {relation: {entityName: "vehicles", refField: "model"}},
                "vehicles[].name": {relation: {entityName: "vehicles", refField: "name"}},
                "vehicles[].employeeId": {relation: {entityName: "vehicles", refField: "employeeId"}}
            },
            keyFields: ["id"],
            joinMetadata: {
                company: {entity: Company, fieldName: "company", refTable: "Company", refColumns: ["id"], joinColumns: ["companyId"], 'type: persist:ONE_TO_MANY},
                vehicles: {entity: Vehicle, fieldName: "vehicles", refTable: "Vehicle", refColumns: ["employeeId"], joinColumns: ["id"], 'type: persist:MANY_TO_ONE}
            }
        },
        [VEHICLE] : {
            entityName: "Vehicle",
            tableName: `Vehicle`,
            fieldMetadata: {
                model: {columnName: "model"},
                name: {columnName: "name"},
                employeeId: {columnName: "employeeId"},
                "employee.id": {relation: {entityName: "employee", refField: "id"}},
                "employee.name": {relation: {entityName: "employee", refField: "name"}},
                "employee.companyId": {relation: {entityName: "employee", refField: "companyId"}}
            },
            keyFields: ["model"],
            joinMetadata: {employee: {entity: Employee, fieldName: "employee", refTable: "Employee", refColumns: ["id"], joinColumns: ["employeeId"], 'type: persist:ONE_TO_MANY}}
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            [COMPANY] : check new (self.dbClient, self.metadata.get(COMPANY)),
            [EMPLOYEE] : check new (self.dbClient, self.metadata.get(EMPLOYEE)),
            [VEHICLE] : check new (self.dbClient, self.metadata.get(VEHICLE))
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

    isolated resource function get vehicles(VehicleTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "query"
    } external;

    isolated resource function get vehicles/[int model](VehicleTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.datastore.MySQLProcessor",
        name: "queryOne"
    } external;

    isolated resource function post vehicles(VehicleInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(VEHICLE).runBatchInsertQuery(data);
        return from VehicleInsert inserted in data
            select inserted.model;
    }

    isolated resource function put vehicles/[int model](VehicleUpdate value) returns Vehicle|persist:Error {
        _ = check self.persistClients.get(VEHICLE).runUpdateQuery(model, value);
        return self->/vehicles/[model].get();
    }

    isolated resource function delete vehicles/[int model]() returns Vehicle|persist:Error {
        Vehicle result = check self->/vehicles/[model].get();
        _ = check self.persistClients.get(VEHICLE).runDeleteQuery(model);
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
