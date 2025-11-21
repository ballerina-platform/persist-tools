// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/http;
import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/googleapis.sheets;
import ballerinax/persist.googlesheets;

const APPOINTMENT = "appointments";
const PATIENT = "patients";
const DOCTOR = "doctors";

# Google Sheets persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final sheets:Client googleSheetClient;

    private final http:Client httpClient;

    private final map<googlesheets:GoogleSheetsClient> persistClients;

    public isolated function init() returns persist:Error? {
        final record {|googlesheets:SheetMetadata...;|} & readonly metadata = {
            [APPOINTMENT]: {
                entityName: "Appointment",
                tableName: "Appointment",
                keyFields: ["id"],
                range: "A:F",
                query: self.queryAppointments,
                queryOne: self.queryOneAppointments,
                dataTypes: {
                    id: "int",
                    reason: "string",
                    appointmentTime: "time:Civil",
                    status: "ENUM",
                    patientId: "int",
                    doctorId: "int"
                },
                fieldMetadata: {
                    id: {columnName: "id", columnId: "A"},
                    reason: {columnName: "reason", columnId: "B"},
                    appointmentTime: {columnName: "appointmentTime", columnId: "C"},
                    status: {columnName: "status", columnId: "D"},
                    patientId: {columnName: "patientId", columnId: "E"},
                    doctorId: {columnName: "doctorId", columnId: "F"}
                },
                associationsMethods: {}
            },
            [PATIENT]: {
                entityName: "Patient",
                tableName: "Patient",
                keyFields: ["id"],
                range: "A:F",
                query: self.queryPatients,
                queryOne: self.queryOnePatients,
                dataTypes: {
                    id: "int",
                    name: "string",
                    age: "int",
                    address: "string",
                    phoneNumber: "string",
                    gender: "ENUM"
                },
                fieldMetadata: {
                    id: {columnName: "id", columnId: "A"},
                    name: {columnName: "name", columnId: "B"},
                    age: {columnName: "age", columnId: "C"},
                    address: {columnName: "address", columnId: "D"},
                    phoneNumber: {columnName: "phoneNumber", columnId: "E"},
                    gender: {columnName: "gender", columnId: "F"}
                },
                associationsMethods: {"appointments": self.queryPatientAppointments}
            },
            [DOCTOR]: {
                entityName: "Doctor",
                tableName: "Doctor",
                keyFields: ["id"],
                range: "A:E",
                query: self.queryDoctors,
                queryOne: self.queryOneDoctors,
                dataTypes: {
                    id: "int",
                    name: "string",
                    specialty: "string",
                    phoneNumber: "string",
                    salary: "decimal"
                },
                fieldMetadata: {
                    id: {columnName: "id", columnId: "A"},
                    name: {columnName: "name", columnId: "B"},
                    specialty: {columnName: "specialty", columnId: "C"},
                    phoneNumber: {columnName: "phoneNumber", columnId: "D"},
                    salary: {columnName: "salary", columnId: "E"}
                },
                associationsMethods: {"appointments": self.queryDoctorAppointments}
            }
        };
        sheets:ConnectionConfig sheetsClientConfig = {
            auth: {
                clientId: clientId,
                clientSecret: clientSecret,
                refreshUrl: sheets:REFRESH_URL,
                refreshToken: refreshToken
            }
        };
        http:ClientConfiguration httpClientConfiguration = {
            auth: {
                clientId: clientId,
                clientSecret: clientSecret,
                refreshUrl: sheets:REFRESH_URL,
                refreshToken: refreshToken
            }
        };
        http:Client|error httpClient = new (string `https://sheets.googleapis.com/v4/spreadsheets/${spreadsheetId}/values`, httpClientConfiguration);
        if httpClient is error {
            return <persist:Error>error(httpClient.message());
        }
        sheets:Client|error googleSheetClient = new (sheetsClientConfig);
        if googleSheetClient is error {
            return <persist:Error>error(googleSheetClient.message());
        }
        self.googleSheetClient = googleSheetClient;
        self.httpClient = httpClient;
        map<int> sheetIds = check googlesheets:getSheetIds(self.googleSheetClient, metadata, spreadsheetId);
        self.persistClients = {
            [APPOINTMENT]: check new (self.googleSheetClient, self.httpClient, metadata.get(APPOINTMENT).cloneReadOnly(), spreadsheetId.cloneReadOnly(), sheetIds.get(APPOINTMENT).cloneReadOnly()),
            [PATIENT]: check new (self.googleSheetClient, self.httpClient, metadata.get(PATIENT).cloneReadOnly(), spreadsheetId.cloneReadOnly(), sheetIds.get(PATIENT).cloneReadOnly()),
            [DOCTOR]: check new (self.googleSheetClient, self.httpClient, metadata.get(DOCTOR).cloneReadOnly(), spreadsheetId.cloneReadOnly(), sheetIds.get(DOCTOR).cloneReadOnly())
        };
    }

    # Get rows from appointment sheet.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get appointments(AppointmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    # Get row from appointment sheet.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get appointments/[int id](AppointmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    # Insert rows into appointment sheet.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post appointments(AppointmentInsert[] data) returns int[]|persist:Error {
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(APPOINTMENT);
        }
        _ = check googleSheetsClient.runBatchInsertQuery(data);
        return from AppointmentInsert inserted in data
            select inserted.id;
    }

    # Update row in appointment sheet.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put appointments/[int id](AppointmentUpdate value) returns Appointment|persist:Error {
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(APPOINTMENT);
        }
        _ = check googleSheetsClient.runUpdateQuery(id, value);
        return self->/appointments/[id].get();
    }

    # Delete row from appointment sheet.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete appointments/[int id]() returns Appointment|persist:Error {
        Appointment result = check self->/appointments/[id].get();
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(APPOINTMENT);
        }
        _ = check googleSheetsClient.runDeleteQuery(id);
        return result;
    }

    private isolated function queryAppointments(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Appointment, persist:Error?> appointmentsStream = self.queryAppointmentsStream();
        stream<Patient, persist:Error?> patientsStream = self.queryPatientsStream();
        stream<Doctor, persist:Error?> doctorsStream = self.queryDoctorsStream();
        record {}[] outputArray = check from record {} 'object in appointmentsStream
            outer join var patient in patientsStream on ['object.patientId] equals [patient?.id]
            outer join var doctor in doctorsStream on ['object.doctorId] equals [doctor?.id]
            select persist:filterRecord({
                                            ...'object,
                                            "patient": patient,
                                            "doctor": doctor
                                        }, fields);
        return outputArray.toStream();
    }

    private isolated function queryOneAppointments(anydata key) returns record {}|persist:Error {
        stream<Appointment, persist:Error?> appointmentsStream = self.queryAppointmentsStream();
        stream<Patient, persist:Error?> patientsStream = self.queryPatientsStream();
        stream<Doctor, persist:Error?> doctorsStream = self.queryDoctorsStream();
        error? unionResult = from record {} 'object in appointmentsStream
            where persist:getKey('object, ["id"]) == key
            outer join var patient in patientsStream on ['object.patientId] equals [patient?.id]
            outer join var doctor in doctorsStream on ['object.doctorId] equals [doctor?.id]
            do {
                return {
                    ...'object,
                    "patient": patient,
                    "doctor": doctor
                };
            };
        if unionResult is error {
            return error persist:Error(unionResult.message());
        }
        return persist:getNotFoundError("Appointment", key);
    }

    private isolated function queryAppointmentsStream(AppointmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    # Get rows from patients sheet.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get patients(PatientTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    # Get row from patients sheet.
    #
    # + id - The value of the primary key field ID
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get patients/[int id](PatientTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    # Insert rows into patients sheet.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post patients(PatientInsert[] data) returns int[]|persist:Error {
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(PATIENT);
        }
        _ = check googleSheetsClient.runBatchInsertQuery(data);
        return from PatientInsert inserted in data
            select inserted.id;
    }

    # Update row in patients sheet.
    #
    # + id - The value of the primary key field ID
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put patients/[int id](PatientUpdate value) returns Patient|persist:Error {
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(PATIENT);
        }
        _ = check googleSheetsClient.runUpdateQuery(id, value);
        return self->/patients/[id].get();
    }

    # Delete row from patients sheet.
    #
    # + id - The value of the primary key field ID
    # + return - The deleted record or an error
    isolated resource function delete patients/[int id]() returns Patient|persist:Error {
        Patient result = check self->/patients/[id].get();
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(PATIENT);
        }
        _ = check googleSheetsClient.runDeleteQuery(id);
        return result;
    }

    private isolated function queryPatients(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Patient, persist:Error?> patientsStream = self.queryPatientsStream();
        record {}[] outputArray = check from record {} 'object in patientsStream
            select persist:filterRecord({
                                            ...'object
                                        }, fields);
        return outputArray.toStream();
    }

    private isolated function queryOnePatients(anydata key) returns record {}|persist:Error {
        stream<Patient, persist:Error?> patientsStream = self.queryPatientsStream();
        error? unionResult = from record {} 'object in patientsStream
            where persist:getKey('object, ["id"]) == key
            do {
                return {
                    ...'object
                };
            };
        if unionResult is error {
            return error persist:Error(unionResult.message());
        }
        return persist:getNotFoundError("Patient", key);
    }

    private isolated function queryPatientsStream(PatientTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    # Get rows from Doctor sheet.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get doctors(DoctorTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "query"
    } external;

    # Get row from Doctor sheet.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get doctors/[int id](DoctorTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Doctor sheet.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post doctors(DoctorInsert[] data) returns int[]|persist:Error {
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(DOCTOR);
        }
        _ = check googleSheetsClient.runBatchInsertQuery(data);
        return from DoctorInsert inserted in data
            select inserted.id;
    }

    # Update row in Doctor sheet.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put doctors/[int id](DoctorUpdate value) returns Doctor|persist:Error {
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(DOCTOR);
        }
        _ = check googleSheetsClient.runUpdateQuery(id, value);
        return self->/doctors/[id].get();
    }

    # Delete row from Doctor sheet.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete doctors/[int id]() returns Doctor|persist:Error {
        Doctor result = check self->/doctors/[id].get();
        googlesheets:GoogleSheetsClient googleSheetsClient;
        lock {
            googleSheetsClient = self.persistClients.get(DOCTOR);
        }
        _ = check googleSheetsClient.runDeleteQuery(id);
        return result;
    }

    private isolated function queryDoctors(string[] fields) returns stream<record {}, persist:Error?>|persist:Error {
        stream<Doctor, persist:Error?> doctorsStream = self.queryDoctorsStream();
        record {}[] outputArray = check from record {} 'object in doctorsStream
            select persist:filterRecord({
                                            ...'object
                                        }, fields);
        return outputArray.toStream();
    }

    private isolated function queryOneDoctors(anydata key) returns record {}|persist:Error {
        stream<Doctor, persist:Error?> doctorsStream = self.queryDoctorsStream();
        error? unionResult = from record {} 'object in doctorsStream
            where persist:getKey('object, ["id"]) == key
            do {
                return {
                    ...'object
                };
            };
        if unionResult is error {
            return error persist:Error(unionResult.message());
        }
        return persist:getNotFoundError("Doctor", key);
    }

    private isolated function queryDoctorsStream(DoctorTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.googlesheets.datastore.GoogleSheetsProcessor",
        name: "queryStream"
    } external;

    private isolated function queryPatientAppointments(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Appointment, persist:Error?> appointmentsStream = self.queryAppointmentsStream();
        return from record {} 'object in appointmentsStream
            where 'object.patientId == value["id"]
            select persist:filterRecord({
                                            ...'object
                                        }, fields);
    }

    private isolated function queryDoctorAppointments(record {} value, string[] fields) returns record {}[]|persist:Error {
        stream<Appointment, persist:Error?> appointmentsStream = self.queryAppointmentsStream();
        return from record {} 'object in appointmentsStream
            where 'object.doctorId == value["id"]
            select persist:filterRecord({
                                            ...'object
                                        }, fields);
    }

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        return ();
    }
}

