import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;

public client class EmployeeClient {
    *persist:AbstractPersistClient;

    private final string entityName = "Employee";
    private final sql:ParameterizedQuery tableName = `Employee`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        "company.id": {columnName: "companyId", 'type: int, relation: {entityName: "company", refTable: "Company", refField: "id"}},
        "company.name": {'type: string, relation: {entityName: "company", refTable: "Company", refField: "name"}},
        "vehicles[].model": {'type: int, relation: {entityName: "vehicles", refTable: "Vehicle", refField: "model"}},
        "vehicles[].name": {'type: string, relation: {entityName: "vehicles", refTable: "Vehicle", refField: "name"}}
    };
    private string[] keyFields = ["id"];

    private final map<persist:JoinMetadata> joinMetadata = {
        company: {entity: Company, fieldName: "company", refTable: "Company", refFields: ["id"], joinColumns: ["companyId"]},
        vehicles: {entity: Vehicle, fieldName: "vehicles", refTable: "Vehicle", refFields: ["employeeId"], joinColumns: ["id"], 'type: persist:MANY}
    };

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(Employee value) returns Employee|persist:Error {
        if value.company is Company {
            CompanyClient companyClient = check new CompanyClient();
            boolean exists = check companyClient->exists(<Company>value.company);
            if !exists {
                value.company = check companyClient->create(<Company>value.company);
            }
        }
        _ = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, EmployeeRelations[] include = []) returns Employee|persist:Error {
        return <Employee>check self.persistClient.runReadByKeyQuery(Employee, key, include);
    }

    remote function read(EmployeeRelations[] include = []) returns stream<Employee, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(Employee, include);
        if result is persist:Error {
            return new stream<Employee, persist:Error?>(new EmployeeStream((), result));
        } else {
            return new stream<Employee, persist:Error?>(new EmployeeStream(result));
        }
    }

    remote function update(Employee value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
        if value.company is record {} {
            Company companyEntity = <Company>value.company;
            CompanyClient companyClient = check new CompanyClient();
            check companyClient->update(companyEntity);
        }
    }

    remote function delete(Employee value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Employee employee) returns boolean|persist:Error {
        Employee|persist:Error result = self->readByKey(employee.id);
        if result is Employee {
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

public enum EmployeeRelations {
    CompanyEntity = "company", VehicleEntity = "vehicles"
}

public class EmployeeStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;
    private EmployeeRelations[]? include;
    private persist:SQLClient? persistClient;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = (), EmployeeRelations[]? include = (), persist:SQLClient? persistClient = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
        self.include = include;
        self.persistClient = persistClient;
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
                record {|Employee value;|} nextRecord = {value: <Employee>streamValue.value};
                check (<persist:SQLClient>self.persistClient).getManyRelations(nextRecord.value, <EmployeeRelations[]>self.include);
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