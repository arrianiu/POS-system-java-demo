package src.util;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatUtil {

    private static final NumberFormat PH_CURRENCY = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-PH"));

    private FormatUtil() {
        // prevent instantiation
    }

    public static String peso(double value) {
        return PH_CURRENCY.format(value);
    }
}
