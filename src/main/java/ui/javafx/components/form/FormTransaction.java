package ui.javafx.components.form;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import controller.FinTracker;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;
import javafx.util.StringConverter;
import model.BankAccount;
import model.BankAccountType;
import model.Category;
import model.PaymentMethod;
import model.Transaction;
import model.TransactionType;

public class FormTransaction extends VBox {

    private final FinTracker finTracker = new FinTracker();

    private Transaction editingTransaction;

    private final TextField valueField = new TextField();
    private final ComboBox<TransactionType> typeBox = new ComboBox<>(
            FXCollections.observableArrayList(TransactionType.values()));
    private final ComboBox<PaymentMethod> paymentMethodBox = new ComboBox<>(
            FXCollections.observableArrayList(PaymentMethod.values()));
    private final TextArea descriptionArea = new TextArea();
    private final DatePicker datePicker = new DatePicker(LocalDate.now());
    private final ComboBox<BankAccount> bankAccountBox = new ComboBox<>();
    private final ComboBox<CategoryOption> categoryBox = new ComboBox<>();
    private final Button saveButton = new Button("Salvar");
    private final Button cancelButton = new Button("Cancelar");
    private final Label statusLabel = new Label();

    public FormTransaction(Runnable onCancelTransaction) {

        getStyleClass().add("form-card");
        setSpacing(12);
        setPadding(new Insets(18));
        setPrefWidth(300);
        setMaxWidth(300);

        Label title = new Label("Adicionar transação");
        title.getStyleClass().addAll("text-primary", "form-title");

        valueField.setPromptText("0,00");
        valueField.getStyleClass().add("form-input");

        descriptionArea.setPromptText("Descrição da transação");
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setWrapText(true);
        descriptionArea.getStyleClass().add("form-input");

        datePicker.getStyleClass().add("form-input");
        datePicker.setMaxWidth(Double.MAX_VALUE);

        configureEnumBox(typeBox, this::translateType);
        typeBox.getSelectionModel().selectFirst();

        configureEnumBox(paymentMethodBox, this::translatePaymentMethod);
        paymentMethodBox.getSelectionModel().selectFirst();

        configureBankAccountBox();
        configureCategoryOptionBox();

        statusLabel.setWrapText(true);
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        saveButton.getStyleClass().addAll("primary", "form-save-button");
        saveButton.setOnAction(e -> createTransaction());

        cancelButton.getStyleClass().add("form-cancel-button");
        cancelButton.setOnAction(e -> onCancelTransaction.run());

        HBox buttonRow = new HBox(10, cancelButton, saveButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(
                title,
                formLabel("Valor", true), valueField,
                formLabel("Tipo", true), typeBox,
                formLabel("Forma de pagamento", true), paymentMethodBox,
                formLabel("Descrição", false), descriptionArea,
                formLabel("Data", false), datePicker,
                formLabel("Conta", true), rowWithAddButton(bankAccountBox, this::promptNewBankAccount),
                formLabel("Categoria", false), rowWithAddButton(categoryBox, this::promptNewCategory),
                statusLabel,
                buttonRow);

        getStylesheets().add(getClass().getResource("FormTransaction.css").toExternalForm());
    }

    public FormTransaction(Runnable onCancelTransaction, Transaction editingTransaction) {

        this(onCancelTransaction); // reaproveita toda a construção normal

        this.editingTransaction = editingTransaction;

        if (editingTransaction != null) {

            Label title = (Label) getChildren().get(0);
            title.setText("Editar transação");

            valueField.setText(editingTransaction.getValue().toString().replace(".", ","));
            typeBox.setValue(editingTransaction.getTransactionType());
            paymentMethodBox.setValue(editingTransaction.getPaymentMethod());
            descriptionArea.setText(editingTransaction.getDescription());
            datePicker.setValue(editingTransaction.getDate());
            saveButton.setText("Salvar alterações");

            // Conta/categoria carregam de forma assíncrona (Task) — a seleção
            // só pode acontecer DEPOIS que a lista chegar, senão o item ainda
            // não existe no ComboBox pra ser selecionado.
            bankAccountBox.getItems().addListener((javafx.collections.ListChangeListener<BankAccount>) change -> {
                bankAccountBox.getItems().stream()
                        .filter(account -> account.getId().equals(editingTransaction.getBankAccountId()))
                        .findFirst()
                        .ifPresent(bankAccountBox.getSelectionModel()::select);
            });

            categoryBox.getItems().addListener((javafx.collections.ListChangeListener<CategoryOption>) change -> {
                categoryBox.getItems().stream()
                        .filter(option -> option.category() != null
                                && option.category().getId().equals(editingTransaction.getCategoryId()))
                        .findFirst()
                        .ifPresent(categoryBox.getSelectionModel()::select);
            });
        }
    }

    private Label formLabel(String text, boolean required) {
        Label label = new Label(required ? text + " *" : text);
        label.getStyleClass().addAll("text-secondary", "form-label");
        return label;
    }

    private HBox rowWithAddButton(ComboBox<?> comboBox, Runnable onAdd) {

        comboBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(comboBox, Priority.ALWAYS);
        comboBox.getStyleClass().add("form-input");

        Button addButton = new Button("+");
        addButton.getStyleClass().add("form-add-button");
        addButton.setOnAction(e -> onAdd.run());

        return new HBox(8, comboBox, addButton);
    }

    private <T> void configureEnumBox(ComboBox<T> box, java.util.function.Function<T, String> translator) {
        box.setConverter(new StringConverter<>() {
            @Override
            public String toString(T value) {
                return value == null ? "" : translator.apply(value);
            }

            @Override
            public T fromString(String string) {
                return null;
            }
        });
        box.getStyleClass().add("form-input");
        box.setMaxWidth(Double.MAX_VALUE);
    }

    private String translateType(TransactionType type) {
        return switch (type) {
            case INCOME -> "Receita";
            case EXPENSE -> "Despesa";
            case INVESTMENT -> "Investimento";
        };
    }

    private String translatePaymentMethod(PaymentMethod method) {
        return switch (method) {
            case PIX -> "Pix";
            case DEBIT_CARD -> "Cartão de débito";
            case CREDIT_CARD -> "Cartão de crédito";
            case CASH -> "Dinheiro";
            case BANK_TRANSFER -> "Transferência";
            case BOLETO -> "Boleto";
        };
    }

    private void configureBankAccountBox() {

        bankAccountBox.getStyleClass().add("form-input");

        Task<List<BankAccount>> task = new Task<>() {
            @Override
            protected List<BankAccount> call() {
                return finTracker.listActiveBankAccounts();
            }
        };

        task.setOnSucceeded(e -> {
            bankAccountBox.getItems().setAll(task.getValue());
            bankAccountBox.getSelectionModel().selectFirst();
        });

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void configureCategoryOptionBox() {

        categoryBox.getStyleClass().add("form-input");
        categoryBox.getItems().add(new CategoryOption(null, "Outros"));
        categoryBox.getSelectionModel().selectFirst();

        Task<List<Category>> task = new Task<>() {
            @Override
            protected List<Category> call() {
                return finTracker.listAllCategories();
            }
        };

        task.setOnSucceeded(e -> task.getValue()
                .forEach(category -> categoryBox.getItems().add(new CategoryOption(category, category.getName()))));

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void promptNewCategory() {

        CategoryFormCard[] cardRef = new CategoryFormCard[1];

        cardRef[0] = new CategoryFormCard(
                name -> {
                    // Task<Category> task = new Task<>() {
                    // @Override
                    // protected Category call() {
                    // return finTracker.createCategory(name); // TODO: confirmar nome do método
                    // }
                    // };
                    // task.setOnSucceeded(e -> Platform.runLater(() -> {
                    // Category created = task.getValue();
                    // CategoryOption option = new CategoryOption(created, created.getName());
                    // categoryBox.getItems().add(option);
                    // categoryBox.getSelectionModel().select(option);
                    // closeChildCard(cardRef[0]);
                    // }));
                    // task.setOnFailed(e -> task.getException().printStackTrace());
                    // Thread thread = new Thread(task);
                    // thread.setDaemon(true);
                    // thread.start();
                },
                () -> closeChildCard(cardRef[0]));

        showChildCard(cardRef[0]);
    }

    private void promptNewBankAccount() {

        BankAccountFormCard[] cardRef = new BankAccountFormCard[1];

        cardRef[0] = new BankAccountFormCard(
                (name, type) -> {
                    // Task<BankAccount> task = new Task<>() {
                    // @Override
                    // protected BankAccount call() {
                    // return finTracker.createBankAccount(name, type); // TODO: confirmar nome do
                    // método
                    // }
                    // };
                    // task.setOnSucceeded(e -> Platform.runLater(() -> {
                    // BankAccount created = task.getValue();
                    // bankAccountBox.getItems().add(created);
                    // bankAccountBox.getSelectionModel().select(created);
                    // closeChildCard(cardRef[0]);
                    // }));
                    // task.setOnFailed(e -> task.getException().printStackTrace());
                    // Thread thread = new Thread(task);
                    // thread.setDaemon(true);
                    // thread.start();
                },
                () -> closeChildCard(cardRef[0]));

        showChildCard(cardRef[0]);
    }

    private void showChildCard(javafx.scene.Node card) {
        if (getParent() instanceof javafx.scene.layout.StackPane parent) {
            javafx.scene.layout.StackPane.setAlignment(card, Pos.CENTER);
            parent.getChildren().add(card);
        }
    }

    private void closeChildCard(javafx.scene.Node card) {
        if (getParent() instanceof javafx.scene.layout.StackPane parent) {
            parent.getChildren().remove(card);
        }
    }

    private void createTransaction() {

        BigDecimal value;
        try {
            value = getValue();
        } catch (NumberFormatException e) {
            showStatus("Informe um valor válido.", false);
            return; // valor inválido — mantém tudo como está, nem chega a tentar salvar
        }

        if (bankAccountBox.getValue() == null) {
            showStatus("Selecione uma conta.", false);
            return;
        }

        saveButton.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // TODO: substituir pela chamada real, quando o método existir:
                // finTracker.addTransaction(value, typeBox.getValue(),
                // paymentMethodBox.getValue(),
                // descriptionArea.getText(), datePicker.getValue(),
                // bankAccountBox.getValue(), categoryBox.getValue().category());
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            saveButton.setDisable(false);
            clearFields();
            showStatus("Transação salva com sucesso.", true);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            saveButton.setDisable(false);
            // Erro: mantém os campos exatamente como o usuário deixou.
            showStatus("Não foi possível salvar. Tente novamente.", false);
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void clearFields() {
        valueField.clear();
        typeBox.getSelectionModel().selectFirst();
        paymentMethodBox.getSelectionModel().selectFirst();
        descriptionArea.clear();
        datePicker.setValue(LocalDate.now());
        bankAccountBox.getSelectionModel().selectFirst();
        categoryBox.getSelectionModel().selectFirst();
    }

    private void showStatus(String message, boolean success) {

        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("success", "danger");
        statusLabel.getStyleClass().add(success ? "success" : "danger");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);

        PauseTransition pause = new PauseTransition(Duration.seconds(3));
        pause.setOnFinished(e -> {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        });
        pause.play();
    }

    private BigDecimal getValue() {
        String text = valueField.getText().trim().replace(",", ".");
        return new BigDecimal(text);
    }
}