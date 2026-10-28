// AUTO-GENERATED FILE. DO NOT MODIFY.

// This file is an auto-generated file by Ballerina persistence layer for model.
// It should not be modified by hand.

import ballerina/http;

# A list of assignments.
#
# + items - The assignments
# + hasMore - Whether records exist after this page
public type AssignmentList record {|
    Assignment[] items;
    boolean hasMore;
|};

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
