package com.bkstek.stour;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.component.DialogPOIOrigin;
import com.bkstek.stour.mapdigital.TileProviderFactory;
import com.bkstek.stour.model.POI;
import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.DirectionsJSONParser;
import com.bkstek.stour.util.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.data.geojson.GeoJsonLayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import at.markushi.ui.CircleButton;

import static com.bkstek.stour.util.CommonDefine.GET_HIS_PLACE;
import static com.bkstek.stour.util.CommonDefine.GOOGLEMAP_DIRECTION;
import static com.bkstek.stour.util.CommonDefine.WMS_FORMAT_ROUTE_STRING;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapLongClickListener, View.OnClickListener{

    public static GoogleMap mMap;
    EditText edFrom, edTo;

    RelativeLayout rlCar, rlBicycle, rlWalk;
    ImageView imCar, imBicycle, imWalk;

    RelativeLayout rlSingle, rlMulti, lnMultiRouting, rlButtonMultiRoute;
    TextView txtSingle, txtMulti;

    LinearLayout rlPosition;

    LinearLayout lnDriving, lnDrivingMulti;
    RelativeLayout rlCarMulti, rlBicycleMulti, rlWalkMulti;
    ImageView imCarMulti, imBicycleMulti, imWalkMulti;

    RelativeLayout rlDown;

    public  static  Context context;
    CircleButton btnSearch;
    public static BitmapDescriptor bitmapDescriptorTo;
    public static BitmapDescriptor bitmapDescriptorFrom;
    Address locationFrom = null;
    Address locationTo = null;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Marker mCurrLocationMarker;
    Location mLastLocation;
    LatLng currentLocation;
    LatLng longClickLocation = new LatLng(21.775414,105.4772916); //location of point long click map
    Marker marker; //marker use for long click map

    private String TAG = ""; // set tag for driection mode

    private String Mode = "";
    private float RADIS_MAX = 230; //max radius POI (unit: m)
    private long TIME_TO_UPDATE = 10000; // 10s

    //list latlong for multi direction
    List<LatLng> latLngList = new ArrayList<>();

    //get test direction json file
    String test_direction = "";

    private String func = "ROUTING"; //default is routing

    ArrayList<POI> listPOI = new ArrayList<>();
    ArrayList<POI> tempPOI = new ArrayList<>();

    private LinearLayout lnHeader;
    SharedPreferences preferences ;

    MediaPlayer mMediaPlayer;

    //show information of destination
    CardView cvInfoDes;
    TextView txtTitleDes, txtDistanceDes, txtDurationDes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        edFrom = (EditText) findViewById(R.id.edFrom);
        edTo = (EditText) findViewById(R.id.edTo);
        btnSearch = (CircleButton) findViewById(R.id.btnSearch);
        rlCar = (RelativeLayout) findViewById(R.id.rlCar);
        rlBicycle = (RelativeLayout) findViewById(R.id.rlBicycle);
        rlWalk = (RelativeLayout) findViewById(R.id.rlWalk);
        imCar = (ImageView) findViewById(R.id.imCar);
        imBicycle = (ImageView) findViewById(R.id.imBicycle);
        imWalk = (ImageView) findViewById(R.id.imWalk);

        rlSingle = (RelativeLayout) findViewById(R.id.rlSingle);
        rlMulti = (RelativeLayout) findViewById(R.id.rlMulti);
        txtSingle = (TextView) findViewById(R.id.txtSingle);
        txtMulti = (TextView) findViewById(R.id.txtMulti);
        lnMultiRouting = (RelativeLayout) findViewById(R.id.lnMultiRouting);
        rlButtonMultiRoute = (RelativeLayout) findViewById(R.id.rlButtonMultiRoute);
        rlPosition = (LinearLayout) findViewById(R.id.rlPosition);
        lnDriving = (LinearLayout) findViewById(R.id.lnDriving);
        lnDrivingMulti = (LinearLayout) findViewById(R.id.lnDrivingMulti);
        rlCarMulti = (RelativeLayout) findViewById(R.id.rlCarMulti);
        rlBicycleMulti = (RelativeLayout) findViewById(R.id.rlBicycleMulti);
        rlWalkMulti = (RelativeLayout) findViewById(R.id.rlWalkMulti);
        imCarMulti = (ImageView) findViewById(R.id.imCarMulti);
        imBicycleMulti = (ImageView) findViewById(R.id.imBicycleMulti);
        imWalkMulti = (ImageView) findViewById(R.id.imWalkMulti);
        lnHeader = findViewById(R.id.lnHeader);

//        cvInfoDes = findViewById(R.id.cvInfoDes);
//        txtDistanceDes = findViewById(R.id.txtDistanceDes);
//        txtDurationDes = findViewById(R.id.txtDurationDes);

        rlDown = (RelativeLayout) findViewById(R.id.rlDown);

        context = MapsActivity.this;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        //get intent data
        getIntentData();

        //check status of location
        statusCheck();

        //get shareprefences
        preferences = getSharedPreferences(CommonDefine.SETTING, MODE_PRIVATE);
        RADIS_MAX = Float.parseFloat(preferences.getString(CommonDefine.RADIUS_ACCESS, "50"));
        TIME_TO_UPDATE = Long.parseLong(preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "10")) * 1000;

        if (func.equals(CommonDefine.SMART)){
            lnHeader.setVisibility(View.GONE);
        } else {
            lnHeader.setVisibility(View.VISIBLE);
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        mSettingsClient = LocationServices.getSettingsClient(this);

//        createLocationCallback();
//        createLocationRequest();
//        buildLocationSettingsRequest();


        bitmapDescriptorFrom
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED);

        bitmapDescriptorTo
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtFrom = edFrom.getText().toString();
                if (txtFrom.equals("Vị trí của bạn")) {
                    TAG = CommonDefine.CURRENT_LOCATION;
                } else {
                    TAG = CommonDefine.TWO_POINT_RANDOM;
                }
                DriectionMap(TAG, Mode);
            }
        });

        rlCar.setOnClickListener(this);
        rlBicycle.setOnClickListener(this);
        rlWalk.setOnClickListener(this);
        rlSingle.setOnClickListener(this);
        rlMulti.setOnClickListener(this);
        rlCarMulti.setOnClickListener(this);
        rlBicycleMulti.setOnClickListener(this);
        rlWalkMulti.setOnClickListener(this);

        rlDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlDown.setVisibility(View.GONE);
                rlPosition.setVisibility(View.VISIBLE);
            }
        });

        test_direction = readRawFiles(R.raw.test_direction);
    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null)
            func = intent.getStringExtra(CommonDefine.FUNC);
        else
            func = "ROUTING";
    }

    //region onclick view

    @Override
    public void onClick(View view) {
        String txtFrom = edFrom.getText().toString();
        if (txtFrom.equals("Vị trí của bạn")) {
            TAG = CommonDefine.CURRENT_LOCATION;
        } else {
            TAG = CommonDefine.TWO_POINT_RANDOM;
        }

        int id = view.getId();
        switch (id) {
            case R.id.rlCar:
                ClearMap();
                ClickModeCar("single");
                Mode = CommonDefine.MODE_DRIVING;
                DriectionMap(TAG, Mode);
//                setUpMap();
                break;
            case R.id.rlBicycle:
                ClickModeBicycle("single");
                Mode = CommonDefine.MODE_BICYCLING;
                DriectionMap(TAG, Mode);
                ClearMap();
//                setUpMap();
                break;
            case R.id.rlWalk:
                ClearMap();
                ClickModeWalk("single");
                Mode = CommonDefine.MODE_WALKING;
                DriectionMap(TAG, Mode);
//                setUpMap();
                break;
            case R.id.rlSingle:
                ClearMap();
                lnDrivingMulti.setVisibility(View.GONE);
                lnDriving.setVisibility(View.VISIBLE);
                ClickSingleLayout();
                setUpMap();
                break;
            case R.id.rlMulti:
                lnDrivingMulti.setVisibility(View.VISIBLE);
                lnDriving.setVisibility(View.GONE);
                rlDown.setVisibility(View.GONE);
                ClickMultiLayout();
                ClearMap();
                setUpMap();
                latLngList = new ArrayList<>();
                tempPOI = new ArrayList<>();
                new DialogPOIOrigin(context, listPOI, tempPOI, latLngList, func).show();
                break;
            case R.id.rlCarMulti:
                ClickModeCar("multi");
                Mode = CommonDefine.MODE_DRIVING;
                if (latLngList.size() == 0) {
                    Toast.makeText(context, "Vui lòng chọn điểm xuất phát", Toast.LENGTH_SHORT).show();
                } else if (latLngList.size() == 1) {
                    Toast.makeText(context, "Vui lòng chọn điểm đến", Toast.LENGTH_SHORT).show();
                } else {
                    ClearMap();
                    for (int i = 1; i < latLngList.size(); i++) {
                        MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), Mode);
                    }
                }

                break;
            case R.id.rlBicycleMulti:
                ClickModeBicycle("multi");
                Mode = CommonDefine.MODE_BICYCLING;
                if (latLngList.size() == 0) {
                    Toast.makeText(context, "Vui lòng chọn điểm xuất phát", Toast.LENGTH_SHORT).show();
                } else if (latLngList.size() == 1) {
                    Toast.makeText(context, "Vui lòng chọn điểm đến", Toast.LENGTH_SHORT).show();
                } else {
                    ClearMap();
                    for (int i = 1; i < latLngList.size(); i++) {
                        MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), Mode);
                    }
                }

                break;
            case R.id.rlWalkMulti:
                ClickModeWalk("multi");
                Mode = CommonDefine.MODE_WALKING;
                if (latLngList.size() == 0) {
                    Toast.makeText(context, "Vui lòng chọn điểm xuất phát", Toast.LENGTH_SHORT).show();
                } else if (latLngList.size() == 1) {
                    Toast.makeText(context, "Vui lòng chọn điểm đến", Toast.LENGTH_SHORT).show();
                } else {
                    ClearMap();
                    for (int i = 1; i < latLngList.size(); i++) {
                        MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), Mode);
                    }
                }

                break;
        }
    }

    private void ClickSingleLayout() {
        rlSingle.setBackgroundResource(R.drawable.custom_oval_layout);
        txtSingle.setTextColor(Color.parseColor("#4487f2"));

        rlMulti.setBackgroundColor(Color.parseColor("#4487f2"));
        txtMulti.setTextColor(Color.parseColor("#ffffff"));
        lnMultiRouting.setVisibility(View.GONE);
        rlPosition.setVisibility(View.VISIBLE);
    }

    private void ClickMultiLayout() {
        rlMulti.setBackgroundResource(R.drawable.custom_oval_layout);
        txtMulti.setTextColor(Color.parseColor("#4487f2"));

        rlSingle.setBackgroundColor(Color.parseColor("#4487f2"));
        txtSingle.setTextColor(Color.parseColor("#ffffff"));

        lnMultiRouting.setVisibility(View.GONE);
        rlPosition.setVisibility(View.GONE);
    }


    private void ClickModeBicycle(String tag) {

        if (tag.equals("single")) {
            rlBicycle.setBackgroundResource(R.drawable.custom_oval_layout);
            imBicycle.setImageResource(R.drawable.bicycle_2);

            rlCar.setBackgroundColor(Color.parseColor("#4487f2"));
            imCar.setImageResource(R.drawable.car_big4);

            rlWalk.setBackgroundColor(Color.parseColor("#4487f2"));
            imWalk.setImageResource(R.drawable.walking_2);
        } else if (tag.equals("multi")) {
            rlBicycleMulti.setBackgroundResource(R.drawable.custom_oval_layout);
            imBicycleMulti.setImageResource(R.drawable.bicycle_2);

            rlCarMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imCarMulti.setImageResource(R.drawable.car_big4);

            rlWalkMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imWalkMulti.setImageResource(R.drawable.walking_2);
        }


    }

    private void ClickModeWalk(String tag) {
        if (tag.equals("single")) {
            rlWalk.setBackgroundResource(R.drawable.custom_oval_layout);
            imWalk.setImageResource(R.drawable.walking_1);

            rlCar.setBackgroundColor(Color.parseColor("#4487f2"));
            imCar.setImageResource(R.drawable.car_big4);

            rlBicycle.setBackgroundColor(Color.parseColor("#4487f2"));
            imBicycle.setImageResource(R.drawable.bycycle_1);
        } else if (tag.equals("multi")) {
            rlWalkMulti.setBackgroundResource(R.drawable.custom_oval_layout);
            imWalkMulti.setImageResource(R.drawable.walking_1);

            rlCarMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imCarMulti.setImageResource(R.drawable.car_big4);

            rlBicycleMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imBicycleMulti.setImageResource(R.drawable.bycycle_1);
        }


    }

    private void ClickModeCar(String tag) {
        if (tag.equals("single")) {
            rlWalk.setBackgroundColor(Color.parseColor("#4487f2"));
            imWalk.setImageResource(R.drawable.walking_2);

            rlCar.setBackgroundResource(R.drawable.custom_oval_layout);
            imCar.setImageResource(R.drawable.car_7);

            rlBicycle.setBackgroundColor(Color.parseColor("#4487f2"));
            imBicycle.setImageResource(R.drawable.bycycle_1);
        } else if (tag.equals("multi")) {
            rlWalkMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imWalkMulti.setImageResource(R.drawable.walking_2);

            rlCarMulti.setBackgroundResource(R.drawable.custom_oval_layout);
            imCarMulti.setImageResource(R.drawable.car_7);

            rlBicycleMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imBicycleMulti.setImageResource(R.drawable.bycycle_1);
        }

    }

    //endregion

    //region command constructor
    private void SetDefault() {
        Mode = CommonDefine.MODE_DRIVING;
        TAG = CommonDefine.CURRENT_LOCATION;

        edFrom.setText("Vị trí của bạn");
        edTo.setSelection(0);
    }
    //endregion

    //region dinh tuyen chung
    private void DriectionMap(String tag, String mode) {
        String txtFrom = edFrom.getText().toString();
        if (txtFrom.equals("")) {
            Toast.makeText(context, "Vui lòng chọn điểm đến", Toast.LENGTH_LONG).show();
        } else {
            rlDown.setVisibility(View.VISIBLE);
            rlPosition.setVisibility(View.GONE);
            switch (tag) {
                case CommonDefine.CURRENT_LOCATION:
                    DirectionGoogleMap(currentLocation, longClickLocation, mode);
                    break;
                case CommonDefine.TWO_POINT_RANDOM:
                    DirectionFor2Point();
                    break;

            }
        }
    }

    private void DirectionFor2Point() {
        hideSoftKeyboard();
        ClearMap();
        setUpMap();
        getAddress(Mode);
    }
    //endregion

    //region clear all map

    public static void ClearMap() {
        if (mMap != null)
            mMap.clear();
    }
    //endregion

    //region long click map
    @Override
    public void onMapLongClick(LatLng latLng) {
        if (mMap != null) {
            if (func.equals(CommonDefine.ROUTING)) {
                if (marker != null) {
                    marker.remove();
                }
                ClearMap();
                marker = mMap.addMarker(new MarkerOptions().position(latLng));
                longClickLocation = latLng;
                edTo.setText(String.valueOf(latLng.latitude).substring(0, 12) + "," + String.valueOf(latLng.longitude).substring(0, 12));
                TAG = CommonDefine.CURRENT_LOCATION;
                Mode = CommonDefine.MODE_DRIVING;
                ClickModeCar("single");
            }
        }
    }

    //endregion

    //region get current location
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
        //set interval location request update
        TIME_TO_UPDATE = Long.parseLong(preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "10")) * 1000;
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
    public void onLocationChanged(final Location location) {

//        Toast.makeText(context, "Location changed \n Lat: " + location.getLatitude(), Toast.LENGTH_SHORT).show();

        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Showing Current Location Marker on Map
        final LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(myLatLng);

//        locationManager.requestLocationUpdates(provider, 1000, 0, this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));


        /*---------------check near object interest POI----------------------*/
        //get list POI

//        getIntentData();
        if (func.equals("SMART")){
            //get radius access POI
            RADIS_MAX = Float.parseFloat(preferences.getString(CommonDefine.RADIUS_ACCESS, "50"));

            //create new list POIs
            listPOI = new ArrayList<>();
            tempPOI = new ArrayList<>();
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_HIS_PLACE,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //nearest POI
                                POI minPOI = null;
                                float minDistance = RADIS_MAX;

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
                                        poi.setVideoDir(object.getString("VideoDir"));

                                        listPOI.add(poi);
                                    }
                                }
                                //show all POI markers
                                showAllPOIMarkers(listPOI);

                                for (POI poi : listPOI){
                                    Location locDest = new Location("dest");
                                    locDest.setLatitude(poi.getLatitude());
                                    locDest.setLongitude(poi.getLongitude());
                                    float distance = location.distanceTo(locDest);

                                    if (distance < minDistance) {
                                        minDistance = distance;
                                        minPOI = poi;
                                    }
                                }

                                if (minDistance <= RADIS_MAX ) { //check distance to POI to RADIUS_MAX
                                    if ( minPOI != null) {
//                                        try {
//                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                                            r.play();
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }

                                        mMediaPlayer = MediaPlayer.create(context, R.raw.raw_find_out);
                                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mMediaPlayer.start();

                                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                mp.release();
                                            }
                                        });

                                        Toast.makeText(MapsActivity.this, "Bạn đang ở gần " + minPOI.getName(), Toast.LENGTH_SHORT).show();

                                        LatLng latLng = new LatLng(minPOI.getLatitude(), minPOI.getLongitude());
                                        MarkerOptions markerOptions1 = new MarkerOptions();
                                        markerOptions1.position(latLng);
                                        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                        markerOptions1.title(minPOI.getName());
                                        markerOptions1.visible(true);

                                        //move map camera
                                        Marker markerPOI = mMap.addMarker(markerOptions1);
                                        markerPOI.showInfoWindow();
                                        markerPOI.setTag(minPOI);
