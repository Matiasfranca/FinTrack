package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {

    // Flag to control if we are running tests
    private static boolean testMode = false;

    public static void setTestMode(boolean enabled) {
        testMode = enabled;
    }

    public static void initialize() {

        // Selects the appropriate schema file based on the environment mode
        String schemaPath = testMode ? "/database/schemas/reset.sql" : "/database/schemas/schema.sql";

        try (Connection connection = DatabaseConnection.getConnection();
                InputStream input = DatabaseInitializer.class.getResourceAsStream(schemaPath)) {

            if (input == null) {
                throw new IllegalStateException("Schema file not found: " + schemaPath);
            }

            String schema = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8);

            for (String sql : schema.split(";")) {
                String statement = sql.trim();

                if (!statement.isEmpty()) {
                    connection.createStatement().execute(statement);
                }
            }

        } catch (IOException | SQLException e) {
            throw new RuntimeException("Failed to initialize database.", e);
        }
    }
}