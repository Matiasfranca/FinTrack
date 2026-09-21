> **Development Status**
>
> The current version is `2.0.0-SNAPSHOT`.
>
> Although this is a development version, the application is fully functional and includes the graphical interface, terminal interface, SQLite persistence, automated tests, and packaging for supported platforms.
>
> Additional features, improvements, and refinements are planned before the final `2.0.0` release.

# FinTrack

FinTrack is a personal finance management application built with Java, JavaFX, SQLite, and Maven. It provides a graphical dashboard and a terminal interface for recording, organizing, and reviewing financial transactions.

## Overview

FinTrack is designed as a local, single-user desktop application. Financial data is stored in a local SQLite database, so no external database server or account system is required.

The application currently provides:

- Income, expense, investment, and redemption transactions
- Transaction dates and descriptions
- Bank account management
- Transaction categories with optional colors
- Multiple payment methods
- Monthly transaction organization
- Monthly financial summaries
- Category-based financial distribution
- Daily financial overview
- JavaFX graphical dashboard
- Terminal interface
- SQLite persistence
- Automatic database initialization
- Database maintenance services
- Unit and repository tests with JUnit 5

## Tech Stack

- **Language:** Java 25
- **Build Tool:** Maven
- **GUI:** JavaFX 25
- **Database:** SQLite
- **JDBC Driver:** Xerial SQLite JDBC
- **Testing:** JUnit Jupiter 5
- **Main Class:** `app.Main`

## Architecture

The project separates presentation, application logic, and persistence into distinct layers:

```text
UI
├── JavaFX
└── Console
        │
        ▼
Controller
└── FinTracker
        │
        ├── Services
        └── Repositories
                │
                ▼
             SQLite
```

### Main responsibilities

- **`ui`** — presentation and user interaction
- **`controller`** — application and business coordination
- **`service`** — application-level services and database maintenance
- **`repository`** — persistence abstractions
- **`repository.sqlite`** — SQLite repository implementations
- **`database`** — database connection, path management, and schema initialization
- **`model`** — domain entities, enums, and DTOs
- **`utils`** — reusable utility components and validation rules
- **`exceptions`** — application-specific exceptions

This separation allows the core application and persistence logic to be reused by different interfaces.

## Project Structure

```text
FinTrack/
├── pom.xml
├── LICENSE
├── README.md
├── resources-linux/
│   ├── postinst
│   └── prerm
├── .github/
│   └── workflows/
│       └── build.yml
└── src/
    ├── main/
    │   └── java/
    │       ├── app/
    │       │   ├── Main.java
    │       │   ├── Main.fxml
    │       │   ├── Main.css
    │       │   └── assets/
    │       ├── controller/
    │       │   └── FinTracker.java
    │       ├── database/
    │       │   ├── DatabaseConnection.java
    │       │   ├── DatabaseInitializer.java
    │       │   ├── DatabasePath.java
    │       │   └── schemas/
    │       │       ├── schema.sql
    │       │       └── reset.sql
    │       ├── exceptions/
    │       │   ├── DataAccessException.java
    │       │   └── InvalidInput.java
    │       ├── model/
    │       │   ├── domain entities and enums
    │       │   └── dto/
    │       ├── repository/
    │       │   ├── repository interfaces
    │       │   └── sqlite/
    │       ├── service/
    │       │   └── DatabaseMaintenanceService.java
    │       ├── ui/
    │       │   ├── console/
    │       │   └── javafx/
    │       └── utils/
    └── test/
        └── java/
            ├── controller/
            ├── repository/
            ├── service/
            └── ui/
```

The project keeps JavaFX stylesheets, FXML files, SQL schemas, and UI assets under `src/main/java`. Maven is configured to include these resource types in the application build.

## Core Domain

### Transactions

A transaction contains:

- value
- date
- transaction type
- payment method
- description
- bank account
- optional category

Supported transaction types:

```text
INCOME
EXPENSE
INVESTMENT
REDEMPTION
```

Supported payment methods:

```text
PIX
DEBIT_CARD
CREDIT_CARD
CASH
BANK_TRANSFER
BOLETO
```

### Bank Accounts

Supported account types:

```text
CHECKING
SAVINGS
CASH
OTHER
```

Accounts can be active or inactive.

### Categories

Categories have a unique name and may contain a color used by the graphical interface.

A transaction may also have no category. The interfaces represent this as the default "Outros" option, while the database stores the category as `NULL`.

## Database

