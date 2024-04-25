# Changelog
This file contains all the notable changes done to the Ballerina Persist Tools through the releases.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Unreleased

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
