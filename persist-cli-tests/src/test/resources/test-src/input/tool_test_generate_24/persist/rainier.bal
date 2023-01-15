import ballerina/persist as _;
import ballerina/time;

// Defines the entity type with the entity identity
// the readonly fields defines the entity identity
type Building record {|
    readonly string buildingCode;
    string city;
    string state;
    string country;
    string postalCode;

    // one-to-many relationship
    Workspace[] workspaces;
|};

type Workspace record {|
    readonly string workspaceId;
    string workspaceType;

    Building location;
    // one-to-one relationship
    Employee employee;
|};

type Department record {|
    readonly string deptNo;
    string deptName;

    // one-to-many relationship
    Employee[] employees;
|};

type Employee record {|
    readonly string empNo;
    string firstName;
    string lastName;
    time:Date birthDate;
    string gender;
    time:Date hireDate;

    Department department;
    Workspace workspace;
|};