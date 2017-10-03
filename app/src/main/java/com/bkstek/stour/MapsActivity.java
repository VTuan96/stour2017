package com.bkstek.stour;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bkstek.stour.component.DialogPOIOrigin;
import com.bkstek.stour.mapdigital.TileProviderFactory;
import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.DirectionsJSONParser;
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

import static com.bkstek.stour.util.CommonDefine.GEOSERVER_FORMAT;
import static com.bkstek.stour.util.CommonDefine.GOOGLEMAP_DIRECTION;
import static com.bkstek.stour.util.CommonDefine.WMS_FORMAT_ROUTE_STRING;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapLongClickListener, View.OnClickListener {

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

    Context context;
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
    LatLng longClickLocation; //location of point long click map
    Marker marker; //marker use for long click map

    private String TAG = ""; // set tag for driection mode

    private String Mode = "";

    //list latlong for multi direction
    List<LatLng> latLngList = new ArrayList<>();

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

        context = MapsActivity.this;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                ClickModeCar("single");
                Mode = CommonDefine.MODE_DRIVING;
                DriectionMap(TAG, Mode);
                break;
            case R.id.rlBicycle:
                ClickModeBicycle("single");
                Mode = CommonDefine.MODE_BICYCLING;
                DriectionMap(TAG, Mode);
                break;
            case R.id.rlWalk:
                ClickModeWalk("single");
                Mode = CommonDefine.MODE_WALKING;
                DriectionMap(TAG, Mode);
                break;
            case R.id.rlSingle:
                lnDrivingMulti.setVisibility(View.GONE);
                lnDriving.setVisibility(View.VISIBLE);
                ClickSingleLayout();
                ClearMap();
                setUpMap();
                break;
            case R.id.rlMulti:
                lnDrivingMulti.setVisibility(View.VISIBLE);
                lnDriving.setVisibility(View.GONE);
                ClickMultiLayout();
                ClearMap();
                setUpMap();
                new DialogPOIOrigin(context, latLngList).show();
                break;
            case R.id.rlCarMulti:
                ClickModeCar("multi");
                Mode = CommonDefine.MODE_DRIVING;
                ClearMap();
                for (int i = 1; i < latLngList.size(); i++) {
                    MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), Mode);
                }
                break;
            case R.id.rlBicycleMulti:
                ClickModeBicycle("multi");
                Mode = CommonDefine.MODE_BICYCLING;
                ClearMap();
                for (int i = 1; i < latLngList.size(); i++) {
                    MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), Mode);
                }
                break;
            case R.id.rlWalkMulti:
                ClickModeWalk("multi");
                Mode = CommonDefine.MODE_WALKING;
                ClearMap();
                for (int i = 1; i < latLngList.size(); i++) {
                    MapsActivity.getLocationForMultiDirection(latLngList.get(0), latLngList.get(i), Mode);
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

        lnMultiRouting.setVisibility(View.VISIBLE);
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
        mLocationRequest = new LocationRequest();

        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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


        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        //Showing Current Location Marker on Map
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);

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
        Location locations = locationManager.getLastKnownLocation(provider);
        List<String> providerList = locationManager.getAllProviders();
        if (null != locations && null != providerList && providerList.size() > 0) {
            double longitude = locations.getLongitude();
            double latitude = locations.getLatitude();
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (null != listAddresses && listAddresses.size() > 0) {

//                    Here we are finding , whatever we want our marker to show when clicked
                    String state = listAddresses.get(0).getAdminArea();
                    String country = listAddresses.get(0).getCountryName();
                    String subLocality = listAddresses.get(0).getSubLocality();
                    markerOptions.title("" + latLng + "," + subLocality + "," + state + "," + country);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
//        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        //this code stops location updates
        if (mGoogleApiClient != null) {
            mLastLocation = location;
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
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
            RoutingInMap(locationFrom, locationTo);
        }
    }
    //endregion

    //region set up digital map

    private void setUpMap() {
        TileProvider tileProvider = TileProviderFactory.getTileProvider(GEOSERVER_FORMAT);
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
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
            addressFrom = coder.getFromLocationName(strAddressFrom, 5);
            addressTo = coder.getFromLocationName(strAddressTo, 5);
            try {
                locationFrom = addressFrom.get(0);
                locationTo = addressTo.get(0);
            } catch (Exception e) {
                Toast.makeText(context, "Địa chỉ bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
            }


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
    //endregion

    private void DirectionGoogleMap(LatLng from, LatLng to, String mode) {
        // Getting URL to the Google Directions API
        ClearMap();
//        setUpMap();
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
        String url = GOOGLEMAP_DIRECTION + output + "?" + parameters;

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
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {

            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

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


}
