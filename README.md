# Resource Management API

Spring Boot REST API for managing resources across Estonia and Finland with Kafka event publishing.

## Quick Start

```
git clone <repository-url>
cd resource-management
docker-compose up --build
```

Wait for all services to start, then access:

- **Application**: http://localhost:8080/swagger-ui.html
- **Kafka Events**: http://localhost:8090

## Test Kafka Events

1. Go to Swagger UI: http://localhost:8080/swagger-ui.html
2. Create a resource using POST `/api/v1/resources`
3. Go to Kafka UI: http://localhost:8090
4. Click **Topics** → **resource-events** → **Messages**
5. See your event published to Kafka

Every CREATE/UPDATE/DELETE operation sends events to Kafka automatically.

## Database Access
- **URL**: localhost:5432/resource_db
- **User/Pass**: postgres/postgres

## Stop Everything
```
docker-compose down
```