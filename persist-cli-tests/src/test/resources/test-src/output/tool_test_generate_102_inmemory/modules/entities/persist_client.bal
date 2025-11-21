// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/jballerina.java;
import ballerina/persist;
import ballerinax/persist.inmemory;

const APPOINTMENT = "appointments";
const PATIENT = "patients";
const DOCTOR = "doctors";
final isolated table<Appointment> key(id) appointmentsTable = table [];
final isolated table<Patient> key(id) patientsTable = table [];
final isolated table<Doctor> key(id) doctorsTable = table [];

# In-Memory persist client.
public isolated client class Client {
    *persist:AbstractPersistClient;

    private final map<inmemory:InMemoryClient> persistClients;

    public isolated function init() returns persist:Error? {
        final map<inmemory:TableMetadata> metadata = {
            [APPOINTMENT]: {
                keyFields: ["id"],
                query: queryAppointments,
                queryOne: queryOneAppointments
            },
            [PATIENT]: {
                keyFields: ["id"],
                query: queryPatients,
                queryOne: queryOnePatients,
                associationsMethods: {"appointments": queryPatientAppointments}
            },
            [DOCTOR]: {
                keyFields: ["id"],
                query: queryDoctors,
                queryOne: queryOneDoctors,
                associationsMethods: {"appointments": queryDoctorAppointments}
            }
        };
        self.persistClients = {
            [APPOINTMENT]: check new (metadata.get(APPOINTMENT).cloneReadOnly()),
            [PATIENT]: check new (metadata.get(PATIENT).cloneReadOnly()),
            [DOCTOR]: check new (metadata.get(DOCTOR).cloneReadOnly())
        };
    }

    # Get rows from appointment table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get appointments(AppointmentTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    # Get row from appointment table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get appointments/[int id](AppointmentTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    # Insert rows into appointment table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post appointments(AppointmentInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach AppointmentInsert value in data {
            lock {
                if appointmentsTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("Appointment", value.id);
                }
                appointmentsTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    # Update row in appointment table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put appointments/[int id](AppointmentUpdate value) returns Appointment|persist:Error {
        lock {
            if !appointmentsTable.hasKey(id) {
                return persist:getNotFoundError("Appointment", id);
            }
            Appointment appointment = appointmentsTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                appointment[k] = v;
            }
            appointmentsTable.put(appointment);
            return appointment.clone();
        }
    }

    # Delete row from appointment table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete appointments/[int id]() returns Appointment|persist:Error {
        lock {
            if !appointmentsTable.hasKey(id) {
                return persist:getNotFoundError("Appointment", id);
            }
            return appointmentsTable.remove(id).clone();
        }
    }

    # Get rows from patients table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get patients(PatientTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    # Get row from patients table.
    #
    # + id - The value of the primary key field ID
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get patients/[int id](PatientTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    # Insert rows into patients table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post patients(PatientInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach PatientInsert value in data {
            lock {
                if patientsTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("Patient", value.id);
                }
                patientsTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    # Update row in patients table.
    #
    # + id - The value of the primary key field ID
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put patients/[int id](PatientUpdate value) returns Patient|persist:Error {
        lock {
            if !patientsTable.hasKey(id) {
                return persist:getNotFoundError("Patient", id);
            }
            Patient patient = patientsTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                patient[k] = v;
            }
            patientsTable.put(patient);
            return patient.clone();
        }
    }

    # Delete row from patients table.
    #
    # + id - The value of the primary key field ID
    # + return - The deleted record or an error
    isolated resource function delete patients/[int id]() returns Patient|persist:Error {
        lock {
            if !patientsTable.hasKey(id) {
                return persist:getNotFoundError("Patient", id);
            }
            return patientsTable.remove(id).clone();
        }
    }

    # Get rows from Doctor table.
    #
    # + targetType - Defines which fields to retrieve from the results
    # + return - A collection of matching records or an error
    isolated resource function get doctors(DoctorTargetType targetType = <>) returns stream<targetType, persist:Error?> = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "query"
    } external;

    # Get row from Doctor table.
    #
    # + id - The value of the primary key field id
    # + targetType - Defines which fields to retrieve from the result
    # + return - The matching record or an error
    isolated resource function get doctors/[int id](DoctorTargetType targetType = <>) returns targetType|persist:Error = @java:Method {
        'class: "io.ballerina.stdlib.persist.inmemory.datastore.InMemoryProcessor",
        name: "queryOne"
    } external;

    # Insert rows into Doctor table.
    #
    # + data - A list of records to be inserted
    # + return - The primary key value(s) of the inserted rows or an error
    isolated resource function post doctors(DoctorInsert[] data) returns int[]|persist:Error {
        int[] keys = [];
        foreach DoctorInsert value in data {
            lock {
                if doctorsTable.hasKey(value.id) {
                    return persist:getAlreadyExistsError("Doctor", value.id);
                }
                doctorsTable.put(value.clone());
            }
            keys.push(value.id);
        }
        return keys;
    }

    # Update row in Doctor table.
    #
    # + id - The value of the primary key field id
    # + value - The record containing updated field values
    # + return - The updated record or an error
    isolated resource function put doctors/[int id](DoctorUpdate value) returns Doctor|persist:Error {
        lock {
            if !doctorsTable.hasKey(id) {
                return persist:getNotFoundError("Doctor", id);
            }
            Doctor doctor = doctorsTable.get(id);
            foreach var [k, v] in value.clone().entries() {
                doctor[k] = v;
            }
            doctorsTable.put(doctor);
            return doctor.clone();
        }
    }

    # Delete row from Doctor table.
    #
    # + id - The value of the primary key field id
    # + return - The deleted record or an error
    isolated resource function delete doctors/[int id]() returns Doctor|persist:Error {
        lock {
            if !doctorsTable.hasKey(id) {
                return persist:getNotFoundError("Doctor", id);
            }
            return doctorsTable.remove(id).clone();
        }
    }

    # Close the database client and release connections.
    #
    # + return - An error if closing fails
    public isolated function close() returns persist:Error? {
        return ();
    }
}

