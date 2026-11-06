import ballerina/dataservice;
import ballerina/persist as _;

# An employee of the company.
#
# + name - The full name
@dataservice:Expose
type Employee record {|
    readonly int id;
    string name;
    # The work email, if the employee has one
    string? email;
    Department department;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type Department record {|
    readonly int id;
    string name;
    Employee[] employees;
|};

type AuditLog record {|
    readonly int id;
    string entry;
|};
