package repository;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import model.TransactionType;
import model.dto.CardData;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import repository.sqlite.SqliteDashboardRepository;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SqliteDashboardRepositoryTest {

    private static SqliteDashboardRepository dashboardRepo;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.setTestMode(true);
        DatabaseInitializer.initialize();
        dashboardRepo = new SqliteDashboardRepository();

        try (var conn = database.DatabaseConnection.getConnection();
            var stmt = conn.createStatement()) {

            stmt.execute("INSERT OR IGNORE INTO MONTH (id, year, month) VALUES (1, 2026, 8)");
            stmt.execute("INSERT OR IGNORE INTO BANK_ACCOUNT (id, name, type) VALUES (1, 'Conta Teste', 'CHECKING')");
            stmt.execute("INSERT INTO \"TRANSACTION\" (month_id, bank_account_id, date, value, type, payment_method, description) VALUES (1, 1, '2026-08-02', 70.00, 'EXPENSE', 'PIX', 'Compra de Teste')");
            stmt.execute("INSERT INTO \"TRANSACTION\" (month_id, bank_account_id, date, value, type, payment_method, description) VALUES (1, 1, '2026-08-01', 50.00, 'EXPENSE', 'PIX', 'Compra de Teste')");
        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare test data", e); 
        }
    }

    @Test
    @Order(1)
    void getCategoryDistributionTest() {

        List<CategoryChartData> categorysList = dashboardRepo.getCategoryDistribution(1, TransactionType.EXPENSE);
        assertNotNull(categorysList, "Category list should not be null");
        assertFalse(categorysList.isEmpty(), "Category list should not be empty");
        assertTrue(categorysList.get(0).getTotalValue().compareTo(BigDecimal.ZERO) > 0,
                "Category total value must be greater than zero");

    }

    @Test
    @Order(2)
    void getMonthlyOverviewTest() {

        List<DailyFinancialData> categorysList = dashboardRepo.getMonthlyOverview(1);
        assertNotNull(categorysList, "Monthly overview list should not be null");
        assertFalse(categorysList.isEmpty(), "Monthly overview list should not be empty");
        assertEquals(1, categorysList.get(0).getDayOfMonth(), "First listed day should be day 1");

    }

    @Test
    @Order(3)
    void getMonthlyCardTest() {

        CardData cardData = dashboardRepo.getMonthlyCard(1);
        assertNotNull(cardData, "CardData object should not be null");
        assertEquals(0, new BigDecimal("120.00").compareTo(cardData.getTotalExpense()), 
                "Total card expenses should equal 120.00"); 

    }

}