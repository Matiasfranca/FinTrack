package ui.javafx.components.charts;

import javafx.scene.layout.HBox;
import ui.javafx.components.charts.expenseDistribution.ExpenseDistribution;
import ui.javafx.components.charts.monthlyOverview.MonthlyOverview;

public class Charts extends HBox {
    public Charts() {

        setSpacing(40);

        getChildren().addAll(new MonthlyOverview(), new ExpenseDistribution());

        // Component stylesheet
        getStylesheets().add(getClass().getResource("Charts.css").toExternalForm());
    }

}
