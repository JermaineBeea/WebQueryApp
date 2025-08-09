package co.za.Main.TradeFunctions;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TradeFunction {

    BigDecimal spread;
    BigDecimal rateKA;
    BigDecimal ratePN;
    boolean basedOnExecution = true;

    public TradeFunction(boolean basedOnExecution, BigDecimal spread, BigDecimal rateKA, BigDecimal ratePN) {
        this.spread = spread;
        this.rateKA = rateKA;
        this.ratePN = ratePN;
        this.basedOnExecution = basedOnExecution;
    }

    public BigDecimal returnProfit(BigDecimal tradeAmount, BigDecimal sellVariable, BigDecimal buyVariable) {
        if (buyVariable.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Buy variable cannot be zero");
        }
        BigDecimal ratio = sellVariable.divide(buyVariable, 10, RoundingMode.HALF_UP);
        return tradeAmount.multiply(rateKA).multiply(ratePN).multiply(ratio.subtract(BigDecimal.ONE));
    }

    public BigDecimal returnProfitFactor(BigDecimal sellVariable, BigDecimal buyVariable) {
        if (buyVariable.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Buy variable cannot be zero");
        }
        BigDecimal ratio = sellVariable.divide(buyVariable, 10, RoundingMode.HALF_UP);
        return ratio.subtract(BigDecimal.ONE);
    }

    public BigDecimal returnTradeAmount(BigDecimal tradeProfit, BigDecimal sellVariable, BigDecimal buyVariable){
        if (buyVariable.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Buy variable cannot be zero");
        }
        BigDecimal coefficient = sellVariable.divide(buyVariable, 10, RoundingMode.HALF_UP).subtract(BigDecimal.ONE);
        if (coefficient.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Coefficient cannot be zero");
        }
        return tradeProfit.divide(rateKA.multiply(ratePN).multiply(coefficient), 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnFactorTradeAmount(BigDecimal profitFactor, BigDecimal tradeProfit) {
        BigDecimal denominator = profitFactor.multiply(rateKA).multiply(ratePN);
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }
        return tradeProfit.divide(denominator, 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnSellVariable(BigDecimal tradeAmount, BigDecimal tradeProfit, BigDecimal buyVariable) {
        BigDecimal denominator = tradeAmount.multiply(rateKA).multiply(ratePN);
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }
        return buyVariable.multiply(tradeProfit.divide(denominator, 10, RoundingMode.HALF_UP).add(BigDecimal.ONE));
    }

    public BigDecimal returnFactorSellVariable(BigDecimal profitFactor, BigDecimal buyVariable){
        return buyVariable.multiply(profitFactor.add(BigDecimal.ONE));
    }

    public BigDecimal returnBuyVariable(BigDecimal tradeAmount, BigDecimal tradeProfit, BigDecimal sellVariable) {
        BigDecimal denominator = tradeAmount.multiply(rateKA).multiply(ratePN);
        if (denominator.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Denominator cannot be zero");
        }
        BigDecimal divisor = tradeProfit.divide(denominator, 10, RoundingMode.HALF_UP).add(BigDecimal.ONE);
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Divisor cannot be zero");
        }
        return sellVariable.divide(divisor, 10, RoundingMode.HALF_UP);
    }

    public BigDecimal returnFactorBuyVariable(BigDecimal profitFactor, BigDecimal sellVariable) {
        BigDecimal divisor = profitFactor.add(BigDecimal.ONE);
        if (divisor.compareTo(BigDecimal.ZERO) == 0) {
            throw new ArithmeticException("Divisor cannot be zero");
        }
        return sellVariable.divide(divisor, 10, RoundingMode.HALF_UP);
    }
}