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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import model.BankAccount;
import model.Category;
import model.PaymentMethod;
import model.Transaction;
import model.TransactionType;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Type;
import utils.FormatCurrency;

public class FormTransaction extends VBox {

    private Node activeChildCard;

    private final FinTracker finTracker = new FinTracker();

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
        this(onCancelTransaction, null);
    }

    public FormTransaction(Runnable onCancelTransaction, Transaction editingTransaction) {

        getStyleClass().add("form-card");
        setSpacing(12);
        setPadding(new Insets(18));
        setPrefWidth(300);
        setMaxWidth(300);

        Label title = new Label("Adicionar transação");
        title.getStyleClass().addAll("text-primary", "form-title");

        this.configureValueField(this.valueField);

        this.descriptionArea.setPromptText("Descrição da transação");
        this.descriptionArea.setPrefRowCount(2);
        this.descriptionArea.setWrapText(true);
        this.descriptionArea.getStyleClass().add("form-input");

        this.datePicker.getStyleClass().add("form-input");
        this.datePicker.setMaxWidth(Double.MAX_VALUE);

        this.configureEnumBox(this.typeBox, this::translateType);
        this.typeBox.getSelectionModel().selectFirst();

        this.configureEnumBox(this.paymentMethodBox, this::translatePaymentMethod);
        this.paymentMethodBox.getSelectionModel().selectFirst();

        this.configureBankAccountBox(editingTransaction);
        this.configureCategoryOptionBox(editingTransaction);

        if (editingTransaction != null) {
            this.valueField.setText(FormatCurrency.formatCurrency(editingTransaction.getValue()).substring(3));
            this.typeBox.setValue(editingTransaction.getTransactionType());
            this.paymentMethodBox.setValue(editingTransaction.getPaymentMethod());
            this.descriptionArea.setText(editingTransaction.getDescription());
            this.datePicker.setValue(editingTransaction.getDate());
            this.saveButton.setText("Alterar");
            this.saveButton.setOnAction(e -> this.createTransaction(editingTransaction));
        } else {
            this.typeBox.getSelectionModel().selectFirst();
            this.paymentMethodBox.getSelectionModel().selectFirst();
            this.saveButton.setOnAction(e -> this.createTransaction(null));
        }

        this.statusLabel.setWrapText(true);
        this.statusLabel.setVisible(false);
        this.statusLabel.setManaged(false);

        this.saveButton.getStyleClass().addAll("primary", "form-save-button");
        this.saveButton.setFocusTraversable(false);

        this.cancelButton.getStyleClass().add("form-cancel-button");
        this.cancelButton.setOnAction(e -> onCancelTransaction.run());

        HBox buttonRow = new HBox(10, cancelButton, saveButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(
                title,
                this.formLabel("Valor", true), this.valueField,
                this.formLabel("Tipo", true), this.typeBox,
                this.formLabel("Forma de pagamento", true), this.paymentMethodBox,
                this.formLabel("Descrição", false), this.descriptionArea,
                this.formLabel("Data", false), this.datePicker,
                this.formLabel("Conta", true), this.rowWithAddButton(bankAccountBox, this::promptNewBankAccount),
                this.formLabel("Categoria", false), this.rowWithAddButton(categoryBox, this::promptNewCategory),
                this.statusLabel,
                buttonRow);

        getStylesheets().add(getClass().getResource("FormTransaction.css").toExternalForm());
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

    private void configureValueField(TextField valueField) {
        java.util.function.UnaryOperator<TextFormatter.Change> filter = change -> {
            String text = change.getControlNewText();
            if (text.matches("[0-9.,]*")) {
                return change;
            }
            return null;
        };

        valueField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                try {
                    String texto = valueField.getText();
                    if (texto == null || texto.isEmpty())
                        return;

                    String textoLimpo = texto.replace(".", "").replace(",", ".");
                    BigDecimal valorDigitado = new BigDecimal(textoLimpo);

                    java.text.NumberFormat format = java.text.NumberFormat
                            .getNumberInstance(java.util.Locale.of("pt", "BR"));
                    format.setMinimumFractionDigits(2);
                    format.setMaximumFractionDigits(2);

                    valueField.setText(format.format(valorDigitado));

                } catch (NumberFormatException e) {

                }
            }
        });

        valueField.setTextFormatter(new TextFormatter<>(filter));
        valueField.setPromptText("0,00");
        valueField.getStyleClass().add("form-input");
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
            case REDEMPTION -> "Resgate";
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

    private void configureBankAccountBox(Transaction editingTransaction) {
        bankAccountBox.getStyleClass().add("form-input");

        Task<List<BankAccount>> task = new Task<>() {
            @Override
            protected List<BankAccount> call() {
                return finTracker.listActiveBankAccounts();
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            bankAccountBox.getItems().setAll(task.getValue());

            if (editingTransaction != null) {
                bankAccountBox.getItems().stream()
                        .filter(acc -> acc.getId().equals(editingTransaction.getBankAccountId()))
                        .findFirst()
                        .ifPresent(bankAccountBox.getSelectionModel()::select);
            } else {
                bankAccountBox.getSelectionModel().selectFirst();
            }
        }));

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void configureCategoryOptionBox(Transaction editingTransaction) {
        categoryBox.getStyleClass().add("form-input");
        categoryBox.getItems().add(new CategoryOption(null, "Outros"));

        Task<List<Category>> task = new Task<>() {
            @Override
            protected List<Category> call() {
                return finTracker.listAllCategories();
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            task.getValue().forEach(cat -> categoryBox.getItems().add(new CategoryOption(cat, cat.getName())));

            if (editingTransaction != null && editingTransaction.getCategoryId() != null) {
                categoryBox.getItems().stream()
                        .filter(opt -> opt.category() != null
                                && opt.category().getId().equals(editingTransaction.getCategoryId()))
                        .findFirst()
                        .ifPresent(categoryBox.getSelectionModel()::select);
            } else {
                categoryBox.getSelectionModel().selectFirst();
            }
        }));

        task.setOnFailed(e -> task.getException().printStackTrace());

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void promptNewCategory() {

        CategoryFormCard[] cardRef = new CategoryFormCard[1];

        cardRef[0] = new CategoryFormCard(
                name -> {
                    Task<Category> task = new Task<>() {
                        @Override
                        protected Category call() {
                            return finTracker.getOrCreateCategory(new Category(name, null));
                        }
                    };
                    task.setOnSucceeded(e -> {
                        CategoryOption option = new CategoryOption(task.getValue(), task.getValue().getName());
                        categoryBox.getItems().add(option);
                        categoryBox.getSelectionModel().select(option);
                        closeChildCard(cardRef[0]);
                    });
                    task.setOnFailed(e -> task.getException().printStackTrace());
                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                },
                () -> closeChildCard(cardRef[0]));

        showChildCard(cardRef[0]);
    }

    private void promptNewBankAccount() {

        BankAccountFormCard[] cardRef = new BankAccountFormCard[1];

        cardRef[0] = new BankAccountFormCard(
                (name, type) -> {
                    Task<BankAccount> task = new Task<>() {
                        @Override
                        protected BankAccount call() {
                            return finTracker.getOrCreateBankAccount(new BankAccount(name, type));
                        }
                    };
                    task.setOnSucceeded(e -> Platform.runLater(() -> {
                        bankAccountBox.getItems().add(task.getValue());
                        bankAccountBox.getSelectionModel().select(task.getValue());
                        closeChildCard(cardRef[0]);
                    }));
                    task.setOnFailed(e -> task.getException().printStackTrace());
                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                },
                () -> closeChildCard(cardRef[0]));

        showChildCard(cardRef[0]);
    }

    private void showChildCard(Node card) {
        if (getParent() instanceof StackPane parent) {

            if (activeChildCard != null) {
                parent.getChildren().remove(activeChildCard);
            }

            StackPane.setAlignment(card, Pos.CENTER);

            parent.getChildren().add(card);

            activeChildCard = card;
        }
    }

    private void closeChildCard(Node card) {
        if (getParent() instanceof StackPane parent) {
            parent.getChildren().remove(card);

            if (activeChildCard == card) {
                activeChildCard = null;
            }
        }
    }

    private void createTransaction(Transaction editingTransaction) {
        Transaction transaction;

        Integer selectedBankAccountId = bankAccountBox.getValue() != null ? bankAccountBox.getValue().getId() : null;

        Category selectedCategoryObj = categoryBox.getValue() != null ? categoryBox.getValue().category() : null;
        Integer selectedCategoryId = selectedCategoryObj != null ? selectedCategoryObj.getId() : null;

        try {
            BigDecimal val = getValue();

            if (editingTransaction != null) {
                transaction = new Transaction(
                        editingTransaction.getId(),
                        descriptionArea.getText(),
                        val,
                        typeBox.getValue(),
                        paymentMethodBox.getValue(),
                        datePicker.getValue(),
                        selectedBankAccountId,
                        selectedCategoryId);
            } else {
                transaction = new Transaction(
                        descriptionArea.getText(),
                        val,
                        typeBox.getValue(),
                        paymentMethodBox.getValue(),
                        datePicker.getValue());
            }
        } catch (NumberFormatException e) {
            showStatus("Informe um valor válido.", false);
            return;
        }

        if (bankAccountBox.getValue() == null) {
            showStatus("Selecione uma conta.", false);
            return;
        }

        saveButton.setDisable(true);

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (editingTransaction != null) {
                    finTracker.updateTransaction(transaction, bankAccountBox.getValue(), selectedCategoryObj);
                } else {
                    finTracker.addTransaction(transaction, bankAccountBox.getValue(), selectedCategoryObj);
                }
                return null;
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            if (editingTransaction != null) {
                TransactionEventBus.getInstance().publish(Type.UPDATED, transaction);
            } else {
                TransactionEventBus.getInstance().publish(Type.CREATED, transaction);
            }
            saveButton.setDisable(false);
            clearFields();
            showStatus("Transação salva com sucesso.", true);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            saveButton.setDisable(false);
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
        String text = valueField.getText().trim();

        String textoSemPontos = text.replace(".", "");
        String textoLimpo = textoSemPontos.replace(",", ".");

        return new BigDecimal(textoLimpo);
    }
}