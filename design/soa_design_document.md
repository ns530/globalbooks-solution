# SOA Design Document for GlobalBooks Inc. Refactoring

## Task 1: SOA Design Principles Applied in Decomposition

In decomposing the legacy monolithic Java system into independent services (Catalog, Orders, Payments, Shipping), the following SOA design principles were applied to ensure a robust, scalable architecture:

1. **Service Autonomy**: Each service (e.g., CatalogService) operates independently with its own data store, allowing for separate deployment and scaling without affecting others. This addresses the monolith's tight coupling, where changes in one module risked system-wide downtime.

2. **Loose Coupling**: Services communicate via standardized interfaces (SOAP for Catalog, REST for Orders) and asynchronous messaging (RabbitMQ for Payments/Shipping), reducing dependencies. For instance, OrdersService invokes CatalogService for price lookups without direct database access.

3. **Reusability**: Services are designed as reusable components. CatalogService can be invoked by multiple clients (e.g., OrdersService and external partners), promoting code reuse and reducing redundancy.

4. **Discoverability**: UDDI-based registry enables dynamic service discovery, allowing clients to locate services at runtime without hardcoded endpoints.

5. **Interoperability**: Use of open standards (SOAP, REST, BPEL) ensures compatibility across platforms, supporting legacy partners (SOAP) and new clients (REST).

6. **Composability**: BPEL orchestrates the "PlaceOrder" workflow by composing services (Catalog → Orders → Payments/Shipping), enabling complex business processes.

These principles were chosen based on the scenario's requirements for global scalability, fault tolerance, and integration with diverse clients.

## Task 2: Key Benefit and Primary Challenge

**Key Benefit**: Improved Scalability and Fault Isolation. By decomposing into autonomous services, GlobalBooks can scale individual components (e.g., Payments during peak sales) independently, reducing load on the monolith. Faults in one service (e.g., Shipping) are isolated, preventing cascading failures, as seen in the original system's buckles under load.

**Primary Challenge**: Increased Integration Complexity. Coordinating four services via RabbitMQ and BPEL introduces overhead in message routing, error handling, and synchronization. This requires additional infrastructure (e.g., message brokers) and expertise, potentially increasing development and maintenance costs compared to the monolith.

This design ensures the refactored system meets SOA best practices while addressing GlobalBooks' pain points.