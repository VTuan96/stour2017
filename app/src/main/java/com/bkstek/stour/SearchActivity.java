package com.bkstek.stour;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.bkstek.stour.model.POI;
import com.bkstek.stour.util.CommonDefine;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    int selection = 0;
    ArrayList<POI> listPOIs = new ArrayList<>();
    ListView lvPOI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        selection = getIntent().getIntExtra(CommonDefine.SELECTION_SEARCH, 0);

        getListPOI(selection);
    }

    private void getListPOI(int selection) {
        switch (selection){
            case 0:

                break;
        }
    }
}
