# syntax=docker/dockerfile:1
# FIXED: Use JDK 17 toolchain in container to bypass local "release 17 not supported" error

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Leverage Docker layer caching for dependencies
COPY pom.xml ./
RUN mvn -q -DskipTests=true dependency:go-offline

# Copy sources and build
COPY src ./src
# FIXED: skip tests in container build to speed up dev image creation
RUN mvn -q -DskipTests=true clean package

# Runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# FIXED: default to dev profile to relax OAuth/JWT as configured in SecurityConfig
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}
ENV SERVER_PORT=8081

# FIXED: Copy built jar with explicit path to avoid Docker COPY glob multi-source error
COPY --from=build /app/target/payment-service-1.0.0.jar /app/app.jar

EXPOSE 8081

# FIXED: Use a simple entrypoint; JVM args can be injected via docker-compose if needed
ENTRYPOINT ["java","-jar","/app/app.jar"]