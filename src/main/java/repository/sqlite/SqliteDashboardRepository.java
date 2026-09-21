package repository.sqlite;

import database.DatabaseConnection;
import exceptions.DataAccessException;
import model.TransactionType;
import model.dto.CardData;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import repository.DashboardRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SqliteDashboardRepository implements DashboardRepository {

    @Override
    public List<CategoryChartData> getCategoryDistribution(int monthId, TransactionType type) {
        String sql = """
                SELECT
                    IFNULL(c.name, 'Outros') as category_name,
                    IFNULL(c.color, '#A9A9A9') as category_color,
                    SUM(t.value) as total_value
                FROM "TRANSACTION" t
                LEFT JOIN CATEGORY c ON t.category_id = c.id
                WHERE t.month_id = ? AND t.type = ?
                GROUP BY t.category_id
                ORDER BY total_value DESC
                """;

        List<CategoryChartData> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, monthId);
            stmt.setString(2, type.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new CategoryChartData(
                            rs.getString("category_name"),
                            rs.getString("category_color"),
                            rs.getBigDecimal("total_value")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate category distribution data", e);
        }

        return result;
    }

    @Override
    public List<DailyFinancialData> getMonthlyOverview(int monthId) {
        String sql = """
                SELECT
                    CAST(strftime('%d', date) AS INTEGER) as day_of_month,
                    IFNULL( SUM(CASE WHEN type = 'INCOME' THEN value ELSE 0 END), 0 ) as daily_income,
                    IFNULL( SUM(CASE WHEN type = 'EXPENSE' THEN value ELSE 0 END), 0 ) as daily_expense,
                    IFNULL( SUM(CASE WHEN type = 'INVESTMENT' THEN value ELSE 0 END), 0 ) as daily_investment,
                    IFNULL(SUM(CASE WHEN type = 'REDEMPTION' THEN value ELSE 0 END), 0) AS daily_redemption  
                FROM "TRANSACTION"
                WHERE month_id = ?
                GROUP BY day_of_month
                ORDER BY day_of_month
                """;

        List<DailyFinancialData> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, monthId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new DailyFinancialData(
                            rs.getInt("day_of_month"),
                            rs.getBigDecimal("daily_income"),
                            rs.getBigDecimal("daily_expense"),
                            rs.getBigDecimal("daily_investment"),
                            rs.getBigDecimal("daily_redemption")
                        ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate daily financial data", e);
        }

        return result;
    }

    @Override
    public List<CategoryChartData> getGlobalCategoryDistribution(TransactionType type) {
        String sql = "SELECT " +
                "IFNULL(c.name, 'Outros') as category_name, " +
                "IFNULL(c.color, '#A9A9A9') as category_color, " +
                "SUM(t.value) as total_value " +
                "FROM \"TRANSACTION\" t " +
                "LEFT JOIN CATEGORY c ON t.category_id = c.id " +
                "WHERE t.type = ? " +
                "GROUP BY t.category_id " +
                "ORDER BY total_value DESC";

        List<CategoryChartData> result = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, type.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new CategoryChartData(
                            rs.getString("category_name"),
                            rs.getString("category_color"),
                            rs.getBigDecimal("total_value")));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate global financial data", e);
        }
        return result;
    }

    @Override
    public CardData getMonthlyCard(int monthId) {
        String sql = """
                SELECT
                    IFNULL( SUM(CASE WHEN type = 'INCOME' THEN value ELSE 0 END), 0 ) as total_income,
                    IFNULL( SUM(CASE WHEN type = 'EXPENSE' THEN value ELSE 0 END), 0 ) as total_expense,
                    IFNULL( SUM(CASE WHEN type = 'INVESTMENT' THEN value ELSE 0 END), 0 ) as total_investment
                FROM "TRANSACTION"
                WHERE month_id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, monthId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new CardData(
                            rs.getBigDecimal("total_income"),
                            rs.getBigDecimal("total_expense"),
                            rs.getBigDecimal("total_investment"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate card summary data", e);
        }

        return new CardData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Override
    public CardData getGlobalCard() {
        String sql = "SELECT " +
                "IFNULL(SUM(CASE WHEN type = 'INCOME' THEN value ELSE 0 END), 0) AS total_income, " +
                "IFNULL(SUM(CASE WHEN type = 'EXPENSE' THEN value ELSE 0 END), 0) AS total_expense, " +
                "IFNULL(SUM(CASE WHEN type = 'INVESTMENT' THEN value ELSE 0 END), 0) AS total_investment, " +
                "IFNULL(SUM(CASE WHEN type = 'REDEMPTION' THEN value ELSE 0 END), 0) AS total_redemption " +
                "FROM \"TRANSACTION\"";

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                BigDecimal income = rs.getBigDecimal("total_income");
                BigDecimal expense = rs.getBigDecimal("total_expense");
                BigDecimal investment = rs.getBigDecimal("total_investment");
                BigDecimal redemption = rs.getBigDecimal("total_redemption");


                return new CardData(income, expense, investment, redemption);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to generate global card summary data", e);
        }

        return new CardData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}