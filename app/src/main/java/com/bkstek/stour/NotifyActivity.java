package com.bkstek.stour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NotifyActivity extends AppCompatActivity {
    Button btnCancel;
    Button btnSign;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        btnCancel = findViewById(R.id.btnCancel);
        btnSign = findViewById(R.id.btnSign);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotifyActivity.this.finish();
            }
        });
    }
}
