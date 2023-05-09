import ballerina/persist as _;
import ballerina/time;

public type MedicalNeed record {|
    readonly string itemId;

    string needId;
    string description;
    time:Civil period;
|};

public type MedicalObject record {|
    readonly int objectId;

    string objectName;
    string types;
    boolean objectFlag;
    MedicalTest[] test;
|};

public type MedicalTest record {|
    readonly string testId;
    MedicalObject object1;
|};