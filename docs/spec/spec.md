# Specification: Ballerina Persist Tools

_Owners_: @daneshk @sahanHe  
_Reviewers_: @daneshk  
_Created_: 2022/07/26   
_Updated_: 2023/01/29  
_Edition_: Swan Lake  

## Introduction

This is the specification for the Persist Tools of [Ballerina language](https://ballerina.io/), which supports several operations on the Ballerina Persistence Layer. Ballerina Persistent Layer provides functionality to store and query data conveniently through a data model instead of SQL query language.

The Persist Tools specification has evolved and may continue to evolve in the future. The released versions of the specification can be found under the relevant GitHub tag.

If you have any feedback or suggestions about the tool, start a discussion via a [GitHub issue](https://github.com/ballerina-platform/ballerina-standard-library/issues) or in the [Discord server](https://discord.gg/ballerinalang). Based on the outcome of the discussion, the specification and implementation can be updated. Community feedback is always welcome. Any accepted proposal, which affects the specification is stored under `/docs/proposals`. Proposals under discussion can be found with the label `type/proposal` in GitHub.

The conforming implementation of the specification is released and included in the distribution. Any deviation from the specification is considered a bug.

## Contents

1. [Overview](#1-overview)
2. [Initializing Persistence Layer in Bal Project](#2-initializing-the-bal-project-with-persistence-layer)
3. [Generating Persistence Derived Types, Clients, and Database Schema](#3-generating-persistence-derived-types-and-clients)
4. [Push Persistence Schema to the Data Provider](#4-push-persistence-schema-to-the-data-provider)

## 1. Overview
This specification elaborates on the `Persist CLI Tool` commands.

## 2. Initializing the Bal Project with Persistence Layer

```bash
bal persist add --datastore="datastore" --module="module_name"
```

| Command Parameter |                                       Description                                        | Mandatory | Default Value  |
|:-----------------:|:----------------------------------------------------------------------------------------:|:---------:|:--------------:|
|    --datastore    |  used to indicate the preferred database client. Currently, 'mysql', 'mssql', 'google sheets' and 'postgresql' are supported.   |    No     |    inmemory    |
|     --module      |      used to indicate the persist enabled module in which the files are generated.       |    No     | <package_name> |
| --id  | Used as an identifier | No | generate-db-client |


The command initializes the bal project with the persistence layer. This command includes the following steps,

1. Create persist directory
   This directory should contain the data model definition file. This file will define the required entities as per the [`persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#2-data-model-definition)
2. Create a model definition file in persist directory
   It will create a file named `model.bal` with required imports(`import ballerina/persist as _;`),  if no files are present in the `persist` directory.
3. Update Ballerina.toml with persist module configurations.
   It will update the Ballerina.toml file with persist configurations.
    ```ballerina
    [[tool.persist]]
    id = "generate-db-client"
    targetModule = "<package_name>.<module_name>"
    options.datastore = "<datastore>"
    filePath = "persist/model.bal"
   ```

The directory structure will be,
```
medical-center
├── persist
         └── model.bal
├── Ballerina.toml
└── main.bal
```

Behaviour of the `add` command,
- Users should invoke the command within a Ballerina project.
- Users can use optional arguments to indicate the preferred module name and data store; otherwise, default values will be used.
- Users cannot execute the command multiple times within the same project. They need to remove the persist configurations from the Ballerina.toml if they want to reinitialize the project.

Apart from the `bal persist add` command, if you want to use the `bal persist generate` command you can initialize the project with the following `init` command,

```bash
bal persist init
```

This command includes the following steps,

1. Create persist directory:
   Within this directory, a data model definition file should be created. This file will outline the necessary entities according to the [`persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#2-data-model-definition)
2. Generate a model definition file within the persist directory:
   This action will create a file named model.bal with the requisite imports (import ballerina/persist as _;) if no files currently exist in the persist directory.

## 3. Generating Persistence Derived Types, Clients, and Database Schema

```bash
bal build
```

The `bal build` command will generate [Derived Entity Types and Persist Clients](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#3-derived-entity-types-and-persist-clients)
as per the `persist` specification and the database schema associated with the data model definition.
Additionally, this command will create(update) `Config.toml` file with configurables used to initialize variables in `persist_db_config.bal`.
```ballerina
[<data model name(definition filename>]
host = "localhost"
port = 3306
user = "root"
password = ""
database = ""
```

It will add generated files under the conventions,
1. If the module name is the same as the package name, it will generate the files under the `default` module.
   ```
   medical-center
   ├── generated
         ├── persist_client.bal
         ├── persist_db_config.bal
         ├── persist_types.bal
         └── script.sql
   ├── persist
         └── model.bal
   ├── Ballerina.toml
   ├── Config.toml
   └── main.bal
   ```
2. If the module name is different from the package name, it will generate the files under a new submodule with the same name as the file.
   ```
   medical-center
   ├── generated
        └── medical-item
              ├── persist_client.bal
              ├── persist_db_config.bal
              ├── persist_types.bal
              └── script.sql
   ├── persist
        └── model.bal
   ├── Ballerina.toml
   ├── Config.toml
   └── main.bal
   ```
   
`persist_db_config.bal` file will contain the configurable variables required for the database access.
 ```ballerina
 import ballerinax/mysql.driver as _;

 configurable int port = ?;
 configurable string host = ?;
 configurable string user = ?;
 configurable string database = ?;
 configurable string password = ?;
```

The database schema will contain the code to create,
1. Tables for each entity with defined primary keys
2. Create foreign key associations between tables if the model has defined associations between entities

Apart from the `bal build` users can also use the following command to generate the same as above mentioned,

```bash
bal persist generate --datastore mysql --module db
```

| Command Parameter |                                       Description                                        | Mandatory | Default Value  |
|:-----------------:|:----------------------------------------------------------------------------------------:|:---------:|:--------------:|
|    --datastore    |  used to indicate the preferred database client. Currently, 'mysql', 'mssql', 'google sheets' and 'postgresql' are supported.   |    Yes     |        |
|     --module      |      used to indicate the persist enabled module in which the files are generated.       |    No     | <package_name> |

Behaviour of the `generate` command,
- User should invoke the command within a Ballerina project
- The model definition file should contain the `persist` module import (`import ballerina/persist as _;`)
- The Model definition file should contain at least one entity
- If the user invokes the command twice, it will not fail. It will generate the files once again.

## 4. Push Persistence Schema to the Data Provider

>**Info:** This command is only supported yet. The SQL script generated by the `generate` command can be used to create the database schema.
> 
```bash
bal persist push
```

This command will run the schema against the database defined in  the `Ballerina.toml` file under the heading ([persist.model.storage.mysql]).
Database configuration in Ballerina.toml should look like following,
```
[persist.model.storage.mysql]
host = "localhost"
port = 3306
user = "root"
password = "Test123#"
database = "persist"
```

The file structure of the project should be similar to the following before running the command.
```
medical-center
├── generated
     ├── persist_client.bal
     ├── persist_db_config.bal
     ├── persist_types.bal
     └── script.sql
├── persist
     └── medical-center.bal
├── Ballerina.toml
├── Config.toml
└── main.bal
```

Running the database schema will create,
1. Tables for each entity with defined primary keys
2. Create foreign key associations between tables if the model has defined associations between entities

Behaviour of the `push` command,
- User should invoke the command within a Ballerina project
- User should add the relevant configuration to the Ballerina.toml file.
- The user should have initiated the persistence layer in the project and executed the `generate` command to generate the SQL script.
- If the user invokes the command twice, it will not fail. It will rerun the SQL script against the database.
