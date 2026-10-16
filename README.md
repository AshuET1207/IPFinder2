# IP Finder

A desktop IP address lookup application built with **Java**, **JavaFX**, and **Spring Boot**.

The application allows users to:

- Validate IPv4 and IPv6 addresses
- Retrieve geographical and network information for an IP address
- Display country, city, ISP, and related information
- Maintain a local lookup history
- Persist history in a JSON file
- Perform network and file operations in background threads
- Navigate between Lookup and History views

---

## Features

### IP Lookup

Enter an IPv4 or IPv6 address and retrieve information from the configured GeoIP service.

The application displays:

- IP address
- Country
- City
- ISP

The underlying API response also supports:

- Country code
- Region
- ZIP code
- Latitude
- Longitude
- Timezone
- Organization
- ASN

### IP Validation

The application validates:

- IPv4 addresses
- IPv6 addresses
- Empty or blank input

Invalid input is rejected before making an external API request.

### Lookup History

Every successful lookup is stored locally.

History entries contain:

- IP address
- Country
- City
- ISP
- Lookup timestamp

The history is limited by the configured maximum number of entries.

### JSON Persistence

Lookup history is persisted to a JSON file.

Default location:

```text
~/.ipfinder/history.json
```

The location and maximum number of entries can be configured through application properties.

### Background Processing

Network requests and file operations are executed outside the JavaFX Application Thread.

This keeps the user interface responsive while:

- Calling the GeoIP API
- Loading history
- Saving history

---

# Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application language |
| JavaFX | Desktop UI |
| Spring Boot | Dependency injection and application configuration |
| Java `HttpClient` | HTTP communication |
| Jackson | JSON serialization/deserialization |
| Maven | Build and dependency management |
| ip-api.com | GeoIP lookup provider |

---

# Architecture

The project follows a lightweight layered architecture.

```text
com.app.ipfinder
│
├── Launcher.java
├── IpFinderApplication.java
│
├── config/
│   ├── AppConfig.java
│   └── IpFinderProperties.java
│
├── ui/
│   ├── StageManager.java
│   └── controller/
│       ├── MainController.java
│       ├── LookupController.java
│       └── HistoryController.java
│
├── service/
│   ├── IpLookupService.java
│   ├── HistoryService.java
│   └── IpValidator.java
│
├── client/
│   ├── GeoIpClient.java
│   └── IpApiGeoIpClient.java
│
├── storage/
│   ├── HistoryStore.java
│   └── JsonFileHistoryStore.java
│
├── model/
│   ├── IpInfo.java
│   └── LookupEntry.java
│
└── exception/
    ├── InvalidIpException.java
    ├── LookupFailedException.java
    └── HistoryStoreException.java
```

---

# Architecture Overview

```mermaid
flowchart LR
    subgraph UI
        MC[MainController]
        LC[LookupController]
        HC[HistoryController]
        SM[StageManager]
    end

    subgraph Services
        LS[IpLookupService]
        HS[HistoryService]
        IV[IpValidator]
    end

    subgraph Client
        GI[GeoIpClient]
        API[IpApiGeoIpClient]
    end

    subgraph Storage
        ST[HistoryStore]
        JS[JsonFileHistoryStore]
    end

    subgraph Models
        II[IpInfo]
        LE[LookupEntry]
    end

    MC --> SM
    LC --> LS
    LC --> SM
    HC --> HS
    HC --> SM

    LS --> IV
    LS --> GI
    LS --> HS

    GI -. implements .-> API
    HS --> ST
    ST -. implements .-> JS

    API --> II
    LS --> LE
    HS --> LE
    JS --> LE
```

---

# Application Startup

The application starts JavaFX through `Launcher`, initializes the Spring ApplicationContext, creates the required services and components, and finally displays the main window.

```mermaid
flowchart TD
    A[Launcher.main] --> B[Application.launch]
    B --> C[IpFinderApplication.init]
    C --> D[Start Spring ApplicationContext]
    D --> E[Create Configuration Beans]
    E --> F[Create Services]
    E --> G[Create Clients]
    E --> H[Create Storage]
    C --> I[JavaFX Application Thread]
    I --> J[IpFinderApplication.start]
    J --> K[Get StageManager]
    K --> L[Set Primary Stage]
    L --> M[Load main.fxml]
    M --> N[Show Main Window]
```

---

# Main Window Navigation

The main window contains the application navigation and a content area.

The user can switch between:

- IP Lookup
- Lookup History

