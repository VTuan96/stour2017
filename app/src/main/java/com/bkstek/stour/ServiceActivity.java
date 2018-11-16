package com.bkstek.stour;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterSlider;
import com.bkstek.stour.component.DialogInfo;
import com.bkstek.stour.fragment.FragmentSlider;
import com.bkstek.stour.model.Banner;
import com.bkstek.stour.model.Place;
import com.bkstek.stour.util.FunctionHelper;
import com.bkstek.stour.util.PicassoImageGetter;
import com.bkstek.stour.util.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_FOOD_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_HOTEL_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_PLACE_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_RESTAURANT_DETAIL;

public class ServiceActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, OnMapReadyCallback {

    ImageView imBack, imPhone;
    TextView txtPlaceName, txtReview, txtdistince, txtDes, txtAddress, txtPhone;
    RatingBar rbStarRate;
    String url;
    int locationID;
    String TAG;
    Toolbar toolbar;
    ViewPager vpSlides;

    //my edit code here
    WebView wvDes;

    ProgressBar pgOne, pgTwo, pgThree, pgFour, pgFive;
    RelativeLayout youtube;
    TextView txtLink;

    Context context;
    List<Banner> bannerList = new ArrayList<>();

    List<Fragment> fragments;
    AdapterSlider adapterSlider;

    String video_id = "uYOMT3VYxvA";

    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;
    private GoogleMap mMap;

    private static final String mimeType = "text/html";
    private static final String encoding = "UTF-8";

    private SliderLayout sliderLayout;
    private int REQUEST_PHONE_CALL = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_service);

        imBack = (ImageView) findViewById(R.id.imBack);
        imPhone = (ImageView) findViewById(R.id.imPhone);
        // imPlace = (ImageView) findViewById(R.id.imPlace);
        txtPlaceName = (TextView) findViewById(R.id.txtPlaceName);
        txtReview = (TextView) findViewById(R.id.txtReview);
        txtdistince = (TextView) findViewById(R.id.txtdistince);
        txtPhone = (TextView) findViewById(R.id.txtPhone);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rbStarRate = (RatingBar) findViewById(R.id.rbStarRate);

        /*
            Change textView to webView to show description html content
         */
        txtDes = (TextView) findViewById(R.id.txtDes);
//        wvDes = findViewById(R.id.wvDes);

        txtAddress = (TextView) findViewById(R.id.txtAddress);
//        vpSlides = (ViewPager) findViewById(R.id.vpSlides);

        pgOne = (ProgressBar) findViewById(R.id.pgOne);
        pgTwo = (ProgressBar) findViewById(R.id.pgTwo);
        pgThree = (ProgressBar) findViewById(R.id.pgThree);
        pgFour = (ProgressBar) findViewById(R.id.pgFour);
        pgFive = (ProgressBar) findViewById(R.id.pgFive);

        youtube = (RelativeLayout) findViewById(R.id.youtube);
        txtLink = (TextView) findViewById(R.id.txtLink);

        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(4); //set scroll delay in seconds :

        pgOne.setIndeterminate(false);
        pgOne.setMax(100);
        pgOne.setProgress(10);

        pgTwo.setIndeterminate(false);
        pgTwo.setMax(100);
        pgTwo.setProgress(15);

        pgThree.setIndeterminate(false);
        pgThree.setMax(100);
        pgThree.setProgress(60);

        pgFour.setIndeterminate(false);
        pgFour.setMax(100);
        pgFour.setProgress(70);

        pgFive.setIndeterminate(false);
        pgFive.setMax(100);
        pgFive.setProgress(60);

        context = ServiceActivity.this;

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        locationID = intent.getIntExtra("PlaceID", 0);
        TAG = intent.getStringExtra("TAG");

        boolean checkNetWork = FunctionHelper.isNetworkConnected(context);
        if (checkNetWork) {
            GetData(locationID);
        } else {
            new DialogInfo(context, "Không có kết nối internet!!!").show();
        }

