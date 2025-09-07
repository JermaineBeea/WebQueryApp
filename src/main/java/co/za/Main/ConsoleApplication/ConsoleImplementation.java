package co.za.Main.ConsoleApplication;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class ConsoleImplementation {

    private static Scanner scanner = new Scanner(System.in);
    private static ConsoleDatabase db;

    public static void main(String[] args) {
        // Initialize with example trading parameters
        BigDecimal spread = new BigDecimal("0.01");  // 0.1% spread
        BigDecimal rateKA = new BigDecimal("17.7055");   // Rate KA
        BigDecimal ratePN = new BigDecimal("1.0");   // Rate PN
        
        // Initialize database with trading parameters (defaults to execution-based)
        db = new ConsoleDatabase(spread, rateKA, ratePN);
        
        // Optionally set to market-based calculation mode
        // db.setBasedOnMarketRate(true);  // Uncomment to use market rates instead of execution rates

        
        try {
            System.out.println("=== Trade Query System Initialization ===");
            System.out.println("Database table created and populated with default values.");
            System.out.println("Calculation Mode: Execution-based (default)");
            
            System.out.println("Variables updated with min/max values:");
            printVariableRanges();

            // Populate calculated query variables based on the values in the database
            db.populateQueryVariables();
            System.out.println("\nQuery variables populated based on database calculations.");
            
            // Show all calculated values
            System.out.println("\n=== Database Contents ===");
            db.printAllVariables();
            
            // Export data
            System.out.println("\n=== Exporting Data ===");
            db.exportToSQL();
            // db.exportToCSV();
            System.out.println("Data export completed.");

            // Interactive query interface
            runQueryInterface();
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private static void printVariableRanges() {
        System.out.println("- Trade Profit: 100 - 1000");
        System.out.println("- Trade Amount: 5000 - 50000");
        System.out.println("- Buy Variable: 1.10 - 1.25");
        System.out.println("- Sell Variable: 1.15 - 1.30");
    }

    private static void runQueryInterface() {
        System.out.println("\n=== Interactive Query Interface ===");
        System.out.println("Available commands:");
        System.out.println("- 'query' to perform a database query");
        System.out.println("- 'show' to display all data");
        System.out.println("- 'export' to export data again");
        System.out.println("- 'mode' to toggle calculation mode (market/execution)");
        System.out.println("- 'exit' to quit");
        
        while (true) {
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine().trim().toLowerCase();
            
            switch (command) {
                case "query":
                    queryTable();
                    break;
                case "show":
                    try {
                        db.printAllVariables();
                    } catch (SQLException e) {
                        System.err.println("Error displaying data: " + e.getMessage());
                    }
                    break;
                case "export":
                    try {
                        db.exportToSQL();
                        db.exportToCSV();
                        System.out.println("Export completed.");
                    } catch (SQLException e) {
                        System.err.println("Error during export: " + e.getMessage());
                    }
                    break;
                case "mode":
                    toggleCalculationMode();
                    break;
                case "exit":
                    System.out.println("Exiting query interface...");
                    return;
                default:
                    System.out.println("Unknown command. Available: query, show, export, mode, exit");
            }
        }
    }

    private static void toggleCalculationMode() {
        try {
            System.out.println("\nCalculation Mode Selection:");
            System.out.println("1. Execution-based (default) - uses execution rates directly");
            System.out.println("2. Market-based - applies spread adjustments to market rates");
            System.out.print("Select mode (1 or 2): ");
            
            String input = scanner.nextLine().trim();
            
            if ("1".equals(input)) {
                db.setBasedOnMarketRate(false);
                System.out.println("Mode set to: Execution-based");
            } else if ("2".equals(input)) {
                db.setBasedOnMarketRate(true);
                System.out.println("Mode set to: Market-based");
            } else {
                System.out.println("Invalid selection. Mode unchanged.");
                return;
            }
            
            // Recalculate with new mode
            System.out.println("Recalculating with new mode...");
            db.populateQueryVariables();
            System.out.println("Calculations updated successfully.");
            
        } catch (Exception e) {
            System.err.println("Error changing mode: " + e.getMessage());
        }
    }

    public static void queryTable() {
        System.out.println("\n--- Database Query ---");
        System.out.print("Enter a variable to query (tradeprofit, tradeamount, buyvariable, sellvariable): ");
        String variable = scanner.nextLine().trim().toLowerCase();
        
        // Input validation for variable
        if (!isValidVariable(variable)) {
            System.out.println("Invalid variable. Available options:");
            System.out.println("- tradeprofit");
            System.out.println("- tradeamount");
            System.out.println("- buyvariable");
            System.out.println("- sellvariable");
            return;
        }
        
        System.out.print("Enter a column to query (maximum, minimum, returnmin, returnmax): ");
        String column = scanner.nextLine().trim().toLowerCase();
        
        // Input validation for column
        if (!isValidColumn(column)) {
            System.out.println("Invalid column. Available options:");
            System.out.println("- maximum     : Maximum value for this variable");
            System.out.println("- minimum     : Minimum value for this variable");
            System.out.println("- returnmin   : Minimum return calculation");
            System.out.println("- returnmax   : Maximum return calculation");
            return;
        }
        
        try {
            BigDecimal value = db.getValueFromColumn(variable, column);
            System.out.println("\n--- Query Result ---");
            System.out.println("Variable: " + variable);
            System.out.println("Column: " + column);
            System.out.println("Value: " + value.toPlainString());
            
            // Provide context for the result
            provideValueContext(variable, column, value);
            
        } catch (Exception e) {
            System.err.println("Error querying the database: " + e.getMessage());
        }
    }

    private static boolean isValidVariable(String variable) {
        return variable.matches("tradeprofit|tradeamount|buyvariable|sellvariable");
    }

    private static boolean isValidColumn(String column) {
        return column.matches("maximum|minimum|returnmin|returnmax");
    }

    private static void provideValueContext(String variable, String column, BigDecimal value) {
        System.out.println("\n--- Context ---");
        
        switch (column) {
            case "maximum":
            case "minimum":
                System.out.println("This is the " + column + " input value set for " + variable + ".");
                break;
            case "returnmin":
            case "returnmax":
                System.out.println("This is the " + column.replace("return", "").replace("min", "minimum").replace("max", "maximum") + 
                                 " calculated return value for " + variable + ".");
                System.out.println("This value is calculated using the trade function formulas.");
                break;
        }
        
        // Variable-specific context
        switch (variable) {
            case "tradeprofit":
                System.out.println("Trade profit represents the expected profit from a trading operation.");
                break;
            case "tradeamount":
                System.out.println("Trade amount represents the capital invested in a trading operation.");
                break;
            case "buyvariable":
                System.out.println("Buy variable represents the exchange rate or price when buying.");
                break;
            case "sellvariable":
                System.out.println("Sell variable represents the exchange rate or price when selling.");
                break;
        }
    }

    private static void cleanup() {
        System.out.println("\nCleaning up resources...");
        try {
            if (scanner != null) {
                scanner.close();
            }
            if (db != null) {
                db.close();
            }
            System.out.println("Resources closed successfully.");
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}