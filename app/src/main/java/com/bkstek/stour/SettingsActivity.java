package com.bkstek.stour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bkstek.stour.util.CommonDefine;

public class SettingsActivity extends AppCompatActivity {

    EditText edtTimeUpdateLocation, edtRadiusAccess;
    Button btnSave;
    SharedPreferences preferences;

    String timeUpdateLocation = "", radiusAccess = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edtRadiusAccess = findViewById(R.id.edtRadiusAccess);
        edtTimeUpdateLocation = findViewById(R.id.edtTimeUpdateLocation);
        btnSave = findViewById(R.id.btnSaveSetting);

        getDefaultPreferences();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidMapField()){
                    preferences = getSharedPreferences(CommonDefine.SETTING, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonDefine.TIME_UPDATE_LOCATION, timeUpdateLocation);
                    editor.putString(CommonDefine.RADIUS_ACCESS, radiusAccess);
                    editor.apply();
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Thông tin các trường nhập chưa hợp lệ!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean checkValidMapField() {
        timeUpdateLocation = edtTimeUpdateLocation.getText().toString();
        radiusAccess = edtRadiusAccess.getText().toString();
        if (timeUpdateLocation.length() > 0 && radiusAccess.length() > 0){
            return true;
        }
        return false;
    }

    private void getDefaultPreferences() {
        preferences = getSharedPreferences(CommonDefine.SETTING, MODE_PRIVATE);
        String timeUpdatePref = preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "5");
        String radiusAccessPref = preferences.getString(CommonDefine.RADIUS_ACCESS, "1");
        edtTimeUpdateLocation.setText(timeUpdatePref);
        edtRadiusAccess.setText(radiusAccessPref);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                // do something here
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
