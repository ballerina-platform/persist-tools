# Specification: Ballerina Persist Tools

_Owners_: @daneshk @sahanHe @TharmiganK  
_Reviewers_: @daneshk  
_Created_: 2022/07/26  
_Updated_: 2026/11/06  
_Edition_: Swan Lake

## Introduction

This is the specification for the Persist Tools of the [Ballerina language](https://ballerina.io/), which support several operations on the Ballerina persistence layer. The Ballerina persistence layer provides functionality to store and query data conveniently through a data model instead of the SQL query language. The data model and the generated client API are specified in the [`persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md). This specification covers the `bal persist` commands and the code generation that runs with `bal build`.

The conforming implementation of the specification is released and included in the distribution. Any deviation from the specification is considered a bug. The released versions of the specification can be found under the relevant GitHub tag.

### Notation

The key words MUST, MUST NOT, REQUIRED, SHALL, SHALL NOT, SHOULD, SHOULD NOT, RECOMMENDED, MAY and OPTIONAL are interpreted as described in [RFC 2119](https://www.rfc-editor.org/rfc/rfc2119) and [RFC 8174](https://www.rfc-editor.org/rfc/rfc8174) when, and only when, they appear in all capitals. Sentences that state behaviour in the present tense are also normative. The overview, notes and examples are informative.

## Contents

- [1. Overview](#1-overview)
- [2. Terminology](#2-terminology)
- [3. Adding the persistence layer](#3-adding-the-persistence-layer)
    - [3.1. Description](#31-description)
    - [3.2. Configuration](#32-configuration)
    - [3.3. Behaviour](#33-behaviour)
- [4. Initializing a model](#4-initializing-a-model)
    - [4.1. Description](#41-description)
    - [4.2. Configuration](#42-configuration)
    - [4.3. Behaviour](#43-behaviour)
- [5. Client generation](#5-client-generation)
    - [5.1. Description](#51-description)
    - [5.2. Configuration](#52-configuration)
    - [5.3. Behaviour](#53-behaviour)
    - [5.4. Generated files](#54-generated-files)
    - [5.5. Derived types](#55-derived-types)
    - [5.6. Database configuration](#56-database-configuration)
    - [5.7. Database schema](#57-database-schema)
    - [5.8. Native dependencies](#58-native-dependencies)
- [6. Pushing the schema](#6-pushing-the-schema)
    - [6.1. Description](#61-description)
    - [6.2. Configuration](#62-configuration)
    - [6.3. Behaviour](#63-behaviour)
- [7. Pulling the schema](#7-pulling-the-schema)
    - [7.1. Description](#71-description)
    - [7.2. Configuration](#72-configuration)
    - [7.3. Behaviour](#73-behaviour)
    - [7.4. Table selection](#74-table-selection)
    - [7.5. Interactions](#75-interactions)
- [8. Migrating the schema](#8-migrating-the-schema)
    - [8.1. Description](#81-description)
    - [8.2. Configuration](#82-configuration)
    - [8.3. Behaviour](#83-behaviour)
- [9. Data service generation](#9-data-service-generation)
    - [9.1. Description](#91-description)
    - [9.2. Configuration](#92-configuration)
    - [9.3. Compile-time validation](#93-compile-time-validation)
    - [9.4. Behaviour](#94-behaviour)
    - [9.5. Generated files](#95-generated-files)
    - [9.6. Service shape](#96-service-shape)
    - [9.7. HTTP service](#97-http-service)
        - [9.7.1. Keys](#971-keys)
        - [9.7.2. JSON](#972-json)
        - [9.7.3. Query options](#973-query-options)
        - [9.7.4. Relations](#974-relations)
        - [9.7.5. Constraints](#975-constraints)
        - [9.7.6. Field rules](#976-field-rules)
        - [9.7.7. Auth](#977-auth)
        - [9.7.8. Errors](#978-errors)
        - [9.7.9. Configuration](#979-configuration)
        - [9.7.10. OpenAPI definition](#9710-openapi-definition)
        - [9.7.11. Generated source](#9711-generated-source)
- [10. Security considerations](#10-security-considerations)
- [References](#references)

## 1. Overview

The persist tool works on a Ballerina project whose data model is written in a model definition file in the `persist` directory. From the model, it generates the derived entity types and the persist client that the `persist` specification defines, along with the database configuration and schema that the client needs.

The tool offers these commands:

| Command | Purpose |
|---|---|
| `bal persist add` | Sets up a project so that `bal build` generates the client. |
| `bal persist init` | Creates the model definition file, for use with `bal persist generate`. |
| `bal persist generate` | Generates the client once, outside the build. |
| `bal persist push` | Creates the database and runs the generated schema against it. |
| `bal persist pull` | Creates a model definition file from the schema of an existing database. |
| `bal persist migrate` | Generates a migration script from the changes to the model. |

For the SQL datastores, the tool can also generate a data service over the model, as described in [9](#9-data-service-generation).

## 2. Terminology

- **Model definition file:** the Ballerina file that defines the entities of a data model as per the [`persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#3-data-model-definition), named `model.bal`.
- **Root model:** the model whose definition file is `persist/model.bal`. A command applies to the root model when `--model` is not given.
- **Named model:** a model whose definition file is `persist/<model-name>/model.bal`, selected with `--model <model-name>`.
- **Datastore:** the data source that the generated client uses, such as `mysql`.
- **Test datastore:** the data source that the generated test client uses, `h2` or `inmemory`.
- **Persist module:** the module in which the generated files are placed, given with `--module`. It is the package's default module when `--module` is not given.
- **Tool entry:** a `[[tool.persist]]` table in `Ballerina.toml`, which makes `bal build` generate the client.
- **Data service:** a network service, generated over the persist client, that serves the entities of the model over a protocol such as HTTP.
- **Exposed entity:** an entity annotated with `@dataservice:Expose`, which the data service serves.
- **Migration label:** the name that the user gives a migration as the positional argument of `bal persist migrate`.

## 3. Adding the persistence layer

### 3.1. Description

The `bal persist add` command initializes a Ballerina project with the persistence layer, so that `bal build` generates the derived types and client.

### 3.2. Configuration

| Option | Description | Mandatory | Default |
|---|---|---|---|
| `--datastore` | The preferred database client. Currently, `mysql`, `mssql`, `postgresql`, `h2`, `inmemory`, `googlesheets` and `redis` are supported. | No | `inmemory` |
| `--module` | The persist module in which the files are generated. | No | `<package_name>` |
| `--id` | The identifier of the tool entry. | No | `generate-db-client`, or `generate-<model-name>-client` when `--model` is given |
| `--test-datastore` | The preferred database client for testing. Currently, `h2` and `inmemory` are supported. | No | None |
| `--model` | The name of the model. | No | The root model |
| `--eager-loading` | Makes the generated get-all methods return arrays instead of streams. Supported for `mysql`, `mssql`, `postgresql` and `h2`. For other datastores, the command gives a warning and ignores it. | No | Off |
| `--with-init-params` | Makes the generated client take the database connection values as init parameters instead of configurable variables. | No | Off |
| `--dataservice` | Enables a data service for the given protocol. Currently, `http` is supported. See [9](#9-data-service-generation). | No | None |

### 3.3. Behaviour

1. The command requires a Ballerina project.
2. The command creates the `persist` directory, which holds the model definition file.
3. If `persist/model.bal` does not exist, the command creates it with the required import, `import ballerina/persist as _;`. When `--model <model-name>` is given, the command uses `persist/<model-name>/model.bal` instead, creating the directory if needed, and that path is the `filePath` of the tool entry. An existing model definition file is kept.
4. The command adds a tool entry to `Ballerina.toml`:

    ```toml
    [[tool.persist]]
    id = "generate-db-client"
    targetModule = "<package_name>.<module_name>"
    options.datastore = "<datastore>"
    options.testDatastore = "<test_datastore>"
    filePath = "persist/model.bal"
    ```

5. When an option is not given, its default value in [3.2](#32-configuration) is used.
6. When `--test-datastore` is given, the tool entry has the `options.testDatastore` option, and `bal build` generates the test client along with the main client.
7. When `--eager-loading` or `--with-init-params` is given, the tool entry has the `options.eagerLoading = true` or `options.withInitParams = true` option.
8. The `id` of each tool entry is unique. The command fails when `Ballerina.toml` already has a tool entry with the same `id`. To add the persistence layer for another model, the user runs the command again with a different `--model`, which also gives the entry a different default `id`. Each tool entry needs its own target module, given with `--module`, because the generated files of two entries in the same module have the same names.
9. The command adds the native dependencies described in [5.8](#58-native-dependencies) to `Ballerina.toml`.

For example, `bal persist add --datastore mysql --module db --test-datastore h2` in the `medical-center` package creates this layout:

```text
medical-center
├── persist
│   └── model.bal
├── Ballerina.toml
└── main.bal
```

## 4. Initializing a model

### 4.1. Description

The `bal persist init` command creates the model definition file without adding a tool entry. It initializes a project for use with `bal persist generate`.

### 4.2. Configuration

| Option | Description | Mandatory | Default |
|---|---|---|---|
| `--model` | The name of the model. | No | The root model |

### 4.3. Behaviour

1. The command creates the `persist` directory, which holds the model definition file.
2. If no files are present in the `persist` directory, the command creates `model.bal` with the required import, `import ballerina/persist as _;`.
3. When `--model <model-name>` is given, the command creates the directory `persist/<model-name>/` and creates `model.bal` in it. The command fails when that model already exists.
4. When `--model` is not given, the command creates `persist/model.bal`.
5. The command still accepts the `--datastore` and `--module` options of earlier versions. It ignores them and prints a message that the command no longer takes them.

## 5. Client generation

### 5.1. Description

The persist tool generates the [derived entity types and persist clients](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#4-derived-entity-types-and-persist-clients) of the `persist` specification, and the database schema associated with the model definition file. `bal build` generates them for each tool entry, and `bal persist generate` generates the same files once.

### 5.2. Configuration

`bal build` takes its configuration from the tool entry described in [3.3](#33-behaviour). `bal persist generate` takes these options:

| Option | Description | Mandatory | Default |
|---|---|---|---|
| `--datastore` | The preferred database client. Currently, `mysql`, `mssql`, `postgresql`, `h2`, `inmemory`, `googlesheets` and `redis` are supported. | Yes | None |
| `--module` | The persist module in which the files are generated. | No | `<package_name>` |
| `--test-datastore` | The preferred database client for testing. Currently, `h2` and `inmemory` are supported. | No | None |
| `--model` | The name of the model. | No | The root model |
| `--eager-loading` | As for `bal persist add`, in [3.2](#32-configuration). | No | Off |
| `--with-init-params` | As for `bal persist add`, in [3.2](#32-configuration). | No | Off |
| `--dataservice` | Also generates a data service for the given protocol, into the package root. See [9](#9-data-service-generation). | No | None |

For example, `bal persist generate --datastore mysql --module db --test-datastore h2` generates the files of a MySQL client and an H2 test client in the `db` module.

### 5.3. Behaviour

1. The command requires a Ballerina project.
2. The model definition file MUST contain the `persist` module import, `import ballerina/persist as _;`.
3. The model definition file MUST contain at least one entity.
4. The tool generates the files in [5.4](#54-generated-files), and creates or updates `Config.toml` as described in [5.6](#56-database-configuration).
5. Invoking `bal persist generate` again does not fail. It generates the files again.
6. The tool adds the native dependencies described in [5.8](#58-native-dependencies) to `Ballerina.toml`.
7. Support for the `googlesheets` and `redis` datastores is experimental. The tool prints a warning when it generates a `googlesheets` client, and `bal persist generate` also prints one for a `redis` client.

### 5.4. Generated files

The tool places the generated files as follows:

1. `bal build` generates the files in the `generated` directory when the target module is the package's default module, and in `generated/<module>` otherwise.
2. `bal persist generate` generates the files in the project root when the target module is the package's default module, and in `modules/<module>` otherwise.

| File | Content |
|---|---|
| `persist_client.bal` | The client API for the entities defined in the model definition file. |
| `persist_db_config.bal` | The configurable variables required for database access. See [5.6](#56-database-configuration). |
| `persist_types.bal` | The derived entity types of the entities defined in the model definition file. See [5.5](#55-derived-types). |
| `persist_test_client.bal` | The client API for the entities defined in the model definition file, for the test datastore. Generated only when a test datastore is given. |
| `persist_test_init.bal` | The initialization of the test database schema of the entities defined in the model definition file, for the test datastore. Generated only when `h2` is the test datastore. |
| `script.sql` | The SQL script that creates the database schema of the entities defined in the model definition file. Generated only for the `mysql`, `mssql`, `postgresql` and `h2` datastores. See [5.7](#57-database-schema). |

For example, the files of the `medical-center` package are laid out as follows when the module name is the package name:

```text
medical-center
├── generated
│   ├── persist_client.bal
│   ├── persist_db_config.bal
│   ├── persist_test_client.bal
│   ├── persist_test_init.bal
│   ├── persist_types.bal
│   └── script.sql
├── persist
│   └── model.bal
├── Ballerina.toml
├── Config.toml
└── main.bal
```

When the module name differs, they are laid out as follows, with `medical-item` as the submodule:

```text
medical-center
├── generated
│   └── medical-item
│       ├── persist_client.bal
│       ├── persist_db_config.bal
│       ├── persist_test_client.bal
│       ├── persist_test_init.bal
│       ├── persist_types.bal
│       └── script.sql
├── persist
│   └── model.bal
├── Ballerina.toml
├── Config.toml
└── main.bal
```

### 5.5. Derived types

`persist_types.bal` holds the derived types of each entity. Each derived type has a one-line doc comment with a fixed summary based on the entity name:

| Derived type | Doc comment |
|---|---|
| `Employee` | Represents the `Employee` entity. *Model doc* |
| `EmployeeOptionalized` | Represents `Employee` with every field optional, for projected reads. |
| `EmployeeWithRelations` | Represents `Employee` with its related entities, for projected reads. |
| `EmployeeTargetType` | The `Employee` type or a projection of it, for reads. |
| `EmployeeInsert` | The fields to insert a new `Employee`. |
| `EmployeeUpdate` | The fields to update on an existing `Employee`. |

1. The entity type adds the first paragraph of the entity's doc comment in the model, if any, shown as *Model doc* above.
2. Each field, in every derived type that has it, takes the first paragraph of its doc comment in the model.

For example, an `Employee` entity documented as "A person employed by the company.", whose `id` field is documented as "The employee's id", gives:

```ballerina
# Represents the `Employee` entity. A person employed by the company.
public type Employee record {|
    # The employee's id
    readonly int id;
    string name;
|};

# The fields to update on an existing `Employee`.
public type EmployeeUpdate record {|
    string name?;
|};
```

Each field of a derived type keeps the `ballerina/constraint` annotations of the model field, and a foreign key field keeps those of the key field it references. The generated clients do not validate them on `post` and `put`.

### 5.6. Database configuration

`persist_db_config.bal` declares the configurable variables required for database access. The tool creates `Config.toml`, or updates it when it exists, with the values that initialize these variables, under a table named after the persist module: `<package_name>` for the default module, or `<package_name>.<module_name>` otherwise.

For the SQL datastores, when `--with-init-params` is given, the client takes the connection values as init parameters, and the tool generates neither `persist_db_config.bal` nor the `Config.toml` values.

For example, for MySQL and the `db` module of the `medical_center` package, `persist_db_config.bal` holds:

```ballerina
import ballerinax/mysql.driver as _;

configurable int port = ?;
configurable string host = ?;
configurable string user = ?;
configurable string database = ?;
configurable string password = ?;
```

and `Config.toml` holds:

```toml
[medical_center.db]
host = "localhost"
port = 3306
user = "root"
password = ""
database = ""
```

### 5.7. Database schema

The database schema in `script.sql` creates:

1. A table for each entity, with the defined primary keys.
2. Foreign key associations between the tables, if the model defines associations between entities.

Before it creates each table, the script drops the table if it exists.

### 5.8. Native dependencies

The generated client calls the native code of its datastore's persist package. The tool adds that native jar to `Ballerina.toml` as a `[[platform.java25.dependency]]` entry, with the group ID `io.ballerina.stdlib` (`io.ballerina.lib` for `redis`), the artifact ID `persist.<datastore>-native`, and the version of the persist package that the tool supports. All SQL datastores, including `h2`, use the `persist.sql-native` artifact. For example, for `mysql`:

```toml
[[platform.java25.dependency]]
groupId = "io.ballerina.stdlib"
artifactId = "persist.sql-native"
version = "1.8.0"
```

1. When a test datastore is given and it uses a different artifact, the tool adds a second entry for it.
2. When the datastore is `googlesheets`, the tool also adds a `[[dependency]]` entry for the Google Sheets connector.
3. When `Ballerina.toml` already has the entry, under `platform.java25.dependency` or the `platform.java21.dependency` of earlier versions, the tool keeps it if its version is compatible with the supported version. Otherwise, the command fails and asks the user to remove the entry and run it again.

## 6. Pushing the schema

### 6.1. Description

The `bal persist push` command creates the database, if it does not exist, and runs the generated `script.sql` against it, to create the tables of the model. It supports the `mysql`, `mssql` and `postgresql` datastores.

### 6.2. Configuration

The command takes the datastore from `options.datastore`, and the persist module from `targetModule`, of the tool entry described in [3.3](#33-behaviour).

The database is configured in `Ballerina.toml` under the `[persist.model.storage.<datastore>]` table, with the `host`, `port`, `user`, `password` and `database` keys. For example:

```toml
[persist.model.storage.mysql]
host = "localhost"
port = 3306
user = "root"
password = "Test123#"
database = "persist"
```

### 6.3. Behaviour

1. The command requires a Ballerina project.
2. The command fails when the datastore is not `mysql`, `mssql` or `postgresql`.
3. The database configuration in [6.2](#62-configuration) MUST be in `Ballerina.toml`.
4. The `persist` directory MUST hold at least one model definition file. The command runs the schema of each model definition file directly in the `persist` directory.
5. The SQL script MUST have been generated, as described in [5](#5-client-generation). The command reads it from `generated/script.sql`, or from `generated/<module_name>/script.sql` when the persist module is not the default module.
6. The command loads the JDBC driver from the project's dependency on the datastore's driver package, `ballerinax/mysql.driver`, `ballerinax/mssql.driver` or `ballerinax/postgresql.driver`, which the generated `persist_db_config.bal` imports.
7. The command creates the database if it does not exist, and then runs the script, which creates the tables and foreign key associations described in [5.7](#57-database-schema).
8. Invoking the command again does not fail. It reruns the SQL script, which drops and recreates the tables, so the data in them is lost.

For example, the `medical-center` package has this layout before the command runs:

```text
medical-center
├── generated
│   ├── persist_client.bal
│   ├── persist_db_config.bal
│   ├── persist_types.bal
│   └── script.sql
├── persist
│   └── model.bal
├── Ballerina.toml
├── Config.toml
└── main.bal
```

## 7. Pulling the schema

### 7.1. Description

The `bal persist pull` command introspects the schema of a database and creates a `model.bal` file with the entities and relations based on that schema.

### 7.2. Configuration

The database configuration is given as command-line options:

| Option | Description | Mandatory | Default |
|---|---|---|---|
| `--datastore` | The preferred datastore. `mysql`, `postgresql` and `mssql` are supported. | No | `mysql` |
| `--host` | The database host. | Yes | None |
| `--port` | The database port. | No | Datastore specific |
| `--user` | The database user. | Yes | None |
| `--database` | The database name. | Yes | None |
| `--tables` | Enables table selection. Accepts table names, or starts the interactive mode when it has no value. See [7.4](#74-table-selection). | No | All tables |
| `--model` | The name of the model. | No | The root model |
| `--dataservice` | Annotates the pulled entities and enables a data service for the given protocol. See [9](#9-data-service-generation). | No | None |
| `--expose-tables` | Annotates only the entities of the given tables. Requires `--dataservice`, unless the tool entry already enables a data service. | No | All tables |

### 7.3. Behaviour

1. The command requires a Ballerina project.
2. The database password is not accepted as a command-line argument. The command prompts the user to enter it.
3. The command creates the `persist` directory if it is not present.
4. If a `model.bal` file is present in the `persist` directory, or in the named model's directory when `--model` is given, the command prompts the user to confirm overwriting it.
5. The command creates a `model.bal` file with the entities and relations based on the introspected schema of the database.
6. When the database has a column of an unsupported data type, the command gives a warning and comments out the relevant field with the tag `[Unsupported[DATA_TYPE]]`.
7. The command does not change the schema of the database in any way.

### 7.4. Table selection

1. Without `--tables`, the command introspects all tables of the database.
2. With `--tables=<table1>,<table2>`, the command introspects only the given tables.
3. With `--tables` and no value, the command displays the available tables, up to 50, and prompts the user to select the tables to introspect. The user can:
    - enter table names separated by commas,
    - enter table numbers separated by commas,
    - enter `all`, or press Enter, to select all tables, or
    - enter `q` or `quit` to abort the operation.
4. When specific tables are selected, the command also includes each table referenced by a foreign key from a selected table, so that the generated model is valid. It logs an INFO message for each table included this way.

For example:

```bash
# Introspect all tables
bal persist pull --datastore mysql --host localhost --port 3306 --user root --database persist

# Introspect only the users and orders tables
bal persist pull --datastore mysql --host localhost --port 3306 --user root --database persist --tables=users,orders

# Select the tables interactively
bal persist pull --datastore mysql --host localhost --port 3306 --user root --database persist --tables
```

### 7.5. Interactions

The `pull` command creates only the model definition file. To use the client API in the project, the user executes the `generate` command after the `pull` command, to generate the derived types and client API.

## 8. Migrating the schema

### 8.1. Description

The `bal persist migrate` command generates the migration scripts required by the changes to the `model.bal` file.

### 8.2. Configuration

The migration label is a REQUIRED positional argument.

| Option | Description | Mandatory | Default |
|---|---|---|---|
| `--datastore` | The preferred datastore. Currently, only `mysql` is supported. | No | `mysql` |
| `--model` | The name of the model. | No | The root model |

### 8.3. Behaviour

1. The command requires a Ballerina project.
2. Support for migrations is experimental. The command prints a warning that its behaviour may change in future releases.
3. The command creates the `persist/migrations` directory, or `persist/<model-name>/migrations` when `--model` is given, if it is not present.
4. The command generates a migration script based on the changes to the `model.bal` file, in a new directory of the migrations directory named `[TIMESTAMP]_[migrationLabel]`, where `migrationLabel` is the migration label. The new directory holds the latest `model.bal` file and the migration script, `script.sql`.
5. If the migrations directory already contains migrations, the command takes the last migration, and generates the next migration script from the changes since the `model.bal` file of that migration.
6. The command does not run the migration script against the database. The user runs the migration script against the database after reviewing it.

For example, `bal persist migrate --datastore mysql migrationLabel` in the `rainier` package creates this layout:

```text
rainier
├── persist
│   ├── model.bal
│   └── migrations
│       └── 20240410120000_migrationLabel
│           ├── model.bal
│           └── script.sql
├── Ballerina.toml
└── main.bal
```

## 9. Data service generation

### 9.1. Description

The persist tool can generate a data service over a persist model. The model selects what is exposed with the `@dataservice:Expose` annotation of the [`ballerina/dataservice`](https://github.com/ballerina-platform/module-ballerina-dataservice/blob/main/docs/spec/spec.md) package, and `Ballerina.toml` says how the service is generated. Without `options.dataservice`, the client, its files and every command behave as they do without data services.

```bash
bal persist add --datastore postgresql --dataservice http
# annotate the entities to expose in persist/model.bal
bal build
```

### 9.2. Configuration

```toml
[[tool.persist]]
id = "generate-db-client"
targetModule = "hr"
filePath = "persist/model.bal"
options.datastore = "postgresql"

[tool.persist.options.dataservice]
protocol = "http"
basePath = "/api"
```

The `[tool.persist.options.dataservice]` table is a sub-table of the `[[tool.persist]]` entry before it, so it must follow that entry before any other table header. `options.dataservice.protocol = "http"` written as a dotted key on the entry is equivalent. This section refers to the keys by their dotted names.

| Key | Required | Default | Meaning |
|---|---|---|---|
| `options.dataservice.protocol` | yes | — | The protocol the service is generated for. Only `http` is supported |
| `options.dataservice.basePath` | no | `/api` | The base path of the service |
| `options.dataservice.auth` | no | `false` | Whether the service is generated with authentication and scopes; see [Auth](#977-auth) |
| `options.dataservice.introspection` | no | `true` | Whether the service publishes its OpenAPI definition |

`basePath` must start with `/`. A trailing `/` is removed, and `/` alone serves the service at the root. Each segment must be non-empty and contain only letters, digits, `_`, `-` and `.`. `persist-options-schema.json` allows the `dataservice` group with the string keys `protocol` and `basePath` and the boolean keys `auth` and `introspection`, and the tool checks their values.

`http` generates an `http:Service` on an `http:Listener`. `graphql`, `mcp`, `odata` and `soap` are reserved for later protocols and are reported as not supported yet.

| `options.dataservice` | `protocol` | Annotated entities | Result |
|---|---|---|---|
| absent | — | none | No service; persist behaves as it does without data services |
| absent | — | some | No service, and a warning that `protocol` is not set |
| present | absent | any | Error: `protocol` is required |
| present | set | none | Error listing the entities that can be annotated |
| present | set | some | The service is generated over the annotated entities |

A model with a data service imports `ballerina/dataservice`, and persist's model validation accepts that import:

```ballerina
import ballerina/persist as _;
import ballerina/dataservice;

@dataservice:Expose
type Employee record {|
    readonly int id;
    string name;
|};

@dataservice:Expose {operations: [dataservice:READ]}
type Department record {|
    readonly int id;
    string name;
|};
```

Persist does not generate a client API for an entity with an unsupported data type, so such an entity cannot be exposed.

A data service is generated only for the SQL datastores `mysql`, `mssql`, `postgresql` and `h2`, and only into the package's default module, so `targetModule` must be the package name. It cannot be combined with `options.withInitParams = true`, because the service creates the client with `new ()`, or with a named model, because a package has at most one data service.

### 9.3. Compile-time validation

Before it generates, the tool compiles the model file on its own, which runs the `ballerina/dataservice` compiler plugin. The plugin checks the `@dataservice:Expose` annotations and the field rules, as the [`ballerina/dataservice` specification](https://github.com/ballerina-platform/module-ballerina-dataservice/blob/main/docs/spec/spec.md) defines. An error fails the command, under `the model definition file(model.bal) has errors.`, as `ERROR [model.bal:(<position>)] <message>`. A warning is printed as `WARNING [model.bal:(<position>)] <message>` and does not fail it.

The tool then reads the annotations from the model's syntax tree, and makes the checks below, which need the tool's options or more than one entity.

Errors are printed with the `ERROR:` prefix, or as the build tool's diagnostic in `bal build`, and fail the command. Warnings are printed with `WARNING:` and do not.

| Case | Severity | Message |
|---|---|---|
| `protocol` missing | error | `'options.dataservice.protocol' is required. Supported values: http.` |
| `protocol` is `graphql`, `mcp`, `odata` or `soap` | error | `the protocol '<value>' is not supported yet. Supported values: http.` |
| `protocol` is any other value | error | `invalid 'options.dataservice.protocol' '<value>'. Supported values: http.` |
| `basePath` without a leading `/` | error | `invalid 'options.dataservice.basePath' '<value>': the base path must start with '/'.` |
| `basePath` with an empty or invalid segment | error | `invalid 'options.dataservice.basePath' '<value>': each path segment must be non-empty and contain only letters, digits, '_', '-' and '.'.` |
| `targetModule` is not the package | error | `a data service is generated only into the default module '<package>'. Remove 'targetModule' or set it to '<package>'.` |
| `withInitParams = true` | error | `'options.withInitParams' cannot be used with a data service. Remove it or set it to false.` |
| A datastore that is not a SQL datastore | error | `the datastore '<datastore>' does not support a data service. Use mysql, mssql, postgresql or h2.` |
| A named model | error | `a data service is not supported for a named persist model. Use 'persist/model.bal' or remove 'options.dataservice'.` |
| No annotated entity | error | `the data service exposes no entity. Add the @dataservice:Expose annotation to the entities to expose: <Entity>, <Entity>.` |
| An annotated entity with an unsupported data type | error | `the entity '<Entity>' has an unsupported data type and cannot be exposed. Remove its @dataservice:Expose annotation.` |
| A key field named as a parameter or variable of the generated resources | error | `the key field '<field>' of the entity '<Entity>' uses a name reserved by the data service. Rename the field, or remove the entity's @dataservice:Expose annotation.` |
| Two annotated entities with the same collection name | error | `the entities '<Entity>' and '<Entity>' map to the same collection '<collection>' in the data service. Rename one of them, or remove its @dataservice:Expose annotation.` |
| `scopes` set while `auth` is not `true` | warning | `the @dataservice:Expose annotation of the entity '<Entity>' sets scopes, but 'options.dataservice.auth' is not true. Set it to true, or remove the scopes.` |
| Annotations without `options.dataservice` | warning | `the model has @dataservice:Expose annotations, but no data service is generated. Set 'options.dataservice.protocol' in the [[tool.persist]] entry to generate one.` |
| `pull` skips an entity | warning | `the table '<table>' has an unsupported data type, so its entity '<Entity>' is not exposed.` |
| `generate` without `--dataservice` on an annotated model | info | `the model has @dataservice:Expose annotations. Use the '--dataservice' option to also generate a data service.` |
| `--expose-tables` without `--dataservice`, when the entry does not enable a data service | error | `the '--expose-tables' option requires the '--dataservice' option.` |
| `--expose-tables` names a table that was not pulled | error | `the table '<table>' in '--expose-tables' is not a pulled table.` |
| `--expose-tables` names a table with an unsupported data type | error | `the table '<table>' in '--expose-tables' has an unsupported data type, so its entity '<Entity>' cannot be exposed.` |
| `add --dataservice` succeeds | info | The next steps `2. Add the @dataservice:Expose annotation to each entity to expose.` and `3. Execute `bal build` to generate the persist client and the <PROTOCOL> data service during package build.` |
| `pull --dataservice` when the entry already has `options.dataservice` | error | `the [[tool.persist]] entry of persist/model.bal already has an 'options.dataservice' group. Remove it from Ballerina.toml and run the command again, or run 'bal persist pull' without '--dataservice'.` |
| `pull --dataservice` succeeds | info | `enabled the <PROTOCOL> data service in the [[tool.persist]] entry of persist/model.bal in Ballerina.toml.` |

A key field becomes a path parameter of the generated resources, so it cannot be named `broken`, `converted`, `created`, `ctx`, `entries`, `entry`, `err`, `fields`, `filter`, `forbidden`, `hasMore`, `i`, `include`, `included`, `includedMore`, `includedRows`, `invalidFields`, `invalidKey`, `item`, `itemKey`, `itemPayload`, `items`, `key`, `keyValues`, `keys`, `next`, `offset`, `partial`, `partials`, `payload`, `query`, `readable`, `related`, `row`, `rows`, `size`, `sort`, `sources`, `stored`, `tooMany`, `value` and `values`. Two entities have the same collection name when their names give the same name after the collection rule in [Service shape](#96-service-shape), such as `Person` and `People`, which both give `people`.

Every invalid `--expose-tables` name is reported, one line each.

### 9.4. Behaviour

| Command | Behaviour with `--dataservice <protocol>` |
|---|---|
| `bal persist add` | Writes the entry as without the option, and adds the `[tool.persist.options.dataservice]` table with `protocol` after it. It cannot be combined with `--module`, `--model` or `--with-init-params` |
| `bal persist pull` | Writes the model with `@dataservice:Expose` on each entity to expose and the `ballerina/dataservice` import, then adds an `options.dataservice` group with the protocol to the entry whose `filePath` is `persist/model.bal`, or creates that entry as `add` would. The group is written as the `[tool.persist.options.dataservice]` table, or inside the entry's inline `options` table when it has one. When the entry already has an `options.dataservice` group, even an empty one, `pull --dataservice` fails before the database is read, and the user removes the group's keys and runs the command again, or runs `pull` without `--dataservice` to keep them. It cannot be combined with `--model` |
| `bal persist generate` | Generates the client and the data service once, into the package root. It cannot be combined with `--module`, `--model` or `--with-init-params` |
| `bal build` | Generates the client and, when `options.dataservice` is present, the data service |

- **`pull`:** without `--expose-tables`, every pulled entity is annotated except one with an unsupported data type, which is named in a warning. With `--expose-tables <table,...>`, only the entities of the named tables are annotated. Names are separated by commas and matched exactly, as `--tables` matches them. Before introspecting, `pull` rejects an entry whose `targetModule` is not the package or that sets `withInitParams = true`.
- **`pull` without `--dataservice`:** when the entry of `persist/model.bal` already has `options.dataservice`, `pull` annotates the pulled entities in the same way, using the entry's protocol, accepts `--expose-tables`, and leaves the entry unchanged, so a later `pull` keeps the service. The entry's `options.dataservice` is validated as `bal build` validates it. When the entry has no `options.dataservice`, `pull` behaves as it does without data services.
- A command that ends in an error leaves the model and `Ballerina.toml` unchanged.

### 9.5. Generated files

| File | Contents |
|---|---|
| `dataservice.bal` | The listener and the service, which holds the persist client |
| `dataservice_types.bal` | The types the service uses: the wire types and their conversion functions, the `<Entity>List` envelopes and the status code records. It is not written when no exposed entity needs a type, and one left from an earlier run is deleted |
| `dataservice_config.bal` | The configurable `dataserviceConfig` record; see [Configuration](#979-configuration) |

From `bal build`, the files are written into `generated/` with the header of persist's generated files and must not be edited. They are rewritten whenever the client is, and the check that skips generation for an unchanged model also covers `options.dataservice`. When `options.dataservice` is removed, the three files are deleted from `generated/`.

From `bal persist generate --dataservice`, the files are written into the package root with the same header without the instruction not to edit them. They are the user's code and are replaced if `generate` runs again. The tool deletes none of them, except a `dataservice_types.bal` that a later `generate` no longer writes, as `bal build` does.

Generated module-level names share the default module's namespace with the user's code, as the persist client's names do, so a user definition with the same name fails to compile:

| Name | Kind |
|---|---|
| `dataserviceListener` | The listener |
| `dataserviceConfig` | The runtime settings, a `dsHttp:Config` or `dsHttp:AuthConfig` |
| `dataserviceScopes` | The scopes of each operation, with `auth = true` |
| `DataserviceAuthInterceptor`, `DataserviceErrorInterceptor` | The request interceptor that checks auth, with `auth = true`, and the response error interceptor |
| `<Entity>List`, `<Entity>Created`, `<Entity>ListCreated` | The envelope and the status code records |
| `<Entity>Payload`, `<Entity>InsertPayload`, `<Entity>UpdatePayload` | The wire types |
| `transform<Entity>ToPayload`, `transformPayloadTo<Entity>Insert`, `transformPayloadTo<Entity>Update` | The conversion functions |
| `dataservice<Entity>Fields`, `dataservice<Entity>Entity` | The field list and the description of an entity |

### 9.6. Service shape

Each exposed entity gets up to six operations, plus a get related per relation. The actions in its `@dataservice:Expose` annotation enable them:

| Action | Operation | Persist client call | Result |
|---|---|---|---|
| `read` | Get many | `get <entities>()` with the clauses of the [query options](#973-query-options) | `<Entity>List` holding one page of the records that match |
| `read` | Get one | `get <entities>/[key]()` | `<Entity>` |
| `read` | Get related | `get <targets>()` filtered by the relation's link, with the query options on a to-many relation | `<Target>List` for a to-many relation, `<Target>` for a to-one relation |
| `create` | Create one | `post <entities>([value])`, then one get many on the returned key, in one transaction | The created `<Entity>`, read back so that generated fields are included |
| `create` | Create many | `post <entities>(values)`, then one get many on the returned keys, in one transaction | `<Entity>List` holding the created records, in the order of the request |
| `update` | Patch one | `put <entities>/[key](value)` | The updated `<Entity>`, as the persist client returns it, or no record without `read` |
| `delete` | Delete one | `delete <entities>/[key]()` | The deleted `<Entity>`, as the persist client returns it, or no record without `read` |

- **Collection name:** the entity name with its first word in lowercase and its last word pluralized as persist pluralizes resource names: `employees` for `Employee`, `projectAssignments` for `ProjectAssignment`, `people` for `Person` and `httpLogs` for `HTTPLog`. The generated code still calls the persist client by its resource name, such as `projectassignments`.
- **Key:** the entity's `readonly` fields, in declaration order. A composite key takes one value per key field, in that order.
- **Input types:** create takes `<Entity>Insert` and patch takes `<Entity>Update`, the types persist generates.
- **Float keys:** a `float` key value that is `NaN` or infinite is invalid input, checked with `dataservice:checkFiniteKey` before the persist client is called.
- **Envelope:** get many and create many return `<Entity>List`, a record with `items` and `hasMore` fields, rather than an array. `hasMore` tells whether records exist after the page; create many sets it to `false`.
- **Atomic creates:** the insert and the read-back run in one Ballerina `transaction`, so a create many with one failing record creates none, and a created record that cannot be read back is an internal error that creates nothing. The records are read back with `dataservice:toKeysQuery` and put in the order of the request with `dataservice:orderByKeys`.
- **Update not read back:** when `put` returns `persist:ReadBackError`, the update is saved, so patch returns no record and the error is logged. `dsHttp:toUpdateError` answers it with `204`, so patch declares `http:NoContent` also when the entity allows `read`.
- **Persist client:** get many passes `offsetClause`, so the service needs `ballerinax/persist.sql` 1.8.0 or later.
- **Create size:** a create holds at most `dataserviceConfig.maxPageSize` records, checked with `dataservice:checkCreateSize`.
- **Reserved:** path segments starting with `$`, such as `$count`, are reserved for later query and metadata operations.
- **Datastores:** the service is generated only for the SQL datastores, because its queries run in the database through the SQL client.

### 9.7. HTTP service

`dataservice.bal` declares the listener and one service. The service creates the persist client and checks the page sizes in its `init`, so a failure stops it from starting:

```ballerina
listener http:Listener dataserviceListener =
        new (dataserviceConfig.port, dataserviceConfig.listenerConfig);

service http:InterceptableService /api on dataserviceListener {

    private final Client persistClient;
    private final dataservice:PageConfig pageConfig;

    function init() returns error? {
        self.persistClient = check new ();
        self.pageConfig = check dataservice:toPageConfig(dataserviceConfig.pageSize, dataserviceConfig.maxPageSize);
    }

    public function createInterceptors() returns DataserviceErrorInterceptor => new;

    // the resources of each exposed entity
}
```

The service's base path is `options.dataservice.basePath`. `dataserviceListener` is a module-level listener, so a service the user writes in the same module can attach to it with another base path and is served on the same port.

For an entity `Employee` with the collection name `employees` and key `id`:

| Operation | Request | Success response |
|---|---|---|
| Get many | `GET <basePath>/employees` with the [query parameters](#973-query-options) | `200`, `{"items": [Employee, ...], "hasMore": false}` |
| Get one | `GET <basePath>/employees/{id}`, optionally with `fields` | `200`, `Employee` |
| Get related, to-one | `GET <basePath>/employees/{id}/department`, optionally with `fields` | `200`, `Department` |
| Get related, to-many | `GET <basePath>/departments/{id}/employees` with the query parameters | `200`, `{"items": [Employee, ...], "hasMore": false}` |
| Create one | `POST <basePath>/employees` with an `EmployeeInsert` object | `201`, `Location`, the created `Employee` |
| Create many | `POST <basePath>/employees` with an array of `EmployeeInsert` | `201`, `{"items": [Employee, ...], "hasMore": false}` |
| Patch one | `PATCH <basePath>/employees/{id}` with an `EmployeeUpdate` object | `200`, the updated `Employee`; `204` without `read` |
| Delete one | `DELETE <basePath>/employees/{id}` | `200`, the deleted `Employee`; `204` without `read` |

- **One route for create.** The payload is typed `EmployeeInsert|EmployeeInsert[]`, so an object creates one record and an array creates many. Create many has no `Location` header.
- **`Location`:** built with `dsHttp:toLocation`, as the collection path followed by the percent-encoded key values, such as `/api/employees/1`. With the base path `/`, it starts at the entity, as in `/employees/1`.
- **Patch is partial.** A field left out of the body is not changed, and a nullable field set to `null` is set to `null`. `EmployeeUpdate` is a closed record without the key fields, so a body that sets a key field is answered with `400` by `ballerina/http`.
- **Empty writes.** `POST` with `[]` is answered with `201` and `{"items": [], "hasMore": false}`, and `PATCH` with `{}` is answered as get one when the entity allows `read`, and with `204` without reading otherwise. Neither calls the persist client's write.
- **Without `read`,** patch and delete return `http:NoContent`, so they answer `204` with no body. With `auth = true`, they also answer `204` to a caller without a `read` scope; see [Auth](#977-auth).
- **Disabled operations.** An operation whose action is not allowed is not generated, so `ballerina/http` answers `405` for its method, and an entity that is not exposed is `404`.
- **Trailing slash.** A path with a trailing slash, such as `<basePath>/employees/`, addresses the same resource as without it, as `ballerina/http` matches paths.

#### 9.7.1. Keys

A single key is one path segment, typed as the key field's type. A composite key is one segment per key field, in declaration order, such as `/assignments/{employeeId}/{projectCode}`. Key fields are `int`, `string`, `float`, `decimal` or `boolean`, and a segment that does not convert to the key's type is answered with `400` by `ballerina/http`.

#### 9.7.2. JSON

| Ballerina type | JSON |
|---|---|
| `int`, `float`, `decimal` | number |
| `string`, `boolean` | string, boolean |
| `T?` | the value or `null` |
| enum | string |
| `byte[]` | a base64 string, RFC 4648 standard alphabet, padded |
| `time:Date` | an ISO 8601 date, `2024-01-02` |
| `time:TimeOfDay` | an ISO 8601 time, `10:15:30` or `10:15:30.5` |
| `time:Civil` | an ISO 8601 date-time without an offset, `2024-01-02T10:15:30` |
| `time:Utc` | an ISO 8601 date-time in UTC, `2024-01-02T10:15:30Z` |

A `decimal` read from the database keeps the column's scale, so `1.5` in a `DECIMAL(10,2)` column is returned as `1.50`.

An entity with a `time:*` or `byte[]` field, or a field with a [field rule](#976-field-rules), gets wire types, `<Entity>Payload`, `<Entity>InsertPayload` and `<Entity>UpdatePayload`, that hold those fields as strings, generated only for the operations that use them. The `<Entity>List` envelope and the status code records of such an entity hold the wire type. The generated functions `transform<Entity>ToPayload`, `transformPayloadTo<Entity>Insert` and `transformPayloadTo<Entity>Update` convert between them with the `ballerina/dataservice` conversion functions. Other entities use the persist types as they are.

#### 9.7.3. Query options

Get many takes the query parameters `filter`, `sort`, `size`, `offset` and `fields`; get one takes `fields`:

```ballerina
resource function get employees(EmployeeFilter? filter = (), string[]? sort = (), int? size = (), int? offset = (),
        string[]? fields = ()) returns EmployeeList|EmployeePartialList|dsHttp:InvalidInput|dsHttp:InternalError

resource function get employees/[int id](string[]? fields = ())
        returns Employee|EmployeePartial|dsHttp:NotFound|dsHttp:InvalidInput|dsHttp:InternalError
```

| Parameter | Form | Example |
|---|---|---|
| `filter` | a JSON object bound to `<Entity>Filter` | `filter={"hiredOn":{"gte":"2024-01-01"},"name":{"startsWith":"A"}}` |
| `sort` | field names, repeated or comma-separated; `-` sorts a field descending | `sort=name,-hiredOn` |
| `size` | the page size; `dataserviceConfig.pageSize` without it, and at most `dataserviceConfig.maxPageSize` | `size=20` |
| `offset` | the number of records to skip | `offset=40` |
| `fields` | field names, repeated or comma-separated; the key fields are always returned | `fields=name,email` |

- **Filter type:** `<Entity>Filter` has one optional field per field that callers can see, except `byte[]` fields and a field whose name starts with `$`: such keys are reserved for joining conditions. Each is a closed record of the operators the field's type allows: `eq`, `ne` and `anyOf` on every type; `lt`, `lte`, `gt` and `gte` except on `boolean` and enum fields; `contains` and `startsWith` on `string` fields; and `isNull` on nullable fields. Values have the field's wire type, so a date is a string.
- **Query:** the resource passes the options and the entity description to `dataservice:toQuery`, which validates them and returns the clauses for the persist client. The page is read with one row more than `size`, and `dataservice:trimToPage` removes that row and gives `hasMore`.
- **Fields:** with `fields`, a record is returned as `<Entity>Partial`, whose key fields are required and whose other fields are optional, and a page as `<Entity>PartialList`. Each record is read in full, converted to its wire type and narrowed with `dataservice:selectFields`. `dataservice:checkFields` validates the names first.
- **Entity description:** `dataservice_types.bal` holds a `dataservice:Entity` per entity that allows `read` or `create`, named `dataservice<Entity>Entity`, with the datastore and `dataservice<Entity>Fields`, the list of the name, column, type and flags of each field that callers can see. A relation's target description refers to the target's field list.
- **Parameters:** a query parameter that the operation does not take, such as `limit`, is ignored, and a repeated `filter`, `size` or `offset` uses its first value, as `ballerina/http` binds query parameters. An empty entry of `sort` or `fields` is dropped, so `fields=name,` is `fields=name` and `sort=` sorts by key. `fields=` with no value, and `sort=-`, which names an empty field, are answered with `400` and `INVALID_INPUT`.
- **Errors:** an invalid option is answered with `400` and `INVALID_INPUT`, with the messages of `ballerina/dataservice`, through `dsHttp:toQueryError`. A filter that does not bind to `<Entity>Filter`, such as one with an unknown field or operator, is answered by `ballerina/http` with `400`, which `DataserviceErrorInterceptor` answers with `INVALID_INPUT`.

#### 9.7.4. Relations

A relation field of an exposed entity is read in two ways when both entities allow `read`: by get related, and by naming it in `fields`. `Internal` on the relation field hides that direction: it has no get related resource, which answers `404`, and no field in `<Entity>Partial`, and its foreign key is still returned and written. `Internal` on the foreign key hides both directions, because reading either one filters on it.

- **Get related:** a resource named by the relation field follows the entity's full key, such as `/assignments/{projectCode}/{seq}/employee`, after get one and in model order of the relation fields. A to-one relation takes `fields`; a to-many relation takes the query parameters of get many on the related entity. `fields` names only the related entity's own fields, so with it get related returns `<Target>RelatedPartial` or `<Target>RelatedPartialList`. On the owning side, the record is read first, for its foreign key, and a to-one related record is then read by that key. On the other side, the record is read only when the result is empty, to tell a missing record (`404`, `NOT_FOUND`) from an empty relation. An empty to-one relation is `404` with `The record with the key '<key>' of the entity '<Entity>' has no '<relation>'.`
- **In `fields`:** get many and get one accept a relation name, which includes the whole related record, and `<relation>.<field>`, which includes the named fields. A to-one relation is read with one query per page and is `null` when there is no related record: on the side of a one to one relation that does not hold the foreign key, or when a foreign key points at a missing record. A foreign key is never null, because persist rejects a nullable relation field on the side that holds it. A to-many relation is read with one query per record, holds at most `dataserviceConfig.pageSize` records in key order, and is `{"items": [...], "hasMore": ...}`.
- **Types:** `<Entity>Partial` has one optional field per readable relation: `<Target>RelatedPartial?` for a to-one relation and `<Target>RelatedPartialList` for a to-many relation. `<Target>RelatedPartial` holds the related entity's own fields, with the key fields required. The entity description lists the readable relations, each with the related entity's own description.

#### 9.7.5. Constraints

The `ballerina/constraint` annotations of the model are kept in the generated persist types, so `ballerina/http` checks them on a request body and answers a value that breaks one with `400` and `INVALID_INPUT`. Wire types copy the annotations of the fields they hold as they are. A date, time or `byte[]` field is a string in a wire type, so create and patch check its constraints on the converted record with `dataservice:checkConstraints`, and answer a failure with `400`, `INVALID_INPUT` and `The field '<field>' of the entity '<Entity>' breaks its '<constraint>' constraint.` The persist client does not check constraints on its own writes.

#### 9.7.6. Field rules

`@dataservice:ReadOnly`, `@dataservice:Immutable` and `@dataservice:Internal` on a model field say what the service does with it, and shape the wire types:

| Rule | `<Entity>Payload` | `<Entity>InsertPayload` | `<Entity>UpdatePayload` |
|---|---|---|---|
| `ReadOnly` | Holds the field | Holds the field as optional, and its value is ignored | Holds the field as optional, and its value is ignored |
| `Immutable` | Holds the field | Holds the field | Does not hold the field |
| `Internal` | Does not hold the field | Does not hold the field | Does not hold the field |

The wire types are closed records, so a request that sets a field its wire type does not hold is answered with `400` by `ballerina/http`. An `Internal` field is also left out of `<Entity>Filter`, `<Entity>Partial` and the entity description, so no query option can use it. `transformPayloadTo<Entity>Insert` sets a `ReadOnly` or `Internal` field to the `value` of its annotation, or to `()`. A foreign key field declared with `@sql:Relation` takes the rules like any field; an inferred foreign key has no field to annotate. A relation field takes only `Internal`, described in [Relations](#974-relations). A foreign key field without a doc comment is described in the wire types as `The key of the related '<Target>'.`

The compiler plugin checks the rules when the tool compiles the model, as described in [Compile-time validation](#93-compile-time-validation).

#### 9.7.7. Auth

With `options.dataservice.auth = true`, the service checks the credential and the scopes of each request in `DataserviceAuthInterceptor`, a request interceptor, before `ballerina/http` dispatches the request and binds its parameters. `ballerina/http` declarative auth is not used, because it runs inside the resource, after dispatching and binding (ballerina-library#9632):

```ballerina
final readonly & map<map<string[]>> dataserviceScopes = {
    "employees": {"GET": ["hr:read"], "POST": ["employees:create"], "PATCH": ["employees:update"], "DELETE": ["hr:admin"]}
};

service http:InterceptableService /api on dataserviceListener {

    private final dsHttp:Authenticator authenticator;
    // the persist client and the page sizes

    function init() returns error? {
        // the persist client and the page sizes
        self.authenticator = check new (dataserviceConfig.serviceConfig.auth, dataserviceScopes,
                dataserviceConfig.serviceConfig.cors);
    }

    public function createInterceptors()
            returns [DataserviceAuthInterceptor, DataserviceErrorInterceptor] => [new (self.authenticator), new];
    // the resources of each exposed entity
}

isolated service class DataserviceAuthInterceptor {
    *http:RequestInterceptor;

    private final dsHttp:Authenticator authenticator;

    isolated function init(dsHttp:Authenticator authenticator) {
        self.authenticator = authenticator;
    }

    isolated resource function 'default [string... path](http:RequestContext ctx, http:Request request)
            returns http:NextService|dsHttp:Unauthorized|dsHttp:Forbidden|error? =>
        self.authenticator.authorize(ctx, request, path);
}
```

- **Scopes:** `scopes` in `@dataservice:Expose` gives each action's scopes; an action not listed requires `<collection>:<action>`, such as `employees:create`. `dataserviceScopes` maps each collection to the scopes of each HTTP method: `GET` for get many, get one and get related, which use the `read` scopes, `POST` for create, `PATCH` for patch and `DELETE` for delete. An empty list has no entry. An operation without scopes, and a path that is not a collection, such as the OpenAPI definition, take the `scopes` of the provider that authenticated the caller, set in `Config.toml`, as `ballerina/http` applies a service's scopes to a resource without scopes of its own; without those, they need only a credential.
- **Providers by scheme:** a `Basic` credential is checked only by the file and LDAP user stores, and a `Bearer` credential only by the JWT and OAuth2 providers, as `ballerina/http` does, so a password is never sent to an OAuth2 introspection endpoint.
- **Providers:** `dataserviceConfig` is a `dsHttp:AuthConfig`, whose `serviceConfig.auth`, an `http:ListenerAuthConfig[]`, has no default, so the program does not start without it. The service's `init` stops the program when the list is empty, because it cannot create the authenticator.
- **Responses:** a request without a valid credential is answered with `401` and `UNAUTHENTICATED`, and a caller without a scope of the operation with `403` and `FORBIDDEN`, whatever the request's path, method or body. A path or method without scopes, such as the OpenAPI definition, needs the deciding provider's `scopes`, as the Scopes rule above says, or only a credential when the provider sets none. An `OPTIONS` request, such as a CORS preflight, needs none. The OpenAPI definition lists `401` and `403` on every operation, from the interceptor's return type.
- **Headers:** the `401` carries one `WWW-Authenticate` challenge per kind of configured provider: `Basic realm="dataservice"` for a user store, and `Bearer realm="dataservice"` for JWT or OAuth2. `ballerina/http` adds CORS headers only to the responses of a resource, so the authenticator, given `dataserviceConfig.serviceConfig.cors`, adds them to its `401` and `403` as `ballerina/http` does for a simple cross-origin request: `Access-Control-Allow-Origin` for an allowed origin, and `Access-Control-Allow-Credentials` and `Access-Control-Expose-Headers` when set. The `404` and `405` of dispatching carry no CORS headers.
- **Patch and delete results:** with `read` allowed, patch and delete return the record only to a caller who may read it. They take the `http:RequestContext`, call `dsHttp:hasScope` with the `read` scopes, return `<Entity>|http:NoContent`, and answer `204` to a caller without one; an empty patch then reads nothing. An empty `read` list takes the deciding provider's `scopes`, so whether a caller may read is known only at runtime, and every patch and delete is checked.
- **Relations:** get related and a relation in `fields` require the entity's `read` scopes. Get many, get one and get related also take the `http:RequestContext` and call `dsHttp:checkRelationScopes` with the related entity's `read` scopes before reading the relation. It answers `403` with `FORBIDDEN` when the caller holds none of them. The caller is the one the service's authenticator authenticated, so the credential is not checked again.
- **Scopes before existence:** a relation's scope check runs before the record is read, so a caller without a `read` scope of the related entity gets `403` whether or not the record exists, and does not learn which records exist.
- **Introspection** is on by default, as without auth. The definition and Swagger UI are served behind the interceptor, so a request without a valid credential is answered with `401` and `UNAUTHENTICATED`, and an authenticated caller can read them when it holds one of the deciding provider's `scopes`, or always when the provider sets none. The `OPTIONS` `Link` header stays public.

#### 9.7.8. Errors

| Error | Status | `code` |
|---|---|---|
| `persist:NotFoundError` | `404` | `NOT_FOUND` |
| `persist:AlreadyExistsError` | `409` | `CONFLICT` |
| `persist:ConstraintViolationError` | `409` | `CONSTRAINT_VIOLATION` |
| A date, time or `byte[]` value that does not parse | `400` | `INVALID_INPUT` |
| A create above `dataserviceConfig.maxPageSize` records, or a `float` key that is not finite | `400` | `INVALID_INPUT` |
| A constraint broken on a date, time or `byte[]` field | `400` | `INVALID_INPUT` |
| An invalid query option, or a filter value that does not parse | `400` | `INVALID_INPUT` |
| A relation whose related entity the caller may not read, with `auth = true` | `403` | `FORBIDDEN` |
| A body, path segment or query parameter that `ballerina/http` cannot bind, or a constraint broken on another field | `400` | `INVALID_INPUT` |
| A missing or rejected credential, with `auth = true` | `401` | `UNAUTHENTICATED` |
| A caller without a scope of the action, with `auth = true` | `403` | `FORBIDDEN` |
| A path that matches no resource | `404` | `NOT_FOUND` |
| A method the path has no resource for, or another `4xx` of `ballerina/http` | `405` or that status | `INVALID_INPUT` |
| Any other error | `500` | `INTERNAL` |

Errors are answered with the `dataservice:DataserviceError` body, through the [`ballerina/dataservice.http`](https://github.com/ballerina-platform/module-ballerina-dataservice.http/blob/main/docs/spec/spec.md) functions, and the errors `ballerina/http` raises before a resource runs through `DataserviceErrorInterceptor`, which keeps their status. The interceptors are defined in the service's own package, because the OpenAPI definition leaves out an interceptor class from another package (ballerina-library#9633). Because the service is an `http:InterceptableService` with a response error interceptor, the OpenAPI definition does not add `ballerina/http`'s default error body to a resource's `400`. Each resource declares the error responses its operation can return:

| Operation | Error responses |
|---|---|
| Get many | `InvalidInput`, `InternalError` |
| Get one, get related | `NotFound`, `InvalidInput`, `InternalError` |
| Create | `InvalidInput`, `Conflict`, `ConstraintViolation`, `InternalError` |
| Patch | `NotFound`, `InvalidInput`, `Conflict`, `ConstraintViolation`, `InternalError` |
| Delete | `NotFound`, `InvalidInput`, `ConstraintViolation`, `InternalError` |

With `auth = true`, a resource that checks a relation also declares `Forbidden`; the `401` and `403` of the operation's scopes come from `DataserviceAuthInterceptor`. A path or method with no resource belongs to no resource, so the definition does not list it.

#### 9.7.9. Configuration

| Setting | Where | Default |
|---|---|---|
| Base path | `options.dataservice.basePath` in `Ballerina.toml` | `/api` |
| Publishing the OpenAPI definition | `options.dataservice.introspection` in `Ballerina.toml` | `true` |
| Port | `port` of `[dataserviceConfig]` in `Config.toml` | `9090` |
| Page size when the caller gives no size | `pageSize` of `[dataserviceConfig]` | `100` |
| Largest page, and the most records one create holds | `maxPageSize` of `[dataserviceConfig]` | `1000` |
| Other listener settings, such as TLS and timeouts | `listenerConfig` of `[dataserviceConfig]` | The `ballerina/http` defaults |
| CORS | `cors` of `[dataserviceConfig.serviceConfig]` | No cross-origin request |
| Authentication providers | `auth` of `[dataserviceConfig.serviceConfig]` | None: required when `auth` is `true`, and not declared otherwise |

The settings' types come from the libraries: `dsHttp:Config`, or `dsHttp:AuthConfig` with auth, both of which include `dataservice:Config`. `dataservice_config.bal` declares only the configurable value and the checked values:

```ballerina
configurable dsHttp:Config dataserviceConfig = {};
// With options.dataservice.auth = true:
// configurable dsHttp:AuthConfig dataserviceConfig = ?;
```

The service carries `@http:ServiceConfig {cors: dataserviceConfig.serviceConfig.cors}`.

A field left out of `[dataserviceConfig]` keeps its default, and an unknown field stops the program at startup, because the record is closed. The program also stops, with the message `ballerina/dataservice` gives, when `maxPageSize` is below 1 or `pageSize` is not between 1 and `maxPageSize`. `-C` arguments and environment variables cannot set one field of the record; set the table in `Config.toml`, or in a file named by `BAL_CONFIG_FILES`.

For example, to serve on port 8080 over TLS and allow a browser application on another origin:

```toml
[dataserviceConfig]
port = 8080

[dataserviceConfig.serviceConfig.cors]
allowOrigins = ["https://app.example.com"]

[dataserviceConfig.listenerConfig.secureSocket.key]
certFile = "/path/to/public.crt"
keyFile = "/path/to/private.key"
```

#### 9.7.10. OpenAPI definition

Unless `options.dataservice.introspection` is `false`, the service carries `@openapi:ServiceInfo {embed: true, title: "<package>"}`, so the compiler embeds an OpenAPI definition built from the service and its doc comments, and `ballerina/http` serves it:

| Request | Response |
|---|---|
| `OPTIONS <basePath>` | A `Link` header naming the definition (`rel="service-desc"`) and Swagger UI (`rel="swagger-ui"`) |
| `GET <basePath>/openapi-doc-dygixywsw` | The OpenAPI definition, whose `info.title` is the package name in words, such as `Dataservice H2` for `dataservice_h2` |
| `GET <basePath>/swagger-ui-dygixywsw` | Swagger UI, which loads its scripts from `unpkg.com` |

#### 9.7.11. Generated source

`dataservice.bal` holds, in order: the client and the listener, then one service with the resources of each exposed entity in model order, as get many, get one, get related, create, patch and delete. `dataservice_types.bal` holds, per entity in model order, the wire types, the `<Entity>List` envelope, the `<Entity>Filter`, `<Entity>Partial` and `<Entity>PartialList` types, the `<Entity>RelatedPartial` and `<Entity>RelatedPartialList` types, the entity description, the `<Entity>Created` and `<Entity>ListCreated` records, and the conversion functions. The list envelopes, `<Entity>List`, `<Entity>PartialList` and `<Entity>RelatedPartialList`, are open records, so a field added to them later, such as a total count, does not break a client; the other types are closed. A resource signature breaks before `returns`, and a long call breaks after its opening parenthesis or after the `=` of its assignment. Every resource and generated public type has a short doc comment, and every module-private member, such as a conversion function, a one-line one. A resource's summary comes from its operation, followed by the entity's doc comment from the model:

| Operation | Summary |
|---|---|
| Get many | `Gets a page of employees.` |
| Get one | `Gets an employee by its key.` |
| Get related | `Gets the department of an employee.` |
| Create | `Creates one or more employees.` |
| Patch | `Updates an employee by its key.` |
| Delete | `Deletes an employee by its key.` |

A field's `+ field` line in the generated types is its doc comment in the model: the first paragraph of the field's own doc comment, or else the field's `+ field` line in the entity's doc comment. A field without either has a fixed line, such as ``The `name` field of `Employee` ``.

## 10. Security considerations

- `bal persist pull` does not accept the database password as a command-line argument. It prompts for it, as described in [7.3](#73-behaviour).
- `bal persist push` reads the database credentials, including the password, from `Ballerina.toml`, as described in [6.2](#62-configuration). A user SHOULD NOT commit real credentials in `Ballerina.toml`.
- `bal persist push` drops and recreates the tables of the model, so the data in them is lost, as described in [6.3](#63-behaviour).
- The generated `Config.toml` holds the database connection values, including the password, as described in [5.6](#56-database-configuration).
- `bal persist pull` does not change the database schema, and `bal persist migrate` does not run its scripts against the database.
- A data service is generated without authentication unless `options.dataservice.auth` is `true`, as described in [9.2](#92-configuration). Without it, every caller can use every enabled operation of each exposed entity.
- With `auth = true`, each operation requires the scopes of its action, and patch, delete and relations check the `read` scopes, as described in [9.7.7](#977-auth). A request without a valid credential is answered with `401` before it is dispatched or bound, so binding errors do not reveal the request types to an unauthenticated caller. The OpenAPI definition and Swagger UI need a credential, and only `OPTIONS`, with its `Link` header, stays public.
- A field annotated with `@dataservice:Internal` is never returned or accepted by the data service, as described in [9.7.6](#976-field-rules).
- The data service allows no cross-origin request unless CORS is configured, as described in [9.7.9](#979-configuration). Swagger UI loads its scripts from `unpkg.com`, as described in [9.7.10](#9710-openapi-definition).

## References

- [Ballerina `persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md)
- [Ballerina `dataservice` specification](https://github.com/ballerina-platform/module-ballerina-dataservice/blob/main/docs/spec/spec.md)
- [Ballerina `dataservice.http` specification](https://github.com/ballerina-platform/module-ballerina-dataservice.http/blob/main/docs/spec/spec.md)
- [RFC 2119: Key words for use in RFCs to Indicate Requirement Levels](https://www.rfc-editor.org/rfc/rfc2119)
- [RFC 8174: Ambiguity of Uppercase vs Lowercase in RFC 2119 Key Words](https://www.rfc-editor.org/rfc/rfc8174)
