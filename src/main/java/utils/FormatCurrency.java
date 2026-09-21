package utils;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class FormatCurrency {
    public static String formatCurrency(BigDecimal value) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        return currencyFormat.format(value);
    }
}