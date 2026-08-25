package ui.javafx.components.header;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class Header extends HBox {
    public Header() {

        getStyleClass().add("header");
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(40);

        Text title = new Text("FinTrack");
        title.getStyleClass().add("brand-title");

        Region spacer = new Region();
        setHgrow(spacer, Priority.ALWAYS);

        Button addTransaction = new Button("+ Adicionar transação");
        addTransaction.getStyleClass().add("add-transaction");   // tirei "primary" daqui

        getChildren().addAll(title, spacer, addTransaction);

        getStylesheets().add(getClass().getResource("Header.css").toExternalForm());
    }
}