import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public enum PatientGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

public type Appointment record {|
    readonly int id;
    time:Date date;
    int patientId;
    int doctorId;
    int assistantId;
    @sql:Relation {keys: ["patientId"]}
    Patient patient;
    @sql:Relation {keys: ["doctorId"]}
    Doctor doctor;
    @sql:Relation {keys: ["assistantId"]}
    Doctor doctor1;
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
    Appointment[] appointments1;
|};