//                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

                                        //routing to POI
                                        DirectionGoogleMapFor2Point(myLatLng, latLng, CommonDefine.MODE_WALKING);
                                    } else {
                                        try {
//                                            mMediaPlayer = MediaPlayer.create(context, R.raw.raw_not_found);
//                                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                                            mMediaPlayer.start();
//
//                                            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                                                @Override
//                                                public void onCompletion(MediaPlayer mp) {
//                                                    mp.release();
//                                                }
//                                            });


                                            //alert not found poi ringstone
                                            alarmRingStone(R.raw.raw_not_found);

                                            Toast.makeText(MapsActivity.this, "Không có địa điểm nào trong bán kính " + (int ) RADIS_MAX + "m", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    try {
                                        mMediaPlayer = MediaPlayer.create(context, R.raw.raw_not_found);
                                        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                        mMediaPlayer.start();

                                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                mp.release();
                                            }
                                        });

                                        Toast.makeText(MapsActivity.this, "Không có địa điểm nào trong bán kính " + (int ) RADIS_MAX + "m", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

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

        } else {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                POI poi = (POI) marker.getTag();
                if (poi != null) {
                    String VideoDir = poi.getVideoDir();
                    int locationID = poi.getId();
                    Intent iYoutube = new Intent(context, YoutubeActivity.class);
                    iYoutube.putExtra("VIDEO_ID", VideoDir);
                    iYoutube.putExtra("locationID", locationID);
                    iYoutube.putExtra("TAG", CommonDefine.SMART);
                    iYoutube.putExtra(CommonDefine.TITLE, poi.getName());
                    context.startActivity(iYoutube);
                }
                return true;
            }
        });

    }

    //notifycation ringstone
    private void alarmRingStone(int raw){
        mMediaPlayer = MediaPlayer.create(context, raw);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.start();

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
    }


    private void showAllPOIMarkers(ArrayList<POI> listPOIs) {
        //clear all marker on map
        mMap.clear();

        for (POI poi: listPOIs){
            LatLng latLng = new LatLng(poi.getLatitude(), poi.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.title(poi.getName());

            Marker marker = mMap.addMarker(markerOptions);
//            marker.showInfoWindow();
            marker.setTag(poi);

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                POI poi = (POI) marker.getTag();
                String VideoDir = poi.getVideoDir();
                int locationID = poi.getId();
                Intent iYoutube = new Intent(context, YoutubeActivity.class);
                iYoutube.putExtra("VIDEO_ID", VideoDir);
                iYoutube.putExtra("locationID", locationID);
                iYoutube.putExtra("TAG", CommonDefine.SMART);
                iYoutube.putExtra(CommonDefine.TITLE, poi.getName());
                startActivity(iYoutube);

                return true;
            }
        });
    }


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    //endregion

    //region get address for 2 point random

    private void getAddress(String googleMode) {
        String addressFrom = edFrom.getText().toString();
        String addressTo = edTo.getText().toString();
        if (addressFrom.equals("")) {
            Toast.makeText(context, "Bạn chưa nhập địa chỉ điểm đi", Toast.LENGTH_LONG).show();
        } else if (addressTo.equals("")) {
            Toast.makeText(context, "Bạn chưa nhập địa chỉ điểm đến", Toast.LENGTH_LONG).show();
        } else {
            getLocationFromAddress(addressFrom, addressTo, googleMode);

        }
    }
    //endregion

    //region set up digital map

    private void setUpMap() {
//        TileProvider tileProvider = TileProviderFactory.getTileProvider(GEOSERVER_FORMAT);
//        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.test_bkhn, getApplicationContext());
            //set color to geojson map
            layer.getDefaultLineStringStyle().setColor(Color.YELLOW);
            layer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //endregion

    // region dinh tuyen tren ban do so hoa thong qua kinh do vi do diem dau va diem cuoi
    private void RoutingInMap(Address locationF, Address locationT) {
        String url = WMS_FORMAT_ROUTE_STRING + "=x1:" + locationF.getLongitude() + ";y1:" + locationF.getLatitude() + ";x2:" + locationT.getLongitude()
                + ";y2:" + locationT.getLatitude() + "&SERVICE=WMS&VERSION=1.1.1" +
                "&REQUEST=GetMap&STYLES=&SRS=EPSG:3857" +
                "&BBOX=%f,%f,%f,%f" +
                "&WIDTH=256&HEIGHT=256";

        Log.d("duytho", url);

        TileProvider tileProvider_route = TileProviderFactory.getTileProvider(url);
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider_route));
    }


    public void getLocationFromAddress(String strAddressFrom, String strAddressTo, String googleMode) {

        Geocoder coder = new Geocoder(this, Locale.US);
        List<Address> addressFrom;
        List<Address> addressTo;
        try {
            addressFrom = coder.getFromLocationName(strAddressFrom, 1);
            addressTo = coder.getFromLocationName(strAddressTo, 1);
            try {
                if (addressFrom.size() == 0) {
                    Toast.makeText(context, "Địa chỉ điểm đi bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
                } else if (addressTo.size() == 0) {
                    Toast.makeText(context, "Địa chỉ điểm đến bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
                } else {
                    locationFrom = addressFrom.get(0);
                    locationTo = addressTo.get(0);
                    LatLng from = new LatLng(locationFrom.getLatitude(), locationFrom.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(from).icon(bitmapDescriptorFrom).title(strAddressFrom)).showInfoWindow();

                    LatLng to = new LatLng(locationTo.getLatitude(), locationTo.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(to).icon(bitmapDescriptorTo).title(strAddressTo)).showInfoWindow();

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(from);
                    builder.include(to);
                    LatLngBounds bound = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 25), 1000, null);

                    DirectionGoogleMapFor2Point(from, to, googleMode);
                    RoutingInMap(locationFrom, locationTo);
                }
            } catch (Exception e) {
                Log.d("error", e.toString());
            }


        } catch (IOException e) {
            // Toast.makeText(context, "Địa chỉ bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
        }
    }


    public static void getLocationForMultiDirection(LatLng from, LatLng to, String googleMode) {
        try {
            mMap.addMarker(new MarkerOptions().position(from).icon(bitmapDescriptorFrom));


            mMap.addMarker(new MarkerOptions().position(to).icon(bitmapDescriptorTo));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(from);
            builder.include(to);
            LatLngBounds bound = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 25), 1000, null);

            DirectionGoogleMapFor2Point(from, to, googleMode);

        } catch (Exception e) {
            // Toast.makeText(context, "Địa chỉ bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
        }
    }

    public static void getLocationForMultiDirectionPOI(POI from, POI to, String googleMode) {
        try {
            LatLng fromLat = new LatLng(from.getLatitude(), from.getLongitude());
            LatLng toLon = new LatLng(to.getLatitude(), to.getLongitude());

            Marker markerFrom = mMap.addMarker(new MarkerOptions()
                                                .position(fromLat)
                                                .icon(bitmapDescriptorTo)
                                                .title(from.getName()));
            markerFrom.showInfoWindow();
            markerFrom.setTag(from);

            Marker markerTo = mMap.addMarker(new MarkerOptions()
                                                .position(toLon)
                                                .icon(bitmapDescriptorFrom)
                                                .title(to.getName()));
            markerTo.showInfoWindow();
            markerTo.setTag(to);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(fromLat);
            builder.include(toLon);
            LatLngBounds bound = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 25), 1000, null);

            DirectionGoogleMapFor2Point(fromLat,toLon , googleMode);

        } catch (Exception e) {
            // Toast.makeText(context, "Địa chỉ bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    private void DirectionGoogleMap(LatLng from, LatLng to, String mode) {
        // Getting URL to the Google Directions API
        ClearMap();
        setUpMap();
        mMap.addMarker(new MarkerOptions().position(longClickLocation));
        String url = getDirectionUrl(from, to, mode);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

    }

    private static void DirectionGoogleMapFor2Point(LatLng from, LatLng to, String mode) {
        // Getting URL to the Google Directions API
        // ClearMap();
        // mMap.addMarker(new MarkerOptions().position(longClickLocation));
        String url = getDirectionUrl(from, to, mode);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);

    }


    //region dinh tuyen bang google map

    private static String getDirectionUrl(LatLng origin, LatLng dest, String modeGoogleMap) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=" + modeGoogleMap;


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = GOOGLEMAP_DIRECTION + output + "?" + parameters + "&key=AIzaSyBcR6y_giPahewOFi2Hv78KpaBPLIZEut0";

        Log.d("GOOGLE_MAP_DIRECTION", url);

        return url;
    }

    private static String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private static class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.executeOnExecutor(THREAD_POOL_EXECUTOR, result);

        }
    }


    private static class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("Vutuan", jObject.toString());

                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = new ArrayList<LatLng>();
            PolylineOptions lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                // Changing the color polyline according to the mode

                lineOptions.color(Color.RED);

                lineOptions.geodesic(true);

            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Log.d("error", e.toString());
            }
            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }


    //endregion


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();

            //show button My Location
            mMap.setMyLocationEnabled(true);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.004508, 105.844160), 16.0f));

        if (mMap != null) {
            setUpMap();
            SetDefault();
            mMap.setOnMapLongClickListener(this);
        }

    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public String readRawFiles(int rawFile) {
        String str="";
        StringBuffer buf = new StringBuffer();
        InputStream is = null;
        try {
            is = this.getResources().openRawResource(rawFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            if (is != null) {
                while ((str = reader.readLine()) != null) {
                    buf.append(str + "\n");
                }
            }
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally{
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        str = buf.toString();
        return str;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
