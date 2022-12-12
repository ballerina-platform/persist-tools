// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated script by Ballerina persistence layer for MultipleAssociations.
// It should not be modified by hand.
import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

public client class MultipleAssociationsClient {
    *persist:AbstractPersistClient;

    private final string entityName = "MultipleAssociations";
    private final sql:ParameterizedQuery tableName = `MultipleAssociations`;

    private final map<persist:FieldMetadata> fieldMetadata = {
        id: {columnName: "id", 'type: int},
        name: {columnName: "name", 'type: string},
        "user.id": {columnName: "userId", 'type: int, relation: {entityName: "user", refTable: "Users", refField: "id"}},
        "user.name": {'type: string, relation: {entityName: "user", refTable: "Users", refField: "name"}},
        "dept.id": {columnName: "deptId", 'type: int, relation: {entityName: "dept", refTable: "Dept", refField: "id"}},
        "dept.name": {'type: string, relation: {entityName: "dept", refTable: "Dept", refField: "name"}},
        "customer.id": {columnName: "customerId", 'type: int, relation: {entityName: "customer", refTable: "Customer", refField: "id"}},
        "customer.name": {'type: string, relation: {entityName: "customer", refTable: "Customer", refField: "name"}},
        "customer.age": {'type: int, relation: {entityName: "customer", refTable: "Customer", refField: "age"}}
    };
    private string[] keyFields = ["id"];

    private final map<persist:JoinMetadata> joinMetadata = {
        user: {entity: User, fieldName: "user", refTable: "Users", refFields: ["id"], joinColumns: ["userId"]},
        dept: {entity: Dept, fieldName: "dept", refTable: "Dept", refFields: ["id"], joinColumns: ["deptId"]},
        customer: {entity: Customer, fieldName: "customer", refTable: "Customer", refFields: ["id"], joinColumns: ["customerId"]}
    };

    private persist:SQLClient persistClient;

    public function init() returns persist:Error? {
        mysql:Client|sql:Error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is sql:Error {
            return <persist:Error>error(dbClient.message());
        }
        self.persistClient = check new (dbClient, self.entityName, self.tableName, self.keyFields, self.fieldMetadata, self.joinMetadata);
    }

    remote function create(MultipleAssociations value) returns MultipleAssociations|persist:Error {
        if value.user is User {
            UserClient userClient = check new UserClient();
            boolean exists = check userClient->exists(<User>value.user);
            if !exists {
                value.user = check userClient->create(<User>value.user);
            }
        }
        if value.dept is Dept {
            DeptClient deptClient = check new DeptClient();
            boolean exists = check deptClient->exists(<Dept>value.dept);
            if !exists {
                value.dept = check deptClient->create(<Dept>value.dept);
            }
        }
        if value.customer is Customer {
            CustomerClient customerClient = check new CustomerClient();
            boolean exists = check customerClient->exists(<Customer>value.customer);
            if !exists {
                value.customer = check customerClient->create(<Customer>value.customer);
            }
        }
        _ = check self.persistClient.runInsertQuery(value);
        return value;
    }

    remote function readByKey(int key, MultipleAssociationsRelations[] include = []) returns MultipleAssociations|persist:Error {
        return <MultipleAssociations>check self.persistClient.runReadByKeyQuery(MultipleAssociations, key, include);
    }

    remote function read(MultipleAssociationsRelations[] include = []) returns stream<MultipleAssociations, persist:Error?> {
        stream<anydata, sql:Error?>|persist:Error result = self.persistClient.runReadQuery(MultipleAssociations, include);
        if result is persist:Error {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream((), result));
        } else {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream(result));
        }
    }

    remote function update(MultipleAssociations value) returns persist:Error? {
        _ = check self.persistClient.runUpdateQuery(value);
        if value.user is record {} {
            User userEntity = <User>value.user;
            UserClient userClient = check new UserClient();
            check userClient->update(userEntity);
        }
        if value.dept is record {} {
            Dept deptEntity = <Dept>value.dept;
            DeptClient deptClient = check new DeptClient();
            check deptClient->update(deptEntity);
        }
        if value.customer is record {} {
            Customer customerEntity = <Customer>value.customer;
            CustomerClient customerClient = check new CustomerClient();
            check customerClient->update(customerEntity);
        }
    }

    remote function delete(MultipleAssociations value) returns persist:Error? {
        _ = check self.persistClient.runDeleteQuery(value);
    }

    remote function exists(MultipleAssociations multipleAssociations) returns boolean|persist:Error {
        MultipleAssociations|persist:Error result = self->readByKey(multipleAssociations.id);
        if result is MultipleAssociations {
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

public enum MultipleAssociationsRelations {
    UserEntity = "user", DeptEntity = "dept", CustomerEntity = "customer"
}

public class MultipleAssociationsStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|MultipleAssociations value;|}|persist:Error? {
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
                record {|MultipleAssociations value;|} nextRecord = {value: <MultipleAssociations>streamValue.value};
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

