package com.bkstek.stour.component;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bkstek.stour.R;

/**
 * Created by acebk on 9/3/2017.
 */

public class DialogInfo extends Dialog {

    public Button btnOK;
    public TextView tvMess;

    public DialogInfo(@NonNull Context context, String mess) {
        super(context);
        setContentView(R.layout.dialog_info_layout);
        tvMess = (TextView) findViewById(R.id.tvMess);
        btnOK = (Button) findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
