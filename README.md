# Market Price Hub

Market Price Hub is a Spring Boot REST application for managing instruments, vendors, and their prices. It provides endpoints to add vendors and instruments, publish prices, retrieve prices by vendor or instrument, find the best price for an instrument, and list all current prices across vendors and instruments.

The project includes a Windows ZIP distribution containing the runnable Spring Boot application and a batch launcher. The application uses in-memory repositories; data is lost when the application stops.

## Requirements

- Windows
- Java 21 or later installed

The launcher uses `JAVA_HOME` if it is set. Otherwise, it runs `java` from the system `PATH`. Maven is not required to run the packaged ZIP.

Note: If the launcher reports that Java cannot be found, install a Java 21-or-later runtime and either set `JAVA_HOME` to its installation directory or add its `bin` directory to `PATH`.

## Run the Windows distribution

1. Download or clone this repository, including the ZIP distribution which is located in the following folder:-

   ```text
   dist\market-price-hub-1.0-windows.zip
   ```

2. Choose a permanent folder where the application can run, for example:

   ```text
   C:\Apps\MarketPriceHub
   ```

3. Copy the ZIP file into that folder. The packaged file is named `market-price-hub-1.0-windows.zip`.
4. Extract the ZIP in that folder using File Explorer's **Extract All...** option or another ZIP utility. Do not run the batch file from inside the ZIP preview.
5. Open the extracted `market-price-hub-1.0` folder. It contains:

   ```text
   market-price-hub-1.0.jar
   marketpricehub.bat
   ```

6. Double-click `marketpricehub.bat`, or run it from Command Prompt or PowerShell. Keep the console window open while using the application. To stop the service, press **Ctrl+C** in that window.
7. Open the Swagger UI in a browser:

   [http://localhost:8080/marketpricehub/swagger-ui/index.html#](http://localhost:8080/marketpricehub/swagger-ui/index.html#)

The Swagger UI lists the available REST endpoints and lets you submit requests to the running application.


### Local demo activity

The Windows launcher starts the application with the `local` profile, which loads the sample vendors, instruments, and prices. It also enables a background demo that continuously makes randomized price lookups and publishes updated prices for existing vendor/instrument pairs. The demo runs several worker threads and its activity is reported in the console logs.

To run the application with the `local` profile but without the background demo, remove `-Ddemo.activity.enabled=true` from both Java command lines in `marketpricehub.bat`, or start the JAR directly without that JVM parameter:

```powershell
java -jar market-price-hub-1.0.jar --spring.profiles.active=local
```

The sample data will still be loaded when the demo is disabled.


## Build the Windows ZIP from source

If you have a Java 21-or-later JDK and Maven available, run this from the repository root:

```powershell
mvn clean package
```

The Windows ZIP is produced at:

```text
target\market-price-hub-1.0-windows.zip
```

Copy and extract that ZIP as described above.

## Documentation

- [System design and sequence diagrams](docs/SystemDesign.md)
- [Domain model](docs/DomainModel.md)
- [Project implementation](docs/ProjectImplementation.md)
- [Development Considerations](docs/DevelopmentConsiderations.md)
- [Application limitations](docs/ApplicationLimitations.md)
