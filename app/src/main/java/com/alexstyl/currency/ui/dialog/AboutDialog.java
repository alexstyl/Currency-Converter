package com.alexstyl.currency.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.alexstyl.currency.R;
import com.alexstyl.currency.ui.BaseDialog;

/**
 * <p>Created by alexstyl on 15/02/15.</p>
 */
public class AboutDialog extends BaseDialog {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        StringBuilder str = new StringBuilder();
        str.append(getString(R.string.about_message_by))
                .append("\n")
                .append(getString(R.string.about_message_contact));

        Dialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.app_name)
                .setMessage(str.toString())
                .setNegativeButton(R.string.close, null)
                .create();
        return dialog;
    }
}
