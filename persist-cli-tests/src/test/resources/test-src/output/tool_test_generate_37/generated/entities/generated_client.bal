// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for entities.
// It should not be modified by hand.

import ballerina/persist;
import ballerina/sql;
import ballerinax/mysql;

const PROFILE = "profile";
const USER = "user";
const DEPT = "dept";
const CUSTOMER = "customer";
const STUDENT = "student";
const MULTIPLE_ASSOCIATIONS = "multipleassociations";

public client class EntitiesClient {
    *persist:AbstractPersistClient;

    private final mysql:Client dbClient;

    private final map<persist:SQLClient> persistClients;

    private final record {|persist:Metadata...;|} metadata = {
        "profile": {
            entityName: "Profile",
            tableName: `Profile`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string},
                isAdult: {columnName: "isAdult", 'type: boolean},
                salary: {columnName: "salary", 'type: float},
                age: {columnName: "age", 'type: decimal},
                isRegistered: {columnName: "isRegistered", 'type: byte},
                userId: {columnName: "userId", 'type: int}
            },
            keyFields: ["id"]
        },
        "user": {
            entityName: "User",
            tableName: `User`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string},
                multipleassociationsId: {columnName: "multipleassociationsId", 'type: int}
            },
            keyFields: ["id"]
        },
        "dept": {
            entityName: "Dept",
            tableName: `Dept`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string},
                multipleassociationsId: {columnName: "multipleassociationsId", 'type: int}
            },
            keyFields: ["id"]
        },
        "customer": {
            entityName: "Customer",
            tableName: `Customer`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string},
                age: {columnName: "age", 'type: int},
                multipleassociationsId: {columnName: "multipleassociationsId", 'type: int}
            },
            keyFields: ["id"]
        },
        "student": {
            entityName: "Student",
            tableName: `Student`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                firstName: {columnName: "firstName", 'type: string},
                age: {columnName: "age", 'type: int},
                lastName: {columnName: "lastName", 'type: string},
                nicNo: {columnName: "nicNo", 'type: string}
            },
            keyFields: ["id", "firstName"]
        },
        "multipleassociations": {
            entityName: "MultipleAssociations",
            tableName: `MultipleAssociations`,
            fieldMetadata: {
                id: {columnName: "id", 'type: int},
                name: {columnName: "name", 'type: string}
            },
            keyFields: ["id"]
        }
    };

    public function init() returns persist:Error? {
        mysql:Client|error dbClient = new (host = host, user = user, password = password, database = database, port = port);
        if dbClient is error {
            return <persist:Error>error(dbClient.message());
        }
        self.dbClient = dbClient;
        self.persistClients = {
            profile: check new (self.dbClient, self.metadata.get(PROFILE)),
            user: check new (self.dbClient, self.metadata.get(USER)),
            dept: check new (self.dbClient, self.metadata.get(DEPT)),
            customer: check new (self.dbClient, self.metadata.get(CUSTOMER)),
            student: check new (self.dbClient, self.metadata.get(STUDENT)),
            multipleassociations: check new (self.dbClient, self.metadata.get(MULTIPLE_ASSOCIATIONS))
        };
    }

    isolated resource function get profile() returns stream<Profile, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(PROFILE).runReadQuery(Profile);
        if result is persist:Error {
            return new stream<Profile, persist:Error?>(new ProfileStream((), result));
        } else {
            return new stream<Profile, persist:Error?>(new ProfileStream(result));
        }
    }

    isolated resource function get profile/[int id]() returns Profile|persist:Error {
        Profile|error result = (check self.persistClients.get(PROFILE).runReadByKeyQuery(Profile, id)).cloneWithType(Profile);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post profile(ProfileInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(PROFILE).runBatchInsertQuery(data);
        return from ProfileInsert inserted in data
            select inserted.id;
    }

    isolated resource function put profile/[int id](ProfileUpdate value) returns Profile|persist:Error {
        _ = check self.persistClients.get(PROFILE).runUpdateQuery(id, value);
        return self->/profile/[id].get();
    }

    isolated resource function delete profile/[int id]() returns Profile|persist:Error {
        Profile result = check self->/profile/[id].get();
        _ = check self.persistClients.get(PROFILE).runDeleteQuery(id);
        return result;
    }

    isolated resource function get user() returns stream<User, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(USER).runReadQuery(User);
        if result is persist:Error {
            return new stream<User, persist:Error?>(new UserStream((), result));
        } else {
            return new stream<User, persist:Error?>(new UserStream(result));
        }
    }

    isolated resource function get user/[int id]() returns User|persist:Error {
        User|error result = (check self.persistClients.get(USER).runReadByKeyQuery(User, id)).cloneWithType(User);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post user(UserInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(USER).runBatchInsertQuery(data);
        return from UserInsert inserted in data
            select inserted.id;
    }

    isolated resource function put user/[int id](UserUpdate value) returns User|persist:Error {
        _ = check self.persistClients.get(USER).runUpdateQuery(id, value);
        return self->/user/[id].get();
    }

    isolated resource function delete user/[int id]() returns User|persist:Error {
        User result = check self->/user/[id].get();
        _ = check self.persistClients.get(USER).runDeleteQuery(id);
        return result;
    }

    isolated resource function get dept() returns stream<Dept, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(DEPT).runReadQuery(Dept);
        if result is persist:Error {
            return new stream<Dept, persist:Error?>(new DeptStream((), result));
        } else {
            return new stream<Dept, persist:Error?>(new DeptStream(result));
        }
    }

    isolated resource function get dept/[int id]() returns Dept|persist:Error {
        Dept|error result = (check self.persistClients.get(DEPT).runReadByKeyQuery(Dept, id)).cloneWithType(Dept);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post dept(DeptInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(DEPT).runBatchInsertQuery(data);
        return from DeptInsert inserted in data
            select inserted.id;
    }

    isolated resource function put dept/[int id](DeptUpdate value) returns Dept|persist:Error {
        _ = check self.persistClients.get(DEPT).runUpdateQuery(id, value);
        return self->/dept/[id].get();
    }

    isolated resource function delete dept/[int id]() returns Dept|persist:Error {
        Dept result = check self->/dept/[id].get();
        _ = check self.persistClients.get(DEPT).runDeleteQuery(id);
        return result;
    }

    isolated resource function get customer() returns stream<Customer, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(CUSTOMER).runReadQuery(Customer);
        if result is persist:Error {
            return new stream<Customer, persist:Error?>(new CustomerStream((), result));
        } else {
            return new stream<Customer, persist:Error?>(new CustomerStream(result));
        }
    }

    isolated resource function get customer/[int id]() returns Customer|persist:Error {
        Customer|error result = (check self.persistClients.get(CUSTOMER).runReadByKeyQuery(Customer, id)).cloneWithType(Customer);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post customer(CustomerInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(CUSTOMER).runBatchInsertQuery(data);
        return from CustomerInsert inserted in data
            select inserted.id;
    }

    isolated resource function put customer/[int id](CustomerUpdate value) returns Customer|persist:Error {
        _ = check self.persistClients.get(CUSTOMER).runUpdateQuery(id, value);
        return self->/customer/[id].get();
    }

    isolated resource function delete customer/[int id]() returns Customer|persist:Error {
        Customer result = check self->/customer/[id].get();
        _ = check self.persistClients.get(CUSTOMER).runDeleteQuery(id);
        return result;
    }

    isolated resource function get student() returns stream<Student, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(STUDENT).runReadQuery(Student);
        if result is persist:Error {
            return new stream<Student, persist:Error?>(new StudentStream((), result));
        } else {
            return new stream<Student, persist:Error?>(new StudentStream(result));
        }
    }

    isolated resource function get student/[string firstName]/[int id]() returns Student|persist:Error {
        Student|error result = (check self.persistClients.get(STUDENT).runReadByKeyQuery(Student, {firstName: firstName, id: id})).cloneWithType(Student);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post student(StudentInsert[] data) returns [int, string][]|persist:Error {
        _ = check self.persistClients.get(STUDENT).runBatchInsertQuery(data);
        return from StudentInsert inserted in data
            select [inserted.id, inserted.firstName];
    }

    isolated resource function put student/[string firstName]/[int id](StudentUpdate value) returns Student|persist:Error {
        _ = check self.persistClients.get(STUDENT).runUpdateQuery({"firstName": firstName, "id": id}, value);
        return self->/student/[firstName]/[id].get();
    }

    isolated resource function delete student/[string firstName]/[int id]() returns Student|persist:Error {
        Student result = check self->/student/[firstName]/[id].get();
        _ = check self.persistClients.get(STUDENT).runDeleteQuery({"firstName": firstName, "id": id});
        return result;
    }

    isolated resource function get multipleassociations() returns stream<MultipleAssociations, persist:Error?> {
        stream<record {}, sql:Error?>|persist:Error result = self.persistClients.get(MULTIPLE_ASSOCIATIONS).runReadQuery(MultipleAssociations);
        if result is persist:Error {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream((), result));
        } else {
            return new stream<MultipleAssociations, persist:Error?>(new MultipleAssociationsStream(result));
        }
    }

    isolated resource function get multipleassociations/[int id]() returns MultipleAssociations|persist:Error {
        MultipleAssociations|error result = (check self.persistClients.get(MULTIPLE_ASSOCIATIONS).runReadByKeyQuery(MultipleAssociations, id)).cloneWithType(MultipleAssociations);
        if result is error {
            return <persist:Error>error(result.message());
        }
        return result;
    }

    isolated resource function post multipleassociations(MultipleAssociationsInsert[] data) returns int[]|persist:Error {
        _ = check self.persistClients.get(MULTIPLE_ASSOCIATIONS).runBatchInsertQuery(data);
        return from MultipleAssociationsInsert inserted in data
            select inserted.id;
    }

    isolated resource function put multipleassociations/[int id](MultipleAssociationsUpdate value) returns MultipleAssociations|persist:Error {
        _ = check self.persistClients.get(MULTIPLE_ASSOCIATIONS).runUpdateQuery(id, value);
        return self->/multipleassociations/[id].get();
    }

    isolated resource function delete multipleassociations/[int id]() returns MultipleAssociations|persist:Error {
        MultipleAssociations result = check self->/multipleassociations/[id].get();
        _ = check self.persistClients.get(MULTIPLE_ASSOCIATIONS).runDeleteQuery(id);
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

public class ProfileStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Profile value;|}|persist:Error? {
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
                Profile|error value = streamValue.value.cloneWithType(Profile);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Profile value;|} nextRecord = {value: value};
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

public class UserStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|User value;|}|persist:Error? {
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
                User|error value = streamValue.value.cloneWithType(User);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|User value;|} nextRecord = {value: value};
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

public class DeptStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Dept value;|}|persist:Error? {
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
                Dept|error value = streamValue.value.cloneWithType(Dept);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Dept value;|} nextRecord = {value: value};
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
                Customer|error value = streamValue.value.cloneWithType(Customer);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Customer value;|} nextRecord = {value: value};
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

public class StudentStream {

    private stream<anydata, sql:Error?>? anydataStream;
    private persist:Error? err;

    public isolated function init(stream<anydata, sql:Error?>? anydataStream, persist:Error? err = ()) {
        self.anydataStream = anydataStream;
        self.err = err;
    }

    public isolated function next() returns record {|Student value;|}|persist:Error? {
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
                Student|error value = streamValue.value.cloneWithType(Student);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|Student value;|} nextRecord = {value: value};
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
                MultipleAssociations|error value = streamValue.value.cloneWithType(MultipleAssociations);
                if value is error {
                    return <persist:Error>error(value.message());
                }
                record {|MultipleAssociations value;|} nextRecord = {value: value};
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

