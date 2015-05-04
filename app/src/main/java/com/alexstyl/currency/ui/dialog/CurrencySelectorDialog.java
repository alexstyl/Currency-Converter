package com.alexstyl.currency.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.alexstyl.currency.Currency;
import com.alexstyl.currency.R;
import com.alexstyl.currency.ui.BaseDialog;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by alexstyl on 12/02/15.
 */
public class CurrencySelectorDialog extends BaseDialog {

    private static final String KEY_MASKED_CURRENCY = "key_masked_currency";

    public CurrencySelectorDialog() {
        // empty constructor
    }

    private ArrayList<String> selectedCurrency = new ArrayList<>();

    private CurrencySelectorAdapter mAdapter;

    /**
     * Sets the already selected currencies, so that they will be shown disabled in the selection list.
     *
     * @param currencies The already selected currencies
     */
    public void setSelectedCurrency(String... currencies) {
        Collections.addAll(selectedCurrency, currencies);
        if (mAdapter != null) {
            // the view might not have been created yet
            mAdapter.setSelectedCurrencies(selectedCurrency);
        }
    }


    public interface OnCurrencySelectedListener {

        /**
         * Called when the user has made a selection of a currency
         *
         * @param cur The selected currency
         */
        void onCurrencySelected(Currency cur);
    }

    private OnCurrencySelectedListener mListener;

    public void setCurrencySelectedListener(OnCurrencySelectedListener l) {
        this.mListener = l;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_currency_selector, null, false);
        GridView mGrid = (GridView) view.findViewById(R.id.currency_grid);
        mAdapter = new CurrencySelectorAdapter(getActivity());
        mGrid.setAdapter(mAdapter);

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .setNegativeButton(android.R.string.cancel, null).create();
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // get item and forward it
                Currency cur = mAdapter.getItem(position);
                mListener.onCurrencySelected(cur);
                dismiss();
            }
        });

        if (selectedCurrency != null) {
            mAdapter.setSelectedCurrencies(selectedCurrency);
        }
        return dialog;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_MASKED_CURRENCY, selectedCurrency);
    }
}