//        GetData(locationID);

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent iYoutube = new Intent(context, YoutubeActivity.class);
//                iYoutube.putExtra("VIDEO_ID", video_id);
//                iYoutube.putExtra("locationID", locationID);
//                iYoutube.putExtra("TAG", TAG);
//                startActivity(iYoutube);

                new AlertDialog.Builder(context)
                        .setTitle("Thông báo")
                        .setMessage("Chưa có video về địa điểm này!")
                        .setCancelable(false)
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Back();
            }
        });

        //Show call screen
        imPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Phone : " + txtPhone.getText(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+" + txtPhone.getText()));
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity)context, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                }
                else
                {
                    startActivity(intent);
                }
            }
        });

        rbStarRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Intent notifyIntent = new Intent(context, NotifyActivity.class);
                startActivity(notifyIntent);
            }
        });


        //setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void Back() {
        if (TAG.equals("HOME") || TAG.equals("HOTEL") || TAG.equals("RES")) {
            Intent iBack = new Intent(context, HomeScreenActivity.class);
            startActivity(iBack);
        } else if (TAG.equals("HIS")) {
            Intent iBack = new Intent(context, HistoryActivity.class);
            startActivity(iBack);
        } else if (TAG.equals("CUL")) {
            Intent iBack = new Intent(context, CultureActivity.class);
            startActivity(iBack);
        } else if (TAG.equals("FOOD")) {
            Intent iBack = new Intent(context, FoodActivity.class);
            startActivity(iBack);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Back();
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

    private Place place = new Place();

    private void GetData(int locationId) {
        final ProgressDialog alertDialog = ProgressDialog.show(context, "", "Vui lòng đợi...");

        if (TAG.equals("HIS") || TAG.equals("CUL") || TAG.equals("HOME")) {
            url = GET_PLACE_DETAIL + "?locationId=" + locationId;
        } else if (TAG.equals("FOOD")) {
            url = GET_FOOD_DETAIL + "?foodId=" + locationId;
        } else if (TAG.equals("HOTEL")) {
            url = GET_HOTEL_DETAIL + "?hotelId=" + locationId;
        } else if (TAG.equals("RES")) {
            url = GET_RESTAURANT_DETAIL + "?restaurantId=" + locationId;
        }

        Log.d("url detail", url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject object = response.getJSONObject("Data");
                            place.setId(object.getInt("Id"));
                            place.setAvatar(object.getString("Avatar"));
                            place.setName(object.getString("Name"));
                            place.setStar(object.getInt("Star"));
                            place.setViewCount(object.getInt("ViewCount"));
                            place.setLongDes(object.getString("LongDes"));
                            place.setLatitude(object.getDouble("Latitude"));
                            place.setLongitude(object.getDouble("Longitude"));
                            place.setAddress(object.getString("Address"));
                            place.setPhoneNumber(object.getString("HotLine"));

                            JSONArray array = object.getJSONArray("FileAttachs");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                Banner banner = new Banner();
                                banner.setUrlBanner(jsonObject.getString("FileUrl"));
                                bannerList.add(banner);

                                SliderView sliderView = new SliderView(context);
                                sliderView.setImageUrl(jsonObject.getString("FileUrl"));
                                sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);

                                //at last add this view in your layout :
                                sliderLayout.addSliderView(sliderView);

                            }

                            SetBanner(bannerList);

                            txtPlaceName.setText(place.getName());
                            rbStarRate.setNumStars(place.getStar());

                            String vcount = String.valueOf(place.getViewCount()) + " Reviews";
                            txtAddress.setText(place.getAddress());
                            txtReview.setText(vcount);
                            txtPhone.setText(place.getPhoneNumber());

                            PicassoImageGetter imageGetter = new PicassoImageGetter(txtDes, getApplicationContext());
                            Spannable html;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                html = (Spannable) Html.fromHtml(place.getLongDes(), Html.FROM_HTML_MODE_LEGACY, imageGetter, null);
                            } else {
                                html = (Spannable) Html.fromHtml(place.getLongDes(), imageGetter, null);
                            }
                            txtDes.setText(html);

                            //set location of place
                            LatLng loc = new LatLng(place.getLatitude(), place.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(loc).title(place.getName())).showInfoWindow();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));

                            alertDialog.dismiss();

                        } catch (JSONException e) {
//                            System.out.println("Error: " + e.toString());
                            new DialogInfo(context, "Không thể lấy dữ liệu từ Server!!!").show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new DialogInfo(context, "Lỗi kết nối đến Server!!!").show();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void SetBanner(List<Banner> linkImage) {
        fragments = new ArrayList<>();

        for (int i = 0; i < linkImage.size(); i++) {
            FragmentSlider fragmentSlider = new FragmentSlider();
            Bundle bundle = new Bundle();
            bundle.putString("linkImage", linkImage.get(i).getUrlBanner());
            fragmentSlider.setArguments(bundle);
            fragments.add(fragmentSlider);
        }


//        adapterSlider = new AdapterSlider(getSupportFragmentManager(), fragments);
//        vpSlides.setAdapter(adapterSlider);
//        adapterSlider.notifyDataSetChanged();
//
//        vpSlides.addOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
