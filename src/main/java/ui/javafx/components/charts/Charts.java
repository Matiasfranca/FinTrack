package ui.javafx.components.charts;

import controller.FinTracker;
import model.TransactionType;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import ui.javafx.components.charts.expenseDistribution.ExpenseDistribution;
import ui.javafx.components.charts.monthlyOverview.MonthlyOverview;
import ui.javafx.events.AppEventBus;
import ui.javafx.events.TransactionEventBus;

import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class Charts extends HBox {

    private final FinTracker finTracker = new FinTracker();

    private List<DailyFinancialData> dailyFinancialData;
    private List<CategoryChartData> expenseData;
    private List<CategoryChartData> investmentData = new ArrayList<>();
    private List<CategoryChartData> redemptions;
    private List<CategoryChartData> netInvestments = new ArrayList<>();

    private MonthlyOverview monthlyOverview;
    private ExpenseDistribution expenseDistribution;

    public Charts() {

        fetchData();

        setSpacing(40);
        setAlignment(javafx.geometry.Pos.TOP_LEFT);

        monthlyOverview = new MonthlyOverview(dailyFinancialData, finTracker);
        expenseDistribution = new ExpenseDistribution(expenseData, investmentData);

        getChildren().addAll(monthlyOverview, expenseDistribution);

        // Component stylesheet
        getStylesheets().add(getClass().getResource("Charts.css").toExternalForm());

        // Events
        TransactionEventBus.getInstance().subscribe(e -> updateCharts());
        AppEventBus.getInstance().subscribe(e -> updateCharts());

    }

    private void fetchData() {
        try {
            YearMonth now = YearMonth.now();
            dailyFinancialData = finTracker.getMonthlyOverview(now);
            expenseData = finTracker.getCategoryDistribution(now, TransactionType.EXPENSE);
            investmentData = finTracker.getGlobalCategoryDistribution(TransactionType.INVESTMENT);
            redemptions = finTracker.getGlobalCategoryDistribution(TransactionType.REDEMPTION);

            netInvestments.clear();

            for (CategoryChartData inv : investmentData) {
                String catName = inv.getCategoryName();
                BigDecimal currentTotal = inv.getTotalValue();

                BigDecimal totalRedeemed = BigDecimal.ZERO;
                for (CategoryChartData red : redemptions) {
                    if (red.getCategoryName().equals(catName)) {
                        totalRedeemed = totalRedeemed.add(red.getTotalValue());
                    }
                }

                BigDecimal netValue = currentTotal.subtract(totalRedeemed);

                if (netValue.compareTo(BigDecimal.ZERO) > 0) {
                    netInvestments.add(new CategoryChartData(
                            catName,
                            inv.getCategoryColor(),
                            netValue
                    ));
                }
            }

            investmentData = netInvestments;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCharts() {
        fetchData();

        monthlyOverview.refreshData(dailyFinancialData);

        expenseDistribution.refreshData(expenseData, investmentData);
    }

}
