package za.co.wethinkcode;

import java.sql.*;
import java.util.Scanner;

public class QueryImplementation {

    public static void main(String[] args) {
        // Initialize QueryFunction with some example values
        int a = 5;
        int b = 6;
        int c = 7;
        
        QueryFunction qf = new QueryFunction(a, b, c);
        VariableDatabase db = new VariableDatabase(qf);
        
        try {
            db.populateTable();
            db.exportToSQL();
            db.exportToCSV();

            System.out.println("Variables populated in the database.");
        } catch (SQLException e) {
            System.out.println("Error populating database: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a variable to query (a, b, c): ");
        String variable = scanner.nextLine().trim().toLowerCase();
        scanner.close();
        
        try {
            Integer value = db.getValue(variable);
            if (value != null) {
                System.out.println("Value for variable '" + variable + "' is: " + value);
            } else {
                System.out.println("Variable '" + variable + "' not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error querying variable: " + e.getMessage());
        }

    }
    
}
