# Project Implementation

## Overview

Market Price Hub is implemented as a Spring Boot REST application. Its code is divided into web controllers, application services, domain records, and repository interfaces with in-memory implementations. This separation keeps HTTP concerns, business rules, and storage operations in distinct parts of the application.

The current repositories store data in process memory. They provide a simple implementation for development and demonstration, rather than durable persistence. The repository interfaces are the seam for replacing these maps with a database-backed implementation.

## Application layers

| Layer | Responsibility | Examples |
|---|---|---|
| Web controllers | Map HTTP routes, validate request data, call services, and format responses. | `PublishPricesController`, `AddVendorController` |
| DTOs | Define the request and response shapes at the API boundary. Request DTOs use Jakarta Bean Validation constraints. | `PublishPricesRequest`, `VendorUpdateRequest` |
| Services | Coordinate application behaviour, including entity checks, price publication, price lookups, and stale-price cleanup. | `PublishPricesService`, `GetPricesForAllVendorsOfGivenInstrumentService`, `GetAllPricesService` |
| Domain model | Represent the business data as immutable Java records. | `Instrument`, `Vendor`, `Price` |
| Repository interfaces | Define the data operations required by services without specifying how data is stored. | `InstrumentRepository`, `VendorRepository`, `PriceRepository` |
| Repository implementations | Implement those operations using the current in-memory maps. | `InMemoryInstrumentRepository`, `InMemoryVendorRepository`, `InMemoryPriceRepository` |

For example, publishing a price follows this path:

1. `PublishPricesController` accepts the HTTP request and validates its DTO.
2. `PublishPricesService` confirms that the instrument and vendor IDs exist.
3. The service creates a `Price` record and saves it through `PriceRepository`.
4. The controller returns the HTTP response.

## Design patterns and implementation choices

### Repository pattern

Services depend on repository interfaces rather than on the map implementations. The interfaces expose domain operations such as `findByInstrumentId`, `findAll`, `findBestPriceByInstrumentId`, and `deleteBefore`.

This hides storage details from the application logic and allows a different implementation to be substituted. It also makes services straightforward to test with in-memory repositories or test doubles.

### Dependency injection

Spring constructs the controllers, services, and repository implementations and supplies their dependencies through constructors. This keeps components loosely coupled and avoids each class constructing its own dependencies.

The current repository implementations are registered as Spring repositories. A replacement implementation should be registered as the bean for the same repository interface, with only one active implementation per interface in a given application context.

### Layered architecture

Controllers handle HTTP-specific work, services express use-case logic, and repositories handle retrieval and storage. This prevents a controller from depending directly on the storage representation and keeps web response formatting out of repository code.

### Immutable domain records

`Instrument`, `Vendor`, and `Price` are Java records. Their data cannot be mutated after construction, which makes the model compact and reduces accidental changes to shared values held in repository maps. The records contain only domain data; request validation is defined on API DTOs.

### Scheduled background work

`ScheduledTasks` invokes stale-price cleanup on a configured cron schedule. The stale age is bound from application configuration and passed to `DeleteStalePricesService`, which delegates deletion to `PriceRepository`. Scheduling policy is therefore separated from the repository's deletion operation.

## Current in-memory persistence

The instrument and vendor implementations use concurrent maps keyed by record ID:

- `InMemoryInstrumentRepository`: `instrumentId -> Instrument`
- `InMemoryVendorRepository`: `vendorId -> Vendor`

Prices are held in a nested map:

- `instrumentId -> (vendorId -> Price)`

This organisation supports looking up prices for an instrument and replaces the previous price when the same vendor publishes another price for the same instrument. It therefore represents the latest stored price for each vendor/instrument pair, not a history of price observations.

`GET /marketpricehub/getAllPrices` uses `PriceRepository.findAll()` to retrieve the current quotes and joins each quote with its vendor and instrument records for the response. The controller orders the result by vendor ID and then instrument ID.

The maps are held only in the running process. Restarting the application clears the data, and separate instances do not share it. The price repository synchronizes its operations in addition to using concurrent maps; this protects its compound updates within one process, but does not provide coordination between application instances.

In the `local` profile, `DataSeeder` loads sample vendors, instruments, and prices from `/demo-data.csv` at application startup. This supplies demonstration data without making the sample loader part of the API request path.

`DemoActivityRunner` runs by default when started with the provided Windows launcher, which sets the JVM system property `-Ddemo.activity.enabled=true` for the `local` profile. After seeding, it starts four background workers that continuously perform randomized vendor and instrument price lookups and publish updated prices for seeded vendor/instrument pairs, pausing between operations. To disable the workload, remove that JVM property from the launcher command; the `local` profile will still load the sample data. The workers are interrupted when the application shuts down.

## Replacing the maps with a database

The service layer already targets `InstrumentRepository`, `VendorRepository`, and `PriceRepository`, so the HTTP controllers and use-case logic can generally remain unchanged when storage changes. A database implementation can be added behind those interfaces, for example using Spring Data repositories or JDBC.

The implementation change would need to:

1. Define a schema and map `Instrument`, `Vendor`, and `Price` to database rows.
2. Implement the existing repository operations using database queries, including best-price selection and stale-price deletion.
3. Register the database-backed implementations in Spring in place of the in-memory implementations.
4. Configure database connection details and migrations for each deployment environment.
5. Add integration tests for persistence, constraints, query behaviour, and transaction boundaries.

The current price map uses `(instrumentId, vendorId)` as its effective key, so a database implementation should preserve that uniqueness rule if the intended behaviour remains one current price per pair. Foreign keys from `Price` to `Instrument` and `Vendor` can enforce referential integrity in the database. Publishing a price currently checks both IDs and then saves the price; with a database, those operations should be performed with suitable transaction and constraint handling to avoid race conditions.

Repository interfaces provide an abstraction boundary, but changing storage is not automatically configuration-free: the new implementation must satisfy every interface operation and preserve the existing behaviour expected by services and tests.

## Related documentation

- [System design and sequence diagrams](SystemDesign.md)
- [Domain model](DomainModel.md)
