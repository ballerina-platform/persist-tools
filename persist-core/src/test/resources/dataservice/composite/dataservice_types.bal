// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# A list of assignments.
public type AssignmentList record {
    # The assignments
    Assignment[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that assignments match, as the `filter` query parameter holds them.
public type AssignmentFilter record {|
    # The conditions on the `projectCode` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} projectCode?;
    # The conditions on the `seq` field
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} seq?;
    # The conditions on the `hours` field
    record {|
        decimal eq?;
        decimal ne?;
        decimal lt?;
        decimal lte?;
        decimal gt?;
        decimal gte?;
        decimal[] anyOf?;
    |} hours?;
    # The conditions on the `billable` field
    record {|
        boolean eq?;
        boolean ne?;
        boolean[] anyOf?;
    |} billable?;
|};

# An assignment with the fields named in the `fields` query parameter.
public type AssignmentPartial record {|
    # The `projectCode` field of `Assignment`
    string projectCode;
    # The `seq` field of `Assignment`
    int seq;
    # The `hours` field of `Assignment`
    decimal hours?;
    # The `billable` field of `Assignment`
    boolean billable?;
|};

# A list of assignments with the fields named in the `fields` query parameter.
public type AssignmentPartialList record {
    # The assignments
    AssignmentPartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `Assignment` that callers can see.
final readonly & dataservice:Field[] dataserviceAssignmentFields = [
    {name: "projectCode", column: "projectCode", 'type: dataservice:STRING, key: true},
    {name: "seq", column: "seq", 'type: dataservice:INT, key: true},
    {name: "hours", column: "hours", 'type: dataservice:DECIMAL},
    {name: "billable", column: "billable", 'type: dataservice:BOOLEAN}
];

# The description of `Assignment` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceAssignmentEntity = {
    name: "Assignment",
    collection: "assignments",
    'resource: "assignments",
    datastore: dataservice:H2,
    fields: dataserviceAssignmentFields
};

# The response to creating one assignment.
public type AssignmentCreated record {|
    *http:Created;
    # The created assignment
    Assignment body;
|};

# The response to creating several assignments.
public type AssignmentListCreated record {|
    *http:Created;
    # The created assignments
    AssignmentList body;
|};

# A list of rates.
public type RateList record {
    # The rates
    Rate[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The conditions that rates match, as the `filter` query parameter holds them.
public type RateFilter record {|
    # The conditions on the `amount` field
    record {|
        decimal eq?;
        decimal ne?;
        decimal lt?;
        decimal lte?;
        decimal gt?;
        decimal gte?;
        decimal[] anyOf?;
    |} amount?;
    # The conditions on the `factor` field
    record {|
        float eq?;
        float ne?;
        float lt?;
        float lte?;
        float gt?;
        float gte?;
        float[] anyOf?;
    |} factor?;
    # The conditions on the `active` field
    record {|
        boolean eq?;
        boolean ne?;
        boolean[] anyOf?;
    |} active?;
    # The conditions on the `label` field
    record {|
        string eq?;
        string ne?;
        string lt?;
        string lte?;
        string gt?;
        string gte?;
        string[] anyOf?;
        string contains?;
        string startsWith?;
    |} label?;
|};

# A rate with the fields named in the `fields` query parameter.
public type RatePartial record {|
    # The `amount` field of `Rate`
    decimal amount;
    # The `factor` field of `Rate`
    float factor;
    # The `active` field of `Rate`
    boolean active;
    # The `label` field of `Rate`
    string label?;
|};

# A list of rates with the fields named in the `fields` query parameter.
public type RatePartialList record {
    # The rates
    RatePartial[] items;
    # Whether records exist after this page
    boolean hasMore;
};

# The fields of `Rate` that callers can see.
final readonly & dataservice:Field[] dataserviceRateFields = [
    {name: "amount", column: "amount", 'type: dataservice:DECIMAL, key: true},
    {name: "factor", column: "factor", 'type: dataservice:FLOAT, key: true},
    {name: "active", column: "active", 'type: dataservice:BOOLEAN, key: true},
    {name: "label", column: "label", 'type: dataservice:STRING}
];

# The description of `Rate` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceRateEntity = {
    name: "Rate",
    collection: "rates",
    'resource: "rates",
    datastore: dataservice:H2,
    fields: dataserviceRateFields
};