```mermaid
flowchart TD
    A[Main Window] --> B{User Action}

    B -->|Lookup| C[MainController.showLookup]
    C --> D[StageManager.showLookup]
    D --> E[Load lookup.fxml]
    E --> F[Replace contentPane]

    B -->|History| G[MainController.showHistory]
    G --> H[StageManager.showHistory]
    H --> I[Load history.fxml]
    I --> J[Replace contentPane]
```

---

# IP Lookup Flow

The lookup operation is performed asynchronously using a JavaFX `Task` and the application's background executor.

```mermaid
flowchart TD
    A[User enters IP address] --> B[Click Lookup]
    B --> C[LookupController.lookup]
    C --> D[Create JavaFX Task]
    D --> E[Background Executor]

    E --> F[IpLookupService.lookup]
    F --> G[IpValidator.validate]

    G --> H{Valid IP?}

    H -->|No| I[Throw InvalidIpException]
    I --> J[Display Error]

    H -->|Yes| K[GeoIpClient.lookup]
    K --> L[IpApiGeoIpClient]
    L --> M[Build HTTP Request]
    M --> N[Call ip-api.com]
    N --> O{HTTP Response}

    O -->|Failure| P[Throw LookupFailedException]
    P --> J

    O -->|Success| Q[Parse JSON]
    Q --> R[Create IpInfo]
    R --> S[Create LookupEntry]
    S --> T[HistoryService.add]
    T --> U[Save History]
    U --> V[Return LookupEntry]
    V --> W[JavaFX Success Handler]
    W --> X[Display Result]
```

---

# IP Validation

The application validates the input before contacting the external API.

```mermaid
flowchart TD
    A[IP Address] --> B{Null or Blank?}

    B -->|Yes| C[InvalidIpException]
    B -->|No| D[Trim Input]

    D --> E{Valid IPv4?}

    E -->|Yes| F[Valid]
    E -->|No| G{Contains ':'?}

    G -->|No| H[InvalidIpException]
    G -->|Yes| I[Validate IPv6]

    I --> J{Valid IPv6?}

    J -->|Yes| F
    J -->|No| H

    C --> K[Return Error]
    H --> K
```

---

# GeoIP API Communication

The application uses `GeoIpClient` as an abstraction for external IP lookup providers.

The current implementation is:

```text
IpApiGeoIpClient
```

The client uses Java's `HttpClient` to communicate with the configured GeoIP API.

```mermaid
sequenceDiagram
    actor User
    participant UI as LookupController
    participant Service as IpLookupService
    participant Client as IpApiGeoIpClient
    participant API as ip-api.com

    User->>UI: Enter IP
    User->>UI: Click Lookup
    UI->>Service: lookup(ip)
    Service->>Client: lookup(ip)

    Client->>Client: Build URI
    Client->>API: HTTP GET /json/{ip}
    API-->>Client: JSON Response

    alt Successful response
        Client->>Client: Parse JSON
        Client-->>Service: IpInfo
        Service-->>UI: LookupEntry
        UI->>User: Display result
    else API failure
        Client-->>Service: LookupFailedException
        Service-->>UI: Error
        UI->>User: Display error
    end
```

---

# Lookup and History

A successful lookup is converted from `IpInfo` into a `LookupEntry`.

The entry is then added to the application's history.

```mermaid
flowchart TD
    A[IP Lookup] --> B[Validate IP]
    B --> C[GeoIP API Request]
    C --> D[Receive IpInfo]
    D --> E[Create LookupEntry]

    E --> F[HistoryService.add]
    F --> G[Add entry to beginning of list]
    G --> H{Exceeds maxEntries?}

    H -->|Yes| I[Remove oldest entries]
    H -->|No| J[Keep entries]

    I --> K[HistoryStore.save]
    J --> K

    K --> L[JsonFileHistoryStore]
    L --> M[Create parent directory]
    M --> N[Serialize entries to JSON]
    N --> O[Write temporary file]
    O --> P[Replace history.json]
```

---

# History Loading

When `HistoryService` is created, it loads previously stored lookup entries.

```mermaid
flowchart TD
    A[Application Startup] --> B[Create HistoryService]
    B --> C[HistoryStore.load]
    C --> D[JsonFileHistoryStore.load]
    D --> E{history.json exists?}

    E -->|No| F[Return empty list]
    E -->|Yes| G[Read JSON file]

    G --> H{File empty?}
    H -->|Yes| F
    H -->|No| I[Deserialize JSON]

    I --> J{Valid JSON?}

    J -->|Yes| K[Return LookupEntry list]
    J -->|No| L[HistoryStoreException]

    F --> M[Initialize HistoryService]
    K --> M
```

