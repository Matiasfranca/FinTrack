package repository.sqlite;

import repository.MonthRepository;
import database.DatabaseConnection;
import exceptions.DataAccessException;
import model.Month;

import java.time.YearMonth;
import java.sql.*;

public class SqliteMonthRepository implements MonthRepository {

    /**
     * Retrieves a month from the database. If it does not exist, inserts it and
     * returns it with its new ID.
     */
    @Override
    public Month getOrCreate(YearMonth yearMonth) {
        Month existingMonth = findByYearMonth(yearMonth);

        if (existingMonth != null) {
            return existingMonth;
        }

        return create(yearMonth);
    }

    private Month findByYearMonth(YearMonth yearMonth) {
        String sql = "SELECT id, year, month FROM MONTH WHERE year = ? AND month = ?";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, yearMonth.getYear());
            stmt.setInt(2, yearMonth.getMonthValue());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Month(
                            rs.getInt("id"),
                            rs.getInt("year"),
                            rs.getInt("month"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve month " + yearMonth, e);
        }
        return null;
    }

    private Month create(YearMonth yearMonth) {
        String sql = "INSERT INTO MONTH (year, month) VALUES (?, ?)";

        // RETURN_GENERATED_KEYS tells SQLite that we want to retrieve the ID that was
        // just generated.
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, yearMonth.getYear());
            stmt.setInt(2, yearMonth.getMonthValue());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    return new Month(newId, yearMonth.getYear(), yearMonth.getMonthValue());
                } else {
                    throw new SQLException("Month creation failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to create month " + yearMonth, e);
        }
    }
}