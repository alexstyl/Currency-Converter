package com.alexstyl.currency.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alexstyl.currency.Currency;
import com.alexstyl.currency.CurrencyConverter;
import com.alexstyl.currency.CurrencyHelper;
import com.alexstyl.currency.R;
import com.alexstyl.currency.ui.BaseFragment;
import com.alexstyl.currency.ui.dialog.CurrencySelectorDialog;
import com.alexstyl.currency.util.DeLog;

import java.util.Random;

/**
 * The ConversionFragment displays the selected currencies and allows the user to convert from one to the other.
 * <p>Created by alexstyl on 10/02/15.</p>
 */
public class ConversionFragment extends BaseFragment implements CurrencyConverter.CurrencyChangeListener,
        CurrencySelectorDialog.OnCurrencySelectedListener, NumPadFragment.OnNumberClickedListener {

    private static final String TAG = "ConvertionFragment";
    private static final String KEY_CURRENCY_1 = "key_currency_1";
    private static final String KEY_CURRENCY_2 = "key_currency_2";
    private static final String TAG_CURRENCY_SELECT = "alexstyl:cur_select";
    private static final String KEY_SELECTED_CURRENCY_INDEX = "key_cur_btn_index";
    private static final String KEY_CURRENCY_1_VALUE = "key_value1";
    private static final String KEY_CURRENCY_2_VALUE = "key_value2";

    /*
     * A button that displays the first selected currency.
     * When clicked, the user can select a different currency
     */
    Button mCurrencyBtn1;
    Button mCurrencyBtn2;

    /*
    Display of the values of the 'from' currency
     */
    TextView mCurrencyTxt1; // value of the 'from' currency
    TextView mCurrencyTxt2; // value of the 'to' currency

    Currency mCurrency1;
    Currency mCurrency2;

//    Double mCurrency1Value;
//    Double mCurrency2Value;

    /**
     * int that stores which button was pressed, in case of configuration change
     */
    private int mCurrencyButtonPressed = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_converter, container, false);

        mCurrencyBtn1 = (Button) view.findViewById(R.id.cur1_button_from);
        mCurrencyBtn2 = (Button) view.findViewById(R.id.cur2_button_from);

        mCurrencyTxt1 = (TextView) view.findViewById(R.id.cur1_edit_from);
        mCurrencyTxt2 = (TextView) view.findViewById(R.id.cur2_edit_from);

        if (savedInstanceState == null) {
            // if the app is running for the first time,
            // just pick a random currency to convert.

            Random r = new Random();
            int index1 = r.nextInt(12);
            mCurrency1 = CurrencyHelper.loadCurrencyFromIndex(getResources(), index1);
            int index2 = r.nextInt(12);
            while (index1 == index2) {
                index2 = r.nextInt(12);
            }
            mCurrency2 = CurrencyHelper.loadCurrencyFromIndex(getResources(), index2);
            // TODO store the previously selected currency in file, so that we can resume when the app starts again

        } else {
            mCurrency1 = (Currency) savedInstanceState.getSerializable(KEY_CURRENCY_1);
            mCurrency2 = (Currency) savedInstanceState.getSerializable(KEY_CURRENCY_2);

            CurrencySelectorDialog dialog = (CurrencySelectorDialog) getFragmentManager().findFragmentByTag(TAG_CURRENCY_SELECT);
            if (dialog != null) {
                mCurrencyButtonPressed = savedInstanceState.getInt(KEY_SELECTED_CURRENCY_INDEX);
                dialog.setCurrencySelectedListener(this);
                dialog.setSelectedCurrency(mCurrency1.getISO(), mCurrency2.getISO());
            }

            mCurrencyTxt1.setText(String.valueOf(savedInstanceState.getDouble(KEY_CURRENCY_1_VALUE)));
            mCurrencyTxt2.setText(String.valueOf(savedInstanceState.getDouble(KEY_CURRENCY_2_VALUE)));

        }

        mCurrencyBtn1.setText(mCurrency1.getSymbol());
        mCurrencyBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencySelectorDialog dialog = new CurrencySelectorDialog();
                mCurrencyButtonPressed = 0;
                dialog.setCurrencySelectedListener(ConversionFragment.this);
                dialog.setSelectedCurrency(mCurrency1.getISO(), mCurrency2.getISO());
                dialog.show(getFragmentManager(), TAG_CURRENCY_SELECT);
            }
        });

        mCurrencyBtn2.setText(mCurrency2.getSymbol());
        mCurrencyBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CurrencySelectorDialog dialog = new CurrencySelectorDialog();
                mCurrencyButtonPressed = 1;
                dialog.setCurrencySelectedListener(ConversionFragment.this);
                dialog.setSelectedCurrency(mCurrency1.getISO(), mCurrency2.getISO());
                dialog.show(getFragmentManager(), TAG_CURRENCY_SELECT);
            }
        });

        view.findViewById(R.id.swap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Currency temp = mCurrency1;
                mCurrency1 = mCurrency2;
                mCurrency2 = temp;

                mCurrencyBtn1.setText(mCurrency1.getSymbol());
                mCurrencyBtn2.setText(mCurrency2.getSymbol());

                mConvertButtonClickListener.onClick(v);

            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        CurrencyConverter.getInstance(getActivity()).addCurrencyChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        CurrencyConverter.getInstance(getActivity()).removeCurrencyChangeListener(this);

    }


    private final View.OnClickListener mConvertButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // When the convert button is pressed, get the converted value and show in on the UI
            Double value =
                    CurrencyConverter.getInstance(getActivity()).convert(
                            mCurrency1.getISO(), mCurrency2.getISO(),
                            mCurrencyTxt1.getText().toString());

            if (value == 0) {
                // if the return value is zero, clear the field so that
                // the user can edit it without having to erase the zero
                mCurrencyTxt2.clearComposingText();
            } else {
                String prettyDouble = CurrencyHelper.getPrettyDecimal(value);
                mCurrencyTxt2.setText(prettyDouble);
            }
        }
    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_CURRENCY_1, mCurrency1);
        outState.putSerializable(KEY_CURRENCY_2, mCurrency2);

        outState.putInt(KEY_SELECTED_CURRENCY_INDEX, mCurrencyButtonPressed);

    }

    @Override
    public void onCurrencySelected(Currency cur) {
        Button modifiedButton;
        if (mCurrencyButtonPressed == 0) {
            mCurrency1 = cur;
            modifiedButton = mCurrencyBtn1;
        } else if (mCurrencyButtonPressed == 1) {
            mCurrency2 = cur;
            modifiedButton = mCurrencyBtn2;
        } else {
            throw new RuntimeException("Which button was pressed?");
        }
        modifiedButton.setText(cur.getSymbol());
        mConvertButtonClickListener.onClick(null);
        mCurrencyButtonPressed = -1;
    }

    @Override
    public void onCurrencyChanged(String from, String to, Double value) {
        DeLog.v(TAG, "Currency Updated! " + from + " -> " + to);
        // we can refresh the UI, but that would seem weird to the UI, as if the app is glitching
        // since the currency are being updated asynchronously

    }


    @Override
    public boolean onNumberClicked(View view, int value) {
        DeLog.v(TAG, "onNumberClicked: " + value);
        mCurrencyTxt1.append(String.valueOf(value));
        return true;
    }

    @Override
    public boolean onSymbolClicked(View view, int symbol) {
        DeLog.v(TAG, "onSymbolClicked: " + symbol);

        switch (symbol) {
            case NumPadFragment.SYMBOL_COMMA:
                mCurrencyTxt1.append(".");
                return true;
            case NumPadFragment.SYMBOL_EQUALS:
                mConvertButtonClickListener.onClick(view);
                // don't consume the event
                return false;
            case NumPadFragment.SYMBOL_BACKSPACE:
                onBackspacePressed();
                return true;
            case NumPadFragment.SYMBOL_CLEAR_ALL:
                onClearAllPressed();
                return true;
            default:
                //
        }
        return false;
    }


    private void onClearAllPressed() {
        mCurrencyTxt1.setText(null);
        mCurrencyTxt2.setText(null);
    }

    private void onBackspacePressed() {
        String text = mCurrencyTxt1.getText().toString();
        if (TextUtils.isEmpty(text)) {
            // nothing to do here
            return;
        }
        mCurrencyTxt1.setText(text.substring(0, text.length() - 1));
    }


    public String getFromValue() {
        String from = mCurrencyTxt1.getText().toString();
        if (TextUtils.isEmpty(from)) {
            return "0";
        }
        return from;
    }

    public String getFrom() {
        return mCurrency1.getISO();
    }

    public String getTo() {
        return mCurrency2.getISO();
    }

    public String getToValue() {
        String from = mCurrencyTxt2.getText().toString();
        if (TextUtils.isEmpty(from)) {
            return "0";
        }
        return from;
    }
}
