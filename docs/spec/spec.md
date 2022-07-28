# Specification: Ballerina Persist Tools

_Owners_: @daneshk @MadhukaHarith92 @sahanHe  
_Reviewers_: @daneshk  
_Created_: 2022/07/26   
_Updated_: 2022/07/26  
_Edition_: Swan Lake  

## Introduction
This is the specification for the Persist Tools of [Ballerina language](https://ballerina.io/), which supports several operations on Ballerina Persistent Layer on top of Ballerina SQL modules and allow performing DB operations easily without writing any SQL statements.

The Persist Tools specification has evolved and may continue to evolve in the future. The released versions of the specification can be found under the relevant GitHub tag.

If you have any feedback or suggestions about the tool, start a discussion via a [GitHub issue](https://github.com/ballerina-platform/ballerina-standard-library/issues) or in the [Slack channel](https://ballerina.io/community/). Based on the outcome of the discussion, the specification and implementation can be updated. Community feedback is always welcome. Any accepted proposal, which affects the specification is stored under `/docs/proposals`. Proposals under discussion can be found with the label `type/proposal` in GitHub.

The conforming implementation of the specification is released and included in the distribution. Any deviation from the specification is considered a bug.

## Contents

1. [Overview](#1-overview)
2. [Generating Database Configurations](#2-generating-database-configurations)

## 1. Overview
This specification elaborates on the operations available in the CLI Tool.

## 2. Generating Database Configurations
The first step is to create database configurations. Users can do this by executing `bal persist init` command inside a Ballerina project. This will add the following entry to the `Config.toml` file inside the project.

```ballerina
[[ballerina.persist]]
host = "localhost"
port = 3306
user = "root"
password =
database =
```

Users can then update the above entry with their database configurations.

- The `bal persist init` command should be executed inside a valid Ballerina project. If not, an error will be thrown.
- If there isn't a `Config.toml` file inside the project root directory, a new `Config.toml` file will get created with the aforementioned configuration.
- If there already is a `Config.toml` file inside the project root directory and there is no entry as `ballerina.persist`, a new entry will be added to the existing `Config.toml` file.
- If there already is a `Config.toml` file inside the project root directory and there already is an entry as `ballerina.persist`, it will be overridden with the above values.
