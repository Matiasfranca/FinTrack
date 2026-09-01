package ui.javafx.components.form;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class CategoryFormCard extends VBox {

    public CategoryFormCard(Consumer<String> onCreate, Runnable onCancel) {

        getStyleClass().add("form-card");
        setSpacing(12);
        setPadding(new Insets(18));
        setPrefWidth(280);
        setMaxWidth(280);

        Label title = new Label("Nova categoria");
        title.getStyleClass().addAll("text-primary", "form-title");

        Label nameLabel = new Label("Nome *");
        nameLabel.getStyleClass().addAll("text-secondary", "form-label");

        TextField nameField = new TextField();
        nameField.setPromptText("Ex: Alimentação");
        nameField.getStyleClass().add("form-input");

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
            onCreate.accept(name);
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.getStyleClass().add("form-cancel-button");
        cancelButton.setOnAction(e -> onCancel.run());

        HBox buttonRow = new HBox(10, cancelButton, createButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(title, nameLabel, nameField, errorLabel, buttonRow);

        getStylesheets().add(getClass().getResource("FormTransaction.css").toExternalForm());
    }
}