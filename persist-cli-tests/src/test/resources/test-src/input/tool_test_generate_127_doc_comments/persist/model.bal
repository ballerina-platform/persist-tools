import ballerina/constraint;
import ballerina/persist as _;
import ballerinax/persist.sql;

# A department in the company,
# grouping its employees.
type Department record {|
    # The department's id
    readonly string id;
    # The department's name
    string name;
    # The employees in the department
    Employee[] employees;
|};

# A person employed by the company.
#
# An employee belongs to one department.
# + id - Not copied, as only description lines are kept
type Employee record {|
    # The employee's id
    @sql:Generated
    readonly int id;
    # The employee's full name
    @constraint:String {maxLength: 50}
    string name;
    int age;
    # The department the employee works in
    Department department;
|};

type Building record {|
    readonly string code;
    string city;
|};
