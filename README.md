# Resource Management API

A comprehensive Spring Boot REST API for managing energy resources (metering points and connection points) across Estonia and Finland with real-time Kafka event streaming.

## 🚀 Quick Start

**Prerequisites:** Docker Desktop only (Java, Maven, PostgreSQL, Kafka not required)

```bash
git clone <repository-url>
cd resource-management
docker-compose up --build
```


- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Kafka Events UI**: http://localhost:8090
- **Database**: localhost:5432 (postgres/postgres)

## ✅ Requirements Fulfilled

This API fully meets the assignment requirements:

### Core REST Operations
- ✅ **Creating new resources** with location and characteristics
- ✅ **Retrieving resources** (both single by ID and filtered lists)
- ✅ **Updating existing resources**, locations, and characteristics
- ✅ **Deleting resources**

### Additional Features
- ✅ **Kafka Integration**: Real-time events on CREATE/UPDATE/DELETE
- ✅ **Bulk Export**: Export all resources to Kafka
- ✅ **Sample Data**: 4 pre-loaded resources (Estonia & Finland)
- ✅ **Comprehensive Testing**: Unit, Integration, Repository tests
- ✅ **Docker Ready**: Complete containerized setup

## 📋 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/resources` | Create new resource with location and characteristics |
| GET | `/api/v1/resources/{id}` | Retrieve a single resource by ID |
| GET | `/api/v1/resources` | Retrieve all resources (with optional filters) |
| PUT | `/api/v1/resources/{id}` | Update existing resource, location, and characteristics |
| DELETE | `/api/v1/resources/{id}` | Delete resource |
| POST | `/api/v1/resources/export-all` | Export all resources to Kafka |

### Query Options

You can retrieve resources using various filters:

```bash
# Get single resource by ID
GET /api/v1/resources/{id}

# Get all resources
GET /api/v1/resources

# Filter by country code
GET /api/v1/resources?countryCode=EE

# Filter by resource type  
GET /api/v1/resources?type=METERING_POINT

# Combine filters
GET /api/v1/resources?countryCode=EE&type=METERING_POINT

# Pagination and sorting
GET /api/v1/resources?page=0&size=20&sort=createdAt,desc
```

## 🧪 Testing the API

### Option 1: Swagger UI (Recommended)
1. Open http://localhost:8080/swagger-ui.html
2. Try any endpoint interactively
3. Check Kafka events at http://localhost:8090


```

## 📊 Real-time Kafka Events

Every resource operation automatically publishes events:

1. Create/Update/Delete a resource via API
2. Event is published to `resource-events` topic
3. View events in Kafka UI: http://localhost:8090
    - Click **Topics** → **resource-events** → **Messages**

## 📁 Project Structure

```
src/
├── main/java/com/energia/resourcemanagement/
│   ├── controller/     # REST endpoints
│   ├── service/        # Business logic
│   ├── repository/     # Data access (JPA)
│   ├── domain/         # Entities & Enums
│   ├── dto/            # Request/Response objects
│   ├── kafka/          # Event publishing
│   ├── mapper/         # MapStruct mapping
│   ├── exception/      # Error handling
│   └── config/         # Spring configuration
├── main/resources/
│   ├── db/migration/   # Flyway database schemas
│   └── application*.properties
└── test/               # Comprehensive test suite
    ├── integration/    # API & Repository tests
    └── unit/          # Service layer tests
```

## 🛠 Technology Stack

- **Java 21** + **Spring Boot 3.5.5**
- **PostgreSQL 15** (database)
- **Apache Kafka** (event streaming)
- **Flyway** (database migrations)
- **MapStruct** (DTO mapping)
- **Testcontainers** (integration testing)
- **Docker & Docker Compose** (containerization)
- **Swagger/OpenAPI** (API documentation)

## 🎯 Sample Data

The application starts with 4 pre-loaded resources:

1. **Estonia Metering Point** (Tallinn) - Residential consumption + Type 2 charging
2. **Finland Connection Point** (Helsinki) - Active status + Commercial consumption
3. **Finland Metering Point** (Helsinki) - Industrial consumption + CCS charging
4. **Estonia Connection Point** (Tallinn) - Inactive status

## 🔧 Development Features

- **Optimistic Locking**: Version control for concurrent updates
- **Validation**: Comprehensive input validation with detailed error messages
- **Error Handling**: Global exception handler with structured error responses
- **Auditing**: Automatic `created_at` and `updated_at` timestamps
- **Health Checks**: Docker health checks for all services
- **Logging**: Structured logging with different levels per environment

## 🧩 Domain Model

```
Resource
├── id (UUID)
├── type (METERING_POINT | CONNECTION_POINT)
├── countryCode (EE | FI)
├── location (embedded)
│   ├── streetAddress
│   ├── city
│   ├── postalCode
│   └── countryCode
├── characteristics (one-to-many)
│   ├── code (max 5 chars)
│   ├── type (CONSUMPTION_TYPE | CHARGING_POINT | CONNECTION_POINT_STATUS)
│   └── value
├── createdAt
├── updatedAt
└── version (optimistic locking)
```

## 🏃‍♂️ Running Tests

```bash
# Run all tests (requires Docker)
./mvnw test

# Run specific test category
./mvnw test -Dtest="*IntegrationTest"
./mvnw test -Dtest="*UnitTest"
```

## 🛑 Stopping the Application

```bash
docker-compose down        # Stop containers
docker-compose down -v     # Stop and remove volumes
```


