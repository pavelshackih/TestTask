package com.testtask.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 *
 */
@SuppressWarnings({"ConstantConditions", "NullableProblems"})
public class WarningDialog extends DialogFragment {

    static final String MESSAGE = "message";
    static final String DEFAULT_TAG = "simpleDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString(MESSAGE));
        builder.setTitle(R.string.warning);
        builder.setPositiveButton(android.R.string.ok, null);
        return builder.create();
    }
}
