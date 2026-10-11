# FinTrack

FinTrack is a local, single-user personal finance desktop application built with Java, JavaFX, SQLite, and Maven. It provides a graphical dashboard and a terminal interface for recording and reviewing financial transactions.

**Current project version:** `2.0.0`

Download the latest published installers from the [GitHub Releases page](https://github.com/Matiasfranca/FinTrack/releases/latest).

## Features

- Record income, expenses, investments, and redemptions.
- Associate transactions with dates, payment methods, bank accounts, and optional categories.
- Manage bank accounts and categories.
- View financial summaries and daily/monthly information.
- Visualize financial information through dashboard cards and charts.
- Use either the JavaFX graphical interface or the terminal interface.
- Store data locally in SQLite; no external database server or user account is required.
- Initialize the local database at startup before selecting graphical or terminal mode.

## Technologies

- **Language:** Java 25
- **Build tool:** Apache Maven
- **Graphical interface:** JavaFX 25
- **Database:** SQLite
- **JDBC driver:** Xerial SQLite JDBC `3.50.3.0`
- **Testing:** JUnit Jupiter `5.13.4`
- **Application entry point:** `app.Main`
- **Packaging:** `jpackage` through GitHub Actions

## Architecture

The application is divided into presentation, coordination, service, and persistence layers:

```text
JavaFX UI ─┐
           ├──> FinTracker controller ──> Services
Console UI ┘                              │
                                          ▼
                                     Repositories
                                          │
                                          ▼
                                        SQLite
```

Main package responsibilities:

- `app` — JavaFX application entry point and FXML/CSS resources.
- `controller` — application coordination through `FinTracker`.
- `database` — database connection, data-file path, initialization, and SQL schemas.
- `exceptions` — application-specific exceptions.
- `model` — domain entities and enums.
- `model.dto` — data-transfer objects used by the application and dashboard.
- `repository` — persistence interfaces.
- `repository.sqlite` — SQLite implementations of the repositories.
- `service` — application services, including database maintenance.
- `ui.console` — terminal interface and input/output helpers.
- `ui.javafx` — JavaFX interface and its components, forms, charts, and event buses.
- `utils` — formatting, enum labels, and validation helpers.

## Project structure

```text
FinTrack/
├── .github/workflows/build.yml
├── resources-linux/
│   ├── postinst
│   └── prerm
├── src/
│   ├── main/java/
│   │   ├── app/          # Main.java, FXML, CSS, assets
│   │   ├── controller/   # FinTracker
│   │   ├── database/     # Connection, initialization, paths, SQL schemas
│   │   ├── exceptions/
│   │   ├── model/        # Entities, enums, DTOs
│   │   ├── repository/   # Repository interfaces and SQLite implementations
│   │   ├── service/
│   │   ├── ui/
│   │   │   ├── console/
│   │   │   └── javafx/
│   │   └── utils/        # Formatting, labels, validation
│   └── test/java/        # Controller, repository, service, console tests
├── .gitignore
├── LICENSE
├── pom.xml
└── README.md
```

JavaFX FXML, CSS, image, and SQL resources are stored under `src/main/java`. The Maven build explicitly includes these resource types so they are copied into the build output.

## Domain model

### Transaction types

```text
INCOME
EXPENSE
INVESTMENT
REDEMPTION
```

Transactions store a value, date, type, payment method, description, bank account, and optional category.

### Payment methods

```text
PIX
DEBIT_CARD
CREDIT_CARD
CASH
BANK_TRANSFER
BOLETO
```

### Bank account types

```text
CHECKING
SAVINGS
CASH
OTHER
```

Bank accounts can be active or inactive. Categories may have a color used by the graphical interface. The interfaces represent an uncategorized transaction with the option **Outros**; in the database, its category is stored as `NULL`.

## Database and data location

FinTrack uses SQLite. The `app.Main` entry point initializes the database before selecting graphical or terminal mode, so both modes share the same startup initialization when launched through that entry point. Foreign-key constraints are enabled on database connections.

The database file is named `fintrack.db` and is stored in a user data directory:

| Platform | Path |
| --- | --- |
| Windows | `%LOCALAPPDATA%/FinTrack/fintrack.db` |
| Linux | `~/.local/share/FinTrack/fintrack.db` |
| Other systems | `~/FinTrack/fintrack.db` |

The `database/schemas/reset.sql` script is intended for database reset/test workflows. Do not run reset scripts against a database containing data you want to keep.

## Graphical interface

The JavaFX dashboard includes financial summary cards, monthly and daily financial views, category distribution charts, transaction listing, transaction forms, and controls for managing bank accounts and categories. FXML and CSS resources are loaded from the application resources included in the Maven build.

## Terminal interface

The application includes a terminal interface implemented in `ui.console.ConsoleUI`, with input and formatting helpers in the same package. It provides transaction operations and financial summaries.

To launch terminal mode during development:

```bash
mvn exec:java -Dexec.args="--console"
```

The Windows installer workflow also configures a `FinTrack-CLI` launcher. The graphical application can additionally offer terminal access through the configured system terminal, depending on the environment.

## Requirements and development commands

Install **JDK 25** and **Maven**. Verify the versions:

```bash
java -version
mvn -version
```

Run the graphical application:

```bash
mvn javafx:run
```

Compile the source code:

```bash
mvn compile
```

Run the automated tests:

```bash
mvn test
```

Create the packaged Maven build:

```bash
mvn clean package
```

The package phase creates the application JAR in `target/` and copies the runtime dependencies to `target/app/lib`. The main JAR is copied to `target/app`; these files are used as inputs by the `jpackage` workflow.

## Automated installer builds

The workflow in `.github/workflows/build.yml` uses GitHub Actions and JDK 25 to build installers with `jpackage` for:

- **Debian/Ubuntu:** `.deb`
- **Fedora-compatible distributions:** `.rpm`
- **Windows:** `.exe`

The workflow runs on pushes to `main`, on tags matching `v*`, and when manually triggered. Build artifacts are uploaded to the workflow run. When a version tag is pushed, the workflow also creates a GitHub Release and attaches the generated installers.

The current workflow expects the Maven artifact `fintrack-2.0.0.jar`, uses `app.Main` as the entry point, and sets the installer version to `2.0.0`. Keep the Maven project version, the `--main-jar` value, and `--app-version` values in the workflow synchronized when preparing a release.

Linux packaging uses the PNG icon in `src/main/java/app/assets/fintrack_ico.png` and resources from `resources-linux/`. Windows packaging uses `src/main/java/app/assets/fintrack_ico.ico`.

## Tests

The test source tree contains tests for:

- `FinTracker` controller behavior
- SQLite transaction, bank account, category, month, and dashboard repositories
- database maintenance
- console input handling

Run all tests with:

```bash
mvn test
```

## Versioning and releases

The current project version in `pom.xml` is `2.0.0` (stable version; no `-SNAPSHOT` suffix).

Development builds may use a version ending in `-SNAPSHOT`. Before producing a stable release, make sure the version in `pom.xml`, the JAR filename referenced by `.github/workflows/build.yml`, and the installer version passed to `jpackage` agree.

To publish a release, push a version tag such as `v2.0.0`. The workflow builds the platform installers and attaches them to the corresponding GitHub Release.

## Download

Download available installers from the [latest FinTrack release](https://github.com/Matiasfranca/FinTrack/releases/latest):

- Windows (`.exe`)
- Debian/Ubuntu (`.deb`)
- Fedora-compatible Linux (`.rpm`)

Installers are only available after a successful packaging workflow and release publication.

## License

**Copyright (c) 2026 Matias Saraiva de França. All Rights Reserved.**

FinTrack is **not an Open Source project**. The source code is made publicly available **strictly for academic study, portfolio evaluation, and technical review**.

No license is granted to copy, distribute, modify, reuse, or commercialize this code, in whole or in part, for other projects.

For detailed terms and conditions, including permitted local execution for testing, see the [`LICENSE`](LICENSE) file.

To request authorization for any other use, contact:

- **Email:** contato.matias7@gmail.com
- **LinkedIn:** https://linkedin.com/in/matias-saraiva-943110236
