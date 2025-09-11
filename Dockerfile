# Multi-stage build for smaller image size
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# IMPORTANT: COPY pom.xml for caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Dependencies LOAD & CACHE!
RUN mvn dependency:go-offline -B

# Copy Sourcecode
COPY src ./src

# Build (dependencies in cache)
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copy jar from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "app.jar"]