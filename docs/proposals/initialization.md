# Proposal: Generate database configurations using `bal persist init` command

_Owners_: @daneshk @MadhukaHarith92 @sahanHe  
_Reviewers_: @daneshk  
_Created_: 2022/07/26   
_Updated_: 2022/07/26  
_Issues_: [#3163](https://github.com/ballerina-platform/ballerina-standard-library/issues/3163)

## Summary
We need to support Ballerina Persistent Layer on top of Ballerina SQL modules and support DB operations easily without writing any SQL statements. As a part of it, the CLI tool will be provided to support several operations. One of those operations is to create database configurations in `Config.toml` file inside the Ballerina project using the `bal persist init` command. In this proposal, we describe how this requirement can be facilitated.

## Goals
To support generating database configurations.

## Motivation
To generate an entry in `Config.toml` file inside the Ballerina project with sample database configurations. Users can then modify this configuration file with their database configuration details. This database information will be later used when creating databases and tables using `bal persist db push` command.

## Description
When users execute `bal persist init` inside a Ballerina project, the following entry will get added to the `Config.toml` file inside the project.

```ballerina
[[ballerina.persist]]
host = "localhost"
port = 3306
user = "root"
password =
database =
```

- The `bal persist init` command should be executed inside a valid Ballerina project. If not, an error will be thrown.
- If there isn't a `Config.toml` file inside the project root directory, a new `Config.toml` file will get created with the aforementioned configuration.
- If there already is a `Config.toml` file inside the project root directory and there is no entry as `ballerina.persist`, a new entry will be added to the existing `Config.toml` file.
- If there already is a `Config.toml` file inside the project root directory and there already is an entry as `ballerina.persist`, it will be overridden with the above values.

## Testing
- Execute the command outside a Ballerina project. Validate if the expected error was returned.
- Execute the command inside a Ballerina project that does not have a `Config.toml` file inside the project root directory. Validate if a new `Config.toml` file is generated inside the project root directory with the above entry.
- Execute the command inside a Ballerina project that already has a `Config.toml` file inside the project root directory but without a `ballerina.persist` entry. Validate if a new `ballerina.persist` entry with the above values is added to the `Config.toml` file.
- Execute the command inside a Ballerina project that already has a `Config.toml` file inside the project root directory with a `ballerina.persist` entry. Validate if the `ballerina.persist` entry was updated with the above values.
