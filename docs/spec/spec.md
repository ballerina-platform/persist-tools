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
- [9. Security considerations](#9-security-considerations)
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

## 2. Terminology

- **Model definition file:** the Ballerina file that defines the entities of a data model as per the [`persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md#3-data-model-definition), named `model.bal`.
- **Root model:** the model whose definition file is `persist/model.bal`. A command applies to the root model when `--model` is not given.
- **Named model:** a model whose definition file is `persist/<model-name>/model.bal`, selected with `--model <model-name>`.
- **Datastore:** the data source that the generated client uses, such as `mysql`.
- **Test datastore:** the data source that the generated test client uses, `h2` or `inmemory`.
- **Persist module:** the module in which the generated files are placed, given with `--module`. It is the package's default module when `--module` is not given.
- **Tool entry:** a `[[tool.persist]]` table in `Ballerina.toml`, which makes `bal build` generate the client.
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

### 3.3. Behaviour

1. The command requires a Ballerina project.
2. The command creates the `persist` directory, which holds the model definition file.
3. If no files are present in the `persist` directory, the command creates `model.bal` in it with the required import, `import ballerina/persist as _;`.
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
8. The `id` of each tool entry is unique. The command fails when `Ballerina.toml` already has a tool entry with the same `id`. To add the persistence layer for another model, the user runs the command again with a different `--model` or `--id`.
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

1. If the module name is the same as the package name, the tool generates the files in the `generated` directory, as part of the default module.
2. If the module name is different from the package name, the tool generates the files under a new submodule with the same name as the file.

| File | Content |
|---|---|
| `persist_client.bal` | The client API for the entities defined in the model definition file. |
| `persist_db_config.bal` | The configurable variables required for database access. See [5.6](#56-database-configuration). |
| `persist_types.bal` | The derived entity types of the entities defined in the model definition file. See [5.5](#55-derived-types). |
| `persist_test_client.bal` | The client API for the entities defined in the model definition file, for the test datastore. Generated only when a test datastore is given. |
| `persist_test_init.bal` | The initialization of the test database schema of the entities defined in the model definition file, for the test datastore. Generated only when `h2` is the test datastore. |
| `script.sql` | The SQL script that creates the database schema of the entities defined in the model definition file. See [5.7](#57-database-schema). |

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
│   └── medical-center.bal
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

## 9. Security considerations

- `bal persist pull` does not accept the database password as a command-line argument. It prompts for it, as described in [7.3](#73-behaviour).
- `bal persist push` reads the database credentials, including the password, from `Ballerina.toml`, as described in [6.2](#62-configuration).
- `bal persist push` drops and recreates the tables of the model, so the data in them is lost, as described in [6.3](#63-behaviour).
- The generated `Config.toml` holds the database connection values, including the password, as described in [5.6](#56-database-configuration).
- `bal persist pull` does not change the database schema, and `bal persist migrate` does not run its scripts against the database.

## References

- [Ballerina `persist` specification](https://github.com/ballerina-platform/module-ballerina-persist/blob/main/docs/spec/spec.md)
- [RFC 2119: Key words for use in RFCs to Indicate Requirement Levels](https://www.rfc-editor.org/rfc/rfc2119)
- [RFC 8174: Ambiguity of Uppercase vs Lowercase in RFC 2119 Key Words](https://www.rfc-editor.org/rfc/rfc8174)
