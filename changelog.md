# Changelog
This file contains all the notable changes done to the Ballerina Persist Tools through the releases.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

### Changed

## [1.2.0] - 2023-09-18

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
