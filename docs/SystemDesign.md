# Market Price Hub Design

## Overview

Market Price Hub provides REST endpoints for managing instruments, vendors, and market prices. The application uses a controller/service/repository design and stores data in in-memory repositories.

Requests pass through validation and Spring MVC controllers before being handled by application services. Services coordinate domain repositories, and repository implementations use concurrent in-memory maps.

The application requires an active Spring profile at startup. The `local` profile additionally loads demo data from `demo-data.csv`.

The sequence diagrams below are included as SVG images for viewers without Mermaid support. Expand each Mermaid source section to inspect or edit the source.

## Design choices and rationale

### Layered REST design

The application separates HTTP handling (controllers), business rules (services), and data access (repository interfaces and implementations). This keeps request parsing and response formatting out of the core operations, while services can be tested independently of Spring MVC. Repository interfaces also allow a different storage implementation to be introduced without changing controller or service responsibilities.

Request and response DTOs define the API boundary rather than exposing persistence details directly. Bean Validation rejects malformed request bodies early, and controllers validate query identifiers before invoking services. This gives clients clear HTTP errors and keeps basic input constraints close to the API contract.

### In-memory storage for the current scope

The repositories use concurrent in-memory maps, avoiding database setup and making the application straightforward to run as a self-contained service or demonstration. The repository interfaces keep that implementation replaceable.

This is a simplicity and portability trade-off, not durable storage: data is lost when the process stops, and separate application instances do not share state. A persistent repository would be needed for production data retention or horizontal scaling.

### One current price per vendor and instrument

Prices are indexed by instrument ID and then vendor ID. Saving another price for the same pair replaces the existing value, so the repository represents each vendor's current quote for each instrument rather than a historical series. This keeps price retrieval and best-price lookup simple for the current API.

The best-price operation returns the lowest numeric price among the stored quotes. The model does not include currency or unit conversion, so callers must only compare prices that are already comparable. Historical quote retention or cross-currency comparison would require additional domain data and behaviour.

### Deterministic read responses

The price-list endpoints sort their results by instrument or vendor ID before returning them. The all-prices endpoint sorts by vendor ID and then instrument ID. This avoids relying on map iteration order, producing stable responses that are easier for API clients to consume and for tests to verify.

### Configurable stale-price cleanup

Staleness is based on each quote's `priceDate`; the scheduled task computes a cutoff from the configured stale age, and the repository removes quotes strictly older than that cutoff. Keeping the cron schedule and age in application properties lets deployments adjust cleanup behaviour without changing business logic.

### Profile-specific demo data

Demo data is loaded from a CSV only under the `local` profile, keeping sample records out of other profiles. The concurrent demo workload is also restricted to `local` and runs by default when using the provided Windows launcher, which sets the `-Ddemo.activity.enabled=true` JVM system property. Removing that property disables the workload while leaving data seeding enabled. Requiring an explicit active profile also prevents the application from starting with an accidental or ambiguous configuration.

## Publish a price

`POST /marketpricehub/publishPrices`

