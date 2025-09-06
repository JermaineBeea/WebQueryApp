package co.za.Main.WebTradeApplication;

import java.math.BigDecimal;
import java.sql.SQLException;
import co.za.Main.TradeModules.TradeFunction;

public class EnhancedTradeQueryImplementation {
    
    private boolean basedOnExecution;
    private TradeFunction tradeFunction;
    private BigDecimal spread;
    private BigDecimal rateKA;
    private BigDecimal ratePN;
    
    public EnhancedTradeQueryImplementation(boolean basedOnExecution, BigDecimal spread, BigDecimal rateKA, BigDecimal ratePN) {
        this.basedOnExecution = basedOnExecution;
        this.spread = spread;
        this.rateKA = rateKA;
        this.ratePN = ratePN;
        this.tradeFunction = new TradeFunction(basedOnExecution, spread, rateKA, ratePN);
        
        System.out.println("=== Enhanced Trade Query Implementation Initialized ===");
        System.out.println("Based on Execution: " + basedOnExecution);
        System.out.println("Spread: " + spread);
        System.out.println("Rate KA: " + rateKA);
        System.out.println("Rate PN: " + ratePN);
    }
    
    public void populateTable(TradeVariableDatabase db) throws SQLException {
        System.out.println("=== Running Enhanced Trade Calculations ===");
        System.out.println("Execution Mode: " + (basedOnExecution ? "EXECUTION-BASED" : "STANDARD"));
        
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

            System.out.println("Input Values Retrieved:");
            System.out.println("- Trade Profit: " + tradeProfitMin + " to " + tradeProfitMax);
            System.out.println("- Trade Amount: " + tradeAmountMin + " to " + tradeAmountMax);
            System.out.println("- Buy Variable: " + buyVariableMin + " to " + buyVariableMax);
            System.out.println("- Sell Variable: " + sellVariableMin + " to " + sellVariableMax);

            // Calculate and update tradeprofit
            calculateTradeProfitValues(db, tradeAmountMin, tradeAmountMax, 
                                     buyVariableMin, buyVariableMax, 
                                     sellVariableMin, sellVariableMax);

            // Calculate and update profitfactor
            calculateProfitFactorValues(db, tradeProfitMin, tradeProfitMax, 
                                      tradeAmountMin, tradeAmountMax, 
                                      profitFactorMin, profitFactorMax);

            // Calculate and update tradeamount
            calculateTradeAmountValues(db, tradeProfitMin, tradeProfitMax, 
                                     buyVariableMin, buyVariableMax, 
                                     sellVariableMin, sellVariableMax, 
                                     profitFactorMin, profitFactorMax);

            // Calculate and update sellvariable
            calculateSellVariableValues(db, tradeProfitMin, tradeProfitMax, 
                                      tradeAmountMin, tradeAmountMax, 
                                      buyVariableMin, buyVariableMax, 
                                      profitFactorMin, profitFactorMax);

            // Calculate and update buyvariable
            calculateBuyVariableValues(db, tradeProfitMin, tradeProfitMax, 
                                     tradeAmountMin, tradeAmountMax, 
                                     sellVariableMin, sellVariableMax, 
                                     profitFactorMin, profitFactorMax);

            // Export results
            db.exportToSQL();
            System.out.println("=== Enhanced Trade calculations completed successfully ===");
            
        } catch (Exception e) {
            System.err.println("Error during enhanced calculations: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Enhanced calculation failed: " + e.getMessage(), e);
        }
    }
    
    private void calculateTradeProfitValues(TradeVariableDatabase db, 
                                          BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                          BigDecimal buyVariableMin, BigDecimal buyVariableMax,
                                          BigDecimal sellVariableMin, BigDecimal sellVariableMax) throws SQLException {
        try {
            if (buyVariableMax.compareTo(BigDecimal.ZERO) > 0 && sellVariableMin.compareTo(BigDecimal.ZERO) > 0 &&
                buyVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                
                BigDecimal tradeProfitFactorMin = tradeFunction.returnProfitFactor(sellVariableMin, buyVariableMax);
                BigDecimal tradeProfitFactorMax = tradeFunction.returnProfitFactor(sellVariableMax, buyVariableMin);
                
                // Apply execution mode modifier
                if (basedOnExecution) {
                    // In execution mode, apply spread effects
                    BigDecimal spreadEffect = BigDecimal.ONE.subtract(spread);
                    tradeProfitFactorMin = tradeProfitFactorMin.multiply(spreadEffect);
                    tradeProfitFactorMax = tradeProfitFactorMax.multiply(spreadEffect);
                    System.out.println("Applied execution-based spread effect: " + spreadEffect);
                }
                
                BigDecimal tradeProfitMinResult = tradeFunction.returnProfit(tradeAmountMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeProfitMaxResult = tradeFunction.returnProfit(tradeAmountMax, sellVariableMax, buyVariableMin);
                
                db.updateQueryResult("tradeprofit", tradeProfitFactorMin, tradeProfitFactorMax, 
                                   tradeProfitMinResult, tradeProfitMaxResult);
                System.out.println("✅ Updated tradeprofit calculations (Mode: " + 
                                 (basedOnExecution ? "EXECUTION" : "STANDARD") + ")");
            } else {
                System.out.println("⚠️ Skipping tradeprofit calculation - invalid input values");
                db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating tradeprofit: " + e.getMessage());
            db.updateQueryResult("tradeprofit", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateProfitFactorValues(TradeVariableDatabase db,
                                           BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                           BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                           BigDecimal profitFactorMin, BigDecimal profitFactorMax) throws SQLException {
        try {
            if (tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal factorReturnMin = tradeFunction.returnFactorBasedOnAmount(tradeProfitMin, tradeAmountMax);
                BigDecimal factorReturnMax = tradeFunction.returnFactorBasedOnAmount(tradeProfitMax, tradeAmountMin);
                
                // Apply execution mode modifier to profit factors
                if (basedOnExecution) {
                    // In execution mode, reduce profit factors due to execution costs
                    BigDecimal executionCostFactor = BigDecimal.ONE.subtract(spread.multiply(new BigDecimal("0.5")));
                    factorReturnMin = factorReturnMin.multiply(executionCostFactor);
                    factorReturnMax = factorReturnMax.multiply(executionCostFactor);
                    System.out.println("Applied execution-based cost factor: " + executionCostFactor);
                }
                
                db.updateQueryResult("profitfactor", profitFactorMin, profitFactorMax, 
                                   factorReturnMin, factorReturnMax);
                System.out.println("✅ Updated profitfactor calculations (Mode: " + 
                                 (basedOnExecution ? "EXECUTION" : "STANDARD") + ")");
            } else {
                System.out.println("⚠️ Skipping profitfactor calculation - invalid trade amount values");
                db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating profitfactor: " + e.getMessage());
            db.updateQueryResult("profitfactor", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateTradeAmountValues(TradeVariableDatabase db,
                                          BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                          BigDecimal buyVariableMin, BigDecimal buyVariableMax,
                                          BigDecimal sellVariableMin, BigDecimal sellVariableMax,
                                          BigDecimal profitFactorMin, BigDecimal profitFactorMax) throws SQLException {
        try {
            if (profitFactorMin.compareTo(BigDecimal.ZERO) != 0 && profitFactorMax.compareTo(BigDecimal.ZERO) != 0 &&
                buyVariableMax.compareTo(BigDecimal.ZERO) > 0 && buyVariableMin.compareTo(BigDecimal.ZERO) > 0 &&
                sellVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0) {
                
                BigDecimal tradeAmountProfitFactorMin = tradeFunction.returnFactorTradeAmount(profitFactorMin, tradeProfitMax);
                BigDecimal tradeAmountProfitFactorMax = tradeFunction.returnFactorTradeAmount(profitFactorMax, tradeProfitMin);
                BigDecimal tradeAmountProfitMinResult = tradeFunction.returnTradeAmount(tradeProfitMin, sellVariableMin, buyVariableMax);
                BigDecimal tradeAmountProfitMaxResult = tradeFunction.returnTradeAmount(tradeProfitMax, sellVariableMax, buyVariableMin);
                
                // Apply execution mode adjustments
                if (basedOnExecution) {
                    // In execution mode, increase required trade amounts due to execution costs
                    BigDecimal executionMultiplier = BigDecimal.ONE.add(spread);
                    tradeAmountProfitMinResult = tradeAmountProfitMinResult.multiply(executionMultiplier);
                    tradeAmountProfitMaxResult = tradeAmountProfitMaxResult.multiply(executionMultiplier);
                    System.out.println("Applied execution-based amount multiplier: " + executionMultiplier);
                }
                
                db.updateQueryResult("tradeamount", tradeAmountProfitFactorMin, tradeAmountProfitFactorMax, 
                                   tradeAmountProfitMinResult, tradeAmountProfitMaxResult);
                System.out.println("✅ Updated tradeamount calculations (Mode: " + 
                                 (basedOnExecution ? "EXECUTION" : "STANDARD") + ")");
            } else {
                System.out.println("⚠️ Skipping tradeamount calculation - invalid input values");
                db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating tradeamount: " + e.getMessage());
            db.updateQueryResult("tradeamount", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateSellVariableValues(TradeVariableDatabase db,
                                           BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                           BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                           BigDecimal buyVariableMin, BigDecimal buyVariableMax,
                                           BigDecimal profitFactorMin, BigDecimal profitFactorMax) throws SQLException {
        try {
            if (tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0 &&
                buyVariableMin.compareTo(BigDecimal.ZERO) > 0 && buyVariableMax.compareTo(BigDecimal.ZERO) > 0 &&
                profitFactorMin.compareTo(BigDecimal.ZERO) != 0 && profitFactorMax.compareTo(BigDecimal.ZERO) != 0) {
                
                BigDecimal sellVariableProfitMinResult = tradeFunction.returnSellVariable(tradeAmountMax, tradeProfitMin, buyVariableMin);
                BigDecimal sellVariableProfitMaxResult = tradeFunction.returnSellVariable(tradeAmountMin, tradeProfitMax, buyVariableMax);
                BigDecimal sellVariableProfitFactorMin = tradeFunction.returnFactorSellVariable(profitFactorMin, buyVariableMax);
                BigDecimal sellVariableProfitFactorMax = tradeFunction.returnFactorSellVariable(profitFactorMax, buyVariableMin);
                
                // Apply execution mode adjustments to sell variables
                if (basedOnExecution) {
                    // In execution mode, adjust sell prices to account for bid-ask spread
                    BigDecimal bidAskAdjustment = BigDecimal.ONE.subtract(spread.multiply(new BigDecimal("0.5")));
                    sellVariableProfitMinResult = sellVariableProfitMinResult.multiply(bidAskAdjustment);
                    sellVariableProfitMaxResult = sellVariableProfitMaxResult.multiply(bidAskAdjustment);
                    System.out.println("Applied execution-based bid-ask adjustment: " + bidAskAdjustment);
                }
                
                db.updateQueryResult("sellvariable", sellVariableProfitFactorMin, sellVariableProfitFactorMax, 
                                   sellVariableProfitMinResult, sellVariableProfitMaxResult);
                System.out.println("✅ Updated sellvariable calculations (Mode: " + 
                                 (basedOnExecution ? "EXECUTION" : "STANDARD") + ")");
            } else {
                System.out.println("⚠️ Skipping sellvariable calculation - invalid input values");
                db.updateQueryResult("sellvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating sellvariable: " + e.getMessage());
            db.updateQueryResult("sellvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
    
    private void calculateBuyVariableValues(TradeVariableDatabase db,
                                          BigDecimal tradeProfitMin, BigDecimal tradeProfitMax,
                                          BigDecimal tradeAmountMin, BigDecimal tradeAmountMax,
                                          BigDecimal sellVariableMin, BigDecimal sellVariableMax,
                                          BigDecimal profitFactorMin, BigDecimal profitFactorMax) throws SQLException {
        try {
            if (profitFactorMax.compareTo(BigDecimal.ZERO) != 0 && profitFactorMin.compareTo(BigDecimal.ZERO) != 0 &&
                sellVariableMin.compareTo(BigDecimal.ZERO) > 0 && sellVariableMax.compareTo(BigDecimal.ZERO) > 0 &&
                tradeAmountMax.compareTo(BigDecimal.ZERO) > 0 && tradeAmountMin.compareTo(BigDecimal.ZERO) > 0) {
                
                BigDecimal buyVariableProfitFactorMin = tradeFunction.returnFactorBuyVariable(profitFactorMax, sellVariableMin);
                BigDecimal buyVariableProfitFactorMax = tradeFunction.returnFactorBuyVariable(profitFactorMin, sellVariableMax);
                BigDecimal buyVariableProfitMinResult = tradeFunction.returnBuyVariable(tradeAmountMax, tradeProfitMin, sellVariableMin);
                BigDecimal buyVariableProfitMaxResult = tradeFunction.returnBuyVariable(tradeAmountMin, tradeProfitMax, sellVariableMax);
                
                // Apply execution mode adjustments to buy variables
                if (basedOnExecution) {
                    // In execution mode, adjust buy prices to account for bid-ask spread
                    BigDecimal bidAskAdjustment = BigDecimal.ONE.add(spread.multiply(new BigDecimal("0.5")));
                    buyVariableProfitMinResult = buyVariableProfitMinResult.multiply(bidAskAdjustment);
                    buyVariableProfitMaxResult = buyVariableProfitMaxResult.multiply(bidAskAdjustment);
                    System.out.println("Applied execution-based buy price adjustment: " + bidAskAdjustment);
                }
                
                db.updateQueryResult("buyvariable", buyVariableProfitFactorMin, buyVariableProfitFactorMax, 
                                   buyVariableProfitMinResult, buyVariableProfitMaxResult);
                System.out.println("✅ Updated buyvariable calculations (Mode: " + 
                                 (basedOnExecution ? "EXECUTION" : "STANDARD") + ")");
            } else {
                System.out.println("⚠️ Skipping buyvariable calculation - invalid input values");
                db.updateQueryResult("buyvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        } catch (ArithmeticException e) {
            System.out.println("❌ Error calculating buyvariable: " + e.getMessage());
            db.updateQueryResult("buyvariable", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
    }
}