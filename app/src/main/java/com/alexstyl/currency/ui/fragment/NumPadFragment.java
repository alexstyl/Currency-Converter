package com.alexstyl.currency.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alexstyl.currency.R;
import com.alexstyl.currency.ui.BaseFragment;
import com.alexstyl.currency.ui.widget.CalculatorPadLayout;

/**
 * Created by alexstyl on 12/02/15.
 */
public class NumPadFragment extends BaseFragment {

    private CalculatorPadLayout numpad;

    // A list of all special characters in the numpad
    public static final int SYMBOL_EQUALS = 0;
    public static final int SYMBOL_COMMA = 1;
    public static final int SYMBOL_BACKSPACE = 2;
    public static final int SYMBOL_CLEAR_ALL = 3;
    public static final int SYMBOL_HISTORY = 4;

    private OnNumberClickedListener mListener;

    public interface OnNumberClickedListener {

        /**
         * Called whenever a number in the numpad was pressed
         *
         * @param view  The view pressed
         * @param value The representing value of the pressed view
         * @return True if the event was consumed by this call
         */
        boolean onNumberClicked(View view, int value);

        /**
         * @param view   The view pressed
         * @param symbol The symbol that was selected
         * @return True if the event was consumed by this call
         */
        boolean onSymbolClicked(View view, int symbol);


    }

    public void setOnNumberClickedListener(OnNumberClickedListener l) {
        this.mListener = l;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_numpad, container, false);

        numpad = (CalculatorPadLayout) view.findViewById(R.id.numpad_layout);

        // we need to add the buttons
        // the order of the buttons is
        // 7 8 9
        // 4 5 6
        // 1 2 3
        // 0 , =

        for (int j = 7; j > 0; j = j - 3) {
            for (int i = 0; i < 3; i++)
                addButtonDigit(i + j);
        }
        addButtonDigit(0);
        addButton(".", SYMBOL_COMMA);
        addButton("=", SYMBOL_EQUALS);

        view.findViewById(R.id.btn_backspace).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInternalListener.onSymbolClicked(v, SYMBOL_BACKSPACE);
            }
        });

        view.findViewById(R.id.btn_clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInternalListener.onSymbolClicked(v, SYMBOL_CLEAR_ALL);
            }
        });
        view.findViewById(R.id.btn_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInternalListener.onSymbolClicked(v, SYMBOL_HISTORY);

            }
        });
        return view;

    }

    /**
     * Adds a new Button of the given digit
     *
     * @param digit The digit to add
     */
    private void addButtonDigit(final int digit) {
        addButton(String.valueOf(digit), -1);
    }

    /**
     * Adds the given button to the numpad.
     *
     * @param label  The label of the button
     * @param symbol The special symbol that represents the button.
     */
    private void addButton(final String label, final int symbol) {
        final Button finalBtn = new Button(new ContextThemeWrapper(getActivity(), R.style.ButtonStyle));
        finalBtn.setText(label);
        finalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (symbol != -1) {
                    mInternalListener.onSymbolClicked(v, symbol);
                } else {
                    mInternalListener.onNumberClicked(v, Integer.valueOf(label));
                }
            }
        });
        numpad.addView(finalBtn);

    }



    private final OnNumberClickedListener mInternalListener = new OnNumberClickedListener() {
        @Override
        public boolean onNumberClicked(View view, int value) {
            if (mListener != null) {
                return mListener.onNumberClicked(view, value);
            }
            return false;

        }

        @Override
        public boolean onSymbolClicked(View view, int symbol) {
            if (mListener != null) {
                return mListener.onSymbolClicked(view, symbol);
            }
            return false;

        }


    };
}
