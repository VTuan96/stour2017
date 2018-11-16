package com.bkstek.stour;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterFood;
import com.bkstek.stour.component.DialogInfo;
import com.bkstek.stour.model.History;
import com.bkstek.stour.util.FunctionHelper;
import com.bkstek.stour.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_FOOD_PLACE;

/**
 * Created by thold on 8/26/2017.
 */

public class FoodActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerList;
    Context context;
    List<History> historyList;
    AdapterFood adapterFood;
    ImageView imBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerList = (RecyclerView) findViewById(R.id.recyclerList);
        imBack = (ImageView) findViewById(R.id.imBack);
        context = FoodActivity.this;
        recyclerList.setNestedScrollingEnabled(false);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        if (FunctionHelper.isNetworkConnected(context)) {
            GetData();
        } else {
//            new DialogInfo(context, "Không có kết nối internet!!!").show();
        }


        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iBack = new Intent(context, HomeScreenActivity.class);
                startActivity(iBack);
                finish();
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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_FOOD_PLACE,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                boolean isActive = object.getBoolean("isActive");
                                if (isActive){
                                    History history = new History();
                                    history.setId(object.getInt("Id"));
                                    history.setName(object.getString("Name"));
                                    history.setAddress(object.getString("Address"));
                                    history.setAvatar(object.getString("Avatar"));
//                                    history.setStar(object.getInt("Star"));
                                    history.setViewCount(object.getInt("ViewCount"));
                                    historyList.add(history);
                                }
                            }

                            adapterFood = new AdapterFood(context, historyList);
                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
                            recyclerList.setLayoutManager(layoutManager);
                            recyclerList.setAdapter(adapterFood);
                            adapterFood.notifyDataSetChanged();

                            alertDialog.dismiss();

                        } catch (JSONException e) {
                            Log.d("error parse json: ", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error connect volley: ", error.toString());
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
