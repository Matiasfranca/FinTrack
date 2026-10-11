package ui.javafx.components.transactions;

import model.BankAccount;
import model.Category;
import model.Transaction;
import controller.FinTracker;
import ui.javafx.events.AppEventBus;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TransactionList extends VBox {

    private final FinTracker finTracker = new FinTracker();
    private final VBox rows = new VBox();
    private final Button btnLoadMore = new Button("Carregar mais...");

    private final Map<Integer, String> accountMap = new HashMap<>();
    private final Map<Integer, String> categoryMap = new HashMap<>();

    private int currentOffset = 0;
    private final int PAGE_LIMIT = 50;
    private final Consumer<Transaction> onEditTransaction;

    public TransactionList(Consumer<Transaction> onEditTransaction) {
        this.onEditTransaction = onEditTransaction;

        setSpacing(10);
        getStyleClass().addAll("chart-card", "surface");

        Label title = new Label("Transações");
        title.getStyleClass().addAll("title", "text-primary");

        rows.getStyleClass().add("transaction-rows");
        
        btnLoadMore.getStyleClass().add("btn-load-more");
        btnLoadMore.setMaxWidth(Double.MAX_VALUE);
        btnLoadMore.setOnAction(e -> loadMoreTransactions());

        loadMaps();
        loadMoreTransactions();

        TransactionEventBus.getInstance().subscribe(TransactionEventBus.Type.CREATED, e -> {
            addTransaction(e, onEditTransaction);
        });

        TransactionEventBus.getInstance().subscribe(TransactionEventBus.Type.UPDATED, e -> {
            updateTransaction(e, onEditTransaction);
        });

        TransactionEventBus.getInstance().subscribe(TransactionEventBus.Type.DELETED, e -> {
            removeTransaction(e);
        });

        AppEventBus.getInstance().subscribe(e -> {
            loadMaps();
            loadMoreTransactions();
        });

        getChildren().addAll(title, rows, btnLoadMore);
        getStylesheets().add(getClass().getResource("TransactionList.css").toExternalForm());
    }

    private void loadMaps() {
        accountMap.clear();
        categoryMap.clear();
        
        List<BankAccount> bankAccounts = finTracker.listActiveBankAccounts();
        for (BankAccount acc : bankAccounts) {
            accountMap.put(acc.getId(), acc.getName());
        }

        List<Category> categories = finTracker.listAllCategories();
        for (Category cat : categories) {
            categoryMap.put(cat.getId(), cat.getName());
        }
    }

    private void loadMoreTransactions() {
        rows.getChildren().clear();
        List<Transaction> transactions = finTracker.listGlobalTransactions(PAGE_LIMIT, currentOffset);

        for (Transaction transaction : transactions) {
            rows.getChildren()
                    .add(new TransactionRow(transaction, accountMap, categoryMap, onEditTransaction));
        }

        if (transactions.size() < PAGE_LIMIT) {
            btnLoadMore.setVisible(false);
            btnLoadMore.setManaged(false); 
        } else {
            currentOffset += PAGE_LIMIT;
        }
    }

    private void addTransaction(Event e, Consumer<Transaction> onEditTransaction) {
        Transaction newTransaction = e.transaction();

        if (newTransaction.getCategoryId() != null && !categoryMap.containsKey(newTransaction.getCategoryId())) {
            loadMaps();
        }

        if (newTransaction.getBankAccountId() != null && !accountMap.containsKey(newTransaction.getBankAccountId())) {
            loadMaps();
        }

        TransactionRow newRow = new TransactionRow(newTransaction, accountMap, categoryMap, onEditTransaction);

        int insertPosition = rows.getChildren().size();

        for (int i = 0; i < rows.getChildren().size(); i++) {
            if (rows.getChildren().get(i) instanceof TransactionRow currentRow) {
                Transaction currentTransaction = currentRow.getTransaction();
                if (newTransaction.getDate().isAfter(currentTransaction.getDate()) ||
                        newTransaction.getDate().isEqual(currentTransaction.getDate())) {
                    insertPosition = i;
                    break;
                }
            }
        }
        rows.getChildren().add(insertPosition, newRow);
    }

    private void updateTransaction(Event e, Consumer<Transaction> onEditTransaction) {
        removeTransaction(e);
        addTransaction(e, onEditTransaction);
    }

    private void removeTransaction(Event e) {
        rows.getChildren().removeIf(node -> node instanceof TransactionRow currentRow &&
                currentRow.getTransaction().getId().equals(e.transaction().getId()));
    }
}