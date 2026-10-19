
package testing;

import persistence.MySqlConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        try (Connection connection = MySqlConnection.open()) {
            System.out.println("JDBC CONNECTION SUCCESSFUL!");
            System.out.println("Database: " + connection.getCatalog());

            try (Statement statement = connection.createStatement();
                 ResultSet result = statement.executeQuery("SELECT 1")) {
                if (result.next()) {
                    System.out.println(
                        "Query successful: " + result.getInt(1));
                }
            }
        } catch (Exception e) {
            System.out.println("JDBC CONNECTION FAILED!");
            e.printStackTrace();
        }
    }
}
