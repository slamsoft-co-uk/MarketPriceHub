# Application Limitations

This document records implementation limitations identified during development and distinguishes existing behaviour from future work.

## Problems encountered and remaining limitations

### Price delivery is pull-based

The application provides a REST API for users to add vendors and instruments, publish prices, and retrieve current prices. This interface is therefore a pull-based service: users must make a request to receive data, and the application does not automatically notify existing subscribers when a price changes.

A publish/subscribe service would allow users to register interest in a vendor, instrument, or vendor/instrument combination and receive price changes as soon as they are published. This would be beneficial for consumers that need timely updates without repeatedly polling the REST endpoints.

The code would require a moderate change rather than a complete redesign. `PublishPricesService` already validates and saves each new `Price`, so a price-change event could be published immediately after a successful repository save. Because the application is currently in-memory and runs as a single process, Spring events or Server-Sent Events could provide a demonstration subscription mechanism. A broker-backed implementation would be needed for subscriptions across application instances, reconnect and replay support, delivery acknowledgements, and durable event history.

The subscription interface would also need to define matching and access rules, add a managed subscription lifecycle, and handle disconnected clients. In addition to the price-change event, the implementation would need integration tests for publication, subscription filtering, reconnect behaviour, and delivery when more than one application instance or repository implementation is used.

### User Security is not implemented

The application currently has no user security in place: endpoints are accessible without authenticating a user, and the application does not enforce permissions by role. Ideally, all users would need to authenticate before accessing the system. Authorization should distinguish at least two roles: a read-only role for obtaining prices, and a read/write role for adding vendors and instruments and publishing prices. Authentication and role-based authorization remain future work.

### In-memory data is not durable

`Instrument`, `Vendor`, and `Price` records are stored in in-memory maps. This is suitable for the current self-contained demonstration but does not retain data after a restart or share data between application instances.

The services depend on repository interfaces, so database storage can be introduced behind those interfaces. The database implementation, schema, migrations, configuration, and integration tests have not been added; durable persistence remains future work.

### Price records represent current quotes, not history

The price repository is keyed by instrument ID and then vendor ID. Publishing another price for the same pair replaces the existing record. This was documented as current-quote behaviour; retaining historical quotes would require changing the storage key and reviewing the read and cleanup operations.

### Reference integrity is checked in the publishing service

The `Price` record stores vendor and instrument IDs rather than object references. The publishing service checks that both IDs exist before saving, but the in-memory repositories do not enforce database-style foreign keys. A future database implementation should add foreign-key constraints and handle the save operation transactionally.
