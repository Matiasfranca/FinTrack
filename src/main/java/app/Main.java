package app;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import service.DatabaseMaintenanceService;
import ui.javafx.JavafxUI;

public class Main extends Application {

    private Stage stage;
    private Scene scene;

    @FXML
    private Button guiButton;

    @FXML
    private HBox terminalOption;

    @FXML
    private HBox guiOption;

    @FXML
    private ImageView terminalPointer;

    @FXML
    private ImageView guiPointer;

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            java.util.Scanner sc = new java.util.Scanner(System.in);
            ui.console.ConsoleUI consoleUI = new ui.console.ConsoleUI();
            consoleUI.start(sc);
            System.exit(0);
            return;
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        database.DatabaseInitializer.initialize();
        DatabaseMaintenanceService.cleanIfFirstDayOfMonth();

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
        stage.close();

        try {
            ProcessBuilder check = new ProcessBuilder("which", "fintrack");
            Process process = check.start();
            int errorCode = process.waitFor();

            if (errorCode == 0) {
                try {
                    ProcessBuilder pb = new ProcessBuilder("x-terminal-emulator", "-e", "fintrack", "--console");
                    pb.start();
                } catch (Exception e) {
                    String[] terminals = { "gnome-terminal", "konsole", "xfce4-terminal", "xterm" };
                    for (String term : terminals) {
                        try {
                            ProcessBuilder pb = new ProcessBuilder(term, term.equals("gnome-terminal") ? "--" : "-e",
                                    "fintrack", "--console");
                            pb.start();
                            break;
                        } catch (Exception ignored) {
                        }
                    }
                }
                javafx.application.Platform.exit();
                System.exit(0);

            } else {
                System.out.println("\n--- MODO TERMINAL (Ambiente de Desenvolvimento) ---\n");

                javafx.application.Platform.exit();

                new Thread(() -> {
                    java.util.Scanner sc = new java.util.Scanner(System.in);
                    ui.console.ConsoleUI consoleUI = new ui.console.ConsoleUI();
                    consoleUI.start(sc);
                    System.exit(0);
                }).start();
            }

        } catch (Exception e) {
            System.err.println("Erro ao iniciar o terminal: " + e.getMessage());
            stage.show();
        }
    }
}