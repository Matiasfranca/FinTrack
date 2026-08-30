package service;

import database.DatabaseConnection;
import exceptions.DataAccessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class DatabaseMaintenanceService {

    static public void cleanIfFirstDayOfMonth() {
        LocalDate today = LocalDate.now();

        if (today.getDayOfMonth() == 1) {
            cleanDatabaseTrash();
        }
    }

    static public void cleanDatabaseTrash() {
        String deleteEmptyMonths = """
                DELETE FROM MONTH
                WHERE id NOT IN (
                    SELECT DISTINCT month_id FROM "TRANSACTION"
                )
                """;

        String deleteUnusedInactiveAccounts = """
                DELETE FROM BANK_ACCOUNT
                WHERE is_active = 0
                  AND id NOT IN (
                      SELECT DISTINCT bank_account_id FROM "TRANSACTION"
                  )
                """;

        try (Connection conn = DatabaseConnection.getConnection()) {

            conn.setAutoCommit(false);

            try (PreparedStatement stmtMonths = conn.prepareStatement(deleteEmptyMonths);
                    PreparedStatement stmtAccounts = conn.prepareStatement(deleteUnusedInactiveAccounts)) {

                stmtMonths.executeUpdate();
                stmtAccounts.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear the database", e);
        }
    }
}