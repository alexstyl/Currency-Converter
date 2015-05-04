package com.alexstyl.currency;

import java.io.Serializable;

/**
 *
 * <p>Created by alexstyl on 11/02/15.</p>
 */
public class Currency implements Serializable {

    private String mLabel;
    private String mISO;
    private String mSymbol;


    public Currency(String iso, String symbol, String label) {
        this.mISO = iso;
        this.mLabel = label;
        this.mSymbol = symbol;
    }


    public String getISO() {
        return mISO;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public String getLabel() {
        return mLabel;
    }

}
