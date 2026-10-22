import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;

@dataservice:Expose
type Document record {|
    readonly int id;
    string title;
    time:Date issuedOn;
    time:Date? expiresOn;
    time:TimeOfDay opensAt;
    time:Civil createdAt;
    time:Utc? signedAt;
    byte[] content;
    byte[]? thumbnail;
    Status status;
|};

enum Status {
    DRAFT,
    FINAL
}

@dataservice:Expose {operations: [dataservice:READ]}
type Holiday record {|
    readonly int id;
    time:Date day;
|};
