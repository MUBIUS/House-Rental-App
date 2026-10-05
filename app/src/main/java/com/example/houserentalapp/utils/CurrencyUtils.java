package com.example.houserentalapp.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtils {

    public static String format(double amount, String currencyCode) {
        String symbol = Constants.getCurrencySymbol(currencyCode);
        // Format with comma separators
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        nf.setGroupingUsed(true);
        // Remove trailing zeros for whole numbers
        if (amount == Math.floor(amount)) {
            nf.setMaximumFractionDigits(0);
        } else {
            nf.setMaximumFractionDigits(2);
        }
        return symbol + nf.format(amount);
    }

    public static String format(String amountStr, String currencyCode) {
        try {
            double amount = Double.parseDouble(amountStr.replaceAll("[^\\d.]", ""));
            return format(amount, currencyCode);
        } catch (NumberFormatException e) {
            return Constants.getCurrencySymbol(currencyCode) + amountStr;
        }
    }

    /** Format per-month label: "₹15,000/mo" */
    public static String formatPerMonth(double amount, String currencyCode) {
        return format(amount, currencyCode) + "/mo";
    }

    public static String formatPerMonth(String amountStr, String currencyCode) {
        return format(amountStr, currencyCode) + "/mo";
    }

    /** Parse a raw string into a double, ignoring currency symbols */
    public static double parse(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        try {
            return Double.parseDouble(value.replaceAll("[^\\d.]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String[] getSupportedCurrencies() {
        return new String[]{
                Constants.CURRENCY_INR,
                Constants.CURRENCY_USD,
                Constants.CURRENCY_EUR,
                Constants.CURRENCY_GBP,
                Constants.CURRENCY_AED,
                Constants.CURRENCY_SGD,
                Constants.CURRENCY_MYR
        };
    }

    public static String[] getCurrencyDisplayNames() {
        return new String[]{
                "₹ INR - Indian Rupee",
                "$ USD - US Dollar",
                "€ EUR - Euro",
                "£ GBP - British Pound",
                "د.إ AED - UAE Dirham",
                "S$ SGD - Singapore Dollar",
                "RM MYR - Malaysian Ringgit"
        };
    }
}
