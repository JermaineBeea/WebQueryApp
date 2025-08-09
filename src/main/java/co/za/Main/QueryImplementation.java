package co.za.Main;

import java.sql.*;

public class QueryImplementation {

    Long aValue; 
    Long bValue;   
    Long cValue;

    public QueryImplementation(Long a, Long b, Long c) {
        this.aValue = a;
        this.bValue = b;
        this.cValue = c;
    }

    public void populateTable(){
        VariableDatabase db = null;
        try {
            // Create new database instance for this operation
            db = new VariableDatabase();
            
            // Create & populate the database table
            db.createTable();
            System.out.println("Database table created and populated with default values.");
            
            // Update "value" column
            db.updateValue("a", "value", aValue);
            db.updateValue("b", "value", bValue);
            db.updateValue("c", "value", cValue);
            System.out.println("Variables updated with 'value' data: a=" + aValue + ", b=" + bValue + ", c=" + cValue);

            // Update both "value" and "query" for variables
            db.populateQueryVariables(); // This will set calculated values for query column
            System.out.println("'query' column populated for each variable.");

            // Export data to files
            db.exportToSQL();

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            throw new RuntimeException("Database operation failed", e);
        } finally {
            if (db != null) {
                try {
                    db.close();
                } catch (SQLException e) {
                    System.out.println("Error closing database: " + e.getMessage());
                }
            }
        }
    }
}