import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public enum PatientGender {
    FEMALE = "FEMALE",
    MALE = "MALE"
}

public enum AppointmentStatus {
    ENDED = "ENDED",
    STARTED = "STARTED",
    SCHEDULED = "SCHEDULED"
}

@sql:Name {value: "appointment"}
public type Appointment record {|
    readonly int id;
    @sql:UniqueIndex {name: "reason_index"}
    string reason;
    time:Civil appointmentTime;
    AppointmentStatus status;
    @sql:Name {value: "patient_id"}
    int patientId;
    int doctorId;
    @sql:Relation {keys: ["doctorId"]}
    Doctor doctor;
    @sql:Relation {keys: ["patientId"]}
    Patient patient;
|};

@sql:Name {value: "patients"}
public type Patient record {|
    @sql:Name {value: "ID_P"}
    @sql:Generated
    readonly int idP;
    string name;
    int age;
    @sql:Name {value: "ADDRESS"}
    string address;
    @sql:Char {length: 10}
    string phoneNumber;
    PatientGender gender;
    Appointment[] appointments;
|};

public type Doctor record {|
    readonly int id;
    string name;
    @sql:Index {name: "specialty_index"}
    string specialty;
    @sql:Name {value: "phone_number"}
    string phoneNumber;
    @sql:Decimal {precision: [10, 2]}
    decimal? salary;
    Appointment[] appointments;
|};

