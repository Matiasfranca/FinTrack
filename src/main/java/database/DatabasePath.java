package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public record DatabasePath(Path path) {

    public static DatabasePath create() {

        String os = System.getProperty("os.name").toLowerCase();

        Path directory;

        if (os.contains("win")) {
            directory = Paths.get(
                    System.getenv("LOCALAPPDATA"),
                    "FinTrack"
            );
        } else if (os.contains("linux")) {
            directory = Paths.get(
                    System.getProperty("user.home"),
                    ".local",
                    "share",
                    "FinTrack"
            );
        } else {
            directory = Paths.get(
                    System.getProperty("user.home"),
                    "FinTrack"
            );
        }

        try {
            Files.createDirectories(directory);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Could not create FinTrack data directory.",
                    e
            );
        }

        return new DatabasePath(
                directory.resolve("fintrack.db")
        );
    }
}