package com.alexstyl.currency;

import android.content.res.Resources;

import java.text.DecimalFormat;

/**
 * <p>Created by alexstyl on 11/02/15.</p>
 */
public class CurrencyHelper {

    private static final String TAG = "Currency";


    /**
     * Cuts down uneccesery decimal points from the given Double value
     * <br> i.e. 12.102342 -> 12.1023
     */
    public static String getPrettyDecimal(Double value) {
        DecimalFormat number_format = new java.text.DecimalFormat("#.####");
        return number_format.format(value);
    }

    /**
     * Returns the currency of the i-th index
     *
     * @param res The resources to use
     * @param i   The index to make the currency of
     * @return
     */
    public static Currency loadCurrencyFromIndex(Resources res, int i) {
        String[] country = res.getStringArray(R.array.country);
        String[] sign = res.getStringArray(R.array.sign);
        String[] label = res.getStringArray(R.array.label);
        int currencyLength = country.length;
        if (i < 0 || i > currencyLength) {
            throw new IllegalArgumentException("There is no currency with the index of " + i);
        }
        return new Currency(country[i], sign[i], label[i]);
    }
}
