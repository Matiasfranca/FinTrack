package database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseInitializer {

    public static void initialize() {

        try (Connection connection = DatabaseConnection.getConnection();
             InputStream input = DatabaseInitializer.class
                     .getResourceAsStream("/database/schemas/schema.sql")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Arquivo schema.sql não encontrado."
                );
            }

            String schema = new String(
                    input.readAllBytes(),
                    StandardCharsets.UTF_8
            );

            for (String sql : schema.split(";")) {

                String statement = sql.trim();

                if (!statement.isEmpty()) {
                    connection.createStatement().execute(statement);
                }
            }

        } catch (IOException | SQLException e) {
            throw new RuntimeException(
                    "Erro ao inicializar o banco de dados.",
                    e
            );
        }
    }
}