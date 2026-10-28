// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/dataservice;
import ballerina/http;

# A list of assignments.
#
# + items - The assignments
# + hasMore - Whether records exist after this page
public type AssignmentList record {|
    Assignment[] items;
    boolean hasMore;
|};

# The conditions that assignments match, as the `filter` query parameter holds them.
#
# + projectCode - The conditions on the `projectCode` field
# + seq - The conditions on the `seq` field
# + hours - The conditions on the `hours` field
# + billable - The conditions on the `billable` field
public type AssignmentFilter record {|
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
    record {|
        int eq?;
        int ne?;
        int lt?;
        int lte?;
        int gt?;
        int gte?;
        int[] anyOf?;
    |} seq?;
    record {|
        decimal eq?;
        decimal ne?;
        decimal lt?;
        decimal lte?;
        decimal gt?;
        decimal gte?;
        decimal[] anyOf?;
    |} hours?;
    record {|
        boolean eq?;
        boolean ne?;
        boolean[] anyOf?;
    |} billable?;
|};

# An assignment with the fields named in the `fields` query parameter.
#
# + projectCode - The `projectCode` field of `Assignment`
# + seq - The `seq` field of `Assignment`
# + hours - The `hours` field of `Assignment`
# + billable - The `billable` field of `Assignment`
public type AssignmentPartial record {|
    string projectCode;
    int seq;
    decimal hours?;
    boolean billable?;
|};

# A list of assignments with the fields named in the `fields` query parameter.
#
# + items - The assignments
# + hasMore - Whether records exist after this page
public type AssignmentPartialList record {|
    AssignmentPartial[] items;
    boolean hasMore;
|};

# The description of `Assignment` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceAssignmentEntity = {
    name: "Assignment",
    'resource: "assignments",
    datastore: dataservice:H2,
    fields: [
        {name: "projectCode", column: "projectCode", 'type: dataservice:STRING, key: true},
        {name: "seq", column: "seq", 'type: dataservice:INT, key: true},
        {name: "hours", column: "hours", 'type: dataservice:DECIMAL},
        {name: "billable", column: "billable", 'type: dataservice:BOOLEAN}
    ]
};

# The response to creating one assignment.
#
# + body - The created assignment
public type AssignmentCreated record {|
    *http:Created;
    Assignment body;
|};

# The response to creating several assignments.
#
# + body - The created assignments
public type AssignmentListCreated record {|
    *http:Created;
    AssignmentList body;
|};

# A list of rates.
#
# + items - The rates
# + hasMore - Whether records exist after this page
public type RateList record {|
    Rate[] items;
    boolean hasMore;
|};

# The conditions that rates match, as the `filter` query parameter holds them.
#
# + amount - The conditions on the `amount` field
# + factor - The conditions on the `factor` field
# + active - The conditions on the `active` field
# + label - The conditions on the `label` field
public type RateFilter record {|
    record {|
        decimal eq?;
        decimal ne?;
        decimal lt?;
        decimal lte?;
        decimal gt?;
        decimal gte?;
        decimal[] anyOf?;
    |} amount?;
    record {|
        float eq?;
        float ne?;
        float lt?;
        float lte?;
        float gt?;
        float gte?;
        float[] anyOf?;
    |} factor?;
    record {|
        boolean eq?;
        boolean ne?;
        boolean[] anyOf?;
    |} active?;
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
#
# + amount - The `amount` field of `Rate`
# + factor - The `factor` field of `Rate`
# + active - The `active` field of `Rate`
# + label - The `label` field of `Rate`
public type RatePartial record {|
    decimal amount;
    float factor;
    boolean active;
    string label?;
|};

# A list of rates with the fields named in the `fields` query parameter.
#
# + items - The rates
# + hasMore - Whether records exist after this page
public type RatePartialList record {|
    RatePartial[] items;
    boolean hasMore;
|};

# The description of `Rate` that `ballerina/dataservice` builds queries from.
final dataservice:Entity dataserviceRateEntity = {
    name: "Rate",
    'resource: "rates",
    datastore: dataservice:H2,
    fields: [
        {name: "amount", column: "amount", 'type: dataservice:DECIMAL, key: true},
        {name: "factor", column: "factor", 'type: dataservice:FLOAT, key: true},
        {name: "active", column: "active", 'type: dataservice:BOOLEAN, key: true},
        {name: "label", column: "label", 'type: dataservice:STRING}
    ]
};
