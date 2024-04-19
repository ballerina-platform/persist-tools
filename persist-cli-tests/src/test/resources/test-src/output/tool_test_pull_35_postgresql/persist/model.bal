import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public enum PatientGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

@sql:Name {value: "appointment"}
public type Appointment record {|
    readonly int id;
    time:Date date;
    @sql:Name {value: "patient_Id"}
    int patientId;
    @sql:Name {value: "Doctor_Id"}
    int doctorId;
    @sql:Relation {keys: ["doctorId"]}
    Doctor doctor;
    @sql:Relation {keys: ["patientId"]}
    Patient patient;
|};

@sql:Name {value: "patients"}
public type Patient record {|
    readonly int id;
    string name;
    @sql:Name {value: "GENDER"}
    PatientGender gender;
    @sql:Name {value: "NIC"}
    string nic;
    Appointment[] appointments;
|};

@sql:Name {value: "DOCTOR"}
public type Doctor record {|
    readonly int id;
    string name;
    @sql:Name {value: "doctor_Specialty"}
    string doctorSpecialty;
    Appointment[] appointments;
|};

