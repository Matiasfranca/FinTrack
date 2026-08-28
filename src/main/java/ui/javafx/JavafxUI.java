package ui.javafx;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import ui.javafx.components.Components;

/**
 * FintrackUI
 */
public class JavafxUI {

    private final Stage stage;
    private final Scene scene;

    public JavafxUI(Stage stage, Scene scene) {

        this.stage = stage;
        this.scene = scene;

    }

    public void start() throws Exception {

        StackPane root = new StackPane(new Components());
        root.getStyleClass().add("background");
        root.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("main-scroll");

        scene.setRoot(scrollPane);

        stage.setMaximized(true);

    }

}