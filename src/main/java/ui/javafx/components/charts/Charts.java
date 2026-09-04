package ui.javafx.components.charts;

import java.time.YearMonth;
import java.util.List;

import controller.FinTracker;
import javafx.scene.layout.HBox;
import model.TransactionType;
import model.dto.DailyFinancialData;
import ui.javafx.components.charts.expenseDistribution.ExpenseDistribution;
import ui.javafx.components.charts.monthlyOverview.MonthlyOverview;

public class Charts extends HBox {

    private final FinTracker finTracker = new FinTracker();

    private List<DailyFinancialData> getMonthlyOverview;

    public Charts() {

        try {
            this.getMonthlyOverview = finTracker.getMonthlyOverview(YearMonth.now());
            finTracker.getCategoryDistribution(YearMonth.now(), TransactionType.EXPENSE);
            finTracker.getCategoryDistribution(YearMonth.now(), TransactionType.INVESTMENT);
        } catch (Exception e) {

        }

        setSpacing(40);

        getChildren().addAll(new MonthlyOverview(this.getMonthlyOverview), new ExpenseDistribution());

        // Component stylesheet
        getStylesheets().add(getClass().getResource("Charts.css").toExternalForm());
    }

}
