package co.za.Main.TradeFunctions;

import java.math.BigDecimal;
import java.sql.*;

public class TradeVariableDatabase {

    private Connection connection;
    private TradeFunction tradeFunction;

    private String dataBaseName = "TradeVariables.db";
    private String tableName = "TradeVariables";

    public TradeVariableDatabase(boolean basedOnExecution, BigDecimal spread, BigDecimal rateKA, BigDecimal ratePN) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName);
            createTable();
            tradeFunction = new TradeFunction(basedOnExecution, spread, rateKA, ratePN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM " + tableName);
            // Populate with default values (0 for maximum and minimum)
            String[] variables = {
                "tradeprofit",
                "tradeamount",
                "buyvariable",
                "sellvariable"
            };
            
            for (String variable : variables) {
                stmt.execute(String.format(
                    "INSERT INTO %s (variable, minimum, maximum, factormin, factormax, returnmin, returnmax) VALUES ('%s', 0, 0, 0, 0, 0, 0)",
                    tableName, variable));
            }
        } catch (SQLException e) {
            System.out.println("Error populating table: " + e.getMessage());
            throw e;
        }
    }

    public void populateQueryVariables() {
        try {
            BigDecimal tradeProfitMax = getValueFromColumn("tradeprofit", "maximum");
            BigDecimal tradeProfitMin = getValueFromColumn("tradeprofit", "minimum");
            BigDecimal tradeAmountMax = getValueFromColumn("tradeamount", "maximum");
            BigDecimal tradeAmountMin = getValueFromColumn("tradeamount", "minimum");
            BigDecimal buyVariableMin = getValueFromColumn("buyvariable", "minimum");
            BigDecimal buyVariableMax = getValueFromColumn("buyvariable", "maximum");
            BigDecimal sellVariableMin = getValueFromColumn("sellvariable", "minimum");
            BigDecimal sellVariableMax = getValueFromColumn("sellvariable", "maximum");

            try (PreparedStatement pstmt = connection.prepareStatement(
                    "UPDATE " + tableName + " SET factormin = ?, factormax = ?, returnmin = ?, returnmax = ? WHERE variable = ?")) {
                
                // Calculate profit factors for all calculations
                BigDecimal profitFactorMin = tradeFunction.returnProfitFactor(sellVariableMin, buyVariableMax);
                BigDecimal profitFactorMax = tradeFunction.returnProfitFactor(sellVariableMax, buyVariableMin);
                
                // Update trade profit calculations
                BigDecimal tradeProfitMinResult = tradeFunction.returnProfit(tradeAmountMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeProfitMaxResult = tradeFunction.returnProfit(tradeAmountMax, sellVariableMax, buyVariableMin);
                updateQueryResult(pstmt, "tradeprofit", profitFactorMin, profitFactorMax, tradeProfitMinResult, tradeProfitMaxResult);

                // Update trade amount calculations
                BigDecimal tradeAmountProfitMinResult = tradeFunction.returnTradeAmount(tradeProfitMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeAmountProfitMaxResult = tradeFunction.returnTradeAmount(tradeProfitMax, sellVariableMax, buyVariableMin);
                BigDecimal tradeAmountProfitFactorMin = tradeFunction.returnFactorTradeAmount(profitFactorMin, tradeProfitMax);
                BigDecimal tradeAmountProfitFactorMax = tradeFunction.returnFactorTradeAmount(profitFactorMax, tradeProfitMin);
                updateQueryResult(pstmt, "tradeamount", tradeAmountProfitFactorMin, tradeAmountProfitFactorMax, tradeAmountProfitMinResult, tradeAmountProfitMaxResult);

                // Update sell variable calculations
                BigDecimal sellVariableProfitMinResult = tradeFunction.returnSellVariable(tradeAmountMax, tradeProfitMin, buyVariableMin);
                BigDecimal sellVariableProfitMaxResult = tradeFunction.returnSellVariable(tradeAmountMin, tradeProfitMax, buyVariableMax);
                BigDecimal sellVariableProfitFactorMin = tradeFunction.returnFactorSellVariable(profitFactorMin, buyVariableMax);
                BigDecimal sellVariableProfitFactorMax = tradeFunction.returnFactorSellVariable(profitFactorMax, buyVariableMin);
                updateQueryResult(pstmt, "sellvariable", sellVariableProfitFactorMin, sellVariableProfitFactorMax, sellVariableProfitMinResult, sellVariableProfitMaxResult);

                // Update buy variable calculations
                BigDecimal buyVariableProfitMinResult = tradeFunction.returnBuyVariable(tradeAmountMax, tradeProfitMin, sellVariableMin);
                BigDecimal buyVariableProfitMaxResult = tradeFunction.returnBuyVariable(tradeAmountMin, tradeProfitMax, sellVariableMax);
                BigDecimal buyVariableProfitFactorMin = tradeFunction.returnFactorBuyVariable(profitFactorMax, sellVariableMin);
                BigDecimal buyVariableProfitFactorMax = tradeFunction.returnFactorBuyVariable(profitFactorMin, sellVariableMax);
                updateQueryResult(pstmt, "buyvariable", buyVariableProfitFactorMin, buyVariableProfitFactorMax, buyVariableProfitMinResult, buyVariableProfitMaxResult);
            }

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (ArithmeticException e) {
            System.err.println("Calculation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateQueryResult(PreparedStatement pstmt, String variable, BigDecimal factorMinResult, BigDecimal factorMaxResult, BigDecimal minResult, BigDecimal maxResult) throws SQLException {
        pstmt.setBigDecimal(1, factorMinResult);
        pstmt.setBigDecimal(2, factorMaxResult);
        pstmt.setBigDecimal(3, minResult);
        pstmt.setBigDecimal(4, maxResult);
        pstmt.setString(5, variable);
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
        String fileName = "variables.csv";
        
        try (java.io.FileWriter writer = new java.io.FileWriter(fileName);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {
            
            // Write CSV header
            printWriter.println("variable,maximum,minimum,factormin,factormax,returnmin,returnmax");
            
            // Write data
            String sql = "SELECT * FROM " + tableName;
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                
                while (rs.next()) {
                    printWriter.printf("%s,%s,%s,%s,%s,%s,%s%n",
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
            
            System.out.println("Database exported to " + fileName + " successfully.");
            
        } catch (java.io.IOException e) {
            throw new SQLException("Error writing to CSV file: " + e.getMessage());
        }
    }

    // Export database to SQL file - FIXED VERSION
    public void exportToSQL() throws SQLException {
        String fileName = "variables.sql";
        
        try (java.io.FileWriter writer = new java.io.FileWriter(fileName);
             java.io.PrintWriter printWriter = new java.io.PrintWriter(writer)) {
            
            // Write DROP and CREATE statements
            printWriter.println("DROP TABLE IF EXISTS variables;");
            printWriter.println();
            printWriter.println("CREATE TABLE variables (");
            printWriter.println("    variable VARCHAR(50) DEFAULT '0',");
            printWriter.println("    maximum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    minimum DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    factormin DECIMAL(20,8) DEFAULT 0,");
            printWriter.println("    factormax DECIMAL(20,8) DEFAULT 0,");
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
                    printWriter.printf("INSERT INTO variables (variable, maximum, minimum, factormin, factormax, returnmin, returnmax) VALUES ('%s', %s, %s, %s, %s, %s, %s);%n",
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
            
            System.out.println("Database exported to " + fileName + " successfully.");
            
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
}