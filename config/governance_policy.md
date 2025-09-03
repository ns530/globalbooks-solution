# Governance Policy for GlobalBooks SOA

## Versioning Strategy
- **URL Conventions**: Use /v{major}.{minor} (e.g., /v1.0/orders for OrdersService).
- **Namespace Conventions**: SOAP: http://globalbooks.com/catalog/v1.0; REST: application/vnd.globalbooks.orders.v1+json.
- **Backward Compatibility**: Maintain for minor versions; major changes require new endpoints.

## SLA Targets
- **Availability**: 99.5% uptime, monitored via UDDI and service health checks.
- **Response Time**: Sub-200 ms for catalog lookups; sub-500 ms for order processing.
- **Monitoring**: Use tools like Prometheus for metrics; alerts for breaches.

## Deprecation Plan
- **Notice Period**: 6 months advance notice via UDDI metadata and client emails.
- **Sunset Process**: Mark as deprecated in registry; provide migration guides; retire after notice period.
- **Fallback**: Legacy clients redirected to supported versions.