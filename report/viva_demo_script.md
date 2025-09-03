# Viva Demo Script: GlobalBooks SOA Implementation

## Step-by-Step Demo Plan
1. **Introduction**: Explain SOA principles applied and decomposition rationale.
2. **Design Artifacts**: Show WSDL, UDDI metadata, governance policy.
3. **CatalogService Demo**: Run SOAP service, test with SOAP UI (getBookById, getBooksByCategory).
4. **OrdersService Demo**: Run REST service, test with Postman (POST /orders, GET /orders/{id}).
5. **Integration Demo**: Start RabbitMQ, run Payments/Shipping consumers, show message flow.
6. **Orchestration Demo**: Deploy BPEL process, execute PlaceOrder workflow.
7. **Security Demo**: Show WS-Security policy, OAuth2 config.
8. **QoS Demo**: Demonstrate persistent messages and publisher confirms in RabbitMQ.
9. **Failure Scenarios**: Simulate service failure, show dead-letter routing.
10. **Conclusion**: Discuss trade-offs and governance.

Prepare screenshots/logs for each step to support viva defense.