# Development Considerations

## Choosing a persistence approach

The application was originally implemented using H2. H2 is a reasonable lightweight starting point and can act as a stepping stone to a more comprehensive database such as Microsoft SQL Server or PostgreSQL. For this project, however, an in-memory implementation was considered more appropriate: it keeps the demo self-contained and avoids requiring a database to run it.

The current repositories use `ConcurrentHashMap` instances to store instruments, vendors, and prices in process memory. This is a practical choice for a small demonstration, but it is not a scalable or production persistence strategy. Data is lost when the application stops, and separate instances do not share data. As data volume and concurrent access grow, in-memory scans and synchronization can also become costly. A future database-backed repository can provide durable storage, indexes, and shared access while preserving the repository interfaces used by the services.

## Supporting both instrument-driven and vendor-driven queries

The price API needs to retrieve quotes in two directions:

- all vendors' prices for a given instrument;
- all instruments' prices for a given vendor.
- all current prices across every vendor and instrument.

The current `InMemoryPriceRepository` holds each price in one nested collection keyed first by instrument ID and then by vendor ID:

```text
instrumentId -> (vendorId -> Price)
```

This makes instrument-based lookups direct and lets the repository replace a vendor's existing quote for an instrument without searching the collection. Vendor-based lookup has no corresponding index, so it scans the instrument collections and checks for that vendor's price in each one. This is a table-scan-like trade-off: it avoids keeping duplicate indexes in memory, but vendor queries become more expensive as the number of instruments grows.

The all-prices endpoint also scans the stored quotes, then joins each price to its vendor and instrument to provide descriptive details. This is convenient for testing and small demonstration datasets, but its work and response size grow with the total number of current vendor/instrument pairs.

### Synchronization and Concurrent access

Every method in `InMemoryPriceRepository` is declared `synchronized`. They all acquire the same repository-instance monitor, so only one operation on that instance can run at a time. This protects compound operations such as updating or deleting entries across the nested maps, but also serializes reads and writes that might otherwise proceed concurrently. In particular, a vendor-driven scan or stale-price deletion holds the monitor while traversing prices, preventing other callers from accessing the repository during that work. With many simultaneous users, this can create lock contention, increase response times, and limit throughput; using `ConcurrentHashMap` does not remove the additional method-level lock.

Alternatives could reduce contention, such as using finer-grained per-key locks, read/write locking, or carefully designed atomic map operations. Each changes the complexity of coordinating multi-step updates and deletions, and may affect consistency guarantees. Any alternative should be evaluated as a compromise between complexity, performance, and safety rather than adopted solely to remove synchronization. A database-backed implementation may offer indexing and transaction mechanisms better suited to higher concurrent loads, but would also need to preserve the repository's behaviour and consistency rules.

An alternative is to maintain a second index keyed by vendor ID. That can make queries in both directions efficient, but each price save or deletion must update both collections consistently. If one update succeeds and the other does not, the indexes can disagree; synchronization, rollback behaviour, or careful encapsulation is then required.

For the size and purpose of this demo, keeping a single source of truth and accepting the vendor-query scan was the simpler compromise. If query volume or data size increases, the storage strategy should be revisited. A database can index both `instrumentId` and `vendorId` without storing two independently maintained copies of each price record.

## Summary of the trade-off

The current design favours simplicity and a single authoritative in-memory representation over durability and optimal performance for every access pattern. That keeps the demo easy to run and reason about, while the repository abstraction leaves room to replace the maps when production-scale persistence or query performance is needed.
