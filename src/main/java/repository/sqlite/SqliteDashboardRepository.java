package repository.sqlite;

import database.DatabaseConnection;
import exceptions.DataAccessException;
import model.TransactionType;
import model.dto.CardData;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import repository.DashboardRepository;
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
                        rs.getBigDecimal("total_value")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Falha ao gerar dados de categorias", e);
        }

        return result;
    }

    @Override
    public List<DailyFinancialData> getMonthlyOverview(int monthId) {
        String sql = """
            SELECT 
                CAST(strftime('%d', date) AS INTEGER) as day_of_month,
                IFNULL( SUM(CASE WHEN type = 'INCOME' THEN value ELSE 0 END), 0 ) as daily_income,
                IFNULL( SUM(CASE WHEN type = 'EXPENSE' THEN value ELSE 0 END), 0 ) as daily_expense
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
                        rs.getBigDecimal("daily_expense")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Falha ao gerar dados diários", e);
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
                        rs.getBigDecimal("total_investment")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Falha ao gerar dados de cards", e);
        }

        return new CardData(java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO, java.math.BigDecimal.ZERO);
    }
}