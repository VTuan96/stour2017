package com.bkstek.stour;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterHistory;
import com.bkstek.stour.adapter.AdapterLBS;
import com.bkstek.stour.model.History;
import com.bkstek.stour.model.LBS;
import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.FunctionHelper;
import com.bkstek.stour.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_HIS_PLACE;

public class LBSActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerList;
    Context context;
    AdapterLBS adapterLBS;
    List<LBS> lbsList = new ArrayList<>();
    List<LBS> lbsListTemp = new ArrayList<>();
    ImageView imBack;

    SearchView svLBS;
    String selection_lbs = CommonDefine.BANK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lbs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerList = (RecyclerView) findViewById(R.id.recyclerList);
//        imBack = (ImageView) findViewById(R.id.imBack);
        recyclerList.setNestedScrollingEnabled(false);
        context = LBSActivity.this;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerList.setLayoutManager(layoutManager);
        adapterLBS = new AdapterLBS(context, lbsList);
        recyclerList.setAdapter(adapterLBS);
        adapterLBS.notifyDataSetChanged();

        toolbar.setTitle("Di tích lịch sử");
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

        svLBS = findViewById(R.id.svLBS);
        svLBS.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));

        if (getIntent() != null){
            selection_lbs = getIntent().getStringExtra(CommonDefine.SELECTION_ULTILITIES);
        }

        GetData();


        svLBS.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() != 0) {
                    lbsListTemp = new ArrayList<>();
                    for (LBS his : lbsList) {
                        if (his.getName().toLowerCase().contains(s.toLowerCase())) {
                            lbsListTemp.add(his);
                        }
                    }

                    recyclerList.removeAllViews();
                    adapterLBS = new AdapterLBS(context, lbsListTemp);
                    recyclerList.setAdapter(adapterLBS);
                    adapterLBS.notifyDataSetChanged();
                } else {
                    recyclerList.removeAllViews();
                    adapterLBS = new AdapterLBS(context, lbsList);
                    recyclerList.setAdapter(adapterLBS);
                    adapterLBS.notifyDataSetChanged();
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
        lbsList = new ArrayList<>();
        String result = readTxt(selection_lbs);
        JSONArray root = null;
        try {
            root = new JSONArray(result);
            int  length= root.length();
            for (int i = 0; i < length; i++){
                JSONObject obj = root.getJSONObject(i);
                String name = obj.getString("name");
                String address = obj.getString("address");
                double lat = Double.parseDouble(obj.getString("lat"));
                double lon = Double.parseDouble(obj.getString("lon"));
                String tel = "", open_time = "";
                if (selection_lbs.equals(CommonDefine.BANK)){
                    tel = obj.getString("tel");
                    open_time = obj.getString("open_time");
                }
                LBS lbs = new LBS(name, address, lat, lon, tel, open_time );
                lbsList.add(lbs);
            }

            adapterLBS = new AdapterLBS(context, lbsList);
            recyclerList.setAdapter(adapterLBS);
            adapterLBS.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private String readTxt(String selection_lbs) {
        int rawFile = 0;
        if (selection_lbs.equals(CommonDefine.BANK)) {
            rawFile = R.raw.bank_service;
            toolbar.setTitle("Ngân hàng");
        }
        else if (selection_lbs.equals(CommonDefine.ATM)) {
            rawFile = R.raw.atm_service;
            toolbar.setTitle("Cây ATM");
        }
        else if (selection_lbs.equals(CommonDefine.MART)) {
            rawFile = R.raw.market_service;
            toolbar.setTitle("Siêu thị");
        }

        //getting the .txt file
        InputStream inputStream = getResources().openRawResource(rawFile);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            int i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_around_lbs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnuConfig){
            Intent intent = new Intent(context, MapsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList(CommonDefine.LIST_LBS, (ArrayList<? extends Parcelable>) lbsList);
            intent.putExtra(CommonDefine.LBS_BUNDLE, bundle);
            intent.putExtra(CommonDefine.FUNC, CommonDefine.AROUND_HERE);
            context.startActivity(intent);
        }
        return true;
    }
}
