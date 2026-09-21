package repository;

import model.TransactionType;
import model.dto.CardData;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import java.util.List;

public interface DashboardRepository {
    List<CategoryChartData> getCategoryDistribution(int monthId, TransactionType type);

    List<DailyFinancialData> getMonthlyOverview(int monthId);

    CardData getMonthlyCard(int monthId);

    CardData getGlobalCard();

    List<CategoryChartData> getGlobalCategoryDistribution(TransactionType type);
}