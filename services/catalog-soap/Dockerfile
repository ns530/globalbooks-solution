# syntax=docker/dockerfile:1
# Build WAR with Maven (JDK 11) and deploy to Tomcat 9 (JDK 11)

FROM maven:3.8.7-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml ./
RUN mvn -q -DskipTests=true dependency:go-offline
COPY src ./src
# FIXED: Produce WAR artifact
RUN mvn -q -DskipTests=true clean package

FROM tomcat:9.0-jdk11-temurin
# FIXED: Clean default apps
RUN rm -rf /usr/local/tomcat/webapps/*
# Copy WAR to ROOT.war so endpoint is /catalog as per web.xml mapping
COPY --from=build /app/target/catalog-soap-1.0.0.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]