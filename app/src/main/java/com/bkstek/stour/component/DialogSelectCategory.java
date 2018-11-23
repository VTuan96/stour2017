package com.bkstek.stour.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.bkstek.stour.CultureActivity;
import com.bkstek.stour.FoodActivity;
import com.bkstek.stour.HistoryActivity;
import com.bkstek.stour.HotelActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.RestaurantActivity;
import com.bkstek.stour.SearchActivity;
import com.bkstek.stour.util.CommonDefine;

public class DialogSelectCategory extends Dialog implements View.OnClickListener {

    CardView cvHistorySearch, cvCulturalSearch, cvFoodSearch, cvHotelSearch, cvRestaurantSearch;
    int selection = -1;

    public DialogSelectCategory( @NonNull Context context, int selection) {
        super(context);
        setContentView(R.layout.layout_select_category_dialog);

        this.selection = selection;
        cvHistorySearch = findViewById(R.id.cvHistorySearch);
        cvCulturalSearch = findViewById(R.id.cvCulturalSearch);
        cvFoodSearch = findViewById(R.id.cvFoodSearch);
        cvHotelSearch = findViewById(R.id.cvHotelSearch);
        cvRestaurantSearch = findViewById(R.id.cvRestaurantSearch);

        cvHistorySearch.setOnClickListener(this);
        cvCulturalSearch.setOnClickListener(this);
        cvFoodSearch.setOnClickListener(this);
        cvHotelSearch.setOnClickListener(this);
        cvRestaurantSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = null;
        switch (id){
            case R.id.cvHistorySearch:
                selection = 0;
                intent = new Intent(getContext(),HistoryActivity.class);
                dismiss();
                break;
            case R.id.cvCulturalSearch:
                selection = 1;
                intent = new Intent(getContext(),CultureActivity.class);
                dismiss();
                break;
            case R.id.cvFoodSearch:
                selection = 2;
                intent = new Intent(getContext(),FoodActivity.class);
                dismiss();
                break;
            case R.id.cvHotelSearch:
                intent = new Intent(getContext(),HotelActivity.class);
                dismiss();
                break;
            case R.id.cvRestaurantSearch:
                intent = new Intent(getContext(),RestaurantActivity.class);
                dismiss();
                break;

        }

        intent.putExtra(CommonDefine.SELECTION_SEARCH,selection);
        getContext().startActivity(intent);
    }

    public int getSelection() {
        return selection;
    }
}
