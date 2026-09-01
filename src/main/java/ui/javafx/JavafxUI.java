package ui.javafx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.javafx.components.Components;
import ui.javafx.components.form.TransactionFormOverlay;

public class JavafxUI {

    private final Stage stage;
    private final Scene scene;

    public JavafxUI(Stage stage, Scene scene) {
        this.stage = stage;
        this.scene = scene;
    }

    public void start() throws Exception {

        TransactionFormOverlay overlay = new TransactionFormOverlay();

        StackPane content = new StackPane(new Components(overlay::show, overlay::show));
        content.getStyleClass().add("background");
        content.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("main-scroll");
        
        StackPane appRoot = new StackPane(scrollPane, overlay);

        scene.setRoot(appRoot);

        stage.setMaximized(true);
        stage.show();
    }
}