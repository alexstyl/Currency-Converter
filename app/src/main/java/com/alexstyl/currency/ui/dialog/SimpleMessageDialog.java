package com.alexstyl.currency.ui.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.alexstyl.currency.ui.BaseDialog;

/**
 * <p>Created by alexstyl on 16/02/15.</p>
 */
public class SimpleMessageDialog extends BaseDialog {


    private static final String KEY_MESSAGE = "alexstyl:message";
    private static final String KEY_TITLE = "alexstyl:title";
    private DialogInterface.OnClickListener mNegativeButtonListener;
    private DialogInterface.OnClickListener mPositiveButtonListener;


    public SimpleMessageDialog() {
    }


    public static SimpleMessageDialog newInstance(int resTitle, int resMessage) {

        SimpleMessageDialog dialog = new SimpleMessageDialog();
        Bundle args = new Bundle(2);
        args.putInt(KEY_TITLE, resTitle);
        args.putInt(KEY_MESSAGE, resMessage);
        dialog.setArguments(args);
        return dialog;
    }


    public void setPositiveListener(DialogInterface.OnClickListener l) {
        this.mPositiveButtonListener = l;
    }

    public void setmNegativeButtonListener(DialogInterface.OnClickListener l) {
        this.mNegativeButtonListener = l;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setNegativeButton(android.R.string.no, mNegativeButtonListener)
                .setPositiveButton(android.R.string.yes, mPositiveButtonListener);

        Bundle args = getArguments();
        int resTitle = args.getInt(KEY_TITLE);
        int resMessage = args.getInt(KEY_MESSAGE);
        if (resTitle != -1) {
            builder.setTitle(resTitle);
        }
        if (resMessage != -1) {
            builder.setMessage(resMessage);
        }

        return builder.create();
    }
}
