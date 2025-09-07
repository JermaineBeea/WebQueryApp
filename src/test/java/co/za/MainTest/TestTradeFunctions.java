package co.za.MainTest;

import java.math.BigDecimal;
import org.junit.jupiter.api.*;
import co.za.Main.TradeModules.TradeFunction;
import java.math.RoundingMode;
import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TestTradeFunctions {

    Logger logger = Logger.getLogger(TestTradeFunctions.class.getName());

    {
        logger.setLevel(Level.ALL);
    }

    BigDecimal tradeAmount = new BigDecimal("10000");
    BigDecimal tradeProfit = new BigDecimal("-88.000000000");
    BigDecimal tradeProfitFactor = new BigDecimal("-0.000497021");
    BigDecimal spread = new BigDecimal("0.01");
    BigDecimal ratePN = new BigDecimal("1.0");
    BigDecimal rateKA = new BigDecimal("17.7055");
    BigDecimal sellVariable = new BigDecimal("17.6967");
    BigDecimal buyVariable = new BigDecimal("17.7055");

    TradeFunction tradeFunction = new TradeFunction(spread, rateKA, ratePN);
    BigDecimal TOLERANCE = new BigDecimal("0.01");

    @BeforeEach
    public void setUp() {
        // Set to execution-based mode (basedOnMarketRate = false) for consistent test results
        tradeFunction.setBasedOnMarketRate(false);
    }

    @Test
    public void testReturnProfit() {
        BigDecimal actualProfit = tradeFunction.returnProfit(tradeAmount, sellVariable, buyVariable)
                .setScale(10, RoundingMode.HALF_UP);
        
        // Use compareTo-based fuzzy check
        BigDecimal diff = tradeProfit.subtract(actualProfit).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + tradeProfit + ", Actual: " + actualProfit + ", Diff: " + diff);

        logger.info("Expected: " + tradeProfit);
        logger.info("Actual: " + actualProfit);
    }

    @Test
    public void testReturnProfitFactor() {
        BigDecimal actualProfitFactor = tradeFunction.returnProfitFactor(sellVariable, buyVariable)
                .setScale(10, RoundingMode.HALF_UP);
        
        BigDecimal diff = tradeProfitFactor.subtract(actualProfitFactor).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + tradeProfitFactor + ", Actual: " + actualProfitFactor + ", Diff: " + diff);

        logger.info("Expected Profit Factor: " + tradeProfitFactor);
        logger.info("Actual Profit Factor: " + actualProfitFactor);
    }

    @Test
    public void testReturnTradeAmount() {
        BigDecimal actualTradeAmount = tradeFunction.returnTradeAmount(tradeProfit, sellVariable, buyVariable)
                .setScale(10, RoundingMode.HALF_UP);
        
        // Use compareTo-based fuzzy check
        BigDecimal diff = tradeAmount.subtract(actualTradeAmount).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + tradeAmount + ", Actual: " + actualTradeAmount + ", Diff: " + diff);

        logger.info("Expected Trade Amount: " + tradeAmount);
        logger.info("Actual Trade Amount: " + actualTradeAmount);
    }

    @Test
    public void testReturnSellVariable() {
        BigDecimal tradeProfit = new BigDecimal("-88.000000000");
        BigDecimal actualSellVariable = tradeFunction.returnSellVariable(tradeAmount, tradeProfit, buyVariable)
                .setScale(10, RoundingMode.HALF_UP);

        BigDecimal diff = sellVariable.subtract(actualSellVariable).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
                "Expected: " + sellVariable + ", Actual: " + actualSellVariable + ", Diff: " + diff);

        logger.info("Expected Sell Variable: " + sellVariable);
        logger.info("Actual Sell Variable: " + actualSellVariable);
    }

    @Test
    public void testReturnBuyVariable() {
        BigDecimal actualBuyVariable = tradeFunction.returnBuyVariable(tradeAmount, tradeProfit, sellVariable)
                .setScale(10, RoundingMode.HALF_UP);

        BigDecimal diff = buyVariable.subtract(actualBuyVariable).abs();

        assertTrue(diff.compareTo(TOLERANCE) <= 0,
            "Expected: " + buyVariable + ", Actual: " + actualBuyVariable + ", Diff: " + diff);

        logger.info("Expected Buy Variable: " + buyVariable);
        logger.info("Actual Buy Variable: " + actualBuyVariable);
    }

    @Test
    public void testMarketRateMode() {
        // Test that the setter works
        tradeFunction.setBasedOnMarketRate(true);
        assertTrue(tradeFunction.isBasedOnMarketRate(), "Should be in market rate mode");

        tradeFunction.setBasedOnMarketRate(false);
        assertFalse(tradeFunction.isBasedOnMarketRate(), "Should be in execution rate mode");
        
        logger.info("Market rate mode toggle test passed");
    }
}