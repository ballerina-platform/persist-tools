import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public enum PatientGender {
    FEMALE = "FEMALE",
    MALE = "MALE"
}

public type Appointment record {|
    readonly int id;
    int patientId;
    int doctorId;
    time:Date date;
    @sql:Relation {keys: ["doctorId"]}
    Doctor doctor;
    @sql:Relation {keys: ["patientId"]}
    Patient patient;
|};

public type Patient record {|
    readonly int id;
    string name;
    PatientGender gender;
    string nic;
    Appointment[] appointments;
|};

public type Doctor record {|
    readonly int id;
    string name;
    string specialty;
    Appointment[] appointments;
|};

