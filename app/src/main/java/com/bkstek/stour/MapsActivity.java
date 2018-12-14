package com.bkstek.stour;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterDetailDirection;
import com.bkstek.stour.adapter.CustomInfoWindowMapAdapter;
import com.bkstek.stour.component.DialogPOIOrigin;
import com.bkstek.stour.helper.CustomDialog;
import com.bkstek.stour.mapdigital.TileProviderFactory;
import com.bkstek.stour.model.DetailDirection;
import com.bkstek.stour.model.LBS;
import com.bkstek.stour.model.POI;
import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.DirectionsJSONParser;
import com.bkstek.stour.util.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
import com.google.gson.JsonObject;
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
import java.util.Map;

import at.markushi.ui.CircleButton;

import static com.bkstek.stour.util.CommonDefine.FUNC;
import static com.bkstek.stour.util.CommonDefine.GET_HIS_PLACE;
import static com.bkstek.stour.util.CommonDefine.GOOGLEMAP_DIRECTION;
import static com.bkstek.stour.util.CommonDefine.WMS_FORMAT_ROUTE_STRING;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapLongClickListener, View.OnClickListener,
        GoogleMap.OnMapClickListener, ActivityCompat.OnRequestPermissionsResultCallback {

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
    Address locationFrom = new Address(Locale.getDefault());
    Address locationTo = new Address(Locale.getDefault());


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
    private double RADIUS_MAX = 230; //max radius POI (unit: m)
    private double RADIUS_AROUND_LBS = 5; //max radius around LBS (unit: km)
    private double TIME_TO_UPDATE = 10000; // 10s

    //list latlong for multi direction
    List<LatLng> latLngList = new ArrayList<>();

    //get test direction json file
    String test_direction = "";

    private String func = "ROUTING"; //default is routing

    ArrayList<POI> listPOI = new ArrayList<>();
    ArrayList<POI> tempPOI = new ArrayList<>();
    public static List<POI> listPOIs = new ArrayList<>();

    private LinearLayout lnHeader;
    SharedPreferences preferences ;

    MediaPlayer mMediaPlayer = new MediaPlayer();

    List<POI> tempListPOI = new ArrayList<>();

    //show information of destination
    public static CardView cvInfoDes;
    public static LinearLayout lnInfoDes;
    public static TextView txtHideInfoDes;
    public static TextView txtTitleDes, txtDistanceDes, txtDurationDes, txtTitleOri, txtTitleDirection;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM = 1111;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE_TO = 2222;

    LatLng latLngOrigin, latLngDestination;
    LinearLayout lnContainMap;

    static boolean isShowCardViewDes = false;
    TextView txtDetailDirection;
    public static ListView lvDetailTravel;
    public static ArrayList<DetailDirection> listDetailDirection;
    public static AdapterDetailDirection adapterDetailDirection;

    //show play media of POI
    CardView cvPlay;
    TextView txtTitlePlay;
    ImageView imgPlay, imgVideo;

    POI lastPOI = new POI();
    Boolean isPlayAudio = false;
    String AudioURL = "";
    String VideoURL = "";

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
//        txtDetailDirection = findViewById(R.id.txtDetailDirection);
        lvDetailTravel = findViewById(R.id.lvDetailTravel);

        cvInfoDes = findViewById(R.id.cvInforDes);
        lnInfoDes = findViewById(R.id.lnInforDes);
        txtHideInfoDes = findViewById(R.id.txtHideInfoDes);
        txtTitleDes = findViewById(R.id.txtTitleDes);
        txtTitleOri = findViewById(R.id.txtTitleOri);
        txtDistanceDes = findViewById(R.id.txtDistanceDes);
        txtDurationDes = findViewById(R.id.txtDurationDes);
        txtTitleDirection = findViewById(R.id.txtTitleDirection);
        lnContainMap = findViewById(R.id.lnContainMap);

        cvPlay = findViewById(R.id.cvPlay);
        txtTitlePlay = findViewById(R.id.txtTitlePlay);
        imgPlay = findViewById(R.id.imgPlay);
        imgVideo = findViewById(R.id.imgVideo);

        rlDown = (RelativeLayout) findViewById(R.id.rlDown);

        context = MapsActivity.this;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions())
                requestLocationPermission();
        }


        //get intent data
        getIntentData();

        //check status of location
        statusCheck();

        //get shareprefences
        preferences = getSharedPreferences(CommonDefine.SETTING, MODE_PRIVATE);
        RADIUS_MAX = Double.parseDouble(preferences.getString(CommonDefine.RADIUS_ACCESS, "50"));
        TIME_TO_UPDATE = Double.parseDouble(preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "10")) * 1000;

        if (func.equals(CommonDefine.SMART)){
            lnHeader.setVisibility(View.GONE);
//            cvPlay.setVisibility(View.VISIBLE);
            cvInfoDes.setVisibility(View.GONE);
        } else {
            lnHeader.setVisibility(View.VISIBLE);
            cvPlay.setVisibility(View.GONE);
            cvInfoDes.setVisibility(View.VISIBLE);
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
                BitmapDescriptorFactory.HUE_AZURE);

        bitmapDescriptorTo
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED);

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

        edFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        edTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_TO);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });


        txtHideInfoDes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isShowCardViewDes = !isShowCardViewDes;
                if (isShowCardViewDes){
                    lnInfoDes.setVisibility(View.VISIBLE);
                    CardView.LayoutParams lpCardView = (CardView.LayoutParams) cvInfoDes.getLayoutParams();
                    lpCardView.width = CardView.LayoutParams.MATCH_PARENT;
                    lpCardView.height = CardView.LayoutParams.WRAP_CONTENT;

                    LinearLayout.LayoutParams lpLinear = (LinearLayout.LayoutParams) lnInfoDes.getLayoutParams();
                    lpLinear.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    lpLinear.height = LinearLayout.LayoutParams.MATCH_PARENT;

                } else {
                    lnInfoDes.setVisibility(View.GONE);
                    LinearLayout.LayoutParams lpLinear = (LinearLayout.LayoutParams) lnInfoDes.getLayoutParams();
                    lpLinear.width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    lpLinear.height = LinearLayout.LayoutParams.WRAP_CONTENT;

                    CardView.LayoutParams lpCardView = (CardView.LayoutParams) cvInfoDes.getLayoutParams();
                    lpCardView.width = CardView.LayoutParams.WRAP_CONTENT;
                    lpCardView.height = CardView.LayoutParams.WRAP_CONTENT;
                }
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


        /*
         * setup data to list view direction travel
        */
        listDetailDirection = new ArrayList<>();
        adapterDetailDirection = new AdapterDetailDirection(context, listDetailDirection);
        lvDetailTravel.setAdapter(adapterDetailDirection);

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Drawable icon = getResources().getDrawable(R.drawable.mr_media_play_light);
                imgPlay.setImageDrawable(icon);
                mp.release();
                Toast.makeText(MapsActivity.this, "complete ", Toast.LENGTH_SHORT).show();
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPlayAudio = !isPlayAudio;
                if (isPlayAudio){
                    Drawable icon = getResources().getDrawable(R.drawable.mr_media_pause_light);
                    imgPlay.setImageDrawable(icon);
                    if (mMediaPlayer != null) {
                        try {
                            mMediaPlayer = new MediaPlayer();
                            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            if (AudioURL != null && !AudioURL.equals(""))
                                mMediaPlayer.setDataSource(AudioURL);
                            else
                                mMediaPlayer.setDataSource(CommonDefine.DOMAIN_STOUR + "/UploadedFiles/files/Dinh%20Tan%20Trao(eng).mp3");
                            mMediaPlayer.prepare(); // might take long! (for buffering, etc)
                            mMediaPlayer.start();
                        } catch (IOException ex){
                            ex.printStackTrace();
                        }
                    }
                } else {
                    Drawable icon = getResources().getDrawable(R.drawable.mr_media_play_light);
                    imgPlay.setImageDrawable(icon);
                    if (mMediaPlayer != null)
                        mMediaPlayer.stop();
                }
            }
        });


        imgVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastPOI != null) {
                    VideoURL = lastPOI.getVideoDir();
                    Intent iYoutube = new Intent(context, YoutubeActivity.class);
                    iYoutube.putExtra(CommonDefine.VIDEO_DIR, VideoURL);
                    iYoutube.putExtra("locationID", lastPOI.getId());
                    iYoutube.putExtra("TAG", CommonDefine.SMART);
                    iYoutube.putExtra(CommonDefine.TITLE, lastPOI.getName());
                    startActivity(iYoutube);

                }
            }
        });
    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            func = intent.getStringExtra(CommonDefine.FUNC);
            Log.d("func--->", func);
        }
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
                setUpMap();
                break;
            case R.id.rlBicycle:
                ClearMap();
                ClickModeBicycle("single");
                Mode = CommonDefine.MODE_BICYCLING;
                DriectionMap(TAG, Mode);
                setUpMap();
                break;
            case R.id.rlWalk:
                ClearMap();
                ClickModeWalk("single");
                Mode = CommonDefine.MODE_WALKING;
                DriectionMap(TAG, Mode);
                setUpMap();
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
                setupDefaultInforDes();
                latLngList = new ArrayList<>();
                tempPOI = new ArrayList<>();
                new DialogPOIOrigin(context, listPOI, tempPOI, latLngList, func).show();
                break;
            case R.id.rlCarMulti:
                ClickModeCar("multi");
                Mode = CommonDefine.MODE_DRIVING;
                int count = listPOIs.size();
                if (count == 0) {
                    Toast.makeText(context, "Bạn chưa chọn điểm đến", Toast.LENGTH_LONG).show();
                } else {
                    ClearMap();
                    for (int i = 1; i < count; i++) {
//                MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), CommonDefine.MODE_DRIVING);
                        MapsActivity.getLocationForMultiDirectionPOI(listPOIs.get(0), listPOIs.get(i), Mode);
                    }
                }

                break;
            case R.id.rlBicycleMulti:
                ClickModeBicycle("multi");
                Mode = CommonDefine.MODE_BICYCLING;
                int countMoto = listPOIs.size();
                if (countMoto == 0) {
                    Toast.makeText(context, "Bạn chưa chọn điểm đến", Toast.LENGTH_LONG).show();
                } else {
                    ClearMap();
                    for (int i = 1; i < countMoto; i++) {
//                MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), CommonDefine.MODE_DRIVING);
                        MapsActivity.getLocationForMultiDirectionPOI(listPOIs.get(0), listPOIs.get(i), Mode);
                    }
                }

                break;
            case R.id.rlWalkMulti:
                ClickModeWalk("multi");
                Mode = CommonDefine.MODE_WALKING;

                int countWalk = listPOIs.size();
                if (countWalk == 0) {
                    Toast.makeText(context, "Bạn chưa chọn điểm đến", Toast.LENGTH_LONG).show();
                } else {
                    ClearMap();
                    for (int i = 1; i < countWalk; i++) {
//                MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), CommonDefine.MODE_DRIVING);
                        MapsActivity.getLocationForMultiDirectionPOI(listPOIs.get(0), listPOIs.get(i), Mode);
                    }
                }

                break;
        }
    }

    private void setupDefaultInforDes() {
        txtTitleOri.setText("Điểm đi: ...");
        txtTitleDes.setText("Điểm đến: ...");
        txtDistanceDes.setText("... km");
        txtDurationDes.setText("... phút");
        listDetailDirection = new ArrayList<>();
        adapterDetailDirection = new AdapterDetailDirection(context, listDetailDirection);
        lvDetailTravel.setAdapter(adapterDetailDirection);
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
//            imBicycleMulti.setImageResource(R.drawable.bicycle_2);
            imBicycle.setImageResource(R.drawable.ic_motorcycle_white_24dp);
            imBicycle.setColorFilter(ContextCompat.getColor(context, R.color.background_map), android.graphics.PorterDuff.Mode.MULTIPLY);

            rlCar.setBackgroundColor(Color.parseColor("#4487f2"));
            imCar.setImageResource(R.drawable.car_big4);

            rlWalk.setBackgroundColor(Color.parseColor("#4487f2"));
            imWalk.setImageResource(R.drawable.walking_2);
        } else if (tag.equals("multi")) {
            rlBicycleMulti.setBackgroundResource(R.drawable.custom_oval_layout);
//            imBicycleMulti.setImageResource(R.drawable.bicycle_2);
            imBicycleMulti.setImageResource(R.drawable.ic_motorcycle_white_24dp);
            imBicycleMulti.setColorFilter(ContextCompat.getColor(context, R.color.background_map), android.graphics.PorterDuff.Mode.MULTIPLY);

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
//            imBicycle.setImageResource(R.drawable.bycycle_1);
            imBicycle.setImageResource(R.drawable.ic_motorcycle_white_24dp);
            imBicycle.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        } else if (tag.equals("multi")) {
            rlWalkMulti.setBackgroundResource(R.drawable.custom_oval_layout);
            imWalkMulti.setImageResource(R.drawable.walking_1);

            rlCarMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imCarMulti.setImageResource(R.drawable.car_big4);

            rlBicycleMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imBicycleMulti.setImageResource(R.drawable.ic_motorcycle_white_24dp);
            imBicycleMulti.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }


    }

    private void ClickModeCar(String tag) {
        if (tag.equals("single")) {
            rlWalk.setBackgroundColor(Color.parseColor("#4487f2"));
            imWalk.setImageResource(R.drawable.walking_2);

            rlCar.setBackgroundResource(R.drawable.custom_oval_layout);
            imCar.setImageResource(R.drawable.car_7);

            rlBicycle.setBackgroundColor(Color.parseColor("#4487f2"));
            imBicycle.setImageResource(R.drawable.ic_motorcycle_white_24dp);
            imBicycle.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        else if (tag.equals("multi")) {
            rlWalkMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imWalkMulti.setImageResource(R.drawable.walking_2);

            rlCarMulti.setBackgroundResource(R.drawable.custom_oval_layout);
            imCarMulti.setImageResource(R.drawable.car_7);

            rlBicycleMulti.setBackgroundColor(Color.parseColor("#4487f2"));
            imBicycleMulti.setImageResource(R.drawable.ic_motorcycle_white_24dp);
            imBicycleMulti.setColorFilter(ContextCompat.getColor(context, R.color.white), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

    }

    //endregion

    //region command constructor
    private void SetDefault() {
        setUpMap();
        Mode = CommonDefine.MODE_DRIVING;
        TAG = CommonDefine.CURRENT_LOCATION;

//        edFrom.setText("Vị trí của bạn");
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
//        if (mMap != null) {
//            if (func.equals(CommonDefine.ROUTING)) {
//                if (marker != null) {
//                    marker.remove();
//                }
//                ClearMap();
//                marker = mMap.addMarker(new MarkerOptions().position(latLng));
//                longClickLocation = latLng;
//                edTo.setText(String.valueOf(latLng.latitude).substring(0, 12) + "," + String.valueOf(latLng.longitude).substring(0, 12));
//                TAG = CommonDefine.CURRENT_LOCATION;
//                Mode = CommonDefine.MODE_DRIVING;
//                ClickModeCar("single");
//            }
//        }
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
        TIME_TO_UPDATE = Double.parseDouble(preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "10")) * 1000;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((long)TIME_TO_UPDATE);
        mLocationRequest.setFastestInterval((long)TIME_TO_UPDATE);
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
        currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

//        locationManager.requestLocationUpdates(provider, 1000, 0, this);

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }


        final LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());


        /*---------------check near object interest POI----------------------*/
        //get list POI

        //get setting mode travel
        preferences = getSharedPreferences(CommonDefine.SETTING, MODE_PRIVATE);
        Mode = preferences.getString(CommonDefine.MODE_TRAVEL, CommonDefine.MODE_DRIVING);

        //get radius access POI
        RADIUS_MAX = Double.parseDouble(preferences.getString(CommonDefine.RADIUS_ACCESS, "50"));
        RADIUS_AROUND_LBS = Double.parseDouble(preferences.getString(CommonDefine.RADIUS_AROUND_LBS, "5"));

//        getIntentData();
        if (func.equals("SMART")){
            //invisible cardview description
            cvInfoDes.setVisibility(View.GONE);

            final ProgressDialog alertDialog = ProgressDialog.show(context, "", "Đang tải dữ liệu...");

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
                                double minDistance = RADIUS_MAX;

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
                                        poi.setVideoDir(object.getString("Audio"));

                                        listPOI.add(poi);
                                    }
                                }
                                //show all POI markers
                                showAllPOIMarkers(listPOI);

                                //setup digital map
                                setUpMap();

                                for (POI poi : listPOI) {
                                    Location locDest = new Location("dest");
                                    locDest.setLatitude(poi.getLatitude());
                                    locDest.setLongitude(poi.getLongitude());
                                    float distance = location.distanceTo(locDest);

                                    if (distance < minDistance) {
                                        minDistance = distance;
                                        minPOI = poi;
                                    }
                                }


                                if (minDistance <= RADIUS_MAX) { //check distance to POI to RADIUS_MAX
                                    if (minPOI != null) {
                                        //alert not found poi ringstone
                                        alarmRingStone(R.raw.raw_find_out);

                                        if (minDistance > 50) //cach POI 50m
                                            Toast.makeText(MapsActivity.this, "Bạn đang cách " + minPOI.getName() + " " + (int) minDistance + " m", Toast.LENGTH_SHORT).show();
                                        else
                                            Toast.makeText(MapsActivity.this, "Bạn đang ở gần " + minPOI.getName(), Toast.LENGTH_SHORT).show();
                                        LatLng latLng = new LatLng(minPOI.getLatitude(), minPOI.getLongitude());
                                        MarkerOptions markerOptions1 = new MarkerOptions();
                                        markerOptions1.position(latLng);
                                        markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                                        markerOptions1.title(minPOI.getName());
                                        markerOptions1.visible(true);

                                        CustomInfoWindowMapAdapter customInfoWindow = new CustomInfoWindowMapAdapter(context);
                                        mMap.setInfoWindowAdapter(customInfoWindow);

                                        //move map camera
                                        Marker markerPOI = mMap.addMarker(markerOptions1);
                                        markerPOI.setTag(minPOI);
                                        markerPOI.showInfoWindow();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                                        if (!lastPOI.equals(minPOI)) {
                                            txtTitlePlay.setText(minPOI.getName());

                                            AudioURL = minPOI.getAudio();

                                            cvPlay.setVisibility(View.VISIBLE);

                                            //routing to POI
                                            DirectionGoogleMapFor2Point(myLatLng, latLng, Mode);

                                            //update last POI
                                            lastPOI = minPOI;

                                        } else {
                                            try {
                                                //alert not found poi ringstone
                                                alarmRingStone(R.raw.raw_not_found);

                                                cvPlay.setVisibility(View.GONE);

                                                Toast.makeText(MapsActivity.this, "Không có địa điểm nào trong bán kính " + (int) RADIUS_MAX + " m", Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        try {
                                            //alert not found poi ringstone
                                            alarmRingStone(R.raw.raw_not_found);

                                            cvPlay.setVisibility(View.GONE);

                                            Toast.makeText(MapsActivity.this, "Không có địa điểm nào trong bán kính " + (int) RADIUS_MAX + "m", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }


                                //dimiss aleart dialog downloading
                                alertDialog.dismiss();

                            } catch(JSONException e){

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

        else { //Either function on Maps

            //Showing Current Location Marker on Map
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(myLatLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);

            //move map camera
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            //show marker of LBS
            if (func.equals(CommonDefine.LBS)){
                Intent intent = getIntent();
                String nameLBS = intent.getStringExtra("name");
                double lat = intent.getDoubleExtra("lat", 21.0f);
                double lon = intent.getDoubleExtra("lon", 105.0f);
                drawMarkerLBS(lat, lon, nameLBS);
            } else if (func.equals(CommonDefine.AROUND_HERE)){ //show all around lbs
                Intent intent = getIntent();
                Bundle bundle = intent.getBundleExtra(CommonDefine.LBS_BUNDLE);
                ArrayList<LBS> listLBS = bundle.getParcelableArrayList(CommonDefine.LIST_LBS);

                for (LBS lbs : listLBS){
                    Location locDest = new Location("dest");
                    locDest.setLatitude(lbs.getLat());
                    locDest.setLongitude(lbs.getLon());
                    float distance = location.distanceTo(locDest);

                    if (distance <= RADIUS_AROUND_LBS * 1000) {
                        drawMarkerLBS(lbs.getLat(), lbs.getLon(), lbs.getName());
                    }
                }
            }

            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    return true;
                }
            });

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    POI poi = (POI) marker.getTag();
                    if (poi != null) {
                        String VideoDir = poi.getVideoDir();
                        int locationID = poi.getId();
//                        Intent iYoutube = new Intent(context, YoutubeActivity.class);
//                        iYoutube.putExtra("VIDEO_ID", VideoDir);
//                        iYoutube.putExtra("locationID", locationID);
//                        iYoutube.putExtra("TAG", CommonDefine.ROUTING);
//                        iYoutube.putExtra("TAG", CommonDefine.ROUTING);
//                        iYoutube.putExtra(CommonDefine.TITLE, poi.getName());
//                        context.startActivity(iYoutube);

                        Intent iDetail = new Intent(context, NoneServiceActivity.class);
                        iDetail.putExtra("PlaceID", locationID);
                        iDetail.putExtra("TAG", "HIS");
                        iDetail.putExtra(CommonDefine.FUNC, CommonDefine.ROUTING);

                        context.startActivity(iDetail);
                        finish();
                    }
                }
            });

        }


    }

    //notifycation ringstone
    private void alarmRingStone(int raw){
        MediaPlayer mMediaPlayer = MediaPlayer.create(context, raw);
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
        CustomInfoWindowMapAdapter inforWindow = new CustomInfoWindowMapAdapter(context);
        mMap.setInfoWindowAdapter(inforWindow);
        Marker marker = null;
        for (POI poi: listPOIs){
            LatLng latLng = new LatLng(poi.getLatitude(), poi.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markerOptions.title(poi.getName());

            marker = mMap.addMarker(markerOptions);
            marker.setTag(poi);
            marker.showInfoWindow();

        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                POI poi = (POI) marker.getTag();
                String VideoDir = poi.getVideoDir();
                int locationID = poi.getId();

                Intent iDetail = new Intent(context, NoneServiceActivity.class);
                iDetail.putExtra("PlaceID", locationID);
                iDetail.putExtra("TAG", "HIS");
                iDetail.putExtra(CommonDefine.FUNC, CommonDefine.SMART);

//                Intent iYoutube = new Intent(context, YoutubeActivity.class);
//                iYoutube.putExtra(CommonDefine.VIDEO_DIR, VideoDir);
//                iYoutube.putExtra("locationID", locationID);
//                iYoutube.putExtra("TAG", CommonDefine.SMART);
//                iYoutube.putExtra(CommonDefine.TITLE, poi.getName());
                startActivity(iDetail);
                finish();
            }
        });
    }

    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public void requestLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            boolean isShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if (isShowRationale) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSIONS_REQUEST_LOCATION);