---

# History Page

The History page displays stored lookup entries in a JavaFX `TableView`.

Users can:

- Refresh the history
- Clear the history

```mermaid
flowchart TD
    A[User selects History] --> B[MainController.showHistory]
    B --> C[StageManager.showHistory]
    C --> D[Load history.fxml]
    D --> E[HistoryController.initialize]
    E --> F[HistoryService.getAll]
    F --> G[Populate TableView]

    G --> H{User Action}

    H -->|Refresh| I[HistoryController.refresh]
    I --> J[HistoryService.getAll]
    J --> G

    H -->|Clear History| K[Show Confirmation]
    K --> L{Confirmed?}

    L -->|No| G
    L -->|Yes| M[HistoryService.clear]
    M --> N[HistoryStore.save empty list]
    N --> O[Clear TableView]
```

---

# Clear History

Clearing history requires user confirmation before the stored entries are removed.

```mermaid
sequenceDiagram
    actor User
    participant UI as HistoryController
    participant Service as HistoryService
    participant Store as HistoryStore
    participant File as history.json

    User->>UI: Click Clear History
    UI->>User: Show confirmation

    alt User cancels
        User-->>UI: Cancel
        UI-->>User: Keep history
    else User confirms
        User-->>UI: Confirm
        UI->>Service: clear()
        Service->>Service: entries.clear()
        Service->>Store: save(empty list)
        Store->>File: Write []
        File-->>Store: Saved
        Store-->>Service: Complete
        Service-->>UI: Complete
        UI->>UI: Clear TableView
    end
```

---

# Error Handling

The application uses custom runtime exceptions to distinguish common failures.

| Exception | Purpose |
|---|---|
| `InvalidIpException` | Invalid IPv4/IPv6 input |
| `LookupFailedException` | External API/network/lookup failure |
| `HistoryStoreException` | History file read/write/serialization failure |

```mermaid
flowchart TD
    A[User Lookup] --> B[Background Task]
    B --> C[IpLookupService]

    C --> D{Operation}

    D -->|Invalid IP| E[InvalidIpException]
    D -->|Network/API Error| F[LookupFailedException]
    D -->|Unexpected Error| G[Other Exception]
    D -->|Success| H[LookupEntry]

    E --> I[Task.setOnFailed]
    F --> I
    G --> I

    I --> J{Exception Type}

    J -->|InvalidIpException| K[Show validation message]
    J -->|LookupFailedException| L[Show lookup error]
    J -->|Other| M[Show generic error]

    H --> N[Task.setOnSucceeded]
    N --> O[Display Result]
```

---

# Threading Model

JavaFX requires UI updates to happen on the JavaFX Application Thread.

Network and file operations are therefore executed through the background executor.

```mermaid
flowchart TD
    A[JavaFX Application Thread] --> B[LookupController]
    B --> C[Create JavaFX Task]
    C --> D[ExecutorService]

    D --> E[Worker Thread]
    E --> F[IpLookupService]
    F --> G[IpValidator]
    F --> H[IpApiGeoIpClient]
    H --> I[HTTP Request]
    I --> J[External API]

    F --> K[HistoryService]
    K --> L[JsonFileHistoryStore]
    L --> M[File System]

    J --> N[Lookup Result]
    M --> N
    N --> O[Task Result]

    O --> P[JavaFX Application Thread]
    P --> Q[Update UI]
```

The background executor uses a fixed thread pool:

```text
ipfinder-worker-1
ipfinder-worker-2
ipfinder-worker-3
ipfinder-worker-4
```

---

# Complete System Flow

The complete application flow can be summarized as follows:

```mermaid
flowchart TD
    A[Application Start] --> B[Spring Context]
    B --> C[JavaFX Main Window]

    C --> D{User Action}

    D -->|Lookup IP| E[Lookup Controller]
    E --> F[Background Task]
    F --> G[IpLookupService]
    G --> H[IpValidator]

    H --> I{Valid?}

    I -->|No| J[Show Validation Error]
    I -->|Yes| K[GeoIpClient]

    K --> L[IpApiGeoIpClient]
    L --> M[ip-api.com]
    M --> N{Success?}

    N -->|No| O[Show Lookup Error]
    N -->|Yes| P[IpInfo]

    P --> Q[LookupEntry]
    Q --> R[HistoryService]
    R --> S[HistoryStore]
    S --> T[history.json]

    Q --> U[Display Result]

    D -->|History| V[History Controller]
    V --> W[HistoryService]
    W --> X[HistoryStore]
    X --> Y[Load history.json]
    Y --> Z[Display History]

    V --> AA[Clear History]
    AA --> AB[HistoryService.clear]
    AB --> AC[Save Empty History]
```

