package ui.javafx.components.transactions;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Transaction;
import ui.javafx.components.FinancialData;

import java.util.List;

public class TransactionList extends VBox {

    public TransactionList() {

        setSpacing(10);
        getStyleClass().addAll("chart-card", "surface");

        Label title = new Label("Transações");
        title.getStyleClass().addAll("title", "text-primary");

        VBox rows = new VBox();
        rows.getStyleClass().add("transaction-rows");

        List<Transaction> transactions = FinancialData.sampleTransactions();
        for (Transaction transaction : transactions) {
            rows.getChildren().add(new TransactionRow(transaction));
        }

        getChildren().addAll(title, rows);

        getStylesheets().add(getClass().getResource("TransactionList.css").toExternalForm());
    }
}