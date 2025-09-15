# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.3 microservice for managing product catalog data in an e-commerce system. It's part of the ECMSP (E-Commerce Microservices Platform) with support for product reservations, categories, attributes, and variants.

## Technology Stack

- **Java 21** with Spring Boot 3.5.3
- **PostgreSQL** database with JPA/Hibernate
- **Apache Kafka** for messaging
- **Maven** for build management
- **Docker** for containerization
- **Lombok** for code generation
- Uses **hypersistence-utils** for JSON column support

## Common Commands

### Build and Test
```bash
# Clean build
mvn clean compile

# Run tests
mvn clean test

# Package application
mvn clean package

# Run application locally
mvn spring-boot:run

# Skip tests during build
mvn clean package -DskipTests
```

### Docker
```bash
# Build Docker image
docker build -t product-service .

# Run with Docker Compose (from parent directory)
docker-compose --profile product-service up

# Run full stack with Kafka
docker-compose --profile product-service --profile kafka up
```

### Testing
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProductServiceTest

# Run tests with coverage
mvn clean test jacoco:report
```

## Architecture Overview

### Domain Model
The service follows DDD patterns with these core entities:
- **Product**: Main entity with categories, pricing, and JSONB info field
- **Variant**: Product variations with specific attributes
- **Category**: Hierarchical product categorization
- **Attribute/AttributeValue**: Configurable product properties
- **VariantAttribute**: Links variants to their attribute values

### Package Structure
```
src/main/java/com/ecmsp/productservice/
├── domain/          # JPA entities
├── dto/             # Request/Response DTOs
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic layer
└── exception/       # Custom exceptions
```

### Key Design Patterns
- **Repository Pattern**: Spring Data JPA repositories for data access
- **DTO Pattern**: Separate DTOs for API contracts
- **Builder Pattern**: Using Lombok @Builder for entity construction
- **Service Layer**: Business logic separation from controllers

## Database Configuration

The service uses PostgreSQL with multiple Spring profiles:
- `dev`: Development configuration
- `compose`: Docker Compose configuration
- `prod`: Production configuration

JSONB columns are used for flexible product info storage using hypersistence-utils.

## Dependencies and Integration

### External Dependencies
- **protos** (1.0.0-SNAPSHOT): gRPC protocol definitions from Nexus repository
- **Spring Kafka**: For event-driven communication
- **hypersistence-utils**: Enhanced Hibernate JSON support

### Repository Configuration
Uses custom Nexus repositories for internal artifacts:
- Releases: `https://nexus.ecmsp.pl/repository/maven-releases/`
- Snapshots: `https://nexus.ecmsp.pl/repository/maven-snapshots/`

## Development Workflow

### Testing Strategy
- Unit tests use H2 in-memory database
- Integration tests use `@SpringBootTest`
- Test utilities in `testutil` package for data generation

### CI/CD
GitHub Actions workflow runs:
1. `mvn clean test -U` on pull requests
2. Docker image building and GCP registry push on main branch

### Local Development
1. Start dependencies: `docker-compose --profile kafka up`
2. Run service: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
3. Access at http://localhost:8080

## Important Notes

- Service runs on port 8080 in containers
- Uses UUID primary keys for all entities
- Lazy loading configured for entity relationships
- JSON columns for flexible product information storage
- Maven wrapper available (`./mvnw` on Unix, `mvnw.cmd` on Windows)