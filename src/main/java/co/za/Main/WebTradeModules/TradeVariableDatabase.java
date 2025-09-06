package co.za.Main.WebTradeModules;

import java.math.BigDecimal;
import java.sql.*;

public class TradeVariableDatabase implements AutoCloseable {

    private Connection connection;
    private String dataBaseName = "trade_variables.db";
    private String tableName = "trade_variables";

    public TradeVariableDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
        createTable();
    }

    public void createTable() throws SQLException {
        String sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                variable VARCHAR(50) DEFAULT '0',
                maximum DECIMAL(20,8) DEFAULT 0,
                minimum DECIMAL(20,8) DEFAULT 0,
                factormin DECIMAL(20,8) DEFAULT 0,
                factormax DECIMAL(20,8) DEFAULT 0,
                returnmin DECIMAL(20,8) DEFAULT 0,
                returnmax DECIMAL(20,8) DEFAULT 0
            )
            """, tableName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            populateTable();
        }
    }

    private void populateTable() throws SQLException {
        // Check if table is already populated
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            if (rs.next() && rs.getInt(1) > 0) {
                return; // Table already has data
            }
        }
        
        try (Statement stmt = connection.createStatement()) {
            String[] variables = {
                "tradeprofit",
                "profitfactor", 
                "tradeamount",
                "buyvariable",
                "sellvariable"
            };
            
            for (String variable : variables) {
                stmt.execute(String.format(
                    "INSERT INTO %s (variable, minimum, maximum, factormin, factormax, returnmin, returnmax) VALUES ('%s', 0, 0, 0, 0, 0, 0)",
                    tableName, variable));
            }
        }
    }

    public BigDecimal getValueFromColumn(String variable, String columnName) throws SQLException {
        String sql = String.format("SELECT %s FROM %s WHERE variable = ?", columnName, tableName);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, variable);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(columnName);
            } else {
                throw new SQLException("Variable not found: " + variable);
            }
        }
    }

    public void updateValue(String variable, String columnName, BigDecimal value) throws SQLException {
        String sql = String.format("UPDATE %s SET %s = ? WHERE variable = ?", tableName, columnName);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, value);
            pstmt.setString(2, variable);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No rows updated for variable: " + variable);
            }
        }
    }

    public void updateQueryResult(String variable, BigDecimal factorMin, BigDecimal factorMax, 
                                BigDecimal returnMin, BigDecimal returnMax) throws SQLException {
        String sql = "UPDATE " + tableName + " SET factormin = ?, factormax = ?, returnmin = ?, returnmax = ? WHERE variable = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setBigDecimal(1, factorMin);
            pstmt.setBigDecimal(2, factorMax);
            pstmt.setBigDecimal(3, returnMin);
            pstmt.setBigDecimal(4, returnMax);
            pstmt.setString(5, variable);
            pstmt.executeUpdate();
        }
    }

    public void exportToSQL() throws SQLException {
        String fileName = "trade_variables.sql";
        
        try (java.io.FileWriter writer = new java.io.FileWriter(fileName);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {
            
            printWriter.println("-- Trade Variables Database Export");
            printWriter.println("-- Generated on: " + new java.util.Date());
            printWriter.println();
            
            printWriter.println("DROP TABLE IF EXISTS trade_variables;");
            printWriter.println();
            printWriter.println("CREATE TABLE trade_variables (");
            printWriter.println("    variable VARCHAR(50) DEFAULT '0',");
            printWriter.println("    maximum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    minimum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    factormin DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    factormax DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    returnmin DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    returnmax DECIMAL(20,8) DEFAULT 0");
            printWriter.println(");");
            printWriter.println();
            
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                printWriter.println("-- Insert data");
                while (rs.next()) {
                    printWriter.printf(
                        "INSERT INTO trade_variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('%s', %s, %s, %s, %s, %s, %s);%n",
                        rs.getString("variable"),
                        rs.getBigDecimal("maximum").toPlainString(),
                        rs.getBigDecimal("minimum").toPlainString(),
                        rs.getBigDecimal("factormin").toPlainString(),
                        rs.getBigDecimal("factormax").toPlainString(),
                        rs.getBigDecimal("returnmin").toPlainString(),
                        rs.getBigDecimal("returnmax").toPlainString()
                    );
                }
            }
            
            printWriter.println();
            printWriter.println("-- End of export");
            System.out.println("Database exported to " + fileName);
            
        } catch (java.io.IOException e) {
            throw new SQLException("Error writing to SQL file: " + e.getMessage());
        }
    }

    public void printAllVariables() throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Trade Variable Database Contents:");
            System.out.println("Variable\t\tMaximum\t\tMinimum\t\tFactorMin\tFactorMax\tReturnMin\tReturnMax");
            System.out.println("================================================================================================");
            
            while (rs.next()) {
                System.out.printf("%-20s\t%s\t%s\t%s\t%s\t%s\t%s%n",
                    rs.getString("variable"),
                    rs.getBigDecimal("maximum").toPlainString(),
                    rs.getBigDecimal("minimum").toPlainString(),
                    rs.getBigDecimal("factormin").toPlainString(),
                    rs.getBigDecimal("factormax").toPlainString(),
                    rs.getBigDecimal("returnmin").toPlainString(),
                    rs.getBigDecimal("returnmax").toPlainString()
                );
            }
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}