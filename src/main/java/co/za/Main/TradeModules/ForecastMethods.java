package co.za.Main.TradeModules;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ForecastMethods {

    List<BigDecimal> historicalPrices;

    public ForecastMethods(List<BigDecimal> historicalPrices) {
        this.historicalPrices = historicalPrices;
    }

    /**
     * This is a dummy placeholder method for forecasting distribution.
     * The actual implementation would depend on the forecasting model used.
     * @return A list of 3 BigDecimal representing the forecasted distribution. The values are for representation only.
     */
    public List<BigDecimal> forecastDistribution() {
        return new ArrayList<BigDecimal>() {{
            add(new BigDecimal("0.2"));
            add(new BigDecimal("0.5"));
            add(new BigDecimal("0.3"));
        }};
    }
    
}
