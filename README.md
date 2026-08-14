# FinTrack

## Overview

FinTrack is a Java console application (with an in-development JavaFX graphical interface) for managing monthly financial transactions. The system allows users to:

* add transactions;
* list registered transactions;
* remove transactions;
* calculate the total monthly balance.

The project uses in-memory storage, so all data is lost when the application is closed.

The codebase follows a layered structure: business logic (`controller`, `model`) is fully decoupled from the presentation layer (`ui`), so the same core logic can be reused by different interfaces (console today, JavaFX planned).

---

## Tech Stack

* **Language:** Java 21
* **Build Tool:** Maven
* **GUI (in development):** JavaFX 21 (`javafx-controls`, `javafx-fxml`)
* **Main class:** `app.Main`

---

## Project Structure

```text
FinTrack/
├── pom.xml
├── src/
│   └── main/
│       └── java/
│           ├── app/
│           │   └── Main.java
│           ├── controller/
│           │   └── FinTracker.java
│           ├── exceptions/
│           │   └── InvalidInput.java
│           ├── model/
│           │   ├── MonthlyTransaction.java
│           │   └── Transaction.java
│           ├── ui/
│           │   ├── console/
│           │   │   ├── ConsoleFormatter.java
│           │   │   ├── ConsoleInput.java
│           │   │   └── ConsoleUI.java
│           │   └── javafx/
│           │       └── FinTrackUI.java
│           └── utils/
│               └── InputValidator.java
```

---

## Project Files

### 1. app/Main.java

Application entry point. Its only responsibility is letting the user choose which interface to run.

#### Responsibilities

* displays the startup choice between Terminal and Graphical Interface;
* reads the selected option;
* delegates control to `ConsoleUI` (the graphical option currently falls back to the terminal, since the JavaFX UI is still in development).

---

### 2. ui/console/ConsoleUI.java

Owns the console menu loop and orchestrates calls to the controller. This class did not exist in the previous version — the menu loop used to live in `Main.java` and now lives here, keeping `Main.java` focused only on choosing the interface.

#### Responsibilities

* runs the main menu loop until the exit option is selected;
* reads the selected option and calls the matching private method (`addTransaction`, `listTransaction`, `removeTransaction`, `calculateTotalBalance`);
* coordinates `ConsoleFormatter` (what to show) and `ConsoleInput` (what to read) around each `FinTracker` call.

#### Main Flow

* option 1: add transaction;
* option 2: list transactions;
* option 3: remove transaction;
* option 4: calculate total balance;
* option 5: exit the program.

---

### 3. ui/console/ConsoleFormatter.java

Responsible **only** for what gets printed to the console — no input reading, no validation, no business logic.

#### Main Methods

* `clearScreen()`: clears the screen by printing multiple blank lines;
* `pause(Scanner sc)`: pauses execution until the user presses Enter;
* `showMenu()`: displays the main menu;
* `showInputDescription()`: displays the description prompt;
* `showInputType()`: displays the income/expense options;
* `showInputValue()`: displays the value prompt;
* `showTransactions(List<Transaction> transactions)`: displays the list of transactions in a formatted table.

---

### 4. ui/console/ConsoleInput.java

Responsible for reading and validating raw user input from the terminal. This was previously mixed into the formatter/controller layer and is now isolated here.

#### Main Methods

* `readString(Scanner sc)`: reads and validates a non-empty description, re-prompting on invalid input;
* `readInt(Scanner sc, int id)`: reads and validates an integer option, re-prompting on invalid input;
* `readDouble(Scanner sc, boolean receipt)`: reads a transaction value, applying the correct sign based on whether it's income or an expense.

---

### 5. controller/FinTracker.java

Class responsible for the application's business logic. It no longer reads any input directly — it only receives already-validated data and coordinates the model layer. This decoupling is what allows both the console UI and the future JavaFX UI to reuse the exact same logic.

#### Main Methods

* `addTransaction(String description, double value, boolean receipt)`: creates and stores a new transaction.
* `listTransaction()`: returns all registered transactions.
* `removeTransaction(int option)`: removes a transaction selected by the user; throws `InvalidInput` if the index is invalid.
* `calculateTotalBalance()`: returns the accumulated balance.

---

### 6. model/Transaction.java

Represents a single financial transaction.

#### Attributes

* `description`: transaction description;
* `value`: transaction value;
* `receipt`: indicates whether the transaction is income (`true`) or expense (`false`);
* `date`: date on which the transaction was created (set automatically to `LocalDate.now()`).

#### Accessor Methods

* `getDescription()`
* `getValue()`
* `isReceipt()`
* `getDate()`

---

### 7. model/MonthlyTransaction.java

Responsible for storing and managing the collection of transactions.

#### Main Methods

* `add(Transaction transaction)`: adds a transaction to the history;
* `del(int option)`: removes a transaction based on the provided index; throws `InvalidInput` if the list is empty or the index is out of range;
* `getTransactions()`: returns an unmodifiable view of the transaction list;
* `totalBalance()`: sums all transaction values (expenses are already stored as negative values, so this is a simple sum).

---

### 8. exceptions/InvalidInput.java

Custom checked exception used to signal invalid user input or invalid operations (e.g. removing from an empty list, or an out-of-range index).

---

### 9. utils/InputValidator.java

Small stateless utility with overloaded validation helpers, reused by `ConsoleInput`.

#### Main Methods

* `isValid(String input)`: true if the string is non-null and not blank;
* `isValid(int input)`: true if the value is non-negative;
* `isValid(double input)`: true if the value is non-negative.

---

### 10. ui/javafx/FinTrackUI.java

Placeholder for the upcoming JavaFX graphical interface. Currently empty — not yet implemented. The `pom.xml` already includes the `javafx-controls` and `javafx-fxml` dependencies in preparation for this.

---

## System Behavior

The program works as a small financial manager. The basic workflow is:

1. the user chooses an interface (terminal, or graphical once available);
2. the user selects an option from the menu;
3. `ConsoleInput` reads and validates the required information;
4. `FinTracker` stores the transaction via the model layer;
5. the balance can be checked at any time.

---

## Main Features

* simple terminal interface, with a graphical interface planned;
* support for income and expenses;
* temporary in-memory storage;
* input reading, validation, display, and business logic fully separated into their own layers;
* organized transaction display.

---

## Getting Started

```bash
# Run from the terminal using Maven
mvn compile exec:java
```

Or build and run the packaged application through your IDE's Maven integration, using `app.Main` as the main class.

---

## Technical Details

* **Language:** Java 21;
* **Build Tool:** Maven;
* **Paradigm:** Object-Oriented Programming, with UI, business logic, and data layers separated;
* **User Input:** `Scanner`, isolated in `ui/console/ConsoleInput.java`;
* **Storage:** in-memory list (`ArrayList`), wrapped by `MonthlyTransaction`;
* **Error Handling:** custom checked exception (`InvalidInput`) and `InputMismatchException` for malformed console input.

## License

FinTrack **is not an Open Source project**.

The source code is made available under the **Software Use and Study License, Version 1.0**.

See the [`LICENSE`](LICENSE) file for the complete license terms.