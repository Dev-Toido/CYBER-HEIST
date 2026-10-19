package persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseSchema {
    private static final String[] REQUIRED_TABLES = {
            "employees", "simulation_sessions", "incidents", "incident_actions"
    };

    private DatabaseSchema() { }

    public static void verify(Connection connection) throws SQLException {
        for (String table : REQUIRED_TABLES) {
            try (Statement statement = connection.createStatement()) {
                statement.executeQuery("SELECT 1 FROM " + table + " LIMIT 0").close();
            } catch (SQLException exception) {
                throw new SQLException("Required table '" + table + "' is missing or inaccessible. Run sql/schema.sql, then retry.", exception);
            }
        }
    }
}