![Sequence diagram: publish a price](images/publish-price.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    actor Client
    participant Controller as PublishPricesController
    participant Service as PublishPricesService
    participant Instruments as InstrumentRepository
    participant Vendors as VendorRepository
    participant Prices as PriceRepository

    Client->>Controller: POST /publishPrices
    Controller->>Controller: Validate request body
    Controller->>Service: publishPrice(vendorId, instrumentId, price, priceDate)
    Service->>Instruments: existsById(instrumentId)
    Instruments-->>Service: exists
    Service->>Vendors: existsById(vendorId)
    Vendors-->>Service: exists
    Service->>Prices: save(Price)
    Prices-->>Service: saved
    Service-->>Controller: success
    Controller-->>Client: 200 OK

    alt Instrument or vendor not found
        Instruments-->>Service: false
        Service-->>Controller: ResourceNotFoundException
        Controller-->>Client: 404 Not Found
    end
```

</details>

## Add an instrument

`POST /marketpricehub/addInstrument`

![Sequence diagram: add an instrument](images/add-instrument.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    actor Client
    participant Controller as AddInstrumentController
    participant Service as AddInstrumentService
    participant Instruments as InstrumentRepository

    Client->>Controller: POST /addInstrument
    Controller->>Controller: Validate instrument request
    Controller->>Service: addInstrument(Instrument)
    Service->>Instruments: save(Instrument)
    Instruments-->>Service: saved
    Service-->>Controller: Instrument
    Controller-->>Client: 200 OK
```

</details>

## Add a vendor

`POST /marketpricehub/addVendor`

![Sequence diagram: add a vendor](images/add-vendor.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    actor Client
    participant Controller as AddVendorController
    participant Service as AddVendorService
    participant Vendors as VendorRepository

    Client->>Controller: POST /addVendor
    Controller->>Controller: Validate vendor request
    Controller->>Service: addVendor(Vendor)
    Service->>Vendors: save(Vendor)
    Vendors-->>Service: saved
    Service-->>Controller: Vendor
    Controller-->>Client: 200 OK
```

</details>

## Retrieve prices for an instrument

`GET /marketpricehub/getPricesForAllVendorsOfGivenInstrument`

![Sequence diagram: retrieve prices for an instrument](images/instrument-prices.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    actor Client
    participant Controller as GetPricesForAllVendorsOfGivenInstrumentController
    participant Service as GetPricesForAllVendorsOfGivenInstrumentService
    participant Prices as PriceRepository
    participant Vendors as VendorRepository

    Client->>Controller: GET /getPricesForAllVendorsOfGivenInstrument
    Controller->>Controller: Validate instrumentId

    alt instrumentId is invalid
        Controller-->>Client: 400 Bad Request
    else instrumentId is valid
        Controller->>Service: getPrices(instrumentId)
        Service->>Prices: findByInstrumentId(instrumentId)
        Prices-->>Service: prices
        loop Each price
            Service->>Vendors: findById(vendorId)
            Vendors-->>Service: vendor
        end
        Service-->>Controller: Map<Vendor, Price>
        Controller->>Controller: Sort by vendor ID
        Controller-->>Client: 200 OK with prices
    end
```

</details>

## Retrieve the best price for an instrument

`GET /marketpricehub/getBestPriceForAllVendorsOfGivenInstrument`

![Sequence diagram: retrieve the best price](images/best-price.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    actor Client
    participant Controller as GetPricesForAllVendorsOfGivenInstrumentController
    participant Service as GetPricesForAllVendorsOfGivenInstrumentService
    participant Prices as PriceRepository

    Client->>Controller: GET /getBestPriceForAllVendorsOfGivenInstrument
    Controller->>Controller: Validate instrumentId

    alt instrumentId is invalid
        Controller-->>Client: 400 Bad Request
    else instrumentId is valid
        Controller->>Service: getBestPrice(instrumentId)
        Service->>Prices: findBestPriceByInstrumentId(instrumentId)
        Prices-->>Service: lowest Price or empty

        alt Price found
            Service-->>Controller: Optional<Price>
            Controller-->>Client: 200 OK with Price
        else No price found
            Controller-->>Client: 404 Not Found
        end
    end
```

</details>

## Retrieve prices for a vendor

`GET /marketpricehub/getPricesForAllInstrumentsOfGivenVendor`

![Sequence diagram: retrieve prices for a vendor](images/vendor-prices.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    actor Client
    participant Controller as GetPricesForAllInstrumentsOfGivenVendorController
    participant Service as GetPricesForAllInstrumentsOfGivenVendorService
    participant Prices as PriceRepository
    participant Instruments as InstrumentRepository

    Client->>Controller: GET /getPricesForAllInstrumentsOfGivenVendor
    Controller->>Controller: Validate vendorId

    alt vendorId is invalid
        Controller-->>Client: 400 Bad Request
    else vendorId is valid
        Controller->>Service: getPrices(vendorId)
        Service->>Prices: findByVendorId(vendorId)
        Prices-->>Service: prices
        loop Each price
            Service->>Instruments: findById(instrumentId)
            Instruments-->>Service: instrument
        end
        Service-->>Controller: Map<Instrument, Price>
        Controller->>Controller: Sort by instrument ID
        Controller-->>Client: 200 OK with prices
    end
```

</details>

## Retrieve all prices

`GET /marketpricehub/getAllPrices`

Returns every currently stored price, including its vendor and instrument IDs and descriptions, price, and price date. Results are ordered by vendor ID and then instrument ID. This endpoint is useful for inspecting the complete current in-memory price set, for example during testing.

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    actor Client
    participant Controller as GetAllPricesController
    participant Service as GetAllPricesService
    participant Prices as PriceRepository
    participant Vendors as VendorRepository
    participant Instruments as InstrumentRepository

    Client->>Controller: GET /getAllPrices
    Controller->>Service: getAllPrices()
    Service->>Prices: findAll()
    Prices-->>Service: prices
    loop Each price
        Service->>Vendors: findById(vendorId)
        Vendors-->>Service: vendor
        Service->>Instruments: findById(instrumentId)
        Instruments-->>Service: instrument
    end
    Service-->>Controller: all prices with vendor and instrument details
    Controller->>Controller: Sort by vendor ID, then instrument ID
    Controller-->>Client: 200 OK with prices
```

</details>

## Scheduled stale-price deletion

Stale prices are removed by a scheduled task. The schedule and stale period are configured in `application.properties`.

![Sequence diagram: scheduled stale-price deletion](images/stale-price-deletion.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    participant S as Scheduler
    participant T as ScheduledTasks
    participant C as StalePriceProperties
    participant D as DeleteStalePricesService
    participant P as PriceRepository

    S->>T: Cron trigger
    T->>C: Read stale age
    C-->>T: Configured days
    T->>T: Calculate cutoff date
    T->>D: deleteStalePrices(cutoff)
    D->>P: deleteBefore(cutoff)
    P-->>D: Number deleted
    D-->>T: Number deleted
    T-->>S: Deletion complete
```

</details>

## Local profile startup and demo activity

The demo data is loaded only when the application runs with the `local` profile. The concurrent demo activity starts after loading when the `demo.activity.enabled` JVM system property is `true`; the provided Windows launcher enables it.

![Sequence diagram: local profile startup and demo-data loading](images/local-data-seeding.svg)

<details>
<summary>Mermaid source</summary>

```mermaid
sequenceDiagram
    participant Spring as Spring Boot
    participant Seeder as DataSeeder
    participant Vendors as VendorRepository
    participant Instruments as InstrumentRepository
    participant Prices as PriceRepository
    participant Demo as DemoActivityRunner

    Spring->>Spring: Require active profile

    alt local profile active
        Spring->>Seeder: run(ApplicationArguments)
        Seeder->>Seeder: Read /demo-data.csv
        loop Each CSV row
            alt VENDOR
                Seeder->>Vendors: save(Vendor)
            else INSTRUMENT
                Seeder->>Instruments: save(Instrument)
            else PRICE
                Seeder->>Prices: save(Price)
            end
        end
        Seeder-->>Spring: demo data loaded
        alt demo.activity.enabled=true
            Spring->>Demo: run(ApplicationArguments)
            Demo->>Demo: Start concurrent demo workers
        else demo activity disabled
            Spring->>Spring: Continue without demo workers
        end
    else No active profile
        Spring-->>Spring: Reject startup
    end
```

</details>
