FROM maven:3.10-eclipse-temurin-25-alpine AS builder

WORKDIR /app

COPY pom.xml ./
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests package

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar
RUN useradd --system --uid 10001 --create-home skillswap
USER skillswap

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
