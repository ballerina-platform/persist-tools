import Demo.entities;

import ballerina/http;
import ballerina/persist;
import ballerina/time;

public type Doctor record {|
    readonly int id;
    string name;
    string specialty;
|};

type Appointment record {|
    int id;
    int doctorId;
    time:Civil appointmentTime;
    entities:AppointmentStatus status;
    record {|
        int id;
        string name;
        string phoneNumber;
    |} patient;
|};

type PatientAppointment record {|
    int id;
    int patientId;
    time:Civil appointmentTime;
    entities:AppointmentStatus status;
    record {|
        int id;
        string name;
        string specialty;
    |} doctor;
|};

type PatientCreated record {|
    *http:Created;
|};

service /hospital on new http:Listener(9090) {
    private final entities:Client dbClient;

    // Initialize the service
    function init() returns error? {
        self.dbClient = check new ();
    }

    // Define the resource to handle POST requests
    resource function post doctors(entities:DoctorInsert doctor) returns http:InternalServerError & readonly|http:Created & readonly|http:Conflict & readonly {
        int[]|persist:Error result = self.dbClient->/doctors.post([doctor]);
        if result is persist:Error {
            if result is persist:AlreadyExistsError {
                return http:CONFLICT;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:CREATED;
    }

    // Define the resource to handle POST requests for patients
    resource function post patients(entities:PatientInsert patient) returns http:InternalServerError & readonly|PatientCreated|http:Conflict & readonly {
        int[]|persist:Error result = self.dbClient->/patients.post([patient]);
        if result is persist:Error {
            return http:INTERNAL_SERVER_ERROR;
        }
        return <PatientCreated> {
            body: {
                insertedId: result[0]
            }
        };
    }

    // Define the resource to handle POST requests for appointments
    resource function post appointments(entities:AppointmentInsert appointment) returns http:InternalServerError & readonly|http:Created & readonly|http:Conflict & readonly {
        int[]|persist:Error result = self.dbClient->/appointments.post([appointment]);
        if result is persist:Error {
            if result is persist:AlreadyExistsError {
                return http:CONFLICT;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:CREATED;
    }

    // Define the resource to handle GET requests for doctors
    resource function get doctors() returns Doctor[]|error {
        stream<Doctor, persist:Error?> doctors = self.dbClient->/doctors.get();
        return from Doctor doctor in doctors
            select doctor;
    }

    // Define the resource to handle GET requests for doctors by id as path param and date as query params
    resource function get doctors/[int id]/appointments(int year, int month, int day) returns Appointment[]|error {
        stream<Appointment, persist:Error?> appointments = self.dbClient->/appointments();
        return from Appointment appointment in appointments
            where appointment.doctorId == id &&
            appointment.appointmentTime.year == year &&
            appointment.appointmentTime.month == month &&
            appointment.appointmentTime.day == day
            select appointment;

    }

    // Define the resource to handle GET requests for appointments for patients by id
    resource function get patients/[int id]/appointments() returns PatientAppointment[]|error {
        stream<PatientAppointment, persist:Error?> appointments = self.dbClient->/appointments();
        return from PatientAppointment appointment in appointments
            where appointment.patientId == id
            select appointment;
    }

    // Define the resource to handle GET requests for patients by id
    resource function get patients/[int id]() returns http:InternalServerError & readonly|http:NotFound & readonly|entities:Patient {
        entities:Patient|persist:Error result = self.dbClient->/patients/[id];
        if result is persist:Error {
            if result is persist:NotFoundError {
                return http:NOT_FOUND;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return result;
    }

    // Define the resource to handle PATCH requests for appointment by id
    resource function patch appointments/[int id](@http:Payload entities:AppointmentStatus status) returns http:InternalServerError & readonly|http:NotFound & readonly|http:NoContent & readonly {
        entities:Appointment|persist:Error result = self.dbClient->/appointments/[id].put({status});
        if result is persist:Error {
            if result is persist:NotFoundError {
                return http:NOT_FOUND;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:NO_CONTENT;
    }

    // Define the resource to handle DELETE requests for patient's appointments passing patient id as path param and date as query params
    resource function delete patients/[int id]/appointments(int year, int month, int day) returns http:InternalServerError & readonly|http:NoContent & readonly|http:NotFound & readonly {
        stream<entities:Appointment, persist:Error?> appointments = self.dbClient->/appointments;
        entities:Appointment[]|persist:Error result = from entities:Appointment appointment in appointments
            where appointment.patientId == id
                && appointment.appointmentTime.year == year
                && appointment.appointmentTime.month == month
                && appointment.appointmentTime.day == day
            select appointment;
        if result is persist:Error {
            if result is persist:NotFoundError {
                return http:NOT_FOUND;
            }
            return http:INTERNAL_SERVER_ERROR;
        }
        foreach entities:Appointment appointment in result {
            entities:Appointment|persist:Error deleteResult = self.dbClient->/appointments/[appointment.id].delete();
            if deleteResult is persist:Error {
                return http:INTERNAL_SERVER_ERROR;
            }
        }

        return http:NO_CONTENT;
    }

    resource function delete patients/[int id]() returns http:NoContent | http:InternalServerError {
        entities:Patient|persist:Error result = self.dbClient->/patients/[id].delete();
        if result is persist:Error {
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:NO_CONTENT;
    }

    resource function delete doctors/[int id]() returns http:NoContent | http:InternalServerError {
        entities:Doctor|persist:Error result = self.dbClient->/doctors/[id].delete();
        if result is persist:Error {
            return http:INTERNAL_SERVER_ERROR;
        }
        return http:NO_CONTENT;
    }
}
