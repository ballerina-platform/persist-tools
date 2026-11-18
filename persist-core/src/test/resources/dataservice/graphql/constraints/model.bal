import ballerina/constraint;
import ballerina/dataservice;
import ballerina/persist as _;
import ballerina/time;

@dataservice:Expose
type Document record {|
    readonly int id;
    @constraint:String {maxLength: 40}
    string title;
    @constraint:Date {option: constraint:PAST}
    time:Date issuedOn;
    @constraint:Int {minValue: 0}
    int pages;
|};

@dataservice:Expose
type Note record {|
    readonly int id;
    string text;
|};
