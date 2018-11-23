package com.bkstek.stour;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterHotelSearch;
import com.bkstek.stour.adapter.AdapterRestaurantSearch;
import com.bkstek.stour.component.DialogInfo;
import com.bkstek.stour.model.History;
import com.bkstek.stour.util.FunctionHelper;
import com.bkstek.stour.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_ALL_HOTEL;
import static com.bkstek.stour.util.CommonDefine.GET_ALL_RESTAURANT;

public class RestaurantActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerList;
    Context context;
    AdapterRestaurantSearch adapterHotel;
    List<History> historyList = new ArrayList<>();
    List<History> historyListTemp = new ArrayList<>();
    ImageView imBack;

    SearchView svRestaurant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerList = (RecyclerView) findViewById(R.id.recyclerList);
//        imBack = (ImageView) findViewById(R.id.imBack);
        recyclerList.setNestedScrollingEnabled(false);
        context = RestaurantActivity.this;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerList.setLayoutManager(layoutManager);
        adapterHotel = new AdapterRestaurantSearch(context, historyList);
        recyclerList.setAdapter(adapterHotel);
        adapterHotel.notifyDataSetChanged();

        toolbar.setTitle("Nhà hàng");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.back2);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iBack = new Intent(context, HomeScreenActivity.class);
                startActivity(iBack);
                finish();
            }
        });

        svRestaurant = findViewById(R.id.svRestaurant);
        svRestaurant.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));

        if (FunctionHelper.isNetworkConnected(context)) {
            GetData();
        } else {
            new DialogInfo(context, "Không có kết nối internet!!!").show();
        }

        svRestaurant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() != 0) {
                    historyListTemp = new ArrayList<>();
                    for (History his : historyList) {
                        if (his.getName().toLowerCase().contains(s.toLowerCase())) {
                            historyListTemp.add(his);
                        }
                    }

                    recyclerList.removeAllViews();
                    adapterHotel = new AdapterRestaurantSearch(context, historyListTemp);
                    recyclerList.setAdapter(adapterHotel);
                    adapterHotel.notifyDataSetChanged();
                } else {
                    recyclerList.removeAllViews();
                    adapterHotel = new AdapterRestaurantSearch(context, historyList);
                    recyclerList.setAdapter(adapterHotel);
                    adapterHotel.notifyDataSetChanged();
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent iBack = new Intent(context, HomeScreenActivity.class);
        startActivity(iBack);
        finish();
    }

    private void GetData() {
        historyList = new ArrayList<>();
        final ProgressDialog alertDialog = ProgressDialog.show(context, "", "Vui lòng đợi...");

        final List<History> historyListHotel = new ArrayList<>();
        historyListHotel.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_ALL_RESTAURANT,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                History hotel = new History();
                                boolean isActive = object.getBoolean("isActive");
                                if (isActive) {
                                    hotel.setId(object.getInt("Id"));
                                    hotel.setName(object.getString("Name"));
                                    hotel.setAvatar(object.getString("Avatar"));
                                    hotel.setAddress(object.getString("Address"));
                                    historyList.add(hotel);
                                }
                            }

                            adapterHotel = new AdapterRestaurantSearch(context, historyList);
                            recyclerList.setAdapter(adapterHotel);
                            adapterHotel.notifyDataSetChanged();

                            //dimiss progress dialog
                            alertDialog.dismiss();

                        } catch (JSONException e) {
                            Log.d("error", "get all hotel " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "connect get hotel " + error.toString());
            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
