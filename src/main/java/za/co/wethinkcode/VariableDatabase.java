package za.co.wethinkcode;

import java.sql.*;

public class VariableDatabase {
    
    private Connection connection;
    QueryFunction queryFunction;
    
    public VariableDatabase(QueryFunction queryFunction) {
        try {
            this.queryFunction = queryFunction;
            connection = DriverManager.getConnection("jdbc:sqlite:variables.db");
            createTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add this method to VariableDatabase.java
    public Integer getValue(String variable) throws SQLException {
        String sql = "SELECT query_return FROM variables WHERE variable = ?";
        try (Connection conn = this.connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, variable);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("query_return");
            }
        }
        return null;
    }
    
    private void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS variables (
                variable VARCHAR(10),
                query_return INTEGER
            )
            """;
        Statement stmt = connection.createStatement();
        stmt.execute(sql);
    }
    
    public void populateTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("DELETE FROM variables");
        
        stmt.execute("INSERT INTO variables VALUES ('a', " + queryFunction.functionA() + ")");
        stmt.execute("INSERT INTO variables VALUES ('b', " + queryFunction.functionB() + ")");
        stmt.execute("INSERT INTO variables VALUES ('c', " + queryFunction.functionC() + ")");
    }

    public void exportToCSV() {
        try (java.io.PrintWriter writer = new java.io.PrintWriter("variables.csv")) {
            writer.println("Variables,Query Return");
            writer.println("a," + queryFunction.functionA());
            writer.println("b," + queryFunction.functionB());
            writer.println("c," + queryFunction.functionC());
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exportToSQL() {
        try (java.io.PrintWriter writer = new java.io.PrintWriter("variables.sql")) {
            writer.println("CREATE TABLE variables (variable VARCHAR(10), query_return INTEGER);");
            writer.println("INSERT INTO variables VALUES ('a', " + queryFunction.functionA() + ");");
            writer.println("INSERT INTO variables VALUES ('b', " + queryFunction.functionB() + ");");
            writer.println("INSERT INTO variables VALUES ('c', " + queryFunction.functionC() + ");");
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}