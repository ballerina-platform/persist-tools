// Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/persist as _;
import ballerina/time;
import ballerinax/persist.sql;

public enum AppointmentStatus {
    SCHEDULED = "SCHEDULED",
    STARTED = "STARTED",
    ENDED = "ENDED"
}

public enum PatientGender {
    MALE = "MALE",
    FEMALE = "FEMALE"
}

@sql:Mapping {name: "appointment"}
public type Appointment record {|
    readonly int id;
    @sql:UniqueIndex {names: ["reason_index"]}
    string reason;
    time:Civil appointmentTime;
    AppointmentStatus status;
    @sql:Mapping {name: "patient_id"}
    @sql:Index {names: ["patientId"]}
    int patientId;
    @sql:Index {names: ["doctorId"]}
    int doctorId;
    @sql:Relation {refs: ["patientId"]}
    Patient patient;
    @sql:Relation {refs: ["doctorId"]}
    Doctor doctor;
|};

@sql:Mapping {name: "patients"}
public type Patient record {|
    @sql:Mapping {name: "ID"}
    @sql:Generated
    readonly int id;
    string name;
    int age;
    @sql:Mapping {name: "ADDRESS"}
    string address;
    @sql:Char {length: 10}
    string phoneNumber;
    PatientGender gender;
    Appointment[] appointments;
|};

public type Doctor record {|
    readonly int id;
    string name;
    @sql:Index {names: ["specialty_index"]}
    string specialty;
    @sql:Mapping {name: "phone_number"}
    string phoneNumber;
    @sql:Decimal {precision: [10, 2]}
    decimal? salary;
    Appointment[] appointments;
|};

