package ui.javafx.components.transactions;

import java.util.function.Consumer;

import controller.FinTracker;
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
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Type;

public class TransactionRow extends HBox {

    private final Transaction transaction;
    private final FinTracker finTracker = new FinTracker();

    public TransactionRow(Transaction transaction, Consumer<Transaction> onEditTransaction) {

        this.transaction = transaction;

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
        optionsButton.setFocusTraversable(false);

        ContextMenu menu = new ContextMenu();
        MenuItem editItem = new MenuItem("Editar");
        editItem.setOnAction(e -> onEditTransaction.accept(transaction));
        MenuItem removeItem = new MenuItem("Remover");
        removeItem.setOnAction(e -> {
            try {
                finTracker.deleteTransaction(this.transaction);
                TransactionEventBus.getInstance().publish(Type.DELETED, transaction);
            } catch (Exception err) {
                System.err.println(err);
            }
        });
        removeItem.getStyleClass().add("danger-item");
        menu.getItems().addAll(editItem, removeItem);

        optionsButton.setOnAction(e -> menu.show(optionsButton, Side.BOTTOM, 0, 0));

        getChildren().addAll(description, spacer, value, optionsButton);
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

}