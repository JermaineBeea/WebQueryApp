package co.za.Main;

import java.sql.*;

public class VariableDatabase {

    private Connection connection;
    String dataBaseName = "variables.db";
    String tableName = "variables";

    public VariableDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Create table with only variable, value, and query
    public void createTable() throws SQLException {
        String sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                variable VARCHAR(10) DEFAULT '0',
                value INTEGER DEFAULT 0,
                query INTEGER DEFAULT 0
            )
            """, tableName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            populateTable();
        }
    }

    // Populate with default rows
    private void populateTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + tableName);

            String[] variables = {"a", "b", "c"};
            for (String variable : variables) {
                String sql = String.format(
                    "INSERT INTO %s (variable, value, query) VALUES ('%s', 0, 0)",
                    tableName, variable
                );
                stmt.execute(sql);
            }
            System.out.println("Table " + tableName + " populated with default values.");
        } catch (SQLException e) {
            System.out.println("Error populating table: " + e.getMessage());
            throw e;
        }
    }

    // Update value and query for a given variable
    public void updateQueryResult(PreparedStatement statement, String variable, long value) {
        try {
            statement.setLong(1, value);      
            statement.setString(2, variable); 
            statement.executeUpdate();
            System.out.println("Updated for variable: " + variable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Example usage: populate query values
    public void populateQueryVariables() {
        try (PreparedStatement pstmt = connection.prepareStatement(
            "UPDATE " + tableName + " SET query = ? WHERE variable = ?")) {
            
            Long a = getValueFromColumn("a", "value");
            Long b = getValueFromColumn("b", "value");
            Long c = getValueFromColumn("c", "value");

            QueryFunction queryFunction = new QueryFunction(a, b, c);

            updateQueryResult(pstmt, "a", queryFunction.returnA());
            updateQueryResult(pstmt, "b", queryFunction.returnB());
            updateQueryResult(pstmt, "c", queryFunction.returnC());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get a column value for a specific variable
    public Long getValueFromColumn(String variable, String columnName) throws SQLException {
        String sql = String.format("SELECT %s FROM %s WHERE variable = ?", columnName, tableName);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, variable);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong(columnName);
            } else {
                throw new SQLException("Variable not found: " + variable);
            }
        }
    }

    // Update a single column for a variable
    public void updateValue(String variable, String columnName, Long value) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = ? WHERE variable = ?", tableName, columnName);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, value);
            pstmt.setString(2, variable);
            pstmt.executeUpdate();
        }
    }

    // Export database to CSV file
    public void exportToCSV() throws SQLException {
        String fileName = "variables.csv";

        try (java.io.FileWriter writer = new java.io.FileWriter(fileName);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {

            // Write CSV header
            printWriter.println("variable,value,query");

            // Write data
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                while (rs.next()) {
                    printWriter.printf("%s,%d,%d%n",
                        rs.getString("variable"),
                        rs.getLong("value"),
                        rs.getLong("query")
                    );
                }
            }

            System.out.println("Database exported to " + fileName + " successfully.");

        } catch (java.io.IOException e) {
            throw new SQLException("Error writing to CSV file: " + e.getMessage());
        }
    }

    // Export database to SQL file
    public void exportToSQL() throws SQLException {
        String fileName = "variables.sql";

        try (java.io.FileWriter writer = new java.io.FileWriter(fileName);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {

            // Write SQL file header
            printWriter.println("-- Variables Database Export");
            printWriter.println("-- Generated on: " + new java.util.Date());
            printWriter.println();

            // Write DROP and CREATE statements
            printWriter.println("DROP TABLE IF EXISTS variables;");
            printWriter.println();
            printWriter.println("CREATE TABLE variables (");
            printWriter.println("    variable VARCHAR(10) DEFAULT '0',");
            printWriter.println("    value INTEGER DEFAULT 0,");
            printWriter.println("    query INTEGER DEFAULT 0");
            printWriter.println(");");
            printWriter.println();

            // Write INSERT statements with data
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {

                printWriter.println("-- Insert data");
                while (rs.next()) {
                    printWriter.printf(
                        "INSERT INTO variables (variable, value, query) VALUES ('%s', %d, %d);%n",
                        rs.getString("variable"),
                        rs.getInt("value"),
                        rs.getInt("query")
                    );
                }
            }

            printWriter.println();
            printWriter.println("-- End of export");

            System.out.println("Database exported to " + fileName + " successfully.");

        } catch (java.io.IOException e) {
            throw new SQLException("Error writing to SQL file: " + e.getMessage());
        }
    }

    // Close database connection
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
