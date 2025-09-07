package co.za.Main.ConsoleApplication;

import java.math.BigDecimal;
import java.sql.*;

import co.za.Main.TradeModules.TradeFunction;

public class ConsoleDatabase {

    private Connection connection;
    private TradeFunction tradeFunction;

    private String dataBaseName = "ConsoleDataBase.db";
    private String tableName = "ConsoleDataBase";
    private String FILENAME = "ConsoleDataBase";


    // Default constants
    private static final String DEFAULT_TRADE_PROFIT_MIN = "-88.000000000";
    private static final String DEFAULT_TRADE_PROFIT_MAX = "-88.000000000";
    private static final String DEFAULT_TRADE_AMOUNT_MIN = "10000";
    private static final String DEFAULT_TRADE_AMOUNT_MAX = "10000";
    private static final String DEFAULT_BUY_VARIABLE_MIN = "17.7055";
    private static final String DEFAULT_BUY_VARIABLE_MAX = "17.7055";
    private static final String DEFAULT_SELL_VARIABLE_MIN = "17.6967";
    private static final String DEFAULT_SELL_VARIABLE_MAX = "17.6967";
    private static final String DEFAULT_PROFIT_FACTOR_MIN = "-0.000497021";
    private static final String DEFAULT_PROFIT_FACTOR_MAX = "-0.000497021";



    public ConsoleDatabase(BigDecimal spread, BigDecimal rateKA, BigDecimal ratePN) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
            createTable();
            tradeFunction = new TradeFunction(spread, rateKA, ratePN);
            // Default to execution-based (basedOnMarketRate = false)
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setBasedOnMarketRate(boolean basedOnMarketRate) {
        if (tradeFunction != null) {
            tradeFunction.setBasedOnMarketRate(basedOnMarketRate);
        }
    }

    public void createTable() throws SQLException {
        String sql = String.format("""
            CREATE TABLE IF NOT EXISTS %s (
                variable VARCHAR(50) DEFAULT '0',
                maximum DECIMAL(20,8) DEFAULT 0,
                minimum DECIMAL(20,8) DEFAULT 0,
                returnmin DECIMAL(20,8) DEFAULT 0,
                returnmax DECIMAL(20,8) DEFAULT 0
            )
            """, tableName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            populateTable();
        }
    }

