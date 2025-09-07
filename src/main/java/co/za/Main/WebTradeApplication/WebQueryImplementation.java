package co.za.Main.WebTradeApplication;

import java.math.BigDecimal;
import java.sql.SQLException;
import co.za.Main.TradeModules.TradeFunction;

public class WebQueryImplementation {
    
    private TradeFunction tradeFunction;
    
    public WebQueryImplementation(boolean basedOnMarketRate, BigDecimal spread, BigDecimal rateKA, BigDecimal ratePN) {
        this.tradeFunction = new TradeFunction(spread, rateKA, ratePN);
        this.tradeFunction.setBasedOnMarketRate(basedOnMarketRate);
        
        System.out.println("=== Enhanced Trade Query Implementation Initialized ===");
        System.out.println("Based on Market Rate: " + basedOnMarketRate);
        System.out.println("Spread: " + spread);
        System.out.println("Rate KA: " + rateKA);
        System.out.println("Rate PN: " + ratePN);
    }
    
    public void populateTable(WebAppDataBase db) throws SQLException {
        System.out.println("=== Running Enhanced Trade Calculations ===");
        System.out.println("Calculation Mode: " + (tradeFunction.isBasedOnMarketRate() ? "MARKET-BASED" : "EXECUTION-BASED"));
        
        try {
            // FIXED: Refresh input values first to get current state from web interface
            db.refreshInputValues();
            
            // Get all min/max values from database (these are the current input values from the web interface)
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

            System.out.println("Current Input Values Retrieved:");
            System.out.println("- Trade Profit: " + tradeProfitMin + " to " + tradeProfitMax);
            System.out.println("- Trade Amount: " + tradeAmountMin + " to " + tradeAmountMax);
            System.out.println("- Buy Variable: " + buyVariableMin + " to " + buyVariableMax);
            System.out.println("- Sell Variable: " + sellVariableMin + " to " + sellVariableMax);
            System.out.println("- Profit Factor: " + profitFactorMin + " to " + profitFactorMax);

            // Calculate and update tradeprofit
            calculateTradeProfitValues(db, tradeAmountMin, tradeAmountMax, 
                                     buyVariableMin, buyVariableMax, 
                                     sellVariableMin, sellVariableMax);

            // Calculate and update profitfactor
            calculateProfitFactorValues(db, buyVariableMin, buyVariableMax, 
                                      sellVariableMin, sellVariableMax);

            // Calculate and update tradeamount
            calculateTradeAmountValues(db, tradeProfitMin, tradeProfitMax, 
                                     buyVariableMin, buyVariableMax, 
                                     sellVariableMin, sellVariableMax);

            // Calculate and update sellvariable
            calculateSellVariableValues(db, tradeProfitMin, tradeProfitMax, 
                                      tradeAmountMin, tradeAmountMax, 
                                      buyVariableMin, buyVariableMax);

            // Calculate and update buyvariable
            calculateBuyVariableValues(db, tradeProfitMin, tradeProfitMax, 
                                     tradeAmountMin, tradeAmountMax, 
                                     sellVariableMin, sellVariableMax);

            // Export results
            db.exportToSQL();
            System.out.println("=== Enhanced Trade calculations completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Error during enhanced calculations: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Enhanced calculation failed: " + e.getMessage(), e);
        }
    }
    
    private void calculateTradeProfitValues(WebAppDataBase db, 
                                          BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                          BigDecimal buyVariableMin, BigDecimal buyVariableMax,
                                          BigDecimal sellVariableMin, BigDecimal sellVariableMax) throws SQLException {
        try {
            if (buyVariableMax.compareTo(BigDecimal.ZERO) > 0 && sellVariableMin.compareTo(BigDecimal.ZERO) > 0 &&
                buyVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                
                // TradeFunction handles the market rate mode internally
                BigDecimal tradeProfitMinResult = tradeFunction.returnProfit(tradeAmountMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeProfitMaxResult = tradeFunction.returnProfit(tradeAmountMax, sellVariableMax, buyVariableMin);
                
                db.updateQueryResult("tradeprofit", tradeProfitMinResult, tradeProfitMaxResult);
                System.out.println("✅ Updated tradeprofit calculations (Mode: " + 
                                 (tradeFunction.isBasedOnMarketRate() ? "MARKET" : "EXECUTION") + ")");
            } else {
                System.out.println("⚠️ Skipping tradeprofit calculation - invalid input values");
                db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating tradeprofit: " + e.getMessage());
            db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateProfitFactorValues(WebAppDataBase db,
                                           BigDecimal buyVariableMin, BigDecimal buyVariableMax,
                                           BigDecimal sellVariableMin, BigDecimal sellVariableMax) throws SQLException {
        try {
            if (buyVariableMax.compareTo(BigDecimal.ZERO) > 0 && buyVariableMin.compareTo(BigDecimal.ZERO) > 0 &&
                sellVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                
                // TradeFunction handles the market rate mode internally
                BigDecimal profitFactorMinResult = tradeFunction.returnProfitFactor(sellVariableMin, buyVariableMax);
                BigDecimal profitFactorMaxResult = tradeFunction.returnProfitFactor(sellVariableMax, buyVariableMin);
                
                db.updateQueryResult("profitfactor", profitFactorMinResult, profitFactorMaxResult);
                System.out.println("✅ Updated profitfactor calculations (Mode: " + 
                                 (tradeFunction.isBasedOnMarketRate() ? "MARKET" : "EXECUTION") + ")");
            } else {
                System.out.println("⚠️ Skipping profitfactor calculation - invalid input values");
                db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating profitfactor: " + e.getMessage());
            db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateTradeAmountValues(WebAppDataBase db,
                                          BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                          BigDecimal buyVariableMin, BigDecimal buyVariableMax,
                                          BigDecimal sellVariableMin, BigDecimal sellVariableMax) throws SQLException {
        try {
            if (buyVariableMax.compareTo(BigDecimal.ZERO) > 0 && buyVariableMin.compareTo(BigDecimal.ZERO) > 0 &&
                sellVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                
                // TradeFunction handles the market rate mode internally
                BigDecimal tradeAmountProfitMinResult = tradeFunction.returnTradeAmount(tradeProfitMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeAmountProfitMaxResult = tradeFunction.returnTradeAmount(tradeProfitMax, sellVariableMax, buyVariableMin);
                
                db.updateQueryResult("tradeamount", tradeAmountProfitMinResult, tradeAmountProfitMaxResult);
                System.out.println("✅ Updated tradeamount calculations (Mode: " + 
                                 (tradeFunction.isBasedOnMarketRate() ? "MARKET" : "EXECUTION") + ")");
            } else {
                System.out.println("⚠️ Skipping tradeamount calculation - invalid input values");
                db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating tradeamount: " + e.getMessage());
            db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateSellVariableValues(WebAppDataBase db,
                                           BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                           BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                           BigDecimal buyVariableMin, BigDecimal buyVariableMax) throws SQLException {
        try {
            if (tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0 &&
                buyVariableMin.compareTo(BigDecimal.ZERO) > 0 && buyVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                
                // TradeFunction handles the market rate mode internally
                BigDecimal sellVariableProfitMinResult = tradeFunction.returnSellVariable(tradeAmountMax, tradeProfitMin, buyVariableMin);
                BigDecimal sellVariableProfitMaxResult = tradeFunction.returnSellVariable(tradeAmountMin, tradeProfitMax, buyVariableMax);
                
                db.updateQueryResult("sellvariable", sellVariableProfitMinResult, sellVariableProfitMaxResult);
                System.out.println("✅ Updated sellvariable calculations (Mode: " + 
                                 (tradeFunction.isBasedOnMarketRate() ? "MARKET" : "EXECUTION") + ")");
            } else {
                System.out.println("⚠️ Skipping sellvariable calculation - invalid input values");
                db.updateQueryResult("sellvariable", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating sellvariable: " + e.getMessage());
            db.updateQueryResult("sellvariable", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateBuyVariableValues(WebAppDataBase db,
                                          BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                          BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                          BigDecimal sellVariableMin, BigDecimal sellVariableMax) throws SQLException {
        try {
            if (sellVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0 &&
                tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0) {
                
                // TradeFunction handles the market rate mode internally
                BigDecimal buyVariableProfitMinResult = tradeFunction.returnBuyVariable(tradeAmountMax, tradeProfitMin, sellVariableMin);
                BigDecimal buyVariableProfitMaxResult = tradeFunction.returnBuyVariable(tradeAmountMin, tradeProfitMax, sellVariableMax);
                
                db.updateQueryResult("buyvariable", buyVariableProfitMinResult, buyVariableProfitMaxResult);
                System.out.println("✅ Updated buyvariable calculations (Mode: " + 
                                 (tradeFunction.isBasedOnMarketRate() ? "MARKET" : "EXECUTION") + ")");
            } else {
                System.out.println("⚠️ Skipping buyvariable calculation - invalid input values");
                db.updateQueryResult("buyvariable", BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating buyvariable: " + e.getMessage());
            db.updateQueryResult("buyvariable", BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}