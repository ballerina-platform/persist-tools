// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for Company.
// It should not be modified by hand.

import ballerinax/mysql;
import ballerina/persist;
import ballerina/sql;

public client class CompanyClient {
    *persist:AbstractPersistClient;

    private final string entityName = "Company";
    private final sql:ParameterizedQuery tableName = `Company`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        "employee[].id": {'type: int, relation: {entityName: "employee", refTable: "Employee", refField: "id"}},
        "employee[].name": {'type: string, relation: {entityName: "employee", refTable: "Employee", refField: "name"}}
    };
    private string[] keyFields = ["id"];

    private final map<persist:JoinMetadata> joinMetadata = {employee: {entity: Employee, fieldName: "employee", refTable: "Employee", refFields: ["companyId"], joinColumns: ["id"], 'type: persist:MANY}};

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(Company value) returns Company|persist:Error {
        _ = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, CompanyRelations[] include = []) returns Company|persist:Error {
        return <Company>check self.persistClient.runReadByKeyQuery(Company, key, include);
    }

    remote function read(CompanyRelations[] include = []) returns stream<Company, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(Company, include);
        if result is persist:Error {
            return new stream<Company, persist:Error?>(new CompanyStream((), result));
        } else {
            return new stream<Company, persist:Error?>(new CompanyStream(result));
        }
    }

    remote function update(Company value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
    }

    remote function delete(Company value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Company company) returns boolean|persist:Error {
        Company|persist:Error result = self->readByKey(company.id);
        if result is Company {
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

public enum CompanyRelations {
    employee
}

public class CompanyStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;
    private CompanyRelations[]? include;
    private persist:SQLClient? persistClient;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = (), CompanyRelations[]? include = (), persist:SQLClient? persistClient = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
        self.include = include;
        self.persistClient = persistClient;
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
                record {|Company value;|} nextRecord = {value: <Company>streamValue.value};
                if self.include is CompanyRelations[] {
                    check (<persist:SQLClient>self.persistClient).getManyRelations(nextRecord.value, <CompanyRelations[]>self.include);
                }
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

