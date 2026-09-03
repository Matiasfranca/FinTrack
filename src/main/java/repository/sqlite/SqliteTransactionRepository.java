package repository.sqlite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import exceptions.DataAccessException;
import model.PaymentMethod;
import model.Transaction;
import model.TransactionType;
import repository.TransactionRepository;

public class SqliteTransactionRepository implements TransactionRepository {

    @Override
    public void save(Transaction transaction, int monthId, int bankAccountId, Integer categoryId) {

        String sql = """
                INSERT INTO "TRANSACTION" (month_id, bank_account_id, category_id, date, value, type, payment_method, description)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, monthId);
            stmt.setInt(2, bankAccountId);
            if (categoryId != null)
                stmt.setInt(3, categoryId);
            else
                stmt.setNull(3, java.sql.Types.INTEGER);
            stmt.setString(4, transaction.getDate().toString());
            stmt.setBigDecimal(5, transaction.getValue());
            stmt.setString(6, transaction.getTransactionType().name());
            stmt.setString(7, transaction.getPaymentMethod().name());
            stmt.setString(8, transaction.getDescription() != null ? transaction.getDescription().trim() : null);

            stmt.executeUpdate();

            try (java.sql.ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    transaction.setId(generatedKeys.getInt(1));
                    transaction.setBankAccountId(bankAccountId);
                    transaction.setCategoryId(categoryId != null ? categoryId : null);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to save transaction", e);
        }
    }

    @Override
    public List<Transaction> findByMonth(YearMonth month) {

        String sql = """
                SELECT t.id, t.date, t.description, t.value, t.type, t.payment_method, t.bank_account_id, t.category_id
                FROM "TRANSACTION" t
                JOIN MONTH m ON t.month_id = m.id
                WHERE m.year = ? AND m.month = ?
                ORDER BY t.date DESC
                """;

        List<Transaction> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, month.getYear());
            stmt.setInt(2, month.getMonthValue());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    int rawCategoryId = rs.getInt("category_id");
                    Integer categoryId = rs.wasNull() ? null : rawCategoryId;

                    result.add(new Transaction(
                            rs.getInt("id"),
                            rs.getString("description"),
                            rs.getBigDecimal("value"),
                            TransactionType.valueOf(rs.getString("type")),
                            PaymentMethod.valueOf(rs.getString("payment_method")),
                            LocalDate.parse(rs.getString("date")),
                            rs.getInt("bank_account_id"),
                            categoryId));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to retrieve transactions for " + month, e);
        }

        return result;
    }

    @Override
    public void delete(int transactionId) {
        String sql = "DELETE FROM \"TRANSACTION\" WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete transaction " + transactionId, e);
        }
    }

    @Override
    public void update(Transaction transaction, int monthId, int bankAccountId, Integer categoryId) {

        String sql = """
                UPDATE "TRANSACTION"
                SET month_id = ?, bank_account_id = ?, category_id = ?,
                    date = ?, value = ?, type = ?, payment_method = ?, description = ?
                WHERE id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, monthId);
            stmt.setInt(2, bankAccountId);

            if (categoryId != null) {
                stmt.setInt(3, categoryId);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }

            stmt.setString(4, transaction.getDate().toString());
            stmt.setBigDecimal(5, transaction.getValue());
            stmt.setString(6, transaction.getTransactionType().name());
            stmt.setString(7, transaction.getPaymentMethod().name());
            stmt.setString(8, transaction.getDescription() != null ? transaction.getDescription().trim() : null);

            stmt.setInt(9, transaction.getId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("Failed to update transaction " + transaction.getId(), e);
        }
    }
}