package ui.javafx.components.charts;

import java.time.YearMonth;
import java.util.List;

import controller.FinTracker;
import javafx.scene.layout.HBox;
import model.TransactionType;
import model.dto.CategoryChartData;
import model.dto.DailyFinancialData;
import ui.javafx.components.charts.expenseDistribution.ExpenseDistribution;
import ui.javafx.components.charts.monthlyOverview.MonthlyOverview;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Event;
import ui.javafx.events.TransactionEventBus.Type;

public class Charts extends HBox {

    private final FinTracker finTracker = new FinTracker();

    private List<DailyFinancialData> dailyFinancialData;
    private List<CategoryChartData> expenseData;
    private List<CategoryChartData> investmentData;

    private MonthlyOverview monthlyOverview;
    private ExpenseDistribution expenseDistribution;

    public Charts() {

        fetchData();

        setSpacing(40);

        this.monthlyOverview = new MonthlyOverview(this.dailyFinancialData, this.finTracker);
        this.expenseDistribution = new ExpenseDistribution(this.expenseData, this.investmentData);

        getChildren().addAll(this.monthlyOverview, this.expenseDistribution);

        // Component stylesheet
        getStylesheets().add(getClass().getResource("Charts.css").toExternalForm());

        // Events
        TransactionEventBus.getInstance().subscribe(Type.CREATED, this::updateCharts);
        TransactionEventBus.getInstance().subscribe(Type.UPDATED, this::updateCharts);
        TransactionEventBus.getInstance().subscribe(Type.DELETED, this::updateCharts);

    }

    private void fetchData() {
        try {
            YearMonth now = YearMonth.now();
            this.dailyFinancialData = this.finTracker.getMonthlyOverview(now);
            this.expenseData = this.finTracker.getCategoryDistribution(now, TransactionType.EXPENSE);
            this.investmentData = this.finTracker.getCategoryDistribution(now, TransactionType.INVESTMENT);
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
