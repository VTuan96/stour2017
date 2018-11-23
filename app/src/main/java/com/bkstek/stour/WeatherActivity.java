package com.bkstek.stour;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.bkstek.stour.util.CommonDefine.GET_FOOD_PLACE;

public class WeatherActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GPSHelper gpsHelper;
    TextView txtTemp, txtHumi;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    Context context = null;
    Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        txtHumi = findViewById(R.id.txtHumi);
        txtTemp = findViewById(R.id.txtTemp);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        context = WeatherActivity.this;

        buildGoogleApiClient();

    }

    private void getData(double lat, double lon) {
        final ProgressDialog alertDialog = ProgressDialog.show(context, "", "Vui lòng đợi...");

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

                            alertDialog.dismiss();
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

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        long TIME_TO_UPDATE = 10000;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(TIME_TO_UPDATE);
        mLocationRequest.setFastestInterval(TIME_TO_UPDATE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        getData(location.getLatitude(), location.getLongitude());
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
    }
}
