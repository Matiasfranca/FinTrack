package ui.javafx.components;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import ui.javafx.components.charts.Charts;
import ui.javafx.components.financialCards.FinancialCards;
import ui.javafx.components.header.Header;
import ui.javafx.components.transactions.TransactionList;

public class Components extends VBox {

    private static final double CONTENT_WIDTH = 1200;

    public Components(Runnable onAddTransaction) {

        setSpacing(30);
        setMaxWidth(CONTENT_WIDTH);
        setPrefWidth(CONTENT_WIDTH);
        setAlignment(Pos.TOP_CENTER);

        getChildren().addAll(
                new Header(onAddTransaction),
                new FinancialCards(),
                new Charts(),
                new TransactionList());
    }
}