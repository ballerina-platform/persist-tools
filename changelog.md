# Changelog
This file contains all the notable changes done to the Ballerina Persist Tools through the releases.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

### Added

- [Add documentation for generated client](https://github.com/ballerina-platform/ballerina-library/issues/8467)
- [Add support for selecting tables in ballerina persist pull command](https://github.com/ballerina-platform/ballerina-library/issues/8468)

### Changed
- [Fix an SQL script generation order issue when there are multiple associations](https://github.com/ballerina-platform/ballerina-library/issues/7921)

## [1.4.0] - 2024-08-20

### Changed
- Fix an issue where client API is still generated even if all entities contain unsupported field(s)
- Fix an issue where unique indexes are declared twice in script.sql in one-to-one associations
- Fix an issue where the cardinality of the first association is taken as the cardinality of all the other associations between same entities
- [Fix an issue where table name of the entity becomes empty when doc comments are above the entity definition](https://github.com/ballerina-platform/ballerina-library/issues/6497)

### Added
- [Add introspection support for PostgreSQL databases](https://github.com/ballerina-platform/ballerina-library/issues/6333)
- [Add introspection support for MSSQL databases](https://github.com/ballerina-platform/ballerina-library/issues/6460)
- [Add h2 database support as a datastore](https://github.com/ballerina-platform/ballerina-library/issues/5715)
- [Add support for generating client APIs for the test datastore](https://github.com/ballerina-platform/ballerina-library/issues/5840)

## [1.3.0] - 2024-05-03

### Changed
- [Refactor migrate command and test cases to support more scenarios](https://github.com/ballerina-platform/ballerina-library/issues/6189)
- [Fix a bug in the `migrate` command where tables with `@sql:Name` annotations are recreated](https://github.com/ballerina-platform/ballerina-library/issues/6374)

### Added
- [Added support for PostgreSQL as a datasource](https://github.com/ballerina-platform/ballerina-standard-library/issues/5829)
- [Integrate the persist code generation to the bal build command](https://github.com/ballerina-platform/ballerina-library/issues/5784)
- [Added introspection support for MySQL databases](https://github.com/ballerina-platform/ballerina-library/issues/6014)
- [Added advanced annotation support for SQL databases](https://github.com/ballerina-platform/ballerina-library/issues/6013)
- [Added support for name, type, generated and relation annotations in migrate command](https://github.com/ballerina-platform/ballerina-library/issues/6189)
- [Added support for index and unique index annotations in migrate command](https://github.com/ballerina-platform/ballerina-library/issues/6189)

## [1.2.1] - 2021-11-21

### Changed
- [Fixed the issue related to existing configurations in Config.toml file](https://github.com/ballerina-platform/persist-tools/issues/314)
- [Fix the logic in persist client generation with respect to refColumns in the joinMetadata](https://github.com/ballerina-platform/persist-tools/issues/312)

## [1.2.0] - 2023-09-19

### Added
- [Added support for module names with module seperator](https://github.com/ballerina-platform/persist-tools/issues/273)

## [1.1.0] - 2023-07-04

### Added
- [Added support for MSSQL as a datasource](https://github.com/ballerina-platform/ballerina-standard-library/issues/4506)

### Changed
- [Updated error messages to be consistent across all data sources](https://github.com/ballerina-platform/ballerina-standard-library/issues/4360)
- [Removed constraint ID from foreign keys in generated SQL scripts](https://github.com/ballerina-platform/ballerina-standard-library/issues/4581)


## [1.0.0] - 2021-06-01

### Added

### Changed
- [Fix bug in generated types when there are byte[] typed fields](https://github.com/ballerina-platform/ballerina-standard-library/issues/4075)
- [Fix bug in supporting optional byte[] type](https://github.com/ballerina-platform/ballerina-standard-library/issues/4074)
- [Migrate the generation of database_configuration.bal to generate command](https://github.com/ballerina-platform/ballerina-standard-library/issues/4118)
- [Change Ballerina.toml configs generation in persist tooling](https://github.com/ballerina-platform/ballerina-standard-library/issues/4135)

## [0.1.0] - 2023-02-21

### Added
 
- Support to for `bal persist init` command
- Support to for `bal persist generate` command
- Support to for `bal persist push` command