isolated function queryAppointments(string[] fields) returns stream<record {}, persist:Error?> {
    table<Appointment> key(id) appointmentsClonedTable;
    lock {
        appointmentsClonedTable = appointmentsTable.clone();
    }
    table<Patient> key(id) patientsClonedTable;
    lock {
        patientsClonedTable = patientsTable.clone();
    }
    table<Doctor> key(id) doctorsClonedTable;
    lock {
        doctorsClonedTable = doctorsTable.clone();
    }
    return from record {} 'object in appointmentsClonedTable
        outer join var patient in patientsClonedTable on ['object.patientId] equals [patient?.id]
        outer join var doctor in doctorsClonedTable on ['object.doctorId] equals [doctor?.id]
        select persist:filterRecord({
                                        ...'object,
                                        "patient": patient,
                                        "doctor": doctor
                                    }, fields);
}

isolated function queryOneAppointments(anydata key) returns record {}|persist:NotFoundError {
    table<Appointment> key(id) appointmentsClonedTable;
    lock {
        appointmentsClonedTable = appointmentsTable.clone();
    }
    table<Patient> key(id) patientsClonedTable;
    lock {
        patientsClonedTable = patientsTable.clone();
    }
    table<Doctor> key(id) doctorsClonedTable;
    lock {
        doctorsClonedTable = doctorsTable.clone();
    }
    from record {} 'object in appointmentsClonedTable
    where persist:getKey('object, ["id"]) == key
    outer join var patient in patientsClonedTable on ['object.patientId] equals [patient?.id]
    outer join var doctor in doctorsClonedTable on ['object.doctorId] equals [doctor?.id]
    do {
        return {
            ...'object,
            "patient": patient,
            "doctor": doctor
        };
    };
    return persist:getNotFoundError("Appointment", key);
}

isolated function queryPatients(string[] fields) returns stream<record {}, persist:Error?> {
    table<Patient> key(id) patientsClonedTable;
    lock {
        patientsClonedTable = patientsTable.clone();
    }
    return from record {} 'object in patientsClonedTable
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryOnePatients(anydata key) returns record {}|persist:NotFoundError {
    table<Patient> key(id) patientsClonedTable;
    lock {
        patientsClonedTable = patientsTable.clone();
    }
    from record {} 'object in patientsClonedTable
    where persist:getKey('object, ["id"]) == key
    do {
        return {
            ...'object
        };
    };
    return persist:getNotFoundError("Patient", key);
}

isolated function queryDoctors(string[] fields) returns stream<record {}, persist:Error?> {
    table<Doctor> key(id) doctorsClonedTable;
    lock {
        doctorsClonedTable = doctorsTable.clone();
    }
    return from record {} 'object in doctorsClonedTable
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryOneDoctors(anydata key) returns record {}|persist:NotFoundError {
    table<Doctor> key(id) doctorsClonedTable;
    lock {
        doctorsClonedTable = doctorsTable.clone();
    }
    from record {} 'object in doctorsClonedTable
    where persist:getKey('object, ["id"]) == key
    do {
        return {
            ...'object
        };
    };
    return persist:getNotFoundError("Doctor", key);
}

isolated function queryPatientAppointments(record {} value, string[] fields) returns record {}[] {
    table<Appointment> key(id) appointmentsClonedTable;
    lock {
        appointmentsClonedTable = appointmentsTable.clone();
    }
    return from record {} 'object in appointmentsClonedTable
        where 'object.patientId == value["id"]
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

isolated function queryDoctorAppointments(record {} value, string[] fields) returns record {}[] {
    table<Appointment> key(id) appointmentsClonedTable;
    lock {
        appointmentsClonedTable = appointmentsTable.clone();
    }
    return from record {} 'object in appointmentsClonedTable
        where 'object.doctorId == value["id"]
        select persist:filterRecord({
                                        ...'object
                                    }, fields);
}

