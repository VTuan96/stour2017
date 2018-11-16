package com.bkstek.stour.component;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.R;
import com.bkstek.stour.adapter.AdapterOrigin;
import com.bkstek.stour.model.History;
import com.bkstek.stour.model.POI;
import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.VolleySingleton;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_HIS_PLACE;
import static com.bkstek.stour.util.CommonDefine.GET_POIS;

/**
 * Created by duy tho le on 10/2/2017.
 */

public class DialogPOIOrigin extends Dialog {
    RecyclerView recyclerPois;
    List<POI> poiList = new ArrayList<>();
    List<POI> tempList = new ArrayList<>();
    Context context;
    AdapterOrigin adapterOrigin;
    List<LatLng> latLngList;
    String TAG = CommonDefine.ROUTING;

    TextView tvTitle;

    public DialogPOIOrigin(@NonNull Context context, List<POI> poiList, List<POI> tempList,List<LatLng> latLngList, String TAG) {
        super(context);

        setContentView(R.layout.dialog_poi_origin);
        setCanceledOnTouchOutside(false);

        this.TAG = TAG;
        this.context = context;
        this.latLngList = latLngList;
        this.tempList = tempList;
        recyclerPois = (RecyclerView) findViewById(R.id.recyclerPois);
        tvTitle = findViewById(R.id.tvTitle);

        if (TAG.equals(CommonDefine.SMART)) {
            tvTitle.setText("Gần đây");
            this.poiList = poiList;
            adapterOrigin = new AdapterOrigin(context, poiList, DialogPOIOrigin.this, latLngList, TAG);
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
            recyclerPois.setLayoutManager(layoutManager);
            recyclerPois.setAdapter(adapterOrigin);
            adapterOrigin.notifyDataSetChanged();
        }
        else if (TAG.equals(CommonDefine.ROUTING)){
            tvTitle.setText("Chọn điểm đi");
            GetPOIOrigin();
        }






    }

    private void GetPOIOrigin() {
        poiList = new ArrayList<>();
        final ProgressDialog alertDialog = ProgressDialog.show(context, "", "Vui lòng đợi...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_HIS_PLACE,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                boolean isActive = object.getBoolean("isActive");
                                if (isActive) {
                                    JSONArray array1 = object.getJSONArray("FileAttachs");
                                    JSONObject object1 = array1.getJSONObject(0);

                                    POI poi = new POI();
                                    poi.setId(object.getInt("Id"));
                                    poi.setName(object.getString("Name"));
                                    poi.setLatitude(object.getDouble("Latitude"));
                                    poi.setLongitude(object.getDouble("Longitude"));
                                    poi.setImage(object1.getString("FileUrl"));
                                    poi.setAddress(object.getString("Address"));
                                    poi.setAudio(object.getString("Audio"));
                                    poi.setVideoDir(object.getString("VideoDir"));
                                    poiList.add(poi);
                                }
                            }

                            adapterOrigin = new AdapterOrigin(context, poiList, DialogPOIOrigin.this,latLngList, TAG);
                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
                            recyclerPois.setLayoutManager(layoutManager);
                            recyclerPois.setAdapter(adapterOrigin);
                            adapterOrigin.notifyDataSetChanged();

                            alertDialog.dismiss();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.dismiss();
    }
}
