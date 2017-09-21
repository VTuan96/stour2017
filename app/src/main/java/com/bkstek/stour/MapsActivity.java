package com.bkstek.stour;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bkstek.stour.mapdigital.TileProviderFactory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.bkstek.stour.util.CommonDefine.GEOSERVER_FORMAT;
import static com.bkstek.stour.util.CommonDefine.WMS_FORMAT_ROUTE_STRING;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText edFrom, edTo;
    Context context;
    Button btnSearch;
    BitmapDescriptor bitmapDescriptorTo;
    BitmapDescriptor bitmapDescriptorFrom;
    Address locationFrom = null;
    Address locationTo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        edFrom = (EditText) findViewById(R.id.edFrom);
        edTo = (EditText) findViewById(R.id.edTo);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        context = MapsActivity.this;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //addMapEvents();

        bitmapDescriptorFrom
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_RED);

        bitmapDescriptorTo
                = BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClearMap();
                setUpMap();
                getAddress();
            }
        });


    }

    private void ClearMap() {
        if (mMap != null)
            mMap.clear();
    }

    private void getAddress() {
        String addressFrom = edFrom.getText().toString();
        String addressTo = edTo.getText().toString();
        if (addressFrom.equals("")) {
            Toast.makeText(context, "Bạn chưa nhập địa chỉ điểm đi", Toast.LENGTH_LONG).show();
        } else if (addressTo.equals("")) {
            Toast.makeText(context, "Bạn chưa nhập địa chỉ điểm đến", Toast.LENGTH_LONG).show();
        } else {
            getLocationFromAddress(addressFrom, addressTo);
            RoutingInMap(locationFrom, locationTo);
        }
    }

    private void setUpMap() {
        TileProvider tileProvider = TileProviderFactory.getTileProvider(GEOSERVER_FORMAT);
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
    }


    // dinh tuyen tren ban do so hoa thong qua kinh do vi do diem dau va diem cuoi
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


    public void getLocationFromAddress(String strAddressFrom, String strAddressTo) {

        Geocoder coder = new Geocoder(this, Locale.US);
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

            locationFrom.getLatitude();
            locationFrom.getLongitude();


            LatLng from = new LatLng(locationFrom.getLatitude(), locationFrom.getLongitude());

            mMap.addMarker(new MarkerOptions().position(from).icon(bitmapDescriptorFrom).title(strAddressFrom)).showInfoWindow();

            LatLng to = new LatLng(locationTo.getLatitude(), locationTo.getLongitude());

            mMap.addMarker(new MarkerOptions().position(to).icon(bitmapDescriptorTo).title(strAddressTo)).showInfoWindow();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((locationFrom.getLatitude() + locationTo.getLatitude()) / 2, (locationFrom.getLongitude() + locationTo.getLongitude()) / 2), 16.0f));

        } catch (IOException e) {
            // Toast.makeText(context, "Địa chỉ bạn nhập không tồn tại. Vui lòng nhập lại", Toast.LENGTH_LONG).show();
        }
    }

    private void addMapEvents() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                Toast.makeText(getApplicationContext(), position.latitude + " : " + position.longitude, Toast.LENGTH_SHORT).show();
            }
        });
    }


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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(21.004508, 105.844160);
//        mMap.setMinZoomPreference(18);
//        mMap.setMaxZoomPreference(21);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Thư viện Tạ Quang Bửu"));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(21.004508, 105.844160), 16.0f));

        if (mMap != null) {
            setUpMap();
        }
    }


}
