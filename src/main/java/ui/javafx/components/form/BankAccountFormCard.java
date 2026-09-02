package ui.javafx.components.form;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.BankAccountType;

import java.util.function.BiConsumer;

public class BankAccountFormCard extends VBox {

    public BankAccountFormCard(BiConsumer<String, BankAccountType> onCreate, Runnable onCancel) {

        getStyleClass().add("form-card");
        setSpacing(12);
        setPadding(new Insets(18));
        setPrefWidth(280);
        setMaxWidth(280);

        setMaxSize(
            Region.USE_PREF_SIZE,
            Region.USE_PREF_SIZE
        );

        Label title = new Label("Nova conta");
        title.getStyleClass().addAll("text-primary", "form-title");

        Label nameLabel = new Label("Nome *");
        nameLabel.getStyleClass().addAll("text-secondary", "form-label");

        TextField nameField = new TextField();
        nameField.setPromptText("Ex: Nubank");
        nameField.getStyleClass().add("form-input");

        Label typeLabel = new Label("Tipo *");
        typeLabel.getStyleClass().addAll("text-secondary", "form-label");

        ComboBox<BankAccountType> typeBox = new ComboBox<>(FXCollections.observableArrayList(BankAccountType.values()));
        typeBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(BankAccountType type) {
                if (type == null) return "";
                return switch (type) {
                    case CHECKING -> "Corrente";
                    case SAVINGS -> "Poupança";
                    case CASH -> "Dinheiro";
                    case OTHER -> "Outro";
                };
            }
            @Override
            public BankAccountType fromString(String string) { return null; }
        });
        typeBox.getStyleClass().add("form-input");
        typeBox.setMaxWidth(Double.MAX_VALUE);
        typeBox.getSelectionModel().selectFirst();

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("form-error");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button createButton = new Button("Criar");
        createButton.getStyleClass().addAll("primary", "form-save-button");
        createButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("Informe um nome.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }
            onCreate.accept(name, typeBox.getValue());
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.getStyleClass().add("form-cancel-button");
        cancelButton.setOnAction(e -> onCancel.run());

        HBox buttonRow = new HBox(10, cancelButton, createButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(title, nameLabel, nameField, typeLabel, typeBox, errorLabel, buttonRow);

        getStylesheets().add(getClass().getResource("FormTransaction.css").toExternalForm());
    }
}