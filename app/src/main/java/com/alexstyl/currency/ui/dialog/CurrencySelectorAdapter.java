package com.alexstyl.currency.ui.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alexstyl.currency.Currency;
import com.alexstyl.currency.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by alexstyl on 12/02/15.
 */
public class CurrencySelectorAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private final List<Currency> mCurrencies;
    private Set<Integer> mDisabledCurrencies;
    private final int mDisabledColor;
    private final int mEnabledColor;

    public CurrencySelectorAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mCurrencies = initCurrencies(context.getResources());
        this.mDisabledCurrencies = new HashSet<>();
        this.mEnabledColor = context.getResources().getColor(android.R.color.black);
        this.mDisabledColor = context.getResources().getColor(R.color.accent);

    }


    /**
     * Returns the list of currencies to be displayed in the adapter
     *
     * @param res The Resources to use
     */
    private List<Currency> initCurrencies(Resources res) {
        String[] country = res.getStringArray(R.array.country);
        String[] sign = res.getStringArray(R.array.sign);
        String[] label = res.getStringArray(R.array.label);

        int size = country.length;
        ArrayList<Currency> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(new Currency(country[i], sign[i], label[i]));

        }

        return list;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mDisabledCurrencies.contains(position);
    }

    @Override
    public int getCount() {
        return mCurrencies.size();
    }

    @Override
    public Currency getItem(int position) {
        return mCurrencies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Sets the already selected currency, so that the adapter marks them as disabled
     */
    public void setSelectedCurrencies(ArrayList<String> selectedCurrency) {
        int index = 0;
        for (Currency cur : mCurrencies) {
            if (selectedCurrency.contains(cur.getISO())) {
                this.mDisabledCurrencies.add(index);
            }
            index++;
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView mSymbol;
        TextView mLabel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = mInflater.inflate(R.layout.row_currency_option, parent, false);
            vh.mSymbol = (TextView) convertView.findViewById(android.R.id.text1);
            vh.mLabel = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        Currency cur = getItem(position);
        vh.mLabel.setText(cur.getLabel());
        vh.mSymbol.setText(cur.getSymbol());
        if (isEnabled(position)) {
            vh.mLabel.setTextColor(mEnabledColor);
            vh.mSymbol.setTextColor(mEnabledColor);
        } else {
            vh.mSymbol.setTextColor(mDisabledColor);
            vh.mLabel.setTextColor(mEnabledColor);
        }
        return convertView;
    }
}
