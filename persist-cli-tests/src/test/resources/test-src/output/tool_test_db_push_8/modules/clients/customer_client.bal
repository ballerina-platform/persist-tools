import ballerina/sql;
import ballerinax/mysql;
import ballerina/persist;

public client class CustomerClient {
    *persist:AbstractPersistClient;

    private final string entityName = "Customer";
    private final sql:ParameterizedQuery tableName = `Customer`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        age: {columnName: "age", 'type: int}
    };
    private string[] keyFields = ["id"];

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata);
    }

    remote function create(Customer value) returns Customer|persist:Error {
        sql:ExecutionResult result = check self.persistClient.runInsertQuery(value);
        if result.lastInsertId is () {
            return value;
        }
        return {id: <int>result.lastInsertId, name: value.name, age: value.age};
    }

    remote function readByKey(int key) returns Customer|persist:Error {
        return <Customer>check self.persistClient.runReadByKeyQuery(Customer, key);
    }

    remote function read() returns stream<Customer, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(Customer);
        if result is persist:Error {
            return new stream<Customer, persist:Error?>(new CustomerStream((), result));
        } else {
            return new stream<Customer, persist:Error?>(new CustomerStream(result));
        }
    }

    remote function execute(sql:ParameterizedQuery filterClause) returns stream<Customer, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runExecuteQuery(filterClause, Customer);
        if result is persist:Error {
            return new stream<Customer, persist:Error?>(new CustomerStream((), result));
        } else {
            return new stream<Customer, persist:Error?>(new CustomerStream(result));
        }
    }

    remote function update(Customer value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
    }

    remote function delete(Customer value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(Customer customer) returns boolean|persist:Error {
        Customer|persist:Error result = self->readByKey(customer.id);
        if result is Customer {
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

public class CustomerStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Customer value;|}|persist:Error? {
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
                record {|Customer value;|} nextRecord = {value: <Customer>streamValue.value};
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

