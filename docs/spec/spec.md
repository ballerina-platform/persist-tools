# Specification: Ballerina Persist Tools

_Owners_: @daneshk @sahanHe @TharmiganK  
_Reviewers_: @daneshk  
_Created_: 2022/07/26   
_Updated_: 2026/10/22  
_Edition_: Swan Lake  

## Introduction

This is the specification for the Persist Tools of [Ballerina language](https://ballerina.io/), which supports several operations on the Ballerina Persistence Layer. Ballerina Persistent Layer provides functionality to store and query data conveniently through a data model instead of SQL query language.

The Persist Tools specification has evolved and may continue to evolve in the future. The released versions of the specification can be found under the relevant GitHub tag.

If you have any feedback or suggestions about the tool, start a discussion via a [GitHub issue](https://github.com/ballerina-platform/ballerina-standard-library/issues) or in the [Discord server](https://discord.gg/ballerinalang). Based on the outcome of the discussion, the specification and implementation can be updated. Community feedback is always welcome. Any accepted proposal, which affects the specification is stored under `/docs/proposals`. Proposals under discussion can be found with the label `type/proposal` in GitHub.

The conforming implementation of the specification is released and included in the distribution. Any deviation from the specification is considered a bug.

## Contents

1. [Overview](#1-overview)
2. [Initializing Persistence Layer in Bal Project](#2-initializing-the-bal-project-with-persistence-layer)
3. [Generating Persistence Derived Types, Clients, and Database Schema](#3-generating-persistence-derived-types-clients-and-database-schema)
4. [Push Persistence Schema to the Data Provider](#4-push-persistence-schema-to-the-data-provider)
5. [Pull Persistence Schema from the Data Provider](#5-pull-persistence-schema-from-the-data-provider)
6. [Migrate Persistence Schema changes to the Data Provider](#6-migrate-persistence-schema-to-the-data-provider)
7. [Data Service Generation](#7-data-service-generation)

## 1. Overview
This specification elaborates on the `Persist CLI Tool` commands.

## 2. Initializing the Bal Project with Persistence Layer

```bash
bal persist add --datastore mysql --module db --test-datastore h2
```

| Command Parameter |                                                         Description                                                          | Mandatory |   Default Value    |
|:-----------------:|:----------------------------------------------------------------------------------------------------------------------------:|:---------:|:------------------:|
|    --datastore    | used to indicate the preferred database client. Currently, 'mysql', 'mssql', 'google sheets' and 'postgresql' are supported. |    No     |      inmemory      |
|     --module      |                        used to indicate the persist enabled module in which the files are generated.                         |    No     |   <package_name>   |
|       --id        |                                                    Used as an identifier                                                     |     No    | generate-db-client |
| --test-datastore  |            used to indicate the preferred database client for testing. Currently, 'h2', 'inmemory' are supported.            |     No    |         No         |
|      --model      |         used to indicate the name of the model. If not specified, the command applies to the default root model.             |     No    |         No         |
|   --dataservice   | used to enable a data service for the given protocol. Currently, 'http' is supported. See [Data service generation](#7-data-service-generation). |     No    |         No         |


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
    options.testDatastore = "<test_datastore>"
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
- User can use test-datastore to indicate the preferred database client for testing. Currently, 'h2', 'inmemory' are supported. If specified it will update the Ballerina.toml file with the testDatastore option to generate the test client along with the main client at the build time.
- Users cannot execute the command multiple times within the same project. They need to remove the persist configurations from the Ballerina.toml if they want to reinitialize the project.

Apart from the `bal persist add` command, if you want to use the `bal persist generate` command you can initialize the project with the following `init` command,

```bash
bal persist init --model <model-name>
```

This command includes the following steps,

1. Create persist directory:
   Within this directory, a data model definition file should be created. This file will outline the necessary entities according to the [`persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#2-data-model-definition)
2. Generate a model definition file within the persist directory:
   This action will create a file named `model.bal` with the requisite imports (`import ballerina/persist as _;`) if no files currently exist in the persist directory.
   - If `--model <model-name>` is specified, it creates the directory structure `persist/<model-name>/` and generates `model.bal` inside it.
   - If `--model` is not specified, it generates `persist/model.bal` (default behavior).

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
         ├── persist_test_client.bal (This file will be generated if the test-datastore is provided)
         ├── persist_test_init.bal (This file will be generated if h2 is provided as the test-datastore)
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
              ├── persist_test_client.bal (This file will be generated if the test-datastore is provided)
              ├── persist_test_init.bal (This file will be generated if h2 is provided as the test-datastore)
              ├── persist_types.bal
              └── script.sql
   ├── persist
        └── model.bal
   ├── Ballerina.toml
   ├── Config.toml
   └── main.bal
   ```

`persist_client.bal` file will contain the client API for the entities defined in the model file.
`persist_db_config.bal` file will contain the configurable variables required for the database access.
 ```ballerina
 import ballerinax/mysql.driver as _;

 configurable int port = ?;
 configurable string host = ?;
 configurable string user = ?;
 configurable string database = ?;
 configurable string password = ?;
```
`persist_types.bal` file will contain the derived entity types based on the entities defined in the model file.
`persist_test_client.bal` file will contain the client API for the entities defined in the model file for the test-datastore.
`persist_test_init.bal` file will contain the initialization of the test database schema based on the entities defined in the model file for the test-datastore.
`script.sql` file will contain the SQL script to create the database schema based on the entities defined in the model file.

The database schema will contain the code to create,
1. Tables for each entity with defined primary keys
2. Create foreign key associations between tables if the model has defined associations between entities

Apart from the `bal build` users can also use the following command to generate the same as above mentioned,

```bash
bal persist generate --datastore mysql --module db --test-datastore h2
```

| Command Parameter |                                       Description                                        | Mandatory | Default Value  |
|:-----------------:|:----------------------------------------------------------------------------------------:|:---------:|:--------------:|
|    --datastore    |  used to indicate the preferred database client. Currently, 'mysql', 'mssql', 'google sheets' and 'postgresql' are supported.   |    Yes     |        |
|     --module      |      used to indicate the persist enabled module in which the files are generated.       |    No     | <package_name> |
| --test-datastore  |            used to indicate the preferred database client for testing. Currently, 'h2', 'inmemory' are supported.            |    No     |         No         |
|      --model      |         used to indicate the name of the model. If not specified, the command applies to the default root model.             |    No     |         No         | 
|   --dataservice   | used to also generate a data service for the given protocol into the package root. See [Data service generation](#7-data-service-generation). |     No    |         No         |

Behaviour of the `generate` command,
- User should invoke the command within a Ballerina project
- The model definition file should contain the `persist` module import (`import ballerina/persist as _;`)
- The Model definition file should contain at least one entity
- If the user invokes the command twice, it will not fail. It will generate the files once again.

## 4. Push Persistence Schema to the Data Provider

>**Info:** This command is not supported yet. The SQL script generated by the `generate` command can be used to create the database schema.
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

## 5. Pull Persistence Schema from the Data Provider

```bash
# Introspect all tables (default behavior)
bal persist pull --datastore mysql --host localhost --port 3306 --user root --database persist

# Introspect only specific tables
bal persist pull --datastore mysql --host localhost --port 3306 --user root --database persist --tables=users,orders

# Use interactive mode to select tables
bal persist pull --datastore mysql --host localhost --port 3306 --user root --database persist --tables
```
| Command Parameter |                                       Description                                       | Mandatory |   Default Value    |
|:-----------------:|:---------------------------------------------------------------------------------------:|:---------:|:------------------:|
|    --datastore    | used to indicate the preferred data store. supports `mysql`, `postgresql`, and `mssql`. |    No     |       mysql        |
|      --host       |                           used to indicate the database host                            |    Yes    |        None        |
|      --port       |                           used to indicate the database port                            |    No     | datastore specific |
|      --user       |                           used to indicate the database user                            |    Yes    |        None        |
|    --database     |                           used to indicate the database name                            |    Yes    |        None        |
|     --tables      | enable table selection. accepts table names or triggers interactive mode if no value    |    No     |    all tables      |
|      --model      |         used to indicate the name of the model. If not specified, the command applies to the default root model.             |     No    |         No         |
|   --dataservice   | used to annotate the pulled entities and enable a data service for the given protocol. See [Data service generation](#7-data-service-generation). |     No    |         No         |
|   --expose-tables   | used to annotate only the entities of the given tables. Requires `--dataservice`, unless the entry already enables a data service. |     No    |         No         |

This command will introspect the schema of the database and create a `model.bal` file with the entities and relations based on the schema of the database. The database configuration should be provided as command-line arguments.

This command should execute within a Ballerina project.  

The `persist` directory is created if it is not already present. If a `model.bal` file is already present in the `persist` directory (or specific model directory if `--model` is used), it will prompt the user to confirm overwriting the existing `model.bal` file.

Running the `pull` command will,
1. Create a `model.bal` file with the entities and relations based on the introspected schema of the database.
2. Not change the schema of the database in any way.

Behaviour of the `pull` command,
- User should invoke the command within a Ballerina project.
- User should provide the relevant database configuration as command-line arguments.
- By default (without `--tables`), all tables in the database will be introspected.
- If `--tables=table1,table2` is provided, only the specified tables will be introspected. **Note:** Tables referenced by foreign keys are automatically included to ensure valid model generation.
- If `--tables` is provided without a value, an interactive prompt will display available tables (up to 50) and allow the user to select which tables to introspect. Users can:
  - Enter table names separated by commas
  - Enter table numbers separated by commas
  - Enter 'all' or press Enter to select all tables
  - Enter 'q' or 'quit' to abort the operation
- When specific tables are selected, the system automatically includes any tables referenced by foreign keys from the selected tables. INFO messages will be logged for each auto-included table.
- The database password is not provided as a command-line argument. The user will be prompted to enter the password.
- If the user invokes the command while a `model.bal` file exists in the `persist` directory, it will prompt the user to confirm overwriting the existing `model.bal` file.
- If the user introspects a database with unsupported data types, it will inform the user by giving a warning and will comment out the relevant field with the tag `[Unsupported[DATA_TYPE]]`.
- The user must execute the `generate` command to generate the derived types and client API after running the `pull` command in order to use the client API in the project.

## 6. Migrate Persistence Schema to the Data Provider

```bash
bal persist migrate --datastore mysql migrationLabel
```
| Command Parameter |                                   Description                                    | Mandatory | Default Value |
|:-----------------:|:--------------------------------------------------------------------------------:|:---------:|:-------------:|
|    --datastore    | used to indicate the preferred data store. Currently, only 'mysql' is supported. |    No     |     mysql     |
|      --model      |         used to indicate the name of the model. If not specified, the command applies to the default root model.             |    No     |         No         |

This command will generate the required migration scripts based on the changes in the `model.bal` file.

This command should execute within a Ballerina project.

The file structure of the project should be similar to the following after running the command.

```
rainier
   ├── persist
         └── model.bal
         └── migrations
               └── 20240410120000_migrationLabel
                  ├── model.bal
                  └── script.sql     
   ├── Ballerina.toml
   └── main.bal
```

The `persist/migrations` directory is created if it is not already present. If the `persist/migrations` directory already contains any migrations, the tool will look at the last migration and generate the next migration script based on the last `model.bal` file that was migrated.

Running the `migrate` command will,
1. Generate a migration script based on the changes in the `model.bal` file. These can be found at `persist/migrations` directory (or `persist/<model-name>/migrations` if `--model` is used), within another directory named in the format `[TIMESTAMP]_[migrationLabel]` where the `migrationLabel` is provided as a positional CLI argument. This directory will contain the latest `model.bal` file and the migration script.
2. Not run the migration script against the database. The user should run the migration script against the database manually after reviewing the script.

Behaviour of the `migrate` command,
- User should invoke the command within a Ballerina project.
- User should provide the migration label as a positional argument. The user also can provide the data store to the `--datastore` parameter to indicate the preferred data store. However, as only `mysql` is currently supported, it is optional and is set to `mysql` by default.

## 7. Data Service Generation

The persist tool can generate a data service over a persist model, as defined in the
[data service proposal](https://github.com/ballerina-platform/ballerina-spec/issues/1503) and its
[HTTP proposal](https://github.com/ballerina-platform/ballerina-spec/issues/1513). The model selects what is exposed
with the `@dataservice:Expose` annotation of the
[`ballerina/dataservice`](https://github.com/ballerina-platform/module-ballerina-dataservice/blob/main/docs/spec/spec.md)
package, and `Ballerina.toml` says how the service is generated.

```bash
bal persist add --datastore postgresql --dataservice http
# annotate the entities to expose in persist/model.bal
bal build
```

### 7.1. Configuration

```toml
[[tool.persist]]
id = "generate-db-client"
targetModule = "hr"
filePath = "persist/model.bal"
options.datastore = "postgresql"
options.dataservice.protocol = "http"
options.dataservice.basePath = "/api"
```

| Key | Required | Default | Meaning |
|---|---|---|---|
| `options.dataservice.protocol` | yes | — | The protocol the service is generated for. Only `http` is supported |
| `options.dataservice.basePath` | no | `/api` | The base path of the service |

`basePath` must start with `/`. A trailing `/` is removed, and `/` alone serves the service at the root. Each segment
must be non-empty and contain only letters, digits, `_`, `-` and `.`.

| `options.dataservice` | `protocol` | Annotated entities | Result |
|---|---|---|---|
| absent | — | none | No service; persist behaves as it does without data services |
| absent | — | some | No service, and a warning that `protocol` is not set |
| present | absent | any | Error: `protocol` is required |
| present | set | none | Error listing the entities that can be annotated |
| present | set | some | The service is generated over the annotated entities |

A data service is generated only into the package's default module, so `targetModule` must be the package name. It
cannot be combined with `options.withInitParams = true`, because the service creates the client with `new ()`, or with a
named model, because a package has at most one data service.

### 7.2. Validation and messages

The tool validates the annotations from the model's syntax tree each time it generates, after the model has compiled.
The `ballerina/dataservice` compiler plugin reports the checks it shares with the tool, with the same text, when the
model is compiled, so a model with such an error fails to compile before the tool's own check runs.

Errors are printed with the `ERROR:` prefix, or as the build tool's diagnostic in `bal build`, and fail the command.
Warnings are printed with `WARNING:` and do not.

| Case | Severity | Message |
|---|---|---|
| `protocol` missing | error | `'options.dataservice.protocol' is required. Supported values: http.` |
| `protocol` is `graphql`, `mcp`, `odata` or `soap` | error | `the protocol '<value>' is not supported yet. Supported values: http.` |
| `protocol` is any other value | error | `invalid 'options.dataservice.protocol' '<value>'. Supported values: http.` |
| `basePath` without a leading `/` | error | `invalid 'options.dataservice.basePath' '<value>': the base path must start with '/'.` |
| `basePath` with an empty or invalid segment | error | `invalid 'options.dataservice.basePath' '<value>': each path segment must be non-empty and contain only letters, digits, '_', '-' and '.'.` |
| `targetModule` is not the package | error | `a data service is generated only into the default module '<package>'. Remove 'targetModule' or set it to '<package>'.` |
| `withInitParams = true` | error | `a data service cannot be generated with 'options.withInitParams', because the service creates the client without arguments.` |
| A named model | error | `a data service is not supported for a named persist model. Use 'persist/model.bal' or remove 'options.dataservice'.` |
| No annotated entity | error | `the data service exposes no entity. Add the @dataservice:Expose annotation to the entities to expose: <Entity>, <Entity>.` |
| An annotated entity with an unsupported data type | error | `the entity '<Entity>' has an unsupported data type and cannot be exposed. Remove its @dataservice:Expose annotation.` |
| `Expose` on something other than a record type | error | `the @dataservice:Expose annotation is allowed only on a record type in the persist model.` |
| `operations` empty | error | `'operations' in the @dataservice:Expose annotation of the entity '<Entity>' cannot be empty.` |
| `operations` not a list literal | error | `'operations' in the @dataservice:Expose annotation of the entity '<Entity>' must be a list of operations.` |
| An unknown operation | error | `invalid operation '<value>' in the @dataservice:Expose annotation of the entity '<Entity>'. Supported values: dataservice:READ, dataservice:CREATE, dataservice:UPDATE, dataservice:DELETE.` |
| A duplicate operation | error | `duplicate operation '<value>' in the @dataservice:Expose annotation of the entity '<Entity>'.` |
| Annotations without `options.dataservice` | warning | `the model has @dataservice:Expose annotations, but no data service is generated. Set 'options.dataservice.protocol' in the [[tool.persist]] entry to generate one.` |
| `pull` skips an entity | warning | `the table '<table>' has an unsupported data type, so its entity '<Entity>' is not exposed.` |
| `generate` without `--dataservice` on an annotated model | info | `the model has @dataservice:Expose annotations. Use the '--dataservice' option to also generate a data service.` |
| `--expose-tables` without `--dataservice`, when the entry does not enable a data service | error | `the '--expose-tables' option requires the '--dataservice' option.` |
| `--expose-tables` names a table that was not pulled | error | `the table '<table>' in '--expose-tables' is not a pulled table.` |
| `--expose-tables` names a table with an unsupported data type | error | `the table '<table>' in '--expose-tables' has an unsupported data type, so its entity '<Entity>' cannot be exposed.` |
| `add --dataservice` succeeds | info | The next steps `2. Add the @dataservice:Expose annotation to each entity to expose.` and `3. Execute `bal build` to generate the persist client and the <PROTOCOL> data service during package build.` |
| `pull --dataservice` succeeds | info | `Enabled the <PROTOCOL> data service in the [[tool.persist]] entry of persist/model.bal in Ballerina.toml.` |

`<value>` in an operation message is the operation as it is written, such as `dataservice:READ` or `"read"`. Every
invalid annotation, and every invalid `--expose-tables` name, is reported, one line each.

### 7.3. Commands

| Command | Behaviour with `--dataservice <protocol>` |
|---|---|
| `bal persist add` | Writes the entry as without the option, and adds `options.dataservice.protocol`. It cannot be combined with `--module`, `--model` or `--with-init-params` |
| `bal persist pull` | Writes the model with `@dataservice:Expose` on each entity to expose and the `ballerina/dataservice` import, then sets `options.dataservice.protocol` in the entry whose `filePath` is `persist/model.bal`, keeping its `basePath`, or creates that entry as `add` would. It cannot be combined with `--model` |
| `bal persist generate` | Generates the client and the data service once, into the package root. It cannot be combined with `--module`, `--model` or `--with-init-params` |
| `bal build` | Generates the client and, when `options.dataservice` is present, the data service |

- **`pull`:** without `--expose-tables`, every pulled entity is annotated except one with an unsupported data type,
  which is named in a warning. With `--expose-tables <table,...>`, only the entities of the named tables are
  annotated. Names are separated by commas and matched exactly, as `--tables` matches them. Before introspecting,
  `pull` rejects an entry whose `targetModule` is not the package or that sets `withInitParams = true`.
- **`pull` without `--dataservice`:** when the entry of `persist/model.bal` already has `options.dataservice`, `pull`
  annotates the pulled entities in the same way, using the entry's protocol, accepts `--expose-tables`, and leaves the
  entry unchanged, so a later `pull` keeps the service. The entry's `options.dataservice` is validated as `bal build`
  validates it. When the entry has no `options.dataservice`, `pull` behaves as it does without data services.
- A command that ends in an error leaves the model and `Ballerina.toml` unchanged.

### 7.4. Generated files

| File | Contents |
|---|---|
| `dataservice.bal` | The persist client the service uses, the listener and the service |
| `dataservice_types.bal` | The types the service uses: the wire types and their conversion functions, the `<Entity>List` envelopes and the status code records. It is not written when no exposed entity needs a type |
| `dataservice_config.bal` | `configurable int dataservicePort = 9090;` and `configurable http:ListenerConfiguration dataserviceListenerConfig = {};` |

From `bal build`, the files are written into `generated/` with the header of persist's generated files and must not
be edited. They are rewritten whenever the client is, and the check that skips generation for an unchanged model also
covers `options.dataservice`. When `options.dataservice` is removed, both files are deleted from `generated/`.

From `bal persist generate --dataservice`, the files are written into the package root with the same header without
the instruction not to edit them. They are the user's code, are replaced if `generate` runs again, and are never
deleted by the tool.

### 7.5. The HTTP service

For an entity `Employee` with the persist resource name `employees` and key `id`:

| Action | Request | Success response |
|---|---|---|
| `read` | `GET <basePath>/employees` | `200`, `{"items": [Employee, ...]}` |
| `read` | `GET <basePath>/employees/{id}` | `200`, `Employee` |
| `create` | `POST <basePath>/employees` with an `EmployeeInsert` object | `201`, `Location`, the created `Employee` |
| `create` | `POST <basePath>/employees` with an array of `EmployeeInsert` | `201`, `{"items": [Employee, ...]}` |
| `update` | `PATCH <basePath>/employees/{id}` with an `EmployeeUpdate` object | `200`, the updated `Employee` |
| `delete` | `DELETE <basePath>/employees/{id}` | `200`, the deleted `Employee` |

- A composite key takes one path segment per key field, in declaration order.
- `POST` with `[]` creates nothing. `PATCH` with `{}` returns the record without updating it.
- An operation whose action is not allowed is not generated, so `ballerina/http` answers `405` for its method, and an
  entity that is not exposed is `404`.
- An entity with a `time:*` or `byte[]` field gets wire types, `<Entity>Payload`, `<Entity>InsertPayload` and
  `<Entity>UpdatePayload`, that hold those fields as ISO 8601 and base64 strings, generated only for the operations
  that use them. Generated functions named `transform<Entity>ToPayload`, `transformPayloadTo<Entity>Insert` and `transformPayloadTo<Entity>Update` convert
  between them with the `ballerina/dataservice` conversion functions.
- Each resource declares only the error responses its operation can return: get many `InternalError`; get one
  `NotFound|InternalError`; create `NotFound|Conflict|ConstraintViolation|InternalError`; patch and delete
  `NotFound|ConstraintViolation|InternalError`; create and patch also `InvalidInput` for an entity with wire types.
- Errors map to `404`, `409`, `400` and `500` with the `dataservice:DataserviceError` body, through the
  [`ballerina/dataservice.http`](https://github.com/ballerina-platform/module-ballerina-dataservice.http/blob/main/docs/spec/spec.md)
  functions.

`dataservice.bal` holds, in order: the client and the listener, then one service with the resources of each exposed
entity in model order, as get many, get one, create, patch and delete. `dataservice_types.bal` holds, per entity in
model order, the wire types, the `<Entity>List` envelope, the `<Entity>Created` and `<Entity>ListCreated` records,
and the conversion functions.
Every resource and generated type has a short doc comment.