    // Define defaults at class level or in method
    private void populateTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + tableName);
            
            // Insert with proper defaults
            insertVariable(stmt, "tradeprofit", DEFAULT_TRADE_PROFIT_MIN, DEFAULT_TRADE_PROFIT_MAX);
            insertVariable(stmt, "tradeamount", DEFAULT_TRADE_AMOUNT_MIN, DEFAULT_TRADE_AMOUNT_MAX);
            insertVariable(stmt, "buyvariable", DEFAULT_BUY_VARIABLE_MIN, DEFAULT_BUY_VARIABLE_MAX);
            insertVariable(stmt, "sellvariable", DEFAULT_SELL_VARIABLE_MIN, DEFAULT_SELL_VARIABLE_MAX);
            insertVariable(stmt, "profitfactor", DEFAULT_PROFIT_FACTOR_MIN, DEFAULT_PROFIT_FACTOR_MAX);
        }
    }

    private void insertVariable(Statement stmt, String variable, String min, String max) throws SQLException {
        stmt.execute(String.format(
            "INSERT INTO %s (variable, minimum, maximum, returnmin, returnmax) VALUES ('%s', %s, %s, 0, 0)",
            tableName, variable, min, max));
    }

    public void populateQueryVariables() {
        try {
            BigDecimal tradeProfitMax = getValueFromColumn("tradeprofit", "maximum");
            BigDecimal tradeProfitMin = getValueFromColumn("tradeprofit", "minimum");
            BigDecimal profitFactorMin = getValueFromColumn("profitfactor", "minimum");
            BigDecimal profitFactorMax = getValueFromColumn("profitfactor", "maximum");
            BigDecimal tradeAmountMax = getValueFromColumn("tradeamount", "maximum");
            BigDecimal tradeAmountMin = getValueFromColumn("tradeamount", "minimum");
            BigDecimal buyVariableMin = getValueFromColumn("buyvariable", "minimum");
            BigDecimal buyVariableMax = getValueFromColumn("buyvariable", "maximum");
            BigDecimal sellVariableMin = getValueFromColumn("sellvariable", "minimum");
            BigDecimal sellVariableMax = getValueFromColumn("sellvariable", "maximum");

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE " + tableName + " SET returnmin = ?, returnmax = ? WHERE variable = ?")) {
                
                // Calculate profit results for all calculations
                BigDecimal tradeProfitMinResult = tradeFunction.returnProfit(tradeAmountMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeProfitMaxResult = tradeFunction.returnProfit(tradeAmountMax, sellVariableMax, buyVariableMin);
                updateQueryResult(pstmt, "tradeprofit", tradeProfitMinResult, tradeProfitMaxResult);

                // Calculate profit factor results
                BigDecimal profitFactorMinResult = tradeFunction.returnProfitFactor(sellVariableMin, buyVariableMax);
                BigDecimal profitFactorMaxResult = tradeFunction.returnProfitFactor(sellVariableMax, buyVariableMin);
                updateQueryResult(pstmt, "profitfactor", profitFactorMinResult, profitFactorMaxResult);

                // Update trade amount calculations
                BigDecimal tradeAmountProfitMinResult = tradeFunction.returnTradeAmount(tradeProfitMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeAmountProfitMaxResult = tradeFunction.returnTradeAmount(tradeProfitMax, sellVariableMax, buyVariableMin);
                updateQueryResult(pstmt, "tradeamount", tradeAmountProfitMinResult, tradeAmountProfitMaxResult);

                // Update sell variable calculations
                BigDecimal sellVariableProfitMinResult = tradeFunction.returnSellVariable(tradeAmountMax, tradeProfitMin, buyVariableMin);
                BigDecimal sellVariableProfitMaxResult = tradeFunction.returnSellVariable(tradeAmountMin, tradeProfitMax, buyVariableMax);
                updateQueryResult(pstmt, "sellvariable", sellVariableProfitMinResult, sellVariableProfitMaxResult);

                // Update buy variable calculations
                BigDecimal buyVariableProfitMinResult = tradeFunction.returnBuyVariable(tradeAmountMax, tradeProfitMin, sellVariableMin);
                BigDecimal buyVariableProfitMaxResult = tradeFunction.returnBuyVariable(tradeAmountMin, tradeProfitMax, sellVariableMax);
                updateQueryResult(pstmt, "buyvariable", buyVariableProfitMinResult, buyVariableProfitMaxResult);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (ArithmeticException e) {
            System.err.println("Calculation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateQueryResult(PreparedStatement pstmt, String variable, BigDecimal minResult, BigDecimal maxResult) throws SQLException {
        pstmt.setBigDecimal(1, minResult);
        pstmt.setBigDecimal(2, maxResult);
        pstmt.setString(3, variable);
        pstmt.executeUpdate();
    }

    public BigDecimal getValueFromColumn(String variable, String columnName) {
        String sql = String.format("SELECT %s FROM %s WHERE variable = ?", columnName, tableName);
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, variable);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(columnName);
            } else {
                throw new SQLException("Variable not found: " + variable);
            }
        } catch (SQLException e) {
            System.err.println("Error querying the database: " + e.getMessage());
            return BigDecimal.ZERO; // Return zero instead of -1 for consistency
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

    // Overloaded method for backward compatibility with long values
    public void updateValue(String variable, String columnName, long value) throws SQLException {
        updateValue(variable, columnName, BigDecimal.valueOf(value));
    }

    // Export database to CSV file
    public void exportToCSV() throws SQLException {
        String filename = FILENAME + ".csv";
        try (java.io.FileWriter writer = new java.io.FileWriter(filename);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {
            
            // Write CSV header
            printWriter.println("variable,maximum,minimum,returnmin,returnmax");
            
            // Write data
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    printWriter.printf("%s,%s,%s,%s,%s%n",
                        rs.getString("variable"),
                        rs.getBigDecimal("maximum").toPlainString(),
                        rs.getBigDecimal("minimum").toPlainString(),
                        rs.getBigDecimal("returnmin").toPlainString(),
                        rs.getBigDecimal("returnmax").toPlainString()
                    );
                }
            }
            
            System.out.println("Database exported to " + filename + " successfully.");
            
        } catch (java.io.IOException e) {
            throw new SQLException("Error writing to CSV file: " + e.getMessage());
        }
    }

    // Export database to SQL file - FIXED VERSION
    public void exportToSQL() throws SQLException {
        String filename = FILENAME + ".sql";
        try (java.io.FileWriter writer = new java.io.FileWriter(filename);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {
            
            // Write DROP and CREATE statements
            printWriter.println("DROP TABLE IF EXISTS " + tableName + ";");
            printWriter.println();
            printWriter.println("CREATE TABLE " + tableName + " (");
            printWriter.println("    variable VARCHAR(50) DEFAULT '0',");
            printWriter.println("    maximum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    minimum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    returnmin DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    returnmax DECIMAL(20,8) DEFAULT 0");
            printWriter.println(");");
            printWriter.println();
            
            // Write INSERT statements with data
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                printWriter.println("-- Insert data");
                while (rs.next()) {
                    printWriter.printf("INSERT INTO %s (variable, maximum, minimum, returnmin, returnmax) VALUES ('%s', %s, %s, %s, %s);%n",
                        tableName, 
                        rs.getString("variable"),
                        rs.getBigDecimal("maximum").toPlainString(),
                        rs.getBigDecimal("minimum").toPlainString(),
                        rs.getBigDecimal("returnmin").toPlainString(),
                        rs.getBigDecimal("returnmax").toPlainString()
                    );
                }
            }
            
            printWriter.println();
            printWriter.println("-- End of export");
            
            System.out.println("Database exported to " + filename + " successfully.");
            
        } catch (java.io.IOException e) {
            throw new SQLException("Error writing to SQL file: " + e.getMessage());
        }
    }
    
    // Method to close the database connection
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
    
    // Helper method to get all variables for debugging
    public void printAllVariables() throws SQLException {
        String sql = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            System.out.println("Variable Database Contents:");
            System.out.println("Variable\t\tMaximum\t\tMinimum\t\tReturnMin\tReturnMax");
            System.out.println("========================================================================");
            
            while (rs.next()) {
                System.out.printf("%-20s\t%s\t%s\t%s\t%s%n",
                    rs.getString("variable"),
                    rs.getBigDecimal("maximum").toPlainString(),
                    rs.getBigDecimal("minimum").toPlainString(),
                    rs.getBigDecimal("returnmin").toPlainString(),
                    rs.getBigDecimal("returnmax").toPlainString()
                );
            }
        }
    }
}