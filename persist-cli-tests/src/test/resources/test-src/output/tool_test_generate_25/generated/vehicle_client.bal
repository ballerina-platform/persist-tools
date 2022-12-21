// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for Vehicle.
// It should not be modified by hand.

import ballerinax/mysql;
import ballerina/persist;
import ballerina/sql;

public client class VehicleClient {
    *persist:AbstractPersistClient;

    private final string entityName = "Vehicle";
    private final sql:ParameterizedQuery tableName = `Vehicle`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        model: {columnName: "model", 'type: int},
        name: {columnName: "name", 'type: string},
        "employee.id": {columnName: "employeeId", 'type: int, relation: {entityName: "employee", refTable: "Employee", refField: "id"}},
        "employee.name": {'type: string, relation: {entityName: "employee", refTable: "Employee", refField: "name"}}
    };
    private string[] keyFields = ["model"];

    private final map<persist:JoinMetadata> joinMetadata = {employee: {entity: Employee, fieldName: "employee", refTable: "Employee", refFields: ["id"], joinColumns: ["employeeId"]}};

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(Vehicle value) returns Vehicle|persist:Error {
        if value.employee is Employee {
            EmployeeClient employeeClient = check new EmployeeClient();
            boolean exists = check employeeClient->exists(<Employee>value.employee);
            if !exists {
                value.employee = check employeeClient->create(<Employee>value.employee);
            }
        }
        _ = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, VehicleRelations[] include = []) returns Vehicle|persist:Error {
        return <Vehicle>check self.persistClient.runReadByKeyQuery(Vehicle, key, include);
    }

    remote function read(VehicleRelations[] include = []) returns stream<Vehicle, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(Vehicle, include);
        if result is persist:Error {
            return new stream<Vehicle, persist:Error?>(new VehicleStream((), result));
        } else {
            return new stream<Vehicle, persist:Error?>(new VehicleStream(result));
        }
    }

    remote function update(Vehicle value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
        if value.employee is record {} {
            Employee employeeEntity = <Employee>value.employee;
            EmployeeClient employeeClient = check new EmployeeClient();
            check employeeClient->update(employeeEntity);
        }
    }

    remote function delete(Vehicle value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Vehicle vehicle) returns boolean|persist:Error {
        Vehicle|persist:Error result = self->readByKey(vehicle.model);
        if result is Vehicle {
            return true;
        } else if result is persist:InvalidKeyError {
            return false;
        } else {
            return result;
        }
    }

    public function close() returns persist:Error? {
        return self.persistClient.close();
    }
}

public enum VehicleRelations {
    EmployeeEntity = "employee"
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
                record {|Vehicle value;|} nextRecord = {value: <Vehicle>streamValue.value};
                return nextRecord;
            }
        } else {
            return ();
        }
    }

    public isolated function close() returns persist:Error? {
        if self.anydataStream is stream<anydata, sql:Error?> {
            var anydataStream = <stream<anydata, sql:Error?>>self.anydataStream;
            sql:Error? e = anydataStream.close();
            if e is sql:Error {
                return <persist:Error>error(e.message());
            }
        }
    }
}
