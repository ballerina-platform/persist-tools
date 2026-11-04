// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/time;

public enum AppointmentStatus {
    SCHEDULED = "SCHEDULED",
    STARTED = "STARTED",
    ENDED = "ENDED"
}

public enum PatientGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

# Represents the `Appointment` entity.
public type Appointment record {|
    readonly int id;
    string reason;
    time:Civil appointmentTime;
    AppointmentStatus status;
    int patientId;
    int doctorId;
|};

# Represents `Appointment` with every field optional, for projected reads.
public type AppointmentOptionalized record {|
    int id?;
    string reason?;
    time:Civil appointmentTime?;
    AppointmentStatus status?;
    int patientId?;
    int doctorId?;
|};

# Represents `Appointment` with its related entities, for projected reads.
public type AppointmentWithRelations record {|
    *AppointmentOptionalized;
    PatientOptionalized patient?;
    DoctorOptionalized doctor?;
|};

# The `Appointment` type or a projection of it, for reads.
public type AppointmentTargetType typedesc<AppointmentWithRelations>;

# The fields to insert a new `Appointment`.
public type AppointmentInsert Appointment;

# The fields to update on an existing `Appointment`.
public type AppointmentUpdate record {|
    string reason?;
    time:Civil appointmentTime?;
    AppointmentStatus status?;
    int patientId?;
    int doctorId?;
|};

# Represents the `Patient` entity.
public type Patient record {|
    readonly int id;
    string name;
    int age;
    string address;
    string phoneNumber;
    PatientGender gender;

|};

# Represents `Patient` with every field optional, for projected reads.
public type PatientOptionalized record {|
    int id?;
    string name?;
    int age?;
    string address?;
    string phoneNumber?;
    PatientGender gender?;
|};

# Represents `Patient` with its related entities, for projected reads.
public type PatientWithRelations record {|
    *PatientOptionalized;
    AppointmentOptionalized[] appointments?;
|};

# The `Patient` type or a projection of it, for reads.
public type PatientTargetType typedesc<PatientWithRelations>;

# The fields to insert a new `Patient`.
public type PatientInsert Patient;

# The fields to update on an existing `Patient`.
public type PatientUpdate record {|
    string name?;
    int age?;
    string address?;
    string phoneNumber?;
    PatientGender gender?;
|};

# Represents the `Doctor` entity.
public type Doctor record {|
    readonly int id;
    string name;
    string specialty;
    string phoneNumber;
    decimal? salary;

|};

# Represents `Doctor` with every field optional, for projected reads.
public type DoctorOptionalized record {|
    int id?;
    string name?;
    string specialty?;
    string phoneNumber?;
    decimal? salary?;
|};

# Represents `Doctor` with its related entities, for projected reads.
public type DoctorWithRelations record {|
    *DoctorOptionalized;
    AppointmentOptionalized[] appointments?;
|};

# The `Doctor` type or a projection of it, for reads.
public type DoctorTargetType typedesc<DoctorWithRelations>;

# The fields to insert a new `Doctor`.
public type DoctorInsert Doctor;

# The fields to update on an existing `Doctor`.
public type DoctorUpdate record {|
    string name?;
    string specialty?;
    string phoneNumber?;
    decimal? salary?;
|};

