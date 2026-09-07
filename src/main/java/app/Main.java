package app;

import javafx.application.Application;
import javafx.application.Platform;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

        if (args.length > 0 && "--console".equals(args[0])) {
            runConsole();
            return;
        }

        launch(args);
    }

    private static void runConsole() {
        java.util.Scanner sc = new java.util.Scanner(System.in);
        ui.console.ConsoleUI consoleUI = new ui.console.ConsoleUI();
        consoleUI.start(sc);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        database.DatabaseInitializer.initialize();
        DatabaseMaintenanceService.cleanIfFirstDayOfMonth();

        this.stage = primaryStage;

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("Main.fxml"));

        loader.setController(this);

        Parent root = loader.load();

        this.scene = new Scene(root, 500, 350);

        scene.getStylesheets().add(
                getClass()
                        .getResource("Main.css")
                        .toExternalForm()
        );

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

        try {

            OperatingSystem os = detectOperatingSystem();

            switch (os) {

                case WINDOWS:
                    openWindowsTerminal();
                    break;

                case LINUX:
                    openLinuxTerminal();
                    break;

                default:
                    runConsoleInCurrentProcess();
                    return;
            }

            /*
             * Close the GUI only after the terminal process
             * has been started successfully.
             */
            Platform.exit();

        } catch (Exception e) {

            System.err.println(
                    "Failed to open terminal mode: "
                            + e.getMessage()
            );

            e.printStackTrace();

            stage.show();
        }
    }

    // ============================================================
    // OPERATING SYSTEM
    // ============================================================

    private OperatingSystem detectOperatingSystem() {

        String os = System.getProperty("os.name")
                .toLowerCase(Locale.ROOT);

        if (os.contains("win")) {
            return OperatingSystem.WINDOWS;
        }

        if (os.contains("linux")) {
            return OperatingSystem.LINUX;
        }

        return OperatingSystem.OTHER;
    }

    // ============================================================
    // WINDOWS
    // ============================================================

    private void openWindowsTerminal() throws IOException {

        String executable = getCurrentExecutable();

        if (executable == null) {
            throw new IOException(
                    "Could not locate the FinTrack executable."
            );
        }

        /*
         * cmd.exe /c start:
         *
         * Opens a new CMD window.
         *
         * /k keeps the window open after the command finishes.
         */
        new ProcessBuilder(
                "cmd.exe",
                "/c",
                "start",
                "\"FinTrack\"",
                "cmd.exe",
                "/k",
                executable,
                "--console"
        ).start();
    }

    // ============================================================
    // LINUX
    // ============================================================

    private void openLinuxTerminal() throws IOException {

        String executable = getCurrentExecutable();

        /*
         * If a real executable exists, the application is most likely
         * running from a jpackage installation.
         */
        if (executable != null) {

            List<TerminalCommand> terminals = List.of(

                    // Debian/Ubuntu and systems that provide
                    // the default terminal through this command.
                    new TerminalCommand(
                            "x-terminal-emulator",
                            List.of(
                                    "-e",
                                    executable,
                                    "--console"
                            )
                    ),

                    // GNOME
                    new TerminalCommand(
                            "gnome-terminal",
                            List.of(
                                    "--",
                                    executable,
                                    "--console"
                            )
                    ),

                    // KDE
                    new TerminalCommand(
                            "konsole",
                            List.of(
                                    "-e",
                                    executable,
                                    "--console"
                            )
                    ),

                    // XFCE
                    new TerminalCommand(
                            "xfce4-terminal",
                            List.of(
                                    "--command",
                                    executable + " --console"
                            )
                    ),

                    // MATE
                    new TerminalCommand(
                            "mate-terminal",
                            List.of(
                                    "--",
                                    executable,
                                    "--console"
                            )
                    ),

                    // LXDE
                    new TerminalCommand(
                            "lxterminal",
                            List.of(
                                    "-e",
                                    executable + " --console"
                            )
                    ),

                    // Universal X11 fallback
                    new TerminalCommand(
                            "xterm",
                            List.of(
                                    "-e",
                                    executable,
                                    "--console"
                            )
                    )
            );

            for (TerminalCommand terminal : terminals) {

                if (commandExists(terminal.command())) {

                    try {

                        List<String> command = new ArrayList<>();

                        command.add(terminal.command());
                        command.addAll(terminal.arguments());

                        new ProcessBuilder(command).start();

                        return;

                    } catch (IOException ignored) {
                        /*
                         * The terminal exists but failed to start.
                         * Try the next available terminal.
                         */
                    }
                }
            }

            throw new IOException(
                    "No compatible terminal emulator was found."
            );
        }

        /*
         * Development environment:
         *
         * mvn javafx:run
         *
         * In this case, there may be no FinTrack executable
         * installed on the system.
         *
         * The console should continue in the terminal where
         * Maven was started.
         */
        runConsoleInCurrentProcess();
    }

    // ============================================================
    // CURRENT EXECUTABLE
    // ============================================================

    private String getCurrentExecutable() {

        Optional<String> command =
                ProcessHandle.current()
                        .info()
                        .command();

        if (command.isEmpty()) {
            return null;
        }

        String executable = command.get();

        /*
         * If the application is being launched directly through
         * Java/Maven, this is likely the Java executable rather
         * than the FinTrack executable.
         */
        String fileName =
                new File(executable)
                        .getName()
                        .toLowerCase(Locale.ROOT);

        if (fileName.equals("java")
                || fileName.equals("java.exe")
                || fileName.equals("javaw.exe")) {

            return null;
        }

        return executable;
    }

    // ============================================================
    // COMMAND CHECK
    // ============================================================

    private boolean commandExists(String command) {

        try {

            Process process;

            if (detectOperatingSystem() == OperatingSystem.WINDOWS) {

                process = new ProcessBuilder(
                        "where",
                        command
                ).start();

            } else {

                process = new ProcessBuilder(
                        "sh",
                        "-c",
                        "command -v " + command
                ).start();
            }

            return process.waitFor() == 0;

        } catch (Exception e) {
            return false;
        }
    }

    // ============================================================
    // DEVELOPMENT ENVIRONMENT
    // ============================================================

    private void runConsoleInCurrentProcess() {

        Platform.exit();

        new Thread(() -> {

            try {

                java.util.Scanner sc =
                        new java.util.Scanner(System.in);

                ui.console.ConsoleUI consoleUI =
                        new ui.console.ConsoleUI();

                consoleUI.start(sc);

            } finally {

                System.exit(0);
            }

        }).start();
    }

    // ============================================================
    // AUXILIARY TYPES
    // ============================================================

    private enum OperatingSystem {
        WINDOWS,
        LINUX,
        OTHER
    }

    private record TerminalCommand(
            String command,
            List<String> arguments
    ) {
    }
}