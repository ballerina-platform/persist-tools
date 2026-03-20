import ballerina/persist as _;

type Class record {|
    readonly int id;
    string name;
    Enrollment[] enrollments;
|};

type Enrollment record {|
    readonly int id;
    Class 'class;
|};
