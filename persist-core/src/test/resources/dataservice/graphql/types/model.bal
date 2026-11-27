import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;

# A reading, with a field of each type.
@dataservice:Expose
type Reading record {|
    readonly int id;
    int count;
    float ratio;
    float? spare;
    decimal amount;
    boolean active;
    time:Date day;
    time:TimeOfDay at;
    time:Civil localTime;
    time:Utc instant;
    time:Date? endDay;
    byte[] payload;
    byte[]? extra;
    Status status;
    Status? previous;
|};

enum Status {
    ACTIVE,
    INACTIVE
}

# A measure, keyed by a float.
@dataservice:Expose {operations: [dataservice:READ, dataservice:UPDATE, dataservice:DELETE]}
type Measure record {|
    readonly float weight;
    string label;
|};
