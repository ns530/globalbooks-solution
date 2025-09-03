# GlobalBooks SOA Refactoring Project

## Overview
This project demonstrates SOA & Microservices refactoring of GlobalBooks Inc.'s monolithic system into services: Catalog (SOAP), Orders (REST), Payments, Shipping (via RabbitMQ), with BPEL orchestration.

## Structure
- `/design`: SOA design document, WSDL, UDDI metadata.
- `/services/catalog-soap`: Java SOAP WAR for CatalogService.
- `/services/orders-rest`: Spring Boot REST for OrdersService.
- `/services/payments`: Node.js RabbitMQ consumer.
- `/services/shipping`: Node.js RabbitMQ consumer.
- `/integration/rabbitmq`: RabbitMQ config.
- `/orchestration/bpel`: BPEL process and deployment.
- `/config`: Security and governance configs.
- `/tests`: SOAP UI, Postman, curl scripts.
- `/report`: Trade-off analysis, viva prep.

## Running the Project
1. **Prerequisites**: Java 8+, Maven, Node.js, RabbitMQ, Apache ODE (for BPEL).
2. **CatalogService**: `cd services/catalog-soap && mvn clean install && mvn tomcat7:run` (runs on :8080).
3. **OrdersService**: `cd services/orders-rest && mvn spring-boot:run` (runs on :8080).
4. **Payments/Shipping**: `cd services/payments && npm install && npm start` (same for shipping).
5. **RabbitMQ**: Start server, apply config from integration/rabbitmq/rabbitmq_config.json.
6. **BPEL**: Deploy orchestration/bpel/place_order.bpel via ODE console.
7. **Tests**: Import tests/catalog_soapui_project.xml into SOAP UI; tests/orders_postman_collection.json into Postman; run tests/orders_curl_tests.sh.

All services include test data and comments for clarity.