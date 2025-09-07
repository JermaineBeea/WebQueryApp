package co.za.Main.TradeModules;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TradeFunction {

    BigDecimal spread;
    BigDecimal rateKA;
    BigDecimal ratePN;
    boolean basedOnMarketRate = false; // Default to false (execution-based)

    // For basedOnMarketRate = false (default), sell/Buy variable is based on execution rate
    // where sellExecution rate = marketSellRate - (spread/2)
    // and buyExecution rate = marketBuyRate + (spread/2)
    // For basedOnMarketRate = true, sell/Buy variable is based on market rate

    public TradeFunction(BigDecimal spread, BigDecimal rateKA, BigDecimal ratePN) {
        this.spread = spread;
        this.rateKA = rateKA;
        this.ratePN = ratePN;
        this.basedOnMarketRate = false; // Default to execution-based
    }

    public void setBasedOnMarketRate(boolean basedOnMarketRate) {
        this.basedOnMarketRate = basedOnMarketRate;
    }

    public boolean isBasedOnMarketRate() {
        return basedOnMarketRate;
    }

    public BigDecimal returnProfit(BigDecimal tradeAmount, BigDecimal sellVariable, BigDecimal buyVariable) {
        if (buyVariable.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Buy variable cannot be zero");
        }
        if(basedOnMarketRate) {
            sellVariable = sellVariable.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP)); 
            buyVariable = buyVariable.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));
        }
        BigDecimal ratio = sellVariable.divide(buyVariable, 10, RoundingMode.HALF_UP);
        return tradeAmount.multiply(rateKA).multiply(ratePN).multiply(ratio.subtract(BigDecimal.ONE));
    }

    public BigDecimal returnProfitFactor(BigDecimal sellVariable, BigDecimal buyVariable) {
        if (buyVariable.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Buy variable cannot be zero");
        }
        if(basedOnMarketRate) {
            sellVariable = sellVariable.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP)); 
            buyVariable = buyVariable.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));
        }
        BigDecimal ratio = sellVariable.divide(buyVariable, 10, RoundingMode.HALF_UP);
        return ratio.subtract(BigDecimal.ONE);
    }

    public BigDecimal returnTradeAmount(BigDecimal tradeProfit, BigDecimal sellVariable, BigDecimal buyVariable){
        if (buyVariable.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Buy variable cannot be zero");
        }
        if(basedOnMarketRate) {
            sellVariable = sellVariable.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP)); 
            buyVariable = buyVariable.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP));
        }
        BigDecimal coefficient = sellVariable.divide(buyVariable, 10, RoundingMode.HALF_UP).subtract(BigDecimal.ONE);
        if (coefficient.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Coefficient cannot be zero");
        }
        return tradeProfit.divide(rateKA.multiply(ratePN).multiply(coefficient), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnSellVariable(BigDecimal tradeAmount, BigDecimal tradeProfit, BigDecimal buyVariable) {
        BigDecimal denominator = tradeAmount.multiply(rateKA).multiply(ratePN);
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }
        if(basedOnMarketRate) {
            buyVariable = buyVariable.add(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP)); 
        } 
        return buyVariable.multiply(tradeProfit.divide(denominator, 10, RoundingMode.HALF_UP).add(BigDecimal.ONE));
    }

    public BigDecimal returnBuyVariable(BigDecimal tradeAmount, BigDecimal tradeProfit, BigDecimal sellVariable) {
        BigDecimal denominator = tradeAmount.multiply(rateKA).multiply(ratePN);
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }
        if(basedOnMarketRate) {
            sellVariable = sellVariable.subtract(spread.divide(BigDecimal.valueOf(2), 10, RoundingMode.HALF_UP)); 
        }
        BigDecimal divisor = tradeProfit.divide(denominator, 10, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Divisor cannot be zero");
        }
        return sellVariable.divide(divisor, 10, RoundingMode.HALF_UP);
    }
}