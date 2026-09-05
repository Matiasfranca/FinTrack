package ui.javafx.components.transactions;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.BankAccount;
import model.Category;
import model.Transaction;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Event;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import controller.FinTracker;
import exceptions.InvalidInput;

public class TransactionList extends VBox {

    private final FinTracker finTracker = new FinTracker();
    private final VBox rows = new VBox();

    private final Map<Integer, String> accountMap = new HashMap<>();
    private final Map<Integer, String> categoryMap = new HashMap<>();

    public TransactionList(Consumer<Transaction> onEditTransaction) {

        setSpacing(10);
        getStyleClass().addAll("chart-card", "surface");

        Label title = new Label("Transações");
        title.getStyleClass().addAll("title", "text-primary");

        this.rows.getStyleClass().add("transaction-rows");

        try {
            List<Transaction> transactions = finTracker.listTransactionsByMonth(YearMonth.from(LocalDate.now()));
            List<BankAccount> bankAccounts = finTracker.listActiveBankAccounts();

            for (BankAccount acc : bankAccounts) {
                accountMap.put(acc.getId(), acc.getName());
            }

            List<Category> categories = finTracker.listAllCategories();
            for (Category cat : categories) {
                categoryMap.put(cat.getId(), cat.getName());
            }

            for (Transaction transaction : transactions) {
                this.rows.getChildren()
                        .add(new TransactionRow(transaction, accountMap, categoryMap, onEditTransaction));
            }

        } catch (InvalidInput e) {
            e.printStackTrace();
        }

        // Os eventos continuam os MESMOS!
        TransactionEventBus.getInstance().subscribe(TransactionEventBus.Type.CREATED, e -> {
            this.addList(e, onEditTransaction);
        });

        TransactionEventBus.getInstance().subscribe(TransactionEventBus.Type.UPDATED, e -> {
            this.updateList(e, onEditTransaction);
        });

        TransactionEventBus.getInstance().subscribe(TransactionEventBus.Type.DELETED, e -> {
            this.removeList(e);
        });

        getChildren().addAll(title, this.rows);
        getStylesheets().add(getClass().getResource("TransactionList.css").toExternalForm());
    }

    private void addList(Event e, Consumer<Transaction> onEditTransaction) {
        Transaction novaTransacao = e.transaction();

        if (novaTransacao.getCategoryId() != null && !categoryMap.containsKey(novaTransacao.getCategoryId())) {
            List<Category> categories = finTracker.listAllCategories();
            for (Category cat : categories) {
                categoryMap.put(cat.getId(), cat.getName());
            }
        }

        if (novaTransacao.getBankAccountId() != null && !accountMap.containsKey(novaTransacao.getBankAccountId())) {
            List<BankAccount> bankAccounts = finTracker.listActiveBankAccounts();
            for (BankAccount acc : bankAccounts) {
                accountMap.put(acc.getId(), acc.getName());
            }
        }
        
        TransactionRow linhaNova = new TransactionRow(novaTransacao, accountMap, categoryMap, onEditTransaction);

        int posicaoParaInserir = this.rows.getChildren().size();

        for (int i = 0; i < this.rows.getChildren().size(); i++) {
            if (this.rows.getChildren().get(i) instanceof TransactionRow linhaAtual) {
                Transaction transacaoAtual = linhaAtual.getTransaction();
                if (novaTransacao.getDate().isAfter(transacaoAtual.getDate()) ||
                        novaTransacao.getDate().isEqual(transacaoAtual.getDate())) {
                    posicaoParaInserir = i;
                    break;
                }
            }
        }
        this.rows.getChildren().add(posicaoParaInserir, linhaNova);
    }

    private void updateList(Event e, Consumer<Transaction> onEditTransaction) {
        this.removeList(e);
        this.addList(e, onEditTransaction);
    }

    private void removeList(Event e) {
        this.rows.getChildren().removeIf(node -> node instanceof TransactionRow linhaAtual &&
                linhaAtual.getTransaction().getId().equals(e.transaction().getId()));
    }
}