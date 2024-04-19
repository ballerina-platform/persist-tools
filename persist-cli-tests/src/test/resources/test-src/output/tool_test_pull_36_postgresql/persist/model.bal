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
    @sql:Relation {keys: ["doctorId"]}
    Doctor doctor;
    @sql:Relation {keys: ["patientId"]}
    Patient patient;
|};

public type Patient record {|
    readonly int id;
    string name;
    PatientGender gender;
    @sql:Varchar {length: 12}
    string nic;
    @sql:Char {length: 10}
    string contact;
    Appointment[] appointments;
|};

public type Doctor record {|
    readonly int id;
    @sql:Varchar {length: 20}
    string name;
    string specialty;
    @sql:Decimal {precision: [10, 2]}
    decimal? salary;
    Appointment[] appointments;
|};

