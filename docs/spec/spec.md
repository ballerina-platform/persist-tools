# Specification: Ballerina Persist Tools

_Owners_: @daneshk @sahanHe  
_Reviewers_: @daneshk  
_Created_: 2022/07/26   
_Updated_: 2023/01/29  
_Edition_: Swan Lake  

## Introduction

This is the specification for the Persist Tools of [Ballerina language](https://ballerina.io/), which supports several operations on the Ballerina Persistent Layer. Ballerina Persistent Layer provides functionality to store and query data conveniently through a data model instead of SQL query language.

The Persist Tools specification has evolved and may continue to evolve in the future. The released versions of the specification can be found under the relevant GitHub tag.

If you have any feedback or suggestions about the tool, start a discussion via a [GitHub issue](https://github.com/ballerina-platform/ballerina-standard-library/issues) or in the [Discord server](https://discord.gg/ballerinalang). Based on the outcome of the discussion, the specification and implementation can be updated. Community feedback is always welcome. Any accepted proposal, which affects the specification is stored under `/docs/proposals`. Proposals under discussion can be found with the label `type/proposal` in GitHub.

The conforming implementation of the specification is released and included in the distribution. Any deviation from the specification is considered a bug.

## Contents

1. [Overview](#1-overview)
2. [Initializing Persistence Layer in Bal Project](#2-initializing-the-bal-project-with-persistence-layer)
3. [Generating Persistent Derived Types and Clients](#3-generating-persistent-derived-types-and-clients)
4. [Push Persistence Schema to the Data Provider](#4-push-persistence-schema-to-the-data-provider)

## 1. Overview
This specification elaborates on the `Persist CLI Tool` commands.

## 2. Initializing the Bal Project with Persistence Layer

```bash
bal persist init
```

The command initializes the bal project with the persistence layer. This command includes the following steps,

1. Create persist directory
   This directory should contain all data model definition files. This file will define the required entities as per the [`persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#2-data-model-definition)
2. Create a model definition file in persist directory
   It will create a file with same name as the package name if no files are present in the `persist` directory. It will create an empty file with required imports(`import ballerina/persist as _;`).
3. Create database_configurations.bal file inside the `default` module in generated directory
   This file contains configuration variables to initialize the data stores
    ```ballerina
    import ballerinax/mysql.driver as _;

    configurable int port = ?;
    configurable string host = ?;
    configurable string user = ?;
    configurable string database = ?;
    configurable string password = ?;
   ```
4. Update Ballerina.toml with database configurations.
   It will update the Ballerina.toml file with the configurations needed for the `bal persist push` command to work.
    ```ballerina
    [persist.<package name>.storage.mysql]
    host = "localhost"
    port = 3306
    user = "root"
    password = ""
    database = "" 
   ```
5. Create(Update) Config.toml file inside the Ballerina project.
   It will create(update) `Config.toml` file with configurables used to initialize variables in Step 3.
    ```ballerina
    [<package name>]
    host = "localhost"
    port = 3306
    user = "root"
    password = ""
    database = ""
    ```

The directory structure will be,
```
medical-center
├── generated
         └── database_configuration.bal
├── persist
         └── medical-center.bal
├── Ballerina.toml
├── Config.toml
└── main.bal
```

Behaviour of the `init` command,
- User should invoke the command within a bal project
- If the user invokes the command twice, it will not fail. It will execute the steps once again.

## 3. Generating Persistent Derived Types and Clients

```bash
bal persist generate
```

The command will generate [Derived Entity Types and Persist Clients](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#3-derived-entity-types-and-persist-clients)
as per the `persist` specification.

It will add generated files under the conventions,
1. If the file name is the same as the package name, it will generate the files under the `default` module.
   ```
   medical-center
   ├── generated
         ├── database_configuration.bal
         ├── generated_client.bal
         └── generated_types.bal
   ├── persist
         └── medical-center.bal
   ├── Ballerina.toml
   ├── Config.toml
   └── main.bal
   ```
2. If the file name is different to the package name, it will generate the files under a new submodule with the same name as the file.
   ```
   medical-center
   ├── generated
        └── medical-item
              ├── database_configuration.bal
              ├── generated_client.bal
              └── generated_types.bal
   ├── persist
        └── medical-item.bal
   ├── Ballerina.toml
   ├── Config.toml
   └── main.bal
   ```

Behaviour of the `generate` command,
- User should invoke the command within a bal project
- The user should have initiated the persistent layer with the latest set of definition files
- All model definition files should contain the `persist` module import (`import ballerina/persist as _;`)
- The Model definition file should contain at least one entity
- If the user invokes the command twice, it will not fail. It will generate the files once again.

## 4. Push Persistence Schema to the Data Provider

```bash
bal persist push
```

This command will create the database schema associated with the data model definition. 
```
medical-center
├── generated
     ├── database_configuration.bal
     ├── generated_client.bal
     └── generated_types.bal
├── persist
     ├── medical-center_db_script.sql
     └── medical-center.bal
├── Ballerina.toml
├── Config.toml
└── main.bal
```

Additionally, this will run the schema against the database defined in  the `Ballerina.toml` file under the heading ([persist.<definition file name>.storage.mysql])

The database schema will create,
1. Tables for each entity with defined primary keys
2. Create foreign key associations between tables if the model has defined associations between entities

Behaviour of the `push` command,
- User should invoke the command within a bal project
- The user should have initiated the persistent layer with the latest set of definition files
- All model definition files should contain the `persist` module import (`import ballerina/persist as _;`)
- The Model definition file should contain at least one entity
- If the user invokes the command twice, it will not fail. It will generate the schema once again.