//                onRestart();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
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

                        onResume();

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

        txtTitleOri.setText("Điểm đi: " + addressFrom);
        txtTitleDes.setText("Điểm đến: " + addressTo);

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
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.tan_trao_map, getApplicationContext());
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
        Geocoder coder = new Geocoder(this, Locale.getDefault());
        List<Address> addressFrom;
        List<Address> addressTo;
        try {
            addressFrom = coder.getFromLocationName(strAddressFrom, 1);
            addressTo = coder.getFromLocationName(strAddressTo, 1);
            try {
//                if (addressFrom.size() == 0) {
//                    Toast.makeText(context, "Địa điểm đi bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
//                } else if (addressTo.size() == 0) {
//                    Toast.makeText(context, "Địa điểm đến bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
//                } else {
//                    locationFrom = addressFrom.get(0);
//                    locationTo = addressTo.get(0);
                    locationFrom.setLatitude(latLngOrigin.latitude);
                    locationFrom.setLongitude(latLngOrigin.longitude);
                    locationTo.setLatitude(latLngDestination.latitude);
                    locationTo.setLongitude(latLngDestination.longitude);
                    LatLng from = new LatLng(locationFrom.getLatitude(), locationFrom.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(from).icon(bitmapDescriptorFrom).title(strAddressFrom)).showInfoWindow();

                    LatLng to = new LatLng(locationTo.getLatitude(), locationTo.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(to).icon(bitmapDescriptorTo).title(strAddressTo)).showInfoWindow();

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(from);
                    builder.include(to);
                    LatLngBounds bound = builder.build();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 25), 1000, null);