---

# Configuration

Application configuration is represented by `IpFinderProperties`.

Example configuration:

```yaml
ipfinder:
  api:
    base-url: http://ip-api.com/json/
    timeout: 5s

  history:
    file: ${user.home}/.ipfinder/history.json
    max-entries: 200
```

The configuration controls:

- GeoIP API base URL
- HTTP timeout
- History file location
- Maximum number of history entries

---

# Dependency Injection

Spring manages the application's major components.

The dependency flow is:

```text
MainController
    │
    └── StageManager

LookupController
    │
    └── IpLookupService
            ├── IpValidator
            ├── GeoIpClient
            │     └── IpApiGeoIpClient
            └── HistoryService
                    └── HistoryStore
                          └── JsonFileHistoryStore

HistoryController
    │
    └── HistoryService
```

Constructor injection is used throughout the application.

---

# Design Principles

The project follows several practical design principles.

## Separation of Concerns

Each layer has a focused responsibility.

```text
Controller
    ↓
Service
    ↓
Client / Storage
    ↓
External API / File System
```

Controllers do not directly access:

- HTTP clients
- JSON files
- Persistence implementations

---

## Interface-Based Design

External dependencies are abstracted through interfaces.

### GeoIP

```java
public interface GeoIpClient {
    IpInfo lookup(String ip);
}
```

Implementation:

```text
IpApiGeoIpClient
```

### History Storage

```java
public interface HistoryStore {
    List<LookupEntry> load();
    void save(List<LookupEntry> entries);
}
```

Implementation:

```text
JsonFileHistoryStore
```

This makes it possible to replace the API provider or storage mechanism without changing the service layer.

---

# Data Models

## IpInfo

Represents information returned by the GeoIP provider.

```text
IpInfo
├── status
├── message
├── query
├── country
├── countryCode
├── regionName
├── city
├── zip
├── lat
├── lon
├── timezone
├── isp
├── org
└── asInfo
```

## LookupEntry

Represents a history record.

```text
LookupEntry
├── ip
├── country
├── city
├── isp
└── timestamp
```

---

# Project Flow

At a high level:

```text
User
 │
 ▼
JavaFX UI
 │
 ▼
Controller
 │
 ▼
Service
 │
 ├──────────────► Validator
 │
 ├──────────────► GeoIP Client ─────► External API
 │
 └──────────────► History Service
                       │
                       ▼
                  History Store
                       │
                       ▼
                  JSON File
```

---

# Running the Application

## Requirements

Install:

- JDK 21
- Maven
- JavaFX-compatible runtime/dependencies

Verify Java:

```bash
java -version
```

Verify Maven:

```bash
mvn -version
```

## Build

```bash
mvn clean package
```

## Run

```bash
mvn spring-boot:run
```

If the project is packaged as an executable application:

```bash
java -jar target/ip-finder.jar
```

---

# Configuration File

Create or modify:

```text
src/main/resources/application.yml
```

Example:

```yaml
spring:
  application:
    name: ip-finder

ipfinder:
  api:
    base-url: http://ip-api.com/json/
    timeout: 5s

  history:
    file: ${user.home}/.ipfinder/history.json
    max-entries: 200
```

---

# History File

The history is stored locally as JSON.

Example:

```json
[
  {
    "ip": "8.8.8.8",
    "country": "United States",
    "city": "Mountain View",
    "isp": "Google LLC",
    "timestamp": "2026-10-16T10:30:00Z"
  }
]
```

---

# Error Scenarios

The application handles common failure cases:

### Invalid IP

```text
Please enter a valid IPv4 or IPv6 address.
```

### Network Failure

```text
Could not reach the lookup service.
```

### API Failure

```text
Lookup failed: <reason>
```

### Rate Limiting

If the external API returns HTTP `429`:

```text
Too many requests. Please wait a minute and try again.
```

### Corrupted History

If the local JSON file cannot be parsed:

```text
Could not read lookup history.
The history file may be corrupted.
```

---

# Future Improvements

Possible future enhancements include:

- Multiple GeoIP provider implementations
- Country flags
- Map visualization
- Detailed IP information view
- Search/filter history
- Export history to CSV
- Delete individual history records
- Dark mode
- Configurable API providers
- Unit and integration tests
- API response caching
- Automatic retry with backoff
- Offline/local IP database support

---

# License

This project is intended for educational and development purposes.

Add an appropriate license file if the project is distributed publicly.