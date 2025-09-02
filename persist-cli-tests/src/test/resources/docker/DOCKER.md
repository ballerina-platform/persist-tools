# Docker Environment for Ballerina Persist Tools

This project uses Docker Compose to manage test databases required for testing the persist tools.

## Services

The Docker Compose setup provides the following services:

### Test Databases (for persist-cli-tests)
- **MySQL** (`mysql-tests`): Port 3307, root password: `Test123#`
- **MSSQL** (`mssql`): Port 1434, SA password: `Test123#`
- **PostgreSQL** (`postgresql`): Port 5432, user: `postgres`, password: `postgres`

### Example Databases (for examples)
- **MySQL Examples** (`mysql-examples`): Port 3308, includes `hospital` database
- **Redis** (`redis`): Port 6379

## Usage

### Manual Control
```bash
# Start all services
./gradlew dockerComposeUp

# Stop all services
./gradlew dockerComposeDown

# Clean all services and volumes
./gradlew dockerComposeClean

# Check service status
./gradlew dockerComposeStatus

# View logs
./gradlew dockerComposeLogs

# Wait for all services to be healthy
./gradlew waitForServices
```

### Automatic Usage
The Gradle build system automatically manages these containers:

- **Tests**: `./gradlew test` automatically starts and stops test databases
- **Examples**: `./gradlew build` automatically manages example databases

## Health Checks

All services include health checks:
- **MySQL**: Uses `mysqladmin ping`
- **MSSQL**: Uses `sqlcmd` connectivity test
- **PostgreSQL**: Uses `pg_isready`
- **Redis**: Uses `redis-cli ping`

The Gradle scripts wait for services to be healthy before proceeding with tests.

## Troubleshooting

### Container Conflicts
If you see "container name already in use" errors:
```bash
./gradlew dockerComposeClean
```

### Port Conflicts
Check if the required ports are available:
- 3307 (MySQL tests)
- 3308 (MySQL examples)
- 1434 (MSSQL)
- 5432 (PostgreSQL)
- 6379 (Redis)

### Windows Support
Docker Compose tasks are automatically skipped on Windows systems.

## Migration from Individual Containers

This setup replaces the previous individual `docker run` commands with a unified Docker Compose approach, providing:

- ✅ Better container lifecycle management
- ✅ Proper health checks instead of arbitrary sleeps
- ✅ Automatic cleanup on failures
- ✅ Centralized configuration
- ✅ Network isolation
- ✅ Volume management