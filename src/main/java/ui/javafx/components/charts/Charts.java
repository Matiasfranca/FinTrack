package ui.javafx.components.charts;

import controller.FinTracker;
import model.TransactionType;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import ui.javafx.components.charts.expenseDistribution.ExpenseDistribution;
import ui.javafx.components.charts.monthlyOverview.MonthlyOverview;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Event;

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

        this.monthlyOverview = new MonthlyOverview(this.dailyFinancialData, this.finTracker);
        this.expenseDistribution = new ExpenseDistribution(this.expenseData, this.investmentData);

        getChildren().addAll(this.monthlyOverview, this.expenseDistribution);

        // Component stylesheet
        getStylesheets().add(getClass().getResource("Charts.css").toExternalForm());

        // Events
        TransactionEventBus.getInstance().subscribe(this::updateCharts);

    }

    private void fetchData() {
        try {
            YearMonth now = YearMonth.now();
            this.dailyFinancialData = this.finTracker.getMonthlyOverview(now);
            this.expenseData = this.finTracker.getCategoryDistribution(now, TransactionType.EXPENSE);
            this.investmentData = this.finTracker.getGlobalCategoryDistribution(TransactionType.INVESTMENT);
            this.redemptions = finTracker.getGlobalCategoryDistribution(TransactionType.REDEMPTION);

            this.netInvestments.clear();

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

            this.investmentData = netInvestments;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCharts(Event e) {
        fetchData();

        this.monthlyOverview.refreshData(this.dailyFinancialData);

        this.expenseDistribution.refreshData(this.expenseData, this.investmentData);
    }

}
