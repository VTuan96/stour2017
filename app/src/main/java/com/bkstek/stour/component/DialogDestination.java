package com.bkstek.stour.component;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.bkstek.stour.MapsActivity;
import com.bkstek.stour.R;
import com.bkstek.stour.adapter.AdapterDestination;
import com.bkstek.stour.model.POI;
import com.bkstek.stour.util.CommonDefine;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duy tho le on 10/2/2017.
 */

public class DialogDestination extends Dialog {
    private List<POI> poiList;
    private List<POI> tempList;
//    RecyclerView recyclerPois;
    ListView recyclerPois;
    Button btnRoute;
    Context context;
    AdapterDestination adapterDestination;
    List<LatLng> latLngList;


    public DialogDestination(@NonNull Context context, List<POI> poiList, List<POI> tempList, List<LatLng> latLngList) {
        super(context);

        setContentView(R.layout.dialog_des_layout);
        setCanceledOnTouchOutside(false);
        recyclerPois = findViewById(R.id.recyclerPois);
        btnRoute = (Button) findViewById(R.id.btnRoute);

        btnRoute.setVisibility(View.VISIBLE);
        this.poiList = poiList;
        this.latLngList = latLngList;
        this.tempList = tempList;
        this.context = context;

        ShowData();

        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Direction();
            }
        });
    }

    private void Direction() {
//        int count = latLngList.size();
        int count = tempList.size();
        if (count == 0) {
            Toast.makeText(context, "Bạn chưa chọn điểm đến", Toast.LENGTH_LONG).show();
        } else {
            dismiss();
            for (int i = 1; i < count; i++) {
//                MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), CommonDefine.MODE_DRIVING);
                MapsActivity.getLocationForMultiDirectionPOI(tempList.get(0), tempList.get(i), CommonDefine.MODE_DRIVING);
            }
        }

    }

    private void ShowData() {
        adapterDestination = new AdapterDestination(context, poiList,tempList, latLngList);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
//        recyclerPois.setLayoutManager(layoutManager);
        recyclerPois.setAdapter(adapterDestination);
        adapterDestination.notifyDataSetChanged();
    }
}
