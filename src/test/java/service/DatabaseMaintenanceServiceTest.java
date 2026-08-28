package service;

import database.DatabaseConnection;
import database.DatabaseInitializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DatabaseMaintenanceServiceTest {

    private static DatabaseMaintenanceService maintenanceService;

    @BeforeAll
    static void setUp() {
        DatabaseConnection.setTestMode(true);
        DatabaseInitializer.initialize();
        maintenanceService = new DatabaseMaintenanceService();

        // Prepares mock data: an empty month and an inactive account with no transactions to test cleanup logic
        try (var conn = database.DatabaseConnection.getConnection();
             var stmt = conn.createStatement()) {

            stmt.execute("INSERT OR IGNORE INTO MONTH (id, year, month) VALUES (999, 2020, 1)");
            stmt.execute("INSERT OR IGNORE INTO BANK_ACCOUNT (id, name, type, is_active) VALUES (999, 'Old Inactive', 'CHECKING', 0)");

        } catch (Exception e) {
            throw new RuntimeException("Failed to prepare test data for DatabaseMaintenanceService", e);
        }
    }

    @Test
    void shouldCleanDatabaseTrashSuccessfully() {
        assertDoesNotThrow(() -> {
            maintenanceService.cleanDatabaseTrash();
        }, "Cleaning database trash should not throw any exceptions");
    }
}