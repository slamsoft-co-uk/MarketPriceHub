# Application Limitations

This document records implementation limitations identified during development and distinguishes existing behaviour from future work.

## Problems encountered and remaining limitations

### In-memory data is not durable

`Instrument`, `Vendor`, and `Price` records are stored in in-memory maps. This is suitable for the current self-contained demonstration but does not retain data after a restart or share data between application instances.

The services depend on repository interfaces, so database storage can be introduced behind those interfaces. The database implementation, schema, migrations, configuration, and integration tests have not been added; durable persistence remains future work.

### Price records represent current quotes, not history

The price repository is keyed by instrument ID and then vendor ID. Publishing another price for the same pair replaces the existing record. This was documented as current-quote behaviour; retaining historical quotes would require changing the storage key and reviewing the read and cleanup operations.

### Reference integrity is checked in the publishing service

The `Price` record stores vendor and instrument IDs rather than object references. The publishing service checks that both IDs exist before saving, but the in-memory repositories do not enforce database-style foreign keys. A future database implementation should add foreign-key constraints and handle the save operation transactionally.