FinTrack uses SQLite for local persistence and automatically creates the required data directory and database file.

The database schema contains the following main tables:

- `MONTH`
- `BANK_ACCOUNT`
- `CATEGORY`
- `TRANSACTION`

Foreign-key constraints are enabled for SQLite connections.

### Database locations

The application stores `fintrack.db` in a platform-specific user data directory:

| Platform | Location |
| --- | --- |
| Windows | `%LOCALAPPDATA%/FinTrack/fintrack.db` |
| Linux | `~/.local/share/FinTrack/fintrack.db` |
| Other systems | `~/FinTrack/fintrack.db` |

The test environment can use `reset.sql` through the database test mode to isolate test data from the user's normal database.

## Graphical Interface

The JavaFX interface is dashboard-oriented and includes components for:

- financial summary cards
- income, expense, investment, and balance information
- monthly overview charts
- category distribution charts
- transaction listing
- transaction forms
- bank account and category creation
- application header and navigation

JavaFX stylesheets are kept alongside the UI components and are included as Maven resources during the build.

## Terminal Interface

The application also provides a terminal interface through `ConsoleUI`.

The terminal interface supports operations such as:

1. Add a transaction
2. List transactions
3. Remove a transaction
4. View financial summaries
5. Exit

It also provides interaction with bank accounts, categories, transaction types, and payment methods.

To explicitly start terminal mode:

```bash
mvn exec:java -Dexec.args="--console"
```

When running a packaged application, the Windows build also creates a dedicated `FinTrack-CLI` launcher. On supported Linux desktop environments, the graphical application can open the terminal interface through the configured system terminal.

## Running the Application

### Requirements

Install:

- JDK 25
- Maven

Verify the installed versions:

```bash
java -version
mvn -version
```

### Run the JavaFX application

```bash
mvn javafx:run
```

### Compile the project

```bash
mvn compile
```

### Run tests

```bash
mvn test
```

### Build the application

```bash
mvn clean package
```

The Maven build creates the application JAR and copies runtime dependencies into `target/app`. This output is used by the packaging workflow.

## Packaging

Packaging is automated through GitHub Actions whenever changes are pushed to `main`, and it can also be triggered manually.

The workflow builds packages for:

- **Debian/Ubuntu:** `.deb`
- **Fedora-compatible Linux distributions:** `.rpm`
- **Windows:** `.exe`

The packaging workflow uses JDK 25 and `jpackage`. Linux packages use the PNG application icon, while the Windows package uses the ICO icon.

For Linux packaging, the workflow also installs the required `fakeroot`/RPM tooling before invoking `jpackage`.

## Database Maintenance

FinTrack includes `DatabaseMaintenanceService`, which performs scheduled database cleanup and maintenance logic when the graphical application starts.

The current startup flow initializes the database first and then runs the monthly maintenance check before displaying the main interface.

## Testing

The project contains automated tests for multiple application layers, including:

- controller behavior
- transaction repository
- bank account repository
- category repository
- month repository
- dashboard repository
- database maintenance
- console input

Run the complete test suite with:

```bash
mvn test
```

## Dependencies

The main Maven dependencies are:

- `org.openjfx:javafx-controls:25`
- `org.openjfx:javafx-fxml:25`
- `org.xerial:sqlite-jdbc:3.50.3.0`
- `org.junit.jupiter:junit-jupiter:5.13.4`

## Versioning

The project follows semantic-style versioning.

Development versions use the `-SNAPSHOT` suffix. A `SNAPSHOT` version represents the current development state of the application and may still receive new features, fixes, and refinements.

The current development version is:

```text
2.0.0-SNAPSHOT
```

Although the current `2.0.0-SNAPSHOT` version is fully functional, additional features and refinements are planned before the final `2.0.0` release.

A completed release removes the `-SNAPSHOT` suffix:

```text
2.0.0
```

## License

**Copyright (c) 2026 Matias Saraiva de França. All Rights Reserved.**

FinTrack is **not an Open Source project**. The source code is made publicly available **strictly for academic study, portfolio evaluation, and technical review**.

No license is granted to copy, distribute, modify, reuse, or commercialize this code, in whole or in part, for other projects.

For detailed terms and conditions, including permitted local execution for testing, see the [`LICENSE`](LICENSE) file.

To request authorization for any other use, contact:

- **Email:** contato.matias7@gmail.com
- **LinkedIn:** https://linkedin.com/in/matias-saraiva-943110236
