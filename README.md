# FinTrack

FinTrack is a personal finance management application built with Java, JavaFX, SQLite, and Maven. It provides a graphical dashboard and a terminal interface for recording and reviewing financial transactions.

## Overview

FinTrack is designed as a local, single-user desktop application. Financial data is stored in a local SQLite database, so no external database server or account system is required.

The application currently supports:

- Income, expense, and investment transactions
- Transaction dates and descriptions
- Bank account management
- Transaction categories
- Multiple payment methods
- Monthly transaction organization
- Monthly financial summaries
- Category-based expense/income distribution
- Daily financial overview
- Graphical dashboard built with JavaFX
- Terminal interface
- SQLite persistence
- Automated database initialization and maintenance
- Unit and repository tests with JUnit 5

## Tech Stack

- **Language:** Java 25
- **Build Tool:** Maven
- **GUI:** JavaFX 25
- **Database:** SQLite
- **JDBC Driver:** Xerial SQLite JDBC
- **Testing:** JUnit Jupiter 5
- **CSS Development:** CSSFX
- **Main Class:** `app.Main`

## Architecture

The project separates the application into distinct layers:

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
- **`controller`** — application/business coordination
- **`service`** — application-level services and maintenance operations
- **`repository`** — persistence abstractions
- **`repository.sqlite`** — SQLite repository implementations
- **`database`** — database connection and schema initialization
- **`model`** — domain entities, enums, and DTOs
- **`utils`** — reusable utility components
- **`exceptions`** — application-specific exceptions

This separation allows the business and persistence logic to be reused by different user interfaces.

## Project Structure

```text
FinTrack/
├── pom.xml
├── LICENSE
├── README.md
├── resources-linux/
│   ├── postinst
│   └── prerm
└── src/
    ├── main/
    │   └── java/
    │       ├── app/
    │       │   ├── Main.java
    │       ├── controller/
    │       │   └── FinTracker.java
    │       ├── database/
    │       │   ├── DatabaseConnection.java
    │       │   ├── DatabaseInitializer.java
    │       │   └── schemas/
    │       │       └── schema.sql
    │       ├── exceptions/
    │       │   ├── DataAccessException.java
    │       │   └── InvalidInput.java
    │       ├── model/
    │       │   ├── BankAccount.java
    │       │   ├── BankAccountType.java
    │       │   ├── Category.java
    │       │   ├── Month.java
    │       │   ├── PaymentMethod.java
    │       │   ├── Transaction.java
    │       │   ├── TransactionType.java
    │       │   └── dto/
    │       ├── repository/
    │       │   ├── BankAccountRepository.java
    │       │   ├── CategoryRepository.java
    │       │   ├── DashboardRepository.java
    │       │   ├── MonthRepository.java
    │       │   ├── TransactionRepository.java
    │       │   └── sqlite/
    │       ├── service/
    │       │   └── DatabaseMaintenanceService.java
    │       ├── ui/
    │       │   ├── console/
    │       │   └── javafx/
    │       └── utils/
    │           └── ValidationRule.java
    └── test/
        └── java/
            ├── controller/
            ├── repository/
            ├── service/
            └── ui/
```

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

A transaction may also have no category.

## Database

FinTrack uses SQLite for local persistence.

The database schema contains the following main tables:

- `MONTH`
- `BANK_ACCOUNT`
- `CATEGORY`
- `TRANSACTION`

Foreign-key constraints are enabled when a database connection is created.

The application initializes the database schema automatically when the graphical application starts.

The test environment can use a separate SQLite database through the database test mode.

## Graphical Interface

The JavaFX interface contains a dashboard-oriented layout with components for:

- financial summary cards
- income and expense information
- monthly overview charts
- category distribution charts
- transaction listing
- application header and navigation
- transaction data presentation

JavaFX stylesheets are kept alongside the UI components and are included as Maven resources during the build.

## Terminal Interface

The application also provides a terminal interface through `ConsoleUI`.

The terminal interface supports operations such as:

1. Add a transaction
2. List transactions
3. Remove a transaction
4. View the dashboard
5. Exit

It also provides interaction with bank accounts, categories, transaction types, and payment methods.

To explicitly start terminal mode:

```bash
mvn exec:java -Dexec.args="--console"
```

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

The Maven build creates the application JAR and copies runtime dependencies into the `target/app` directory.

## Database Maintenance

FinTrack includes `DatabaseMaintenanceService`, which performs database maintenance when the application starts.

The graphical application invokes the maintenance process before displaying the main interface.

## Testing

The project contains tests for multiple layers of the application, including:

- controller behavior
- transaction repository
- bank account repository
- category repository
- month repository
- dashboard repository
- database maintenance
- console input

The test suite can be executed with:

```bash
mvn test
```

## Dependencies

The main Maven dependencies are:

- `org.openjfx:javafx-controls:25`
- `org.openjfx:javafx-fxml:25`
- `org.xerial:sqlite-jdbc:3.50.3.0`
- `fr.brouillard.oss:cssfx:11.4.0`
- `org.junit.jupiter:junit-jupiter:5.13.4`

## Versioning

The project follows semantic-style versioning.

Development versions use the `-SNAPSHOT` suffix:

```text
2.0.0-SNAPSHOT
```

A completed release uses the corresponding stable version:

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
