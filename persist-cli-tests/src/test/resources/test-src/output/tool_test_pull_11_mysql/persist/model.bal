import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public enum PatientGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

public type Appointment record {|
    readonly int id;
    @sql:Index {names: ["patientId"]}
    int patientId;
    @sql:Index {names: ["doctorId"]}
    int doctorId;
    time:Date date;
    @sql:Relation {refs: ["patientId"]}
    Patient patient;
    @sql:Relation {refs: ["doctorId"]}
    Doctor doctor;
|};

public type Patient record {|
    readonly int id;
    string name;
    PatientGender gender;
    @sql:VarChar {length: 12}
    string nic;
    @sql:Char {length: 10}
    string contact;
    Appointment[] appointments;
|};

public type Doctor record {|
    readonly int id;
    @sql:VarChar {length: 20}
    string name;
    string specialty;
    @sql:Decimal {precision: [10, 2]}
    decimal? salary;
    Appointment[] appointments;
|};

