package app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import ui.javafx.JavafxUI;

public class Main extends Application {

    private Stage stage;
    private Scene scene;

    @FXML
    private HBox terminalOption;

    @FXML
    private HBox guiOption;

    @FXML
    private ImageView terminalPointer;

    @FXML
    private ImageView guiPointer;

    @Override
    public void start(Stage primaryStage) throws Exception {

        database.DatabaseInitializer.initialize();

        this.stage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));

        loader.setController(this);

        Parent root = loader.load();

        Scene scene = new Scene(root, 500, 350);
        this.scene = scene;

        scene.getStylesheets().add(
                getClass().getResource("Main.css").toExternalForm());

        primaryStage.setTitle("FinTrack");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @FXML
    private void initialize() {

        guiPointer.setVisible(true);
        terminalPointer.setVisible(false);

        terminalOption.setOnMouseEntered(e -> {
            terminalPointer.setVisible(true);
            guiPointer.setVisible(false);
        });

        guiOption.setOnMouseEntered(e -> {
            terminalPointer.setVisible(false);
            guiPointer.setVisible(true);
        });
    }

    @FXML
    private void openGUI(ActionEvent event) throws Exception {

        JavafxUI ui = new JavafxUI(stage, scene);
        ui.start();
    }

    @FXML
    private void openTERMINAL(ActionEvent event) {

        stage.hide();
        System.out.println("Iniciando o modo Terminal...\n");

        Thread consoleThread = new Thread(() -> {

            java.util.Scanner sc = new java.util.Scanner(System.in);
            ui.console.ConsoleUI consoleUI = new ui.console.ConsoleUI();

            consoleUI.start(sc);

            javafx.application.Platform.exit();
            System.exit(0);
        });

        consoleThread.start();
    }
}