# Proposal: Create databases and tables using `bal persist db push` command

_Owners_: @daneshk @MadhukaHarith92 @sahanHe  
_Reviewers_: @daneshk  
_Created_: 2022/09/05   
_Updated_: 2022/09/05  
_Issues_: [#3157](https://github.com/ballerina-platform/ballerina-standard-library/issues/3157)

## Summary
We need to support Ballerina Persistent Layer on top of Ballerina SQL modules and support DB operations easily without writing any SQL statements. As a part of it, the CLI tool will be provided to support several operations. One of those operations is to create databases and tables for the entity records defined in the Ballerina project. In this proposal, we describe how this requirement can be facilitated.

## Goals
To support creating databases and tables.

## Motivation
To create databases and tables corresponding to the entity records using the database configurations provided by the users. Users can then use these client objects to perform database operations programmatically without having to write SQL statements.

## Description
Consider the following Ballerina project named `medical-center`.
```
medical-center
├── Ballerina.toml
├── entities.bal
└── main.bal
```

Users can define Entity records inside the Ballerina project.
```ballerina
import ballerina/time;
import ballerina/persist;

@persist:Entity {
    key: ["needId"],
    tableName: "MedicalNeeds"
}
public type MedicalNeed record {|
    @persist:AutoIncrement
    readonly int needId = -1;

    int itemId;
    int beneficiaryId;
    time:Civil period;
    string urgency;
    int quantity;
|};
```

Users can provide database configurations through the `Config.toml` file in the project root.
```
[foo.bar]
host = "localhost"
port = 3306
user = "root"
password = "root@123"
database = "testdb"
```

When users execute `bal persist db push` inside a Ballerina project, the `MedicalNeeds` table will be created with `needId` as the primary key and columns: `itemId`, `beneficiaryId`, `period`, `urgency`, and `quantity`.

- The `bal persist push` command should be executed inside a valid Ballerina project. If not, an error will be thrown.
- If the database given in the `Config.toml` file does not exist a new database will be created or an error will be returned.
- If a table defined in an entity record does not exist, a new table will be created.
- If a table defined in an entity record already exists it will be dropped and a new table will be created.

## Testing
- Execute the command outside a Ballerina project. Validate if the expected error was returned.
- Execute the command when the database given in the `Config.toml` file does not exist. Validate if a new database was created.
- Execute the command when a table defined in an entity record does not exist. Validate if a new table was created.
- Execute the command when a table defined in an entity record already exists with data. Validate if a new table was created.
- Execute the command when a database configuration(`host`, `port`, `user`, `password`) provided by a user is invalid. Validate if an error is returned.
