# syntax=docker/dockerfile:1
# Build and run Spring Boot 2.5.x app with JDK 11

FROM maven:3.8.7-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -q -DskipTests=true dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests=true clean package

FROM eclipse-temurin:11-jre
WORKDIR /app
ENV SERVER_PORT=8080
COPY --from=build /app/target/orders-rest-1.0.0.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]