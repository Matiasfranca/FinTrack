package ui.javafx.components.charts;

import java.time.YearMonth;
import java.util.List;

import controller.FinTracker;
import javafx.scene.layout.HBox;
import model.TransactionType;
import model.dto.DailyFinancialData;
import ui.javafx.components.charts.expenseDistribution.ExpenseDistribution;
import ui.javafx.components.charts.monthlyOverview.MonthlyOverview;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Event;
import ui.javafx.events.TransactionEventBus.Type;

public class Charts extends HBox {

    private final FinTracker finTracker = new FinTracker();

    private List<DailyFinancialData> dailyFinancialData;
    MonthlyOverview monthlyOverview;

    public Charts() {

        try {
            this.dailyFinancialData = finTracker.getMonthlyOverview(YearMonth.now());
            finTracker.getCategoryDistribution(YearMonth.now(), TransactionType.EXPENSE);
            finTracker.getCategoryDistribution(YearMonth.now(), TransactionType.INVESTMENT);
        } catch (Exception e) {

        }

        setSpacing(40);

        this.monthlyOverview = new MonthlyOverview(dailyFinancialData);

        getChildren().addAll(monthlyOverview, new ExpenseDistribution());

        // Component stylesheet
        getStylesheets().add(getClass().getResource("Charts.css").toExternalForm());

        // Events
        TransactionEventBus.getInstance().subscribe(Type.CREATED, this::updateCharts);
        TransactionEventBus.getInstance().subscribe(Type.UPDATED, this::updateCharts);
        TransactionEventBus.getInstance().subscribe(Type.DELETED, this::updateCharts);

    }

    private void updateCharts(Event e) {
        try {
            this.dailyFinancialData = finTracker.getMonthlyOverview(YearMonth.now());
            this.monthlyOverview.refreshData(dailyFinancialData);
            finTracker.getCategoryDistribution(YearMonth.now(), TransactionType.EXPENSE);
            finTracker.getCategoryDistribution(YearMonth.now(), TransactionType.INVESTMENT);
        } catch (Exception err) {

        }
    }

}
