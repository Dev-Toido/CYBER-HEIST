package persistence;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class MySqlConnection {
    private static final Path CONFIG_PATH = Path.of("config", "database.properties");
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/cyber_heist?serverTimezone=UTC&connectTimeout=5000&socketTimeout=5000";

    private MySqlConnection() { }

    public static Connection open() throws SQLException {
        Properties properties = new Properties();
        if (Files.exists(CONFIG_PATH)) {
            try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
                properties.load(input);
            } catch (IOException exception) {
                throw new SQLException("Could not read " + CONFIG_PATH.toAbsolutePath() + ".", exception);
            }
        }

        String url = envOrProperty("CYBERHEIST_DB_URL", properties, "db.url", DEFAULT_URL);
        String username = envOrProperty("CYBERHEIST_DB_USER", properties, "db.username", "cyberheist_app");
        String password = envOrProperty("CYBERHEIST_DB_PASSWORD", properties, "db.password", "");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("MySQL Connector/J was not found. Build with Maven so mysql-connector-j is on the classpath.", exception);
        }

        return DriverManager.getConnection(url, username, password);
    }

    private static String envOrProperty(String envName, Properties properties, String key, String fallback) {
        String env = System.getenv(envName);
        if (env != null && !env.isBlank()) return env.trim();
        String value = properties.getProperty(key);
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    public static String configPath() { return CONFIG_PATH.toAbsolutePath().toString(); }
    public static boolean configFileExists() { return Files.exists(CONFIG_PATH); }
}
