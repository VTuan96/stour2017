package com.bkstek.stour;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterFood;
import com.bkstek.stour.helper.GPSHelper;
import com.bkstek.stour.model.History;
import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.bkstek.stour.util.CommonDefine.GET_FOOD_PLACE;

public class WeatherActivity extends AppCompatActivity {
    GPSHelper gpsHelper;
    TextView txtTemp, txtHumi;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        txtHumi = findViewById(R.id.txtHumi);
        txtTemp = findViewById(R.id.txtTemp);

        gpsHelper = new GPSHelper(WeatherActivity.this);
        GetData(gpsHelper.getLatitude(), gpsHelper.getLongitude());


    }

    private void GetData(double lat, double lon) {
        String urlGetWeather = CommonDefine.URL_OPEN_WEATHER + "lat=" + lat + "&lon=" + lon + "&appid=" + CommonDefine.API_KEY_OPEN_WEATHER;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlGetWeather,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject root = response.getJSONObject("main");
                            String temp = root.getString("temp");
                            String humi = root.getString("humidity");
                            double tempDou = Double.parseDouble(temp);
                            txtTemp.setText(((int) tempDou - 273) + "");
                            txtHumi.setText(humi);
                        } catch (JSONException e) {
                            e.printStackTrace();
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

        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