//                    DirectionGoogleMapFor2Point(from, to, googleMode);
                    DirectionGoogleMap2Point(from, to, googleMode);
                    RoutingInMap(locationFrom, locationTo);
//                }
            } catch (Exception e) {
                Log.d("error", e.toString());
            }


        } catch (IOException e) {
            // Toast.makeText(context, "Địa chỉ bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
        }
    }

    private void DirectionGoogleMap2Point(LatLng from, LatLng to, String googleMode) {
        String url = getDirectionUrl(from, to, googleMode);

        DownloadTaskExtend downloadTask = new DownloadTaskExtend();

        // Start downloading json data from Google Directions API
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
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

            CustomInfoWindowMapAdapter infoWindowMapAdapter = new CustomInfoWindowMapAdapter(context);
            mMap.setInfoWindowAdapter(infoWindowMapAdapter);

            Marker markerFrom = mMap.addMarker(new MarkerOptions()
                                                .position(fromLat)
                                                .icon(bitmapDescriptorFrom)
                                                .title(from.getName()));
            markerFrom.setTag(from);
            markerFrom.showInfoWindow();

            Marker markerTo = mMap.addMarker(new MarkerOptions()
                                                .position(toLon)
                                                .icon(bitmapDescriptorTo)
                                                .title(to.getName()));
            markerTo.setTag(to);
            markerTo.showInfoWindow();

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
        String url = GOOGLEMAP_DIRECTION + output + "?" + parameters + "&key=AIzaSyBcR6y_giPahewOFi2Hv78KpaBPLIZEut0&language=vi";

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

    @Override
    public void onMapClick(LatLng latLng) {
        Toast.makeText(context, "Click map", Toast.LENGTH_SHORT).show();

        isShowCardViewDes = !isShowCardViewDes;
        if (isShowCardViewDes){
            cvInfoDes.setVisibility(View.VISIBLE);
            lnInfoDes.setVisibility(View.VISIBLE);
        } else {
            cvInfoDes.setVisibility(View.INVISIBLE);
            lnInfoDes.setVisibility(View.GONE);
        }
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


    private static class DownloadTaskExtend extends AsyncTask<String, Void, String> {

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

            ParserTaskExtend parserTask = new ParserTaskExtend();

            parserTask.executeOnExecutor(THREAD_POOL_EXECUTOR, result);

        }
    }


    //class parser data from google map service to direction
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

                List<HashMap<String, String>> path = result.get(0);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }


                lineOptions.addAll(points);
                lineOptions.width(15);
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


    //class parser data from google map service to direction
    private static class ParserTaskExtend extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

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

                JSONArray jRoutes = jObject.getJSONArray("routes");
                /** Traversing all routes */
                JSONArray jLegs = ((JSONObject) jRoutes.get(0)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                JSONObject obj = jLegs.getJSONObject(0);
                String distance = obj.getJSONObject("distance").getString("text");
                String duration = obj.getJSONObject("duration").getString("text");

                List<HashMap<String, String>> temp1 = new ArrayList<HashMap<String, String>>();
                HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("distance", distance);
                hashMap1.put("duration", duration);
                temp1.add(hashMap1);
                routes.add(temp1);


                JSONArray jSteps = ((JSONObject) jLegs.get(0)).getJSONArray("steps");
                List<HashMap<String, String>> temp2 = new ArrayList<HashMap<String, String>>();
                int lengthStep = jSteps.length();
                for (int i = 0; i < lengthStep; i++){
                    HashMap<String, String> hashMap2 = new HashMap<>();
                    JSONObject itemStep = jSteps.getJSONObject(i);
                    String distanceStep = itemStep.getJSONObject("distance").getString("text");
                    String html_instructions = itemStep.getString("html_instructions");
                    hashMap2.put("html_instructions", html_instructions);
                    hashMap2.put("distance", distanceStep);
                    temp2.add(hashMap2);
                    routes.add(temp2);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = new ArrayList<LatLng>();
            PolylineOptions lineOptions = new PolylineOptions();

            List<HashMap<String, String>> path = result.get(0);

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

            //get information of direction
            String distance = result.get(1).get(0).get("distance");
            String duration = result.get(1).get(0).get("duration");

            //show direction
            txtDistanceDes.setText(distance);
            txtDurationDes.setText(duration);

            //show detail direction on listview
            listDetailDirection = new ArrayList<>();
            List<HashMap<String, String>> steps = result.get(2);
            int lengthStep = steps.size();
            for (int j = 0 ; j < lengthStep ; j++){
                HashMap<String, String> itemStep = steps.get(j);
                String distanceStep = itemStep.get("distance");
                String html_instructions = itemStep.get("html_instructions");
                DetailDirection detailDirection = new DetailDirection(html_instructions, distanceStep);
                listDetailDirection.add(detailDirection);
            }
            adapterDetailDirection = new AdapterDetailDirection(context, listDetailDirection);
            lvDetailTravel.setAdapter(adapterDetailDirection);

            isShowCardViewDes = true;
            lnInfoDes.setVisibility(View.VISIBLE);
            CardView.LayoutParams lpCardView = (CardView.LayoutParams) cvInfoDes.getLayoutParams();
            lpCardView.width = CardView.LayoutParams.MATCH_PARENT;
            lpCardView.height = CardView.LayoutParams.WRAP_CONTENT;

            LinearLayout.LayoutParams lpLinear = (LinearLayout.LayoutParams) lnInfoDes.getLayoutParams();
            lpLinear.width = LinearLayout.LayoutParams.MATCH_PARENT;
            lpLinear.height = LinearLayout.LayoutParams.MATCH_PARENT;

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

            SetDefault();
            mMap.setOnMapLongClickListener(this);
        }

//        if (func.equals(CommonDefine.LBS)){
//            Intent intent = getIntent();
//            String nameLBS = intent.getStringExtra("name");
//            double lat = intent.getDoubleExtra("lat", 21.0f);
//            double lon = intent.getDoubleExtra("lon", 105.0f);
//        }

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
        if (preferences != null)
            TIME_TO_UPDATE = Double.parseDouble(preferences.getString(CommonDefine.TIME_UPDATE_LOCATION, "10")) * 1000;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval((long)TIME_TO_UPDATE);
        mLocationRequest.setFastestInterval((long)TIME_TO_UPDATE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (checkPermissions()) {

            if (mGoogleApiClient != null && mGoogleApiClient.isConnected())
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null)
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,  this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent  = new Intent(context, HomeScreenActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
        builder.setMessage("Bạn chưa mở chế độ định vị GPS. Bạn có muốn mở cài đặt GPS?")
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_FROM) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                edFrom.setText(place.getName());
                latLngOrigin = place.getLatLng();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_TO) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                edTo.setText(place.getName());
                latLngDestination = place.getLatLng();
                edTo.requestFocus();
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }




    public void drawMarkerLBS(double lat, double lon, String name){
        LatLng loc = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(loc).title(name)).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
    }
}
