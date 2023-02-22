// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

const COMPANIES = "companies";
const EMPLOYEES = "employees";
const VEHICLES = "vehicles";

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "companies": {
            entityName: "Company",
            tableName: `Company`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string}
            },
            keyFields: ["id"]
        },
        "employees": {
            entityName: "Employee",
            tableName: `Employee`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string},
                companyId: {columnName: "companyId", 'type: int}
            },
            keyFields: ["id"]
        },
        "vehicles": {
            entityName: "Vehicle",
            tableName: `Vehicle`,
            fieldMetadata: {
                model: {columnName: "model", 'type: int},
                name: {columnName: "name", 'type: string},
                employeeId: {columnName: "employeeId", 'type: int}
            },
            keyFields: ["model"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            companies: check new (self.dbClient, self.metadata.get(COMPANIES)),
            employees: check new (self.dbClient, self.metadata.get(EMPLOYEES)),
            vehicles: check new (self.dbClient, self.metadata.get(VEHICLES))
        };
    }

    isolated resource function get companies() returns stream<Company, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(COMPANIES).runReadQuery(Company);
        if result is persist:Error {
            return new stream<Company, persist:Error?>(new CompanyStream((), result));
        } else {
            return new stream<Company, persist:Error?>(new CompanyStream(result));
        }
    }

    isolated resource function get companies/[int id]() returns Company|persist:Error {
        Company|error result = (check self.persistClients.get(COMPANIES).runReadByKeyQuery(Company, id)).cloneWithType(Company);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post companies(CompanyInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(COMPANIES).runBatchInsertQuery(data);
        return from CompanyInsert inserted in data
            select inserted.id;
    }

    isolated resource function put companies/[int id](CompanyUpdate value) returns Company|persist:Error {
        _ = check self.persistClients.get(COMPANIES).runUpdateQuery(id, value);
        return self->/companies/[id].get();
    }

    isolated resource function delete companies/[int id]() returns Company|persist:Error {
        Company result = check self->/companies/[id].get();
        _ = check self.persistClients.get(COMPANIES).runDeleteQuery(id);
        return result;
    }

    isolated resource function get employees() returns stream<Employee, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(EMPLOYEES).runReadQuery(Employee);
        if result is persist:Error {
            return new stream<Employee, persist:Error?>(new EmployeeStream((), result));
        } else {
            return new stream<Employee, persist:Error?>(new EmployeeStream(result));
        }
    }

    isolated resource function get employees/[int id]() returns Employee|persist:Error {
        Employee|error result = (check self.persistClients.get(EMPLOYEES).runReadByKeyQuery(Employee, id)).cloneWithType(Employee);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post employees(EmployeeInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(EMPLOYEES).runBatchInsertQuery(data);
        return from EmployeeInsert inserted in data
            select inserted.id;
    }

    isolated resource function put employees/[int id](EmployeeUpdate value) returns Employee|persist:Error {
        _ = check self.persistClients.get(EMPLOYEES).runUpdateQuery(id, value);
        return self->/employees/[id].get();
    }

    isolated resource function delete employees/[int id]() returns Employee|persist:Error {
        Employee result = check self->/employees/[id].get();
        _ = check self.persistClients.get(EMPLOYEES).runDeleteQuery(id);
        return result;
    }

    isolated resource function get vehicles() returns stream<Vehicle, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(VEHICLES).runReadQuery(Vehicle);
        if result is persist:Error {
            return new stream<Vehicle, persist:Error?>(new VehicleStream((), result));
        } else {
            return new stream<Vehicle, persist:Error?>(new VehicleStream(result));
        }
    }

    isolated resource function get vehicles/[int model]() returns Vehicle|persist:Error {
        Vehicle|error result = (check self.persistClients.get(VEHICLES).runReadByKeyQuery(Vehicle, model)).cloneWithType(Vehicle);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post vehicles(VehicleInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(VEHICLES).runBatchInsertQuery(data);
        return from VehicleInsert inserted in data
            select inserted.model;
    }

    isolated resource function put vehicles/[int model](VehicleUpdate value) returns Vehicle|persist:Error {
        _ = check self.persistClients.get(VEHICLES).runUpdateQuery(model, value);
        return self->/vehicles/[model].get();
    }

    isolated resource function delete vehicles/[int model]() returns Vehicle|persist:Error {
        Vehicle result = check self->/vehicles/[model].get();
        _ = check self.persistClients.get(VEHICLES).runDeleteQuery(model);
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

public class CompanyStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Company value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                Company|error value = streamValue.value.cloneWithType(Company);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Company value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

public class EmployeeStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Employee value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                Employee|error value = streamValue.value.cloneWithType(Employee);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Employee value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

public class VehicleStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Vehicle value;|}|persist:Error? {
        if self.err is persist:Error {
            return <persist:Error>self.err;
        } else if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            var streamValue = anydataStream.next();
            if streamValue is () {
                return streamValue;
            } else if (streamValue is sql:Error) {
                return <persist:Error>error(streamValue.message());
            } else {
                Vehicle|error value = streamValue.value.cloneWithType(Vehicle);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Vehicle value;|} nextRecord = {value: value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        check persist:closeEntityStream(self.anydataStream);
    }
}

