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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import com.bkstek.stour.adapter.AdapterCulture;
import com.bkstek.stour.adapter.AdapterHistory;
import com.bkstek.stour.component.DialogInfo;
import com.bkstek.stour.model.History;
import com.bkstek.stour.util.FunctionHelper;
import com.bkstek.stour.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import com.bkstek.stour.R;

import static com.bkstek.stour.util.CommonDefine.GET_CULTURE_PLACE;

/**
 * Created by thold on 8/26/2017.
 */

public class CultureActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerList;
    ImageView imBack;
    Context context;
    AdapterCulture adapterCulture;

    List<History> historyList = new ArrayList<>();
    List<History> historyListTemp = new ArrayList<>();
    SearchView svCuture;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_culture_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerList = (RecyclerView) findViewById(R.id.recyclerList);
//        imBack = (ImageView) findViewById(R.id.imBack);
        recyclerList.setNestedScrollingEnabled(false);
        context = CultureActivity.this;

        toolbar.setTitle("Văn hóa lễ hội");
        setSupportActionBar(toolbar);

        svCuture = findViewById(R.id.svCulture);
        svCuture.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));

        toolbar.setNavigationIcon(R.drawable.back2);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iBack = new Intent(context, HomeScreenActivity.class);
                startActivity(iBack);
                finish();
            }
        });

        if (FunctionHelper.isNetworkConnected(context)) {
            GetData();
        } else {
//            new DialogInfo(context, "Không có kết nối internet!!!").show();
        }

        svCuture.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
                    adapterCulture = new AdapterCulture(context, historyListTemp);
                    recyclerList.setAdapter(adapterCulture);
                    adapterCulture.notifyDataSetChanged();
                } else {
                    recyclerList.removeAllViews();
                    adapterCulture = new AdapterCulture(context, historyList);
                    recyclerList.setAdapter(adapterCulture);
                    adapterCulture.notifyDataSetChanged();
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

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_CULTURE_PLACE,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                History history = new History();
                                history.setId(object.getInt("Id"));
                                history.setName(object.getString("Name"));
                                history.setAddress(object.getString("Address"));
                                history.setAvatar(object.getString("Avatar"));
//                                history.setStar(object.getInt("Star"));
                                history.setViewCount(object.getInt("ViewCount"));
                                historyList.add(history);
                            }

                            adapterCulture = new AdapterCulture(context, historyList);
                            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
                            recyclerList.setLayoutManager(layoutManager);
                            recyclerList.setAdapter(adapterCulture);
                            adapterCulture.notifyDataSetChanged();

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.tool_bar_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        return super.onOptionsItemSelected(item);
//    }
}
