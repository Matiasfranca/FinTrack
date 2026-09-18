package repository;

import model.*;
import org.junit.jupiter.api.*;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import repository.sqlite.SqliteTransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqliteTransactionRepositoryTest {

    private static SqliteTransactionRepository transactionRepo;
    private static int testMonthId = 1;
    private static int testBankId = 1;
    private static Integer testCategoryId = null;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.setTestMode(true);
        DatabaseInitializer.initialize();
        transactionRepo = new SqliteTransactionRepository();

        // Ensures that month 1 and bank account 1 exist in the test database to satisfy foreign keys
        try (var conn = database.DatabaseConnection.getConnection();
                var stmt = conn.createStatement()) {

            stmt.execute("INSERT OR IGNORE INTO MONTH (id, year, month) VALUES (1, 2026, 8)");
            stmt.execute("INSERT OR IGNORE INTO BANK_ACCOUNT (id, name, type) VALUES (1, 'Conta Teste', 'CHECKING')");

        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare test data", e);
        }
    }

    @Test
    @Order(1)
    void shouldSaveTransactionSuccessfully() {
        Transaction newTransaction = new Transaction(
                "Compra de Teste",
                new BigDecimal("150.50"),
                TransactionType.EXPENSE,
                PaymentMethod.PIX,
                LocalDate.of(2026, 8, 27));

        // Executes save
        assertDoesNotThrow(() -> {
            transactionRepo.save(newTransaction, testMonthId, testBankId, testCategoryId);
        });
    }

    @Test
    @Order(2)
    void shouldFindTransactionsByMonth() {
        YearMonth targetMonth = YearMonth.of(2026, 8);

        List<Transaction> transactions = transactionRepo.findByMonth(targetMonth);

        assertNotNull(transactions);
        assertFalse(transactions.isEmpty(), "Should find at least the transaction registered in the previous test");

        // Validates if data matches
        Transaction found = transactions.get(0);
        assertEquals("Compra de Teste", found.getDescription());
        assertEquals(0, new BigDecimal("150.50").compareTo(found.getValue()));
    }

    @Test
    @Order(3)
    void shouldUpdateTransactionSuccessfully() {
        YearMonth targetMonth = YearMonth.of(2026, 8);
        List<Transaction> transactions = transactionRepo.findByMonth(targetMonth);

        Assumptions.assumeTrue(!transactions.isEmpty(), "Must have transactions to update");

        Transaction toEdit = transactions.get(0);

        // Creates a transaction with new data while keeping the same ID
        Transaction updatedTransaction = new Transaction(
                toEdit.getId(),
                "Compra Editada",
                new BigDecimal("200.00"),
                TransactionType.EXPENSE,
                PaymentMethod.DEBIT_CARD,
                toEdit.getDate(),
                testBankId,
                testCategoryId);

        assertDoesNotThrow(() -> {
            transactionRepo.update(updatedTransaction, testMonthId, testBankId,
                    testCategoryId);
        });

        // Verifies if changes took effect
        List<Transaction> verification = transactionRepo.findByMonth(targetMonth);
        assertEquals("Compra Editada", verification.get(0).getDescription());
        assertEquals(0, new BigDecimal("200.00").compareTo(verification.get(0).getValue()));
    }

    @Test
    @Order(4)
    void shouldDeleteTransactionSuccessfully() {
        YearMonth targetMonth = YearMonth.of(2026, 8);
        List<Transaction> transactions = transactionRepo.findByMonth(targetMonth);

        if (!transactions.isEmpty()) {
            int idToDelete = transactions.get(0).getId();

            assertDoesNotThrow(() -> {
                transactionRepo.delete(idToDelete);
            });
        }
    }
}