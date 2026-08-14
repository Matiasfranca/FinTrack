# FinTrack

## Overview

FinTrack is a Java console application for managing monthly financial transactions. The system allows users to:

* add transactions;
* list registered transactions;
* remove transactions;
* calculate the total monthly balance.

The project uses in-memory storage, so all data is lost when the application is closed.

---

## Project Structure

```text
fintrack/
├── app/
│   └── Main.java
├── controller/
│   └── FinTracker.java
├── exceptions/
│   └── InvalidInput.java
├── model/
│   ├── MonthlyTransaction.java
│   └── Transaction.java
└── utils/
    └── Formatter.java
```

---

## Project Files

### 1. app/Main.java

Main application entry point.

#### Responsibilities

* starts the main menu;
* reads the option selected by the user;
* calls the controller methods according to the selected option;
* controls the program loop until the exit option is selected.

#### Main Flow

* option 1: add transaction;
* option 2: list transactions;
* option 3: remove transaction;
* option 4: calculate total balance;
* option 5: exit the program.

---

### 2. controller/FinTracker.java

Class responsible for the application's business logic.

#### Features

* add a new transaction;
* list all registered transactions;
* remove a transaction by its displayed index;
* calculate the total monthly balance.

#### Main Methods

* `addTransaction(Scanner sc)`: reads the description, type (income or expense), and value.
* `listTransaction()`: displays all registered transactions.
* `removeTransaction(Scanner sc)`: removes a transaction selected by the user.
* `calculateTotalBalance()`: returns the accumulated balance.

#### Implemented Validations

* description cannot be empty;
* type must be 1 for income or 2 for expense;
* value cannot be negative.

---

### 3. model/Transaction.java

Represents a single financial transaction.

#### Attributes

* `description`: transaction description;
* `value`: transaction value;
* `receipt`: indicates whether the transaction is income (`true`) or expense (`false`);
* `date`: date on which the transaction was created.

#### Accessor Methods

* `getDescription()`
* `getValue()`
* `isReceipt()`
* `getDate()`

---

### 4. model/MonthlyTransaction.java

Responsible for storing and managing the collection of transactions.

#### Features

* add a transaction;
* remove a transaction from the list;
* return the list of transactions;
* calculate the total balance by summing all transaction values.

#### Main Methods

* `add(Transaction transaction)`: adds a transaction to the history;
* `del(int option)`: removes a transaction based on the provided index;
* `getTransactions()`: returns the list of transactions;
* `totalBalance()`: calculates the total balance.

---

### 5. exceptions/InvalidInput.java

Custom exception class used to handle invalid input.

#### Purpose

* encapsulates specific error messages for system validations.

---

### 6. utils/Formatter.java

Class responsible for formatting and displaying information in the console.

#### Main Methods

* `clearScreen()`: clears the screen by printing multiple blank lines;
* `pause(Scanner sc)`: pauses execution until the user presses Enter;
* `showMenu()`: displays the main menu;
* `showInputDescription()`: displays the description prompt;
* `showInputType()`: displays the income/expense options;
* `showInputValue()`: displays the value prompt;
* `showTransactions(List<Transaction> transactions)`: displays the list of transactions in an organized format.

---

## System Behavior

The program works as a small financial manager running in the terminal. The basic workflow is:

1. the user selects an option from the menu;
2. the system requests the required information;
3. the input is validated;
4. the transaction is stored in memory;
5. the balance can be checked at any time.

---

## Main Features

* simple terminal interface;
* support for income and expenses;
* temporary in-memory storage;
* basic input validation;
* organized transaction display.

---

## Technical Details

* **Language:** Java;
* **Paradigm:** Object-Oriented Programming;
* **User Input:** `Scanner`;
* **Storage:** in-memory list (`ArrayList`);
* **Error Handling:** custom exceptions and `InputMismatchException`.

## License

FinTrack **is not an Open Source project**.

The source code is made available under the **Software Use and Study License, Version 1.0**.

See the [`LICENSE`](LICENSE) file for the complete license terms.
