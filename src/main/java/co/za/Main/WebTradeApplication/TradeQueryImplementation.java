package  co.za.Main.WebTradeApplication;

import java.math.BigDecimal;
import java.sql.SQLException;
import co.za.Main.TradeModules.TradeFunction;

public class TradeQueryImplementation {
    
    private boolean basedOnExecution;
    private TradeFunction tradeFunction;
    
    // Default trade parameters
    private BigDecimal spread = new BigDecimal("0.001");  
    private BigDecimal rateKA = new BigDecimal("0.95");   
    private BigDecimal ratePN = new BigDecimal("0.98");   
    
    public TradeQueryImplementation(boolean basedOnExecution) {
        this.basedOnExecution = basedOnExecution;
        this.tradeFunction = new TradeFunction(basedOnExecution, spread, rateKA, ratePN);
    }
    
    public void populateTable(TradeVariableDatabase db) throws SQLException {
        System.out.println("Running trade calculations with basedOnExecution: " + basedOnExecution);
        
        try {
            // Get all min/max values from database
            BigDecimal tradeProfitMax = db.getValueFromColumn("tradeprofit", "maximum");
            BigDecimal tradeProfitMin = db.getValueFromColumn("tradeprofit", "minimum");
            BigDecimal profitFactorMin = db.getValueFromColumn("profitfactor", "minimum");
            BigDecimal profitFactorMax = db.getValueFromColumn("profitfactor", "maximum");
            BigDecimal tradeAmountMax = db.getValueFromColumn("tradeamount", "maximum");
            BigDecimal tradeAmountMin = db.getValueFromColumn("tradeamount", "minimum");
            BigDecimal buyVariableMin = db.getValueFromColumn("buyvariable", "minimum");
            BigDecimal buyVariableMax = db.getValueFromColumn("buyvariable", "maximum");
            BigDecimal sellVariableMin = db.getValueFromColumn("sellvariable", "minimum");
            BigDecimal sellVariableMax = db.getValueFromColumn("sellvariable", "maximum");

            System.out.println("Retrieved values from database:");
            System.out.println("Trade Profit: " + tradeProfitMin + " to " + tradeProfitMax);
            System.out.println("Buy Variable: " + buyVariableMin + " to " + buyVariableMax);
            System.out.println("Sell Variable: " + sellVariableMin + " to " + sellVariableMax);

            // Calculate and update tradeprofit
            try {
                if (buyVariableMax.compareTo(BigDecimal.ZERO) > 0 && sellVariableMin.compareTo(BigDecimal.ZERO) > 0 &&
                    buyVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                    
                    BigDecimal tradeProfitFactorMin = tradeFunction.returnProfitFactor(sellVariableMin, buyVariableMax);
                    BigDecimal tradeProfitFactorMax = tradeFunction.returnProfitFactor(sellVariableMax, buyVariableMin);
                    BigDecimal tradeProfitMinResult = tradeFunction.returnProfit(tradeAmountMin, sellVariableMin, buyVariableMax);
                    BigDecimal tradeProfitMaxResult = tradeFunction.returnProfit(tradeAmountMax, sellVariableMax, buyVariableMin);
                    
                    db.updateQueryResult("tradeprofit", tradeProfitFactorMin, tradeProfitFactorMax, 
                                       tradeProfitMinResult, tradeProfitMaxResult);
                    System.out.println("Updated tradeprofit calculations");
                } else {
                    System.out.println("Skipping tradeprofit calculation - invalid input values");
                    db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                }
            } catch (ArithmeticException e) {
                System.out.println("Error calculating tradeprofit: " + e.getMessage());
                db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }

            // Calculate and update profitfactor
            try {
                if (tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal factorReturnMin = tradeFunction.returnFactorBasedOnAmount(tradeProfitMin, tradeAmountMax);
                    BigDecimal factorReturnMax = tradeFunction.returnFactorBasedOnAmount(tradeProfitMax, tradeAmountMin);
                    
                    db.updateQueryResult("profitfactor", profitFactorMin, profitFactorMax, 
                                       factorReturnMin, factorReturnMax);
                    System.out.println("Updated profitfactor calculations");
                } else {
                    System.out.println("Skipping profitfactor calculation - invalid trade amount values");
                    db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                }
            } catch (ArithmeticException e) {
                System.out.println("Error calculating profitfactor: " + e.getMessage());
                db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }

            // Calculate and update tradeamount
            try {
                if (profitFactorMin.compareTo(BigDecimal.ZERO) != 0 && profitFactorMax.compareTo(BigDecimal.ZERO) != 0 &&
                    buyVariableMax.compareTo(BigDecimal.ZERO) > 0 && buyVariableMin.compareTo(BigDecimal.ZERO) > 0 &&
                    sellVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                    
                    BigDecimal tradeAmountProfitFactorMin = tradeFunction.returnFactorTradeAmount(profitFactorMin, tradeProfitMax);
                    BigDecimal tradeAmountProfitFactorMax = tradeFunction.returnFactorTradeAmount(profitFactorMax, tradeProfitMin);
                    BigDecimal tradeAmountProfitMinResult = tradeFunction.returnTradeAmount(tradeProfitMin, sellVariableMin, buyVariableMax);
                    BigDecimal tradeAmountProfitMaxResult = tradeFunction.returnTradeAmount(tradeProfitMax, sellVariableMax, buyVariableMin);
                    
                    db.updateQueryResult("tradeamount", tradeAmountProfitFactorMin, tradeAmountProfitFactorMax, 
                                       tradeAmountProfitMinResult, tradeAmountProfitMaxResult);
                    System.out.println("Updated tradeamount calculations");
                } else {
                    System.out.println("Skipping tradeamount calculation - invalid input values");
                    db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                }
            } catch (ArithmeticException e) {
                System.out.println("Error calculating tradeamount: " + e.getMessage());
                db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }

            // Calculate and update sellvariable
            try {
                if (tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0 &&
                    buyVariableMin.compareTo(BigDecimal.ZERO) > 0 && buyVariableMax.compareTo(BigDecimal.ZERO) > 0 &&
                    profitFactorMin.compareTo(BigDecimal.ZERO) != 0 && profitFactorMax.compareTo(BigDecimal.ZERO) != 0) {
                    
                    BigDecimal sellVariableProfitMinResult = tradeFunction.returnSellVariable(tradeAmountMax, tradeProfitMin, buyVariableMin);
                    BigDecimal sellVariableProfitMaxResult = tradeFunction.returnSellVariable(tradeAmountMin, tradeProfitMax, buyVariableMax);
                    BigDecimal sellVariableProfitFactorMin = tradeFunction.returnFactorSellVariable(profitFactorMin, buyVariableMax);
                    BigDecimal sellVariableProfitFactorMax = tradeFunction.returnFactorSellVariable(profitFactorMax, buyVariableMin);
                    
                    db.updateQueryResult("sellvariable", sellVariableProfitFactorMin, sellVariableProfitFactorMax, 
                                       sellVariableProfitMinResult, sellVariableProfitMaxResult);
                    System.out.println("Updated sellvariable calculations");
                } else {
                    System.out.println("Skipping sellvariable calculation - invalid input values");
                    db.updateQueryResult("sellvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                }
            } catch (ArithmeticException e) {
                System.out.println("Error calculating sellvariable: " + e.getMessage());
                db.updateQueryResult("sellvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }

            // Calculate and update buyvariable
            try {
                if (profitFactorMax.compareTo(BigDecimal.ZERO) != 0 && profitFactorMin.compareTo(BigDecimal.ZERO) != 0 &&
                    sellVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0 &&
                    tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0) {
                    
                    BigDecimal buyVariableProfitFactorMin = tradeFunction.returnFactorBuyVariable(profitFactorMax, sellVariableMin);
                    BigDecimal buyVariableProfitFactorMax = tradeFunction.returnFactorBuyVariable(profitFactorMin, sellVariableMax);
                    BigDecimal buyVariableProfitMinResult = tradeFunction.returnBuyVariable(tradeAmountMax, tradeProfitMin, sellVariableMin);
                    BigDecimal buyVariableProfitMaxResult = tradeFunction.returnBuyVariable(tradeAmountMin, tradeProfitMax, sellVariableMax);
                    
                    db.updateQueryResult("buyvariable", buyVariableProfitFactorMin, buyVariableProfitFactorMax, 
                                       buyVariableProfitMinResult, buyVariableProfitMaxResult);
                    System.out.println("Updated buyvariable calculations");
                } else {
                    System.out.println("Skipping buyvariable calculation - invalid input values");
                    db.updateQueryResult("buyvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                }
            } catch (ArithmeticException e) {
                System.out.println("Error calculating buyvariable: " + e.getMessage());
                db.updateQueryResult("buyvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }

            // Export results
            db.exportToSQL();
            System.out.println("Trade calculations completed successfully");
            
        } catch (Exception e) {
            System.err.println("Unexpected error during calculations: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Calculation failed: " + e.getMessage(), e);
        }
    }
}