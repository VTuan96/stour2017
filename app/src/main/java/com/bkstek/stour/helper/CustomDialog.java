package com.bkstek.stour.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.bkstek.stour.R;

public class CustomDialog {
    private Context context;

    public CustomDialog(Context context) {
        this.context = context;
    }

    public void showAlertDialog(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.Theme_AppCompat_Dialog_Alert).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
