package com.alexstyl.currency;

import java.io.Serializable;

/**
 * Class that represents some value in a given currency
 * <p>Created by alexstyl on 16/02/15.</p>
 */
public class CurrencyValue implements Serializable {

    private Currency mCurrency;
    private double mValue;

    public CurrencyValue(Currency currency, double value) {
        if (currency == null) {
            throw new RuntimeException();
        }
        this.mCurrency = currency;
        this.mValue = value;
    }

    /**
     * Returns the value of the currency
     */
    public double getValue() {
        return mValue;
    }

    public String getISO() {
        return mCurrency.getISO();
    }


    public String getSymbol() {
        return mCurrency.getSymbol();
    }
}
