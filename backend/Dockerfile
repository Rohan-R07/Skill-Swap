# Build stage
FROM eclipse-temurin:25-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and configuration
COPY .mvn .mvn
COPY mvnw mvnw.cmd pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -q

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw package -q -DskipTests

# Run stage
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
