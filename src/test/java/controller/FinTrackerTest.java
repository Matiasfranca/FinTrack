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
        String description = null;
        BigDecimal value = new BigDecimal("150.75");
        TransactionType type = TransactionType.EXPENSE;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;
        LocalDate date = LocalDate.of(2026, 6, 15);
        
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = new Category(0, "", "#FF5733");

        assertDoesNotThrow(() -> {
            finTracker.addTransaction(description, value, type, paymentMethod, date, account, category);
        });
    }

    @Test
    @Order(2)
    void shouldThrowExceptionWhenValueIsNegativeOrZero() {
        String description = "Freelance";
        BigDecimal value = new BigDecimal("-10.00");
        TransactionType type = TransactionType.INCOME;
        PaymentMethod paymentMethod = PaymentMethod.PIX;
        LocalDate date = LocalDate.now();
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = null;

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.addTransaction(description, value, type, paymentMethod, date, account, category);
        });

        assertEquals("Invalid value.", exception.getMessage());
    }

    @Test
    @Order(3)
    void shouldAddTransactionSuccessfullyWithNullDate() {
        String description = " ";
        BigDecimal value = new BigDecimal("8.50");
        TransactionType type = TransactionType.EXPENSE;
        PaymentMethod paymentMethod = PaymentMethod.CASH;
        LocalDate date = null;
        
        BankAccount account = new BankAccount("Nubank", BankAccountType.SAVINGS);
        Category category = new Category("Food", "#FF5733");

        assertDoesNotThrow(() -> {
            finTracker.addTransaction(description, value, type, paymentMethod, date, account, category);
        });
    }

    @Test
    @Order(4)
    void shouldThrowExceptionWhenValueIsZero() {
        String description = "Test Zero";
        BigDecimal value = BigDecimal.ZERO;
        TransactionType type = TransactionType.EXPENSE;
        PaymentMethod paymentMethod = PaymentMethod.PIX;
        LocalDate date = LocalDate.now();
        BankAccount account = new BankAccount("Nubank", BankAccountType.CHECKING);
        Category category = null;

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.addTransaction(description, value, type, paymentMethod, date, account, category);
        });

        assertEquals("Invalid value.", exception.getMessage());
    }
}