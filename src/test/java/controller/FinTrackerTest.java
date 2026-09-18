package controller;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import exceptions.InvalidInput;
import model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

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
        Transaction transaction = new Transaction(1, "Bad Update", new BigDecimal("-50.00"), TransactionType.EXPENSE, PaymentMethod.PIX, LocalDate.now(), 1, 1);

        assertDoesNotThrow(() -> {
            finTracker.deleteTransaction(transaction);
        });
    }

    @Test
    @Order(9)
    void shouldThrowExceptionWhenTryingToDeleteWithInvalidId() {
        Transaction transaction = new Transaction(0, "Bad Update", new BigDecimal("-50.00"), TransactionType.EXPENSE, PaymentMethod.PIX, LocalDate.now(), 1, 1);

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.deleteTransaction(transaction);
        });

        assertEquals("Cannot delete a transaction with an invalid ID.", exception.getMessage());
    }

    @Test
    @Order(10)
    void shouldListTransactionsByMonthSuccessfully() {
        YearMonth targetMonth = YearMonth.of(2026, 6);

        assertDoesNotThrow(() -> {
            var transactions = finTracker.listTransactionsByMonth(targetMonth);
            assertNotNull(transactions, "The transaction list should never be null.");
        });
    }

    @Test
    @Order(11)
    void shouldThrowExceptionWhenListingTransactionsWithNullYearMonth() {
        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.listTransactionsByMonth(null);
        });

        assertEquals("YearMonth cannot be null.", exception.getMessage());
    }

    @Test
    @Order(12)
    void shouldGetCategoryDistributionSuccessfully() {
        YearMonth targetMonth = YearMonth.of(2026, 6);

        assertDoesNotThrow(() -> {
            var distribution = finTracker.getCategoryDistribution(targetMonth, TransactionType.EXPENSE);
            assertNotNull(distribution, "The category distribution list should never be null.");
        });
    }

    @Test
    @Order(13)
    void shouldGetMonthlyOverviewSuccessfully() {
        YearMonth targetMonth = YearMonth.of(2026, 6);

        assertDoesNotThrow(() -> {
            var overview = finTracker.getMonthlyOverview(targetMonth);
            assertNotNull(overview, "The monthly overview list should never be null.");
        });
    }

    @Test
    @Order(14)
    void shouldGetMonthlyCardSuccessfully() {
        YearMonth targetMonth = YearMonth.of(2026, 6);

        assertDoesNotThrow(() -> {
            var card = finTracker.getMonthlyCard(targetMonth);
            assertNotNull(card, "The card data summary should never be null.");
        });
    }

    @Test
    @Order(15)
    void shouldThrowExceptionWhenDashboardMethodsReceiveNullYearMonth() {
        assertAll(
            () -> {
                InvalidInput ex1 = assertThrows(InvalidInput.class, () -> finTracker.getCategoryDistribution(null, TransactionType.EXPENSE));
                assertEquals("YearMonth and TransactionType cannot be null.", ex1.getMessage());
            },
            () -> {
                InvalidInput ex2 = assertThrows(InvalidInput.class, () -> finTracker.getMonthlyOverview(null));
                assertEquals("YearMonth cannot be null.", ex2.getMessage());
            },
            () -> {
                InvalidInput ex3 = assertThrows(InvalidInput.class, () -> finTracker.getMonthlyCard(null));
                assertEquals("YearMonth cannot be null.", ex3.getMessage());
            }
        );
    }

    @Test
    @Order(16)
    void shouldListAllCategoriesSuccessfully() {
        assertDoesNotThrow(() -> {
            var categories = finTracker.listAllCategories();
            assertNotNull(categories, "The categories list should never be null.");
        });
    }

    @Test
    @Order(17)
    void shouldUpdateCategorySuccessfully() {
        Category category = new Category(1, "Updated Category", "#00FF00");

        assertDoesNotThrow(() -> {
            finTracker.updateCategory(category);
        });
    }

    @Test
    @Order(18)
    void shouldThrowExceptionWhenUpdatingInvalidCategory() {
        Category invalidCategory = new Category(0, "", null);

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.updateCategory(invalidCategory);
        });

        assertEquals("Cannot update category with invalid ID or name.", exception.getMessage());
    }

    @Test
    @Order(19)
    void shouldDeleteCategorySuccessfully() {
        int validCategoryId = 1;

        assertDoesNotThrow(() -> {
            finTracker.deleteCategory(validCategoryId);
        });
    }

    @Test
    @Order(20)
    void shouldThrowExceptionWhenDeletingCategoryWithInvalidId() {
        int invalidCategoryId = 0;

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.deleteCategory(invalidCategoryId);
        });

        assertEquals("Cannot delete a category with an invalid ID.", exception.getMessage());
    }

    @Test
    @Order(21)
    void shouldListBankAccountsSuccessfully() {
        assertAll(
            () -> {
                var allAccounts = finTracker.listAllBankAccounts();
                assertNotNull(allAccounts, "All bank accounts list should never be null.");
            },
            () -> {
                var activeAccounts = finTracker.listActiveBankAccounts();
                assertNotNull(activeAccounts, "Active bank accounts list should never be null.");
            }
        );
    }

    @Test
    @Order(22)
    void shouldUpdateBankAccountSuccessfully() {
        BankAccount account = new BankAccount(1, "Updated Bank", BankAccountType.SAVINGS, true);

        assertDoesNotThrow(() -> {
            finTracker.updateBankAccount(account);
        });
    }

    @Test
    @Order(23)
    void shouldThrowExceptionWhenUpdatingInvalidBankAccount() {
        BankAccount invalidAccount = new BankAccount(0, "", null, true);

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.updateBankAccount(invalidAccount);
        });

        assertEquals("Cannot update bank account with invalid ID or name.", exception.getMessage());
    }

    @Test
    @Order(24)
    void shouldDeactivateBankAccountSuccessfully() {
        int validAccountId = 1;

        assertDoesNotThrow(() -> {
            finTracker.deactivateBankAccount(validAccountId);
        });
    }

    @Test
    @Order(25)
    void shouldThrowExceptionWhenDeactivatingInvalidBankAccount() {
        int invalidAccountId = -1;

        InvalidInput exception = assertThrows(InvalidInput.class, () -> {
            finTracker.deactivateBankAccount(invalidAccountId);
        });

        assertEquals("Cannot deactivate a bank account with an invalid ID.", exception.getMessage());
    }
}