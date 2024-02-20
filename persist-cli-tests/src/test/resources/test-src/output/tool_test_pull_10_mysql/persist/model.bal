import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public enum PatientGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

@sql:Mapping {name: "appointment"}
public type Appointment record {|
    readonly int id;
    @sql:Mapping {name: "patient_Id"}
    @sql:Index {names: ["patient_Id"]}
    int patientId;
    @sql:Mapping {name: "Doctor_Id"}
    @sql:Index {names: ["Doctor_Id"]}
    int doctorId;
    time:Date date;
    @sql:Relation {refs: ["patientId"]}
    Patient patient;
    @sql:Relation {refs: ["doctorId"]}
    Doctor doctor;
|};

@sql:Mapping {name: "patients"}
public type Patient record {|
    readonly int id;
    string name;
    @sql:Mapping {name: "GENDER"}
    PatientGender gender;
    @sql:Mapping {name: "NIC"}
    string nic;
    Appointment[] appointments;
|};

@sql:Mapping {name: "DOCTOR"}
public type Doctor record {|
    readonly int id;
    string name;
    @sql:Mapping {name: "doctor_Specialty"}
    string doctorSpecialty;
    Appointment[] appointments;
|};

