package ui.javafx.components.form;

import controller.FinTracker;
import exceptions.InvalidInput;
import model.BankAccount;
import model.Category;
import model.PaymentMethod;
import model.Transaction;
import model.TransactionType;
import ui.javafx.components.form.bankAccountForm.BankAccountFormCard;
import ui.javafx.components.form.categoryForm.CategoryFormCard;
import ui.javafx.components.form.categoryForm.CategoryOption;
import ui.javafx.events.AppEventBus;
import ui.javafx.events.TransactionEventBus;
import ui.javafx.events.TransactionEventBus.Type;
import utils.CurrencyInputFormatter;
import utils.EnumLabels;
import utils.FormatCurrency;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * The "add / edit transaction" form itself.
 *
 * Only the form's own layout and wiring live here. Currency masking
 * (utils.CurrencyInputFormatter) and enum-to-label translation
 * (utils.EnumLabels) are shared utilities, and creating a brand new
 * bank account or category is delegated to their own small cards
 * (BankAccountFormCard, CategoryFormCard) in sibling packages.
 */
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

    private final Button addBankAccountButton = new Button("+");
    private final Button delBankAccountButton = new Button("-");
    private final Button addCategoryButton = new Button("+");
    private final Button delCategoryButton = new Button("-");

    private final PauseTransition statusTimer = new PauseTransition(Duration.seconds(3));

    public FormTransaction(Runnable onCancelTransaction) {
        this(onCancelTransaction, null);
    }

    public FormTransaction(Runnable onCancelTransaction, Transaction editingTransaction) {

        getStyleClass().add("form-card");
        setSpacing(8);
        setPadding(new Insets(18));
        setPrefWidth(300);
        setMaxWidth(300);

        Label title = new Label("Adicionar transação");
        title.getStyleClass().addAll("text-primary", "form-title");

        CurrencyInputFormatter.attach(valueField);
        valueField.getStyleClass().add("form-input");
        // Clears the red border as soon as the user types something.
        valueField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.isBlank()) {
                valueField.getStyleClass().remove("field-error");
            }
        });

        descriptionArea.setPromptText("Descrição da transação");
        descriptionArea.setPrefRowCount(2);
        descriptionArea.setWrapText(true);
        descriptionArea.getStyleClass().add("form-input");

        datePicker.getStyleClass().add("form-input");
        datePicker.setMaxWidth(Double.MAX_VALUE);

        configureEnumBox(typeBox, EnumLabels::label);
        TransactionType initialType = (editingTransaction != null)
                ? editingTransaction.getTransactionType()
                : TransactionType.INCOME;
        typeBox.setValue(initialType);

        loadCategoriesForType(initialType, editingTransaction);
        toggleAddButtons(initialType);

        typeBox.valueProperty().addListener((obs, oldType, newType) -> {
            if (newType != null && newType != oldType) {
                loadCategoriesForType(newType, null);
                toggleAddButtons(newType);
            }
        });

        configureEnumBox(paymentMethodBox, EnumLabels::label);
        paymentMethodBox.getSelectionModel().selectFirst();

        configureBankAccountBox(editingTransaction);

        // Configure delete and deactivate buttons
        delCategoryButton.setOnAction(e -> {
            CategoryOption option = categoryBox.getValue();

            if (option != null && option.category() != null) {
                deleteCategory(option);
            }
        });
        delBankAccountButton.setOnAction(e -> {
            BankAccount account = bankAccountBox.getValue();

            if (account != null) {
                deactivateBankAccount(account);
            }
        });

        if (editingTransaction != null) {
            valueField.setText(FormatCurrency.formatCurrency(editingTransaction.getValue()).substring(3));
            typeBox.setValue(editingTransaction.getTransactionType());
            paymentMethodBox.setValue(editingTransaction.getPaymentMethod());
            descriptionArea.setText(editingTransaction.getDescription());
            datePicker.setValue(editingTransaction.getDate());
            saveButton.setText("Alterar");
            saveButton.setOnAction(e -> createTransaction(editingTransaction));
        } else {
            typeBox.getSelectionModel().selectFirst();
            paymentMethodBox.getSelectionModel().selectFirst();
            saveButton.setOnAction(e -> createTransaction(null));
        }

        statusLabel.setWrapText(true);
        statusLabel.setMinHeight(Region.USE_PREF_SIZE);
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        saveButton.getStyleClass().addAll("primary", "form-save-button");
        saveButton.setFocusTraversable(false);

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
                formLabel("Conta", true),
                rowWithAddButton(bankAccountBox, addBankAccountButton, delBankAccountButton,
                        this::promptNewBankAccount, () -> bankAccountBox.getValue() != null),
                formLabel("Categoria", false),
                rowWithAddButton(categoryBox, addCategoryButton, delCategoryButton, this::promptNewCategory,
                        () -> categoryBox.getValue() != null && categoryBox.getValue().category() != null
                                && categoryBox.getValue().category().getId() != null),
                statusLabel,
                buttonRow);

        // Shared stylesheet lives one package up (ui.javafx.components.form),
        // since BankAccountFormCard and CategoryFormCard need it too.
        getStylesheets().add(getClass().getResource("/ui/javafx/components/form/FormTransaction.css").toExternalForm());
    }

    private void toggleAddButtons(TransactionType type) {
        boolean isRedemption = (type == TransactionType.REDEMPTION);
        addCategoryButton.setDisable(isRedemption);
    }

    private Label formLabel(String text, boolean required) {
        Label label = new Label(required ? text + " *" : text);
        label.getStyleClass().addAll("text-secondary", "form-label");
        return label;
    }

    private HBox rowWithAddButton(ComboBox<?> comboBox, Button addButton, Button delButton, Runnable onAdd,
            java.util.function.Supplier<Boolean> canDelete) {

        comboBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(comboBox, Priority.ALWAYS);
        comboBox.getStyleClass().add("form-input");

        addButton.getStyleClass().add("form-add-button");
        delButton.getStyleClass().add("form-del-button");
        addButton.setOnAction(e -> onAdd.run());

        Runnable updateDeleteVisibility = () -> {
            boolean visible = canDelete.get();
            delButton.setVisible(visible);
            delButton.setManaged(visible);
        };

        updateDeleteVisibility.run();
        comboBox.valueProperty().addListener((obs, oldV, newV) -> updateDeleteVisibility.run());

        return new HBox(8, comboBox, addButton, delButton);
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

    private void configureBankAccountBox(Transaction editingTransaction) {
        bankAccountBox.getStyleClass().add("form-input");

        bankAccountBox.setButtonCell(new ListCell<>() {
            {
                setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
            }

            @Override
            protected void updateItem(BankAccount item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().remove("combo-placeholder");

                if (empty || item == null) {
                    setText("Nenhuma conta");
                    getStyleClass().add("combo-placeholder");
                } else {
                    setText(item.toString());
                }
            }
        });

        bankAccountBox.valueProperty()
                .addListener((obs, oldV, newV) -> bankAccountBox.getStyleClass().remove("field-error"));

        Task<List<BankAccount>> task = new Task<>() {
            @Override
            protected List<BankAccount> call() {
                return finTracker.listActiveBankAccounts();
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            bankAccountBox.setPromptText("Crie uma conta");
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

    private void loadCategoriesForType(TransactionType selectedType, Transaction editingTransaction) {
        categoryBox.getItems().clear();

        TransactionType queryType = selectedType;

        Task<List<Category>> task = new Task<>() {
            @Override
            protected List<Category> call() {
                return finTracker.listCategoriesByTransactionType(queryType);
            }
        };

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            if (task.getValue() != null) {
                if (task.getValue().isEmpty() && queryType != TransactionType.REDEMPTION) {
                    categoryBox.getItems().add(new CategoryOption(null, "Outros"));
                }
                task.getValue().forEach(cat -> categoryBox.getItems().add(new CategoryOption(cat, cat.getName())));
            }

            if (editingTransaction != null && editingTransaction.getCategoryId() != null) {
                categoryBox.getItems().stream()
                        .filter(opt -> opt.category() != null
                                && editingTransaction.getCategoryId().equals(opt.category().getId()))
                        .findFirst()
                        .ifPresentOrElse(
                                categoryBox.getSelectionModel()::select,
                                () -> categoryBox.getSelectionModel().selectFirst());
            } else {
                categoryBox.getSelectionModel().selectFirst();
            }

            if (selectedType == TransactionType.REDEMPTION && categoryBox.getItems().isEmpty()) {
                saveButton.setDisable(true);
                showStatus("Sem investimento para resgatar.", false);
            } else {
                saveButton.setDisable(false);
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
                    task.setOnSucceeded(e -> Platform.runLater(() -> {
                        Category newCategory = task.getValue();
                        CategoryOption option = new CategoryOption(newCategory, newCategory.getName());

                        categoryBox.getItems().add(option);
                        categoryBox.getSelectionModel().select(option);

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
        // Blocking prerequisite: checked first so the user never has to fix
        // other fields only to be stopped by the missing account afterwards.

        if (valueField.getText().isBlank()) {
            if (!valueField.getStyleClass().contains("field-error")) {
                valueField.getStyleClass().add("field-error");
            }
            showStatus("Informe um valor válido.", false);
            return;
        }

        if (bankAccountBox.getValue() == null) {
            if (!bankAccountBox.getStyleClass().contains("field-error")) {
                bankAccountBox.getStyleClass().add("field-error");
            }
            showStatus(bankAccountBox.getItems().isEmpty()
                    ? "Crie uma conta no botão +."
                    : "Selecione uma conta.", false);
            return;
        }

        Transaction transaction;

        Integer selectedBankAccountId = bankAccountBox.getValue() != null ? bankAccountBox.getValue().getId() : null;

        Category selectedCategoryObj = categoryBox.getValue() != null ? categoryBox.getValue().category() : null;
        Integer selectedCategoryId = selectedCategoryObj != null ? selectedCategoryObj.getId() : null;

        try {
            BigDecimal val = CurrencyInputFormatter.parse(valueField.getText());

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
            if (!valueField.getStyleClass().contains("field-error")) {
                valueField.getStyleClass().add("field-error");
            }
            showStatus("Erro ao salvar.", false);
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
            showStatus("Transação salva com sucesso.", true);
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            saveButton.setDisable(false);
            showStatus("Não foi possível salvar.", false);
        }));

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void deactivateBankAccount(BankAccount account) {
        try {
            finTracker.deactivateBankAccount(account.getId());

            // Clear first so the combo doesn't keep a stale value, then remove.
            bankAccountBox.getSelectionModel().clearSelection();
            bankAccountBox.getItems().remove(account);
            bankAccountBox.getSelectionModel().selectFirst();

            showStatus("Conta desativada com sucesso.", true);
            AppEventBus.getInstance().publish(AppEventBus.Type.DATA_CHANGED);
        } catch (InvalidInput e) {
            showStatus("Erro ao desativar conta", false);
        }
    }

    private void deleteCategory(CategoryOption option) {
        try {
            finTracker.deleteCategory(option.category().getId());

            categoryBox.getSelectionModel().clearSelection();
            categoryBox.getItems().remove(option);

            // Keep the default "Outros" fallback when no real category is left
            // (except for redemptions, which must always have an investment).
            if (categoryBox.getItems().isEmpty() && typeBox.getValue() != TransactionType.REDEMPTION) {
                categoryBox.getItems().add(new CategoryOption(null, "Outros"));
            }
            categoryBox.getSelectionModel().selectFirst();

            showStatus("Categoria excluída com sucesso.", true);
            AppEventBus.getInstance().publish(AppEventBus.Type.DATA_CHANGED);
        } catch (InvalidInput e) {
            showStatus("Erro ao excluir categoria", false);
        }
    }

    private void showStatus(String message, boolean success) {

        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("success", "danger");
        statusLabel.getStyleClass().add(success ? "success" : "danger");
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);

        statusTimer.stop();
        statusTimer.setOnFinished(e -> {
            statusLabel.setVisible(false);
            statusLabel.setManaged(false);
        });
        statusTimer.playFromStart();
    }
}