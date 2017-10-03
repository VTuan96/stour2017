package com.bkstek.stour.component;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.R;
import com.bkstek.stour.adapter.AdapterOrigin;
import com.bkstek.stour.model.POI;
import com.bkstek.stour.util.VolleySingleton;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_POIS;

/**
 * Created by duy tho le on 10/2/2017.
 */

public class DialogPOIOrigin extends Dialog {
    RecyclerView recyclerPois;
    List<POI> poiList = new ArrayList<>();
    Context context;
    AdapterOrigin adapterOrigin;
    List<LatLng> latLngList;

    public DialogPOIOrigin(@NonNull Context context, List<LatLng> latLngList) {
        super(context);

        setContentView(R.layout.dialog_poi_origin);
        setCanceledOnTouchOutside(false);

        this.context = context;
        this.latLngList=latLngList;

        recyclerPois = (RecyclerView) findViewById(R.id.recyclerPois);

        GetPOIOrigin();
    }

    private void GetPOIOrigin() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_POIS,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                POI poi = new POI();
                                poi.setId(object.getInt("Id"));
                                poi.setName(object.getString("Name"));
                                poi.setLatitude(object.getString("Latitude"));
                                poi.setLongitude(object.getString("Longitude"));
                                poi.setImage(object.getString("Image"));
                                poi.setAddress(object.getString("Address"));
                                poiList.add(poi);
                            }

                            adapterOrigin = new AdapterOrigin(context, poiList, DialogPOIOrigin.this,latLngList);
                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 1);
                            recyclerPois.setLayoutManager(layoutManager);
                            recyclerPois.setAdapter(adapterOrigin);
                            adapterOrigin.notifyDataSetChanged();
                        } catch (JSONException e) {

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
}
