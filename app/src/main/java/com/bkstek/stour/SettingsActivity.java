package com.bkstek.stour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bkstek.stour.util.CommonDefine;

public class SettingsActivity extends AppCompatActivity {

    EditText edtTimeUpdateLocation, edtRadiusAccess, edtRadiusAroundLBS;
    Button btnSave;
    SharedPreferences preferences;

    //Mode travel settings
    RadioButton rbWalking, rbDriving, rbBicycling;
    RadioGroup rgModeTravel;

    String timeUpdateLocation = "", radiusAccess = "", radisAroundLBS = "";
    int modeTravel = 0;
    String modeTravelStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edtRadiusAccess = findViewById(R.id.edtRadiusAccess);
        edtTimeUpdateLocation = findViewById(R.id.edtTimeUpdateLocation);
        edtRadiusAroundLBS = findViewById(R.id.edtRadiusAroundLBS);
        btnSave = findViewById(R.id.btnSaveSetting);

        rbWalking = findViewById(R.id.rbWalking);
        rbBicycling = findViewById(R.id.rbBicycling);
        rbDriving = findViewById(R.id.rbDriving);
        rgModeTravel = findViewById(R.id.rgModeTravel);

        getDefaultPreferences();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidMapField()){
                    preferences = getSharedPreferences(CommonDefine.SETTING, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(CommonDefine.TIME_UPDATE_LOCATION, timeUpdateLocation);
                    editor.putString(CommonDefine.RADIUS_ACCESS, radiusAccess);
                    editor.putString(CommonDefine.MODE_TRAVEL, modeTravelStr);
                    editor.putString(CommonDefine.RADIUS_AROUND_LBS, radisAroundLBS);
                    editor.apply();
                    editor.commit();

                    Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Thông tin các trường nhập chưa hợp lệ!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        rgModeTravel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                modeTravel = group.getCheckedRadioButtonId();
                if (modeTravel == R.id.rbWalking)
                    modeTravelStr = CommonDefine.MODE_WALKING;
                else if (modeTravel == R.id.rbBicycling)
                    modeTravelStr = CommonDefine.MODE_BICYCLING;
                else if (modeTravel == R.id.rbDriving)
                    modeTravelStr = CommonDefine.MODE_DRIVING;
            }
        });

    }

    private boolean checkValidMapField() {
        timeUpdateLocation = edtTimeUpdateLocation.getText().toString();
        radiusAccess = edtRadiusAccess.getText().toString();
        radisAroundLBS = edtRadiusAroundLBS.getText().toString();
        if (timeUpdateLocation.length() > 0 && radiusAccess.length() > 0 && radisAroundLBS.length() >0){
            return true;
        }
        return false;
    }

    private void getDefaultPreferences() {
        preferences = getSharedPreferences(CommonDefine.SETTING, MODE_PRIVATE);
        String timeUpdatePref = preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "5");
        String radiusAccessPref = preferences.getString(CommonDefine.RADIUS_ACCESS, "1000");
        String radiusAroudnLBSPref = preferences.getString(CommonDefine.RADIUS_AROUND_LBS, "5");
        edtTimeUpdateLocation.setText(timeUpdatePref);
        edtRadiusAccess.setText(radiusAccessPref);
        edtRadiusAroundLBS.setText(radiusAroudnLBSPref);
        modeTravelStr = preferences.getString(CommonDefine.MODE_TRAVEL, CommonDefine.MODE_DRIVING);
        if (modeTravelStr.equals(CommonDefine.MODE_DRIVING))
            modeTravel = R.id.rbDriving;
        else if (modeTravelStr.equals(CommonDefine.MODE_BICYCLING))
            modeTravel = R.id.rbBicycling;
        else if (modeTravelStr.equals(CommonDefine.MODE_WALKING))
            modeTravel = R.id.rbWalking;
        rgModeTravel.check(modeTravel);
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
