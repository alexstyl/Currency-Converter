package com.alexstyl.currency.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.alexstyl.currency.R;
import com.alexstyl.currency.ui.dialog.AboutDialog;
import com.alexstyl.currency.ui.fragment.ConversionFragment;
import com.alexstyl.currency.ui.fragment.HistoryFragment;
import com.alexstyl.currency.ui.fragment.NumPadFragment;

/**
 * The MainActivity of the app. This activity consists of 3 fragments.
 * The {@linkplain com.alexstyl.currency.ui.fragment.ConversionFragment} that converts the currencies,
 * The {@linkplain com.alexstyl.currency.ui.fragment.HistoryFragment} that keeps track of the requested conversions and the
 * {@linkplain com.alexstyl.currency.ui.fragment.NumPadFragment} that is a numpad digits.
 */
public class MainActivity extends ActionBarActivity {

    private static final String KEY_ACTIVITY_CREATED = "key_act_created";
    private static final String KEY_ACTIVITY_RESUMED = "key_act_resumed";

    private TextView mActivityCreatedLog;
    private TextView mActivityResumedLog;

    private int activityCreatedCount;
    private int activityResumedCount;

    private SlidingPaneLayout mSlidingPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            // activity is being created for the first time
            activityCreatedCount = 0;
            activityResumedCount = 0;
        } else {
            activityCreatedCount = savedInstanceState.getInt(KEY_ACTIVITY_CREATED);
            activityResumedCount = savedInstanceState.getInt(KEY_ACTIVITY_RESUMED);
        }
        setContentView(R.layout.activity_main);


        mSlidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.sliding_layout);
        mSlidingPaneLayout.openPane();

        mActivityCreatedLog = (TextView) findViewById(R.id.activity_created_log);
        mActivityResumedLog = (TextView) findViewById(R.id.activity_resumed_log);

        findViewById(R.id.about).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutDialog dialog = new AboutDialog();
                dialog.show(getSupportFragmentManager(),null);

            }
        });

        activityCreatedCount++;
    }

    @Override
    protected void onStart() {
        super.onStart();

        NumPadFragment numpadfragment = (NumPadFragment) getSupportFragmentManager().findFragmentById(R.id.numpad);
        final HistoryFragment historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentById(R.id.history_frag);
        final ConversionFragment conversionFragment = (ConversionFragment) getSupportFragmentManager().findFragmentById(R.id.converter_frag);

        numpadfragment.setOnNumberClickedListener(new NumPadFragment.OnNumberClickedListener() {
            @Override
            public boolean onNumberClicked(View view, int value) {
                return conversionFragment.onNumberClicked(view, value);

            }

            @Override
            public boolean onSymbolClicked(View view, int symbol) {

                if (conversionFragment.onSymbolClicked(view, symbol)) {
                    // someone consumed the event. bail
                    return true;
                }
                if (symbol == NumPadFragment.SYMBOL_EQUALS) {
                    String fromV = conversionFragment.getFromValue();
                    String from = conversionFragment.getFrom();
                    String toV = conversionFragment.getToValue();
                    String to = conversionFragment.getTo();
                    String entry = String.format("%s %s = %s %s", fromV, from, toV, to);
                    historyFragment.addEntry(entry);
                    return true;
                } else if (symbol == NumPadFragment.SYMBOL_HISTORY) {
                    mSlidingPaneLayout.closePane();
                }
                return false;
            }


        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        activityResumedCount++;
        refreshActivityLogs();
    }

    private void refreshActivityLogs() {
        mActivityCreatedLog.setText(String.format(getString(R.string.log_activity_created), activityCreatedCount));
        mActivityResumedLog.setText(String.format(getString(R.string.log_activity_resumed), activityResumedCount));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_ACTIVITY_RESUMED, activityResumedCount);
        outState.putInt(KEY_ACTIVITY_CREATED, activityCreatedCount);
    }

}
