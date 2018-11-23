package com.bkstek.stour.component;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;

import com.bkstek.stour.CultureActivity;
import com.bkstek.stour.FoodActivity;
import com.bkstek.stour.HistoryActivity;
import com.bkstek.stour.HotelActivity;
import com.bkstek.stour.LBSActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.RestaurantActivity;
import com.bkstek.stour.WeatherActivity;
import com.bkstek.stour.util.CommonDefine;

public class DialogUltilities extends Dialog implements View.OnClickListener  {
    CardView cvBank, cvATM, cvWeather, cvMart;
    public DialogUltilities(@NonNull Context context) {
        super(context);
        setContentView(R.layout.layout_ultilities_dialog);

        cvBank = findViewById(R.id.cvBank);
        cvATM = findViewById(R.id.cvATM);
        cvWeather = findViewById(R.id.cvWeather);
        cvMart = findViewById(R.id.cvMart);

        cvBank.setOnClickListener(this);
        cvATM.setOnClickListener(this);
        cvMart.setOnClickListener(this);
        cvWeather.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id){
            case R.id.cvATM:
                intent = new Intent(getContext(),LBSActivity.class);
                intent.putExtra(CommonDefine.SELECTION_ULTILITIES, CommonDefine.ATM);
                dismiss();
                break;
            case R.id.cvBank:
                intent = new Intent(getContext(),LBSActivity.class);
                intent.putExtra(CommonDefine.SELECTION_ULTILITIES, CommonDefine.BANK);
                dismiss();
                break;
            case R.id.cvWeather:
                intent = new Intent(getContext(),WeatherActivity.class);
                dismiss();
                break;
            case R.id.cvMart:
                intent = new Intent(getContext(),LBSActivity.class);
                intent.putExtra(CommonDefine.SELECTION_ULTILITIES, CommonDefine.MART);
                dismiss();
                break;

        }

        getContext().startActivity(intent);
    }
}
