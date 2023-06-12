import ballerina/persist as _;

type MedicalItem record {|
    readonly int itemId;
    string name;
    string itemType;
    string unit;
    float quantity;
    decimal price;
|};

type MedicalNeed record {|
    readonly int needId;
    int itemId;
    int beneficiaryId;
    string urgency;
    int quantity;
|};
