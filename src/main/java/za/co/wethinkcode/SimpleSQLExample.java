package za.co.wethinkcode;

import java.sql.*;

public class SimpleSQLExample {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:sample.db"; // SQLite will create this file if it doesn't exist

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                // Create a table
                String createTableSQL = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, name TEXT)";
                Statement stmt = conn.createStatement();
                stmt.execute(createTableSQL);

                // Insert a user
                String insertSQL = "INSERT INTO users (name) VALUES (?)";
                PreparedStatement pstmt = conn.prepareStatement(insertSQL);
                pstmt.setString(1, "Alice");
                pstmt.executeUpdate();

                // Query the user
                String selectSQL = "SELECT * FROM users";
                ResultSet rs = stmt.executeQuery(selectSQL);

                // Print results
                while (rs.next()) {
                    System.out.println("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
