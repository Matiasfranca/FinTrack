package ui.javafx.components.transactions;

import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.function.Consumer;

import controller.FinTracker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import model.Transaction;
import model.TransactionType;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Type;
import utils.FormatCurrency;

public class TransactionRow extends VBox {

    private final Transaction transaction;
    private final FinTracker finTracker = new FinTracker();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Atualizado para receber os Dicionários!
    public TransactionRow(Transaction transaction, Map<Integer, String> accountMap, Map<Integer, String> categoryMap, Consumer<Transaction> onEditTransaction) {

        this.transaction = transaction;

        getStyleClass().add("transaction-row-container");
        setSpacing(0);

        HBox mainRow = new HBox();
        mainRow.setAlignment(Pos.CENTER_LEFT);
        mainRow.setSpacing(12);
        mainRow.setPadding(new Insets(12, 12, 12, 12));
        mainRow.getStyleClass().add("transaction-main-row");

        String catName = transaction.getCategoryId() != null ? categoryMap.getOrDefault(transaction.getCategoryId(), "Outros") : "Outros";
        String accName = transaction.getBankAccountId() != null ? accountMap.getOrDefault(transaction.getBankAccountId(), "Conta Removida") : "Conta Desconhecida";

        String descText =  accName + " - " + catName;

        Label description = new Label(descText);
        description.getStyleClass().addAll("text-primary", "transaction-description");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean isIncome = transaction.isReceipt();
        String sign = isIncome ? "+ " : "- ";
        String formatted = sign + (FormatCurrency.formatCurrency(transaction.getValue()));

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

        optionsButton.setOnAction(e -> {
            e.consume();
            menu.show(optionsButton, Side.BOTTOM, 0, 0);
        });

        mainRow.getChildren().addAll(description, spacer, value, optionsButton);

        GridPane detailsBox = new GridPane();
        detailsBox.setHgap(30);
        detailsBox.setVgap(8);
        detailsBox.setPadding(new Insets(0, 12, 12, 12));
        detailsBox.setVisible(false);
        detailsBox.setManaged(false);

        detailsBox.add(createDetailLabel("Data:"), 0, 0);
        detailsBox.add(createDetailValue(transaction.getDate().format(dateFormatter)), 1, 0);

        detailsBox.add(createDetailLabel("Tipo:"), 0, 1);
        detailsBox.add(createDetailValue(translateType(transaction.getTransactionType())), 1, 1);

        detailsBox.add(createDetailLabel("Pagamento:"), 0, 2);
        detailsBox.add(createDetailValue(translatePaymentMethod(transaction.getPaymentMethod())), 1, 2);
        
        detailsBox.add(createDetailLabel("Conta:"), 0, 3);
        detailsBox.add(createDetailValue(accName), 1, 3);

        detailsBox.add(createDetailLabel("Categoria:"), 0, 4);
        detailsBox.add(createDetailValue(catName), 1, 4);

        detailsBox.add(createDetailLabel("Descrição:"), 0, 5);
        detailsBox.add(createDetailValue(transaction.getDescription() == null || transaction.getDescription().isBlank() ? "Não informada" : transaction.getDescription()), 1, 5);

        mainRow.setOnMouseClicked(e -> {
            boolean isExpanded = detailsBox.isVisible();
            detailsBox.setVisible(!isExpanded);
            detailsBox.setManaged(!isExpanded);
        });

        getChildren().addAll(mainRow, detailsBox);
    }

    private Label createDetailLabel(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().addAll("text-secondary", "detail-label");
        return lbl;
    }

    private Label createDetailValue(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().addAll("text-primary", "detail-value");
        return lbl;
    }

    private String translatePaymentMethod(model.PaymentMethod method) {
        if (method == null)
            return "Não informado";
        return switch (method) {
            case PIX -> "Pix";
            case DEBIT_CARD -> "Cartão de débito";
            case CREDIT_CARD -> "Cartão de crédito";
            case CASH -> "Dinheiro";
            case BANK_TRANSFER -> "Transferência";
            case BOLETO -> "Boleto";
        };
    }

    private String translateType(TransactionType type) {
        return switch (type) {
            case INCOME -> "Receita";
            case EXPENSE -> "Despesa";
            case INVESTMENT -> "Investimento";
        };
    }

    public Transaction getTransaction() {
        return this.transaction;
    }
}