package controller;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import exceptions.InvalidInput;
import model.*;
import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FinTrackerTest {

    private static FinTracker finTracker;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.setTestMode(true);
        DatabaseInitializer.initialize();
        finTracker = new FinTracker();
    }

    @Test
    @Order(1)
    void shouldAddTransactionSuccessfully() {
        Transaction transaction = new Transaction(null, new BigDecimal("150.75"), TransactionType.EXPENSE, PaymentMethod.CREDIT_CARD, LocalDate.of(2026, 6, 15));
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = new Category(0, "", "#FF5733");

        assertDoesNotThrow(() -> {
            finTracker.addTransaction(transaction, account, category);
        });
    }

    @Test
    @Order(2)
    void shouldThrowExceptionWhenValueIsNegativeOrZero() {
        Transaction transaction = new Transaction("Freelance", new BigDecimal("-10.00"), TransactionType.INCOME, PaymentMethod.PIX, LocalDate.now());
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = null;

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.addTransaction(transaction, account, category);
        });

        assertEquals("Invalid value.", exception.getMessage());
    }

    @Test
    @Order(3)
    void shouldAddTransactionSuccessfullyWithNullDate() {
        Transaction transaction = new Transaction(" ", new BigDecimal("8.50"), TransactionType.EXPENSE, PaymentMethod.CASH, null);
        BankAccount account = new BankAccount("Nubank", BankAccountType.SAVINGS);
        Category category = new Category("Food", "#FF5733");

        assertDoesNotThrow(() -> {
            finTracker.addTransaction(transaction, account, category);
        });
    }

    @Test
    @Order(4)
    void shouldThrowExceptionWhenValueIsZero() {
        Transaction transaction = new Transaction("Test Zero", BigDecimal.ZERO, TransactionType.EXPENSE, PaymentMethod.PIX, LocalDate.now());
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = null;

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.addTransaction(transaction, account, category);
        });

        assertEquals("Invalid value.", exception.getMessage());
    }

    @Test
    @Order(5)
    void shouldUpdateTransactionSuccessfullyWithExistingDate() {
        Transaction transaction = new Transaction(1, "Initial", new BigDecimal("100.00"), TransactionType.EXPENSE, PaymentMethod.CASH, LocalDate.of(2026, 6, 15), 1, 1);
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = new Category("Food", "#FF5733");

        assertDoesNotThrow(() -> {
            finTracker.updateTransaction(transaction, account, category);
        });
        
    }

    @Test
    @Order(6)
    void shouldUpdateTransactionSuccessfullyWithNullDate() {
        Transaction transaction = new Transaction(1, "Updated Without Date", new BigDecimal("200.00"), TransactionType.INCOME, PaymentMethod.PIX, null, 1, 1);
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = new Category("Salary", "#00FF00");

        assertDoesNotThrow(() -> {
            finTracker.updateTransaction(transaction, account, category);
        });
        
        assertNotNull(transaction.getDate());
    }

    @Test
    @Order(7)
    void shouldThrowExceptionWhenUpdateValueIsNegativeOrZero() {
        Transaction transaction = new Transaction(1, "Bad Update", new BigDecimal("-50.00"), TransactionType.EXPENSE, PaymentMethod.PIX, LocalDate.now(), 1, 1);
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = null;

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.updateTransaction(transaction, account, category);
        });

        assertEquals("Invalid value.", exception.getMessage());
    }

    @Test
    @Order(8)
    void shouldDeleteTransactionSuccessfully() {
        int validTransactionId = 1; // Assumindo que a transação 1 existe (inserida nos testes anteriores)

        assertDoesNotThrow(() -> {
            finTracker.deleteTransaction(validTransactionId);
        });
    }

    @Test
    @Order(9)
    void shouldThrowExceptionWhenTryingToDeleteWithInvalidId() {
        int invalidTransactionId = 0; // ID zero ou negativo é inválido

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.deleteTransaction(invalidTransactionId);
        });

        assertEquals("Cannot delete a transaction with an invalid ID.", exception.getMessage());
    }
}