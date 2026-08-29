package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static boolean testMode = false;

    public static void setTestMode(boolean enabled) {
        testMode = enabled;
        DatabaseInitializer.setTestMode(enabled);
    }

    private static String getUrl() {
        return testMode ? "jdbc:sqlite:fintrack-test.db" : "jdbc:sqlite:fintrack.db";
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(getUrl());

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }
}