package ui.javafx.components.transactions;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import model.Transaction;
import ui.javafx.components.FinancialData;

public class TransactionRow extends HBox {

    public TransactionRow(Transaction transaction) {

        getStyleClass().add("transaction-row");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(12);
        setPadding(new Insets(12, 4, 12, 4));

        Label description = new Label(transaction.getDescription());
        description.getStyleClass().addAll("text-primary", "transaction-description");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean isIncome = transaction.isReceipt();
        String sign = isIncome ? "+ " : "- ";
        String formatted = sign + (transaction.getValue());

        Label value = new Label(formatted);
        value.getStyleClass().addAll(isIncome ? "success" : "danger", "transaction-value");

        Button optionsButton = new Button("⋮");
        optionsButton.getStyleClass().add("options-button");

        ContextMenu menu = new ContextMenu();
        MenuItem editItem = new MenuItem("Editar");
        MenuItem removeItem = new MenuItem("Remover");
        removeItem.getStyleClass().add("danger-item");
        menu.getItems().addAll(editItem, removeItem);

optionsButton.setOnAction(e -> {
    // if (menu.getStylesheets().isEmpty() && optionsButton.getScene() != null) {
    //     menu.getStylesheets().addAll(optionsButton.getScene().getStylesheets());
    // }
    menu.show(optionsButton, Side.BOTTOM, 0, 0);
});
        getChildren().addAll(description, spacer, value, optionsButton);
    }
}