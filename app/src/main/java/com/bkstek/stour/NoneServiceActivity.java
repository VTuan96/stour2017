package com.bkstek.stour;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterSlider;
import com.bkstek.stour.component.DialogInfo;
import com.bkstek.stour.fragment.FragmentSlider;
import com.bkstek.stour.helper.CustomDialog;
import com.bkstek.stour.model.Banner;
import com.bkstek.stour.model.Place;
import com.bkstek.stour.util.CommonDefine;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_CULTURE_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_CULTURE_PLACE;
import static com.bkstek.stour.util.CommonDefine.GET_FOOD_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_HOTEL_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_PLACE_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_RESTAURANT_DETAIL;

public class NoneServiceActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, OnMapReadyCallback {

    ImageView imBack, imPhone;
    TextView txtPlaceName, txtReview, txtdistince, txtDes, txtAddress, txtPhone, txtOpen;

    LinearLayout lnOpen, lnAudio;
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

    //    String video_id = "uYOMT3VYxvA";
    String video_id = "https://www.youtube.com/watch?v=kok0wm_W3es&feature=youtu.be";

    private static final int REQUEST_CALL_PHONE_PERMISSION = 1;
    private GoogleMap mMap;

    private static final String mimeType = "text/html";
    private static final String encoding = "UTF-8";
    private SliderLayout sliderLayout;

    private String VideoDir = "";
    private String title = "";

    ImageButton btnAudio;
    private boolean isPlayAudio = false;
    MediaPlayer playerAudio;
    String AudioUrl = "";
    private int REQUEST_PHONE_CALL = 124; //request call phone of none service
    String FUNC = CommonDefine.ROUTING;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_none_service);

        imBack = (ImageView) findViewById(R.id.imBackNoneService);
        imPhone = (ImageView) findViewById(R.id.imPhoneNoneService);
        // imPlace = (ImageView) findViewById(R.id.imPlace);
        txtPlaceName = (TextView) findViewById(R.id.txtPlaceNameNoneService);
//        txtReview = (TextView) findViewById(R.id.txtReviewNoneService);
//        txtdistince = (TextView) findViewById(R.id.txtdistince);
        txtPhone = (TextView) findViewById(R.id.txtPhoneNoneService);
        txtOpen = findViewById(R.id.txtOpenNoneService);
        lnOpen = findViewById(R.id.lnOpenNoneService);
        lnAudio = findViewById(R.id.lnAudioNoneService);
        btnAudio = findViewById(R.id.btnAudio);

        toolbar = (Toolbar) findViewById(R.id.toolBarNoneService);
        rbStarRate = (RatingBar) findViewById(R.id.rbStarRateNoneService);

        /*
            Change textView to webView to show description html content
         */
        txtDes = (TextView) findViewById(R.id.txtDesNoneService);
//        wvDes = findViewById(R.id.wvDes);

        txtAddress = (TextView) findViewById(R.id.txtAddressNoneService);
//        vpSlides = (ViewPager) findViewById(R.id.vpSlides);

        pgOne = (ProgressBar) findViewById(R.id.pgOneNoneService);
        pgTwo = (ProgressBar) findViewById(R.id.pgTwoNoneService);
        pgThree = (ProgressBar) findViewById(R.id.pgThreeNoneService);
        pgFour = (ProgressBar) findViewById(R.id.pgFourNoneService);
        pgFive = (ProgressBar) findViewById(R.id.pgFiveNoneService);

        youtube = (RelativeLayout) findViewById(R.id.youtubeNoneService);
        txtLink = (TextView) findViewById(R.id.txtLinkNoneService);

        sliderLayout = findViewById(R.id.imageSliderNoneService);
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

        context = NoneServiceActivity.this;

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        locationID = intent.getIntExtra("PlaceID", 0);
        TAG = intent.getStringExtra("TAG");
        FUNC = intent.getStringExtra(CommonDefine.FUNC);

        VideoDir = intent.getStringExtra(CommonDefine.VIDEO_DIR);

        boolean checkNetWork = FunctionHelper.isNetworkConnected(context);
        if (checkNetWork) {
            GetData(locationID);
        } else {
            new DialogInfo(context, "Không có kết nối internet!!!").show();
        }

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iYoutube = new Intent(context, YoutubeActivity.class);
                iYoutube.putExtra("VIDEO_ID", VideoDir);
                iYoutube.putExtra(CommonDefine.TITLE, title);
                iYoutube.putExtra("locationID", locationID);
                if (TAG.equals("HOME"))
                    iYoutube.putExtra("TAG", "HIS");
                else
                    iYoutube.putExtra("TAG", TAG);
                startActivity(iYoutube);
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
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "+" + txtPhone.getText()));
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_PHONE_CALL);
                } else {
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

        btnAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AudioUrl.equals(null) || AudioUrl.equalsIgnoreCase("null")) {
                    CustomDialog customDialog = new CustomDialog(context);
                    customDialog.showAlertDialog("Thông báo", "Không có Audio liên quan!");
                } else {
                    isPlayAudio = !isPlayAudio;
                    if (isPlayAudio) {
//                        Drawable icon = getResources().getDrawable(R.drawable.mr_media_pause_light);
//                        btnAudio.setImageDrawable(icon);
                        btnAudio.setBackgroundResource(R.drawable.ic_pause_circle_outline_black_24dp);
                        try {
                            playerAudio = new MediaPlayer();
                            playerAudio.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            playerAudio.setDataSource(CommonDefine.DOMAIN_STOUR + AudioUrl);
                            playerAudio.prepare();
                            playerAudio.start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        btnAudio.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                        playerAudio.stop();
                    }
                }
            }
        });


        //setup map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapNoneService);
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
            FUNC = getIntent().getStringExtra(CommonDefine.FUNC);
            if (FUNC.equals(CommonDefine.SMART)) {
                iBack = new Intent(context, MapsActivity.class);
                iBack.putExtra(CommonDefine.FUNC, CommonDefine.SMART);
            } else if (FUNC.equals(CommonDefine.ROUTING)) {
                iBack = new Intent(context, MapsActivity.class);
                iBack.putExtra(CommonDefine.FUNC, CommonDefine.ROUTING);
            }

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

        if (TAG.equals("HIS") || TAG.equals("HOME")) {
            url = GET_PLACE_DETAIL + "?locationId=" + locationId;
            lnOpen.setVisibility(View.VISIBLE);
            lnAudio.setVisibility(View.VISIBLE);
        } else if (TAG.equals("FOOD")) {
            url = GET_FOOD_DETAIL + "?foodId=" + locationId;
            lnOpen.setVisibility(View.GONE);
            lnAudio.setVisibility(View.GONE);
        } else if (TAG.equals("HOTEL")) {
            url = GET_HOTEL_DETAIL + "?hotelId=" + locationId;
            lnAudio.setVisibility(View.GONE);
            btnAudio.setVisibility(View.GONE);
        } else if (TAG.equals("RES")) {
            url = GET_RESTAURANT_DETAIL + "?restaurantId=" + locationId;
            lnAudio.setVisibility(View.GONE);
            btnAudio.setVisibility(View.GONE);
        } else if (TAG.equals("CUL")) {
            url = GET_CULTURE_DETAIL + "?culturalId=" + locationId;
            lnOpen.setVisibility(View.GONE);
            lnAudio.setVisibility(View.GONE);
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
//                            place.setStar(object.getInt("Star"));
                            place.setViewCount(object.getInt("ViewCount"));
                            place.setLongDes(object.getString("LongDes"));
                            place.setLatitude(object.getDouble("Latitude"));
                            place.setLongitude(object.getDouble("Longitude"));
                            place.setAddress(object.getString("Address"));
                            place.setVideoDir(object.getString("VideoDir"));

                            VideoDir = place.getVideoDir();
                            title = place.getName();
                            if (TAG.equals("HIS") || TAG.equals("HOME")) {
                                place.setPhoneNumber(object.getString("PhoneNumber"));
                                place.setOpenDate(object.getString("OpenDate"));
                                place.setAudio(object.getString("Audio"));
                                txtPhone.setText(place.getPhoneNumber());
                                txtOpen.setText(place.getOpenDate());

                                AudioUrl = object.getString("Audio");
//                                try {
//                                    playerAudio.setDataSource(CommonDefine.DOMAIN_STOUR + AudioUrl);
//                                    playerAudio.prepare();
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                            }

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

                            txtPlaceName.setText(place.getName().trim());
//                            rbStarRate.setNumStars(place.getStar());

                            String vcount = String.valueOf(place.getViewCount()) + " Reviews";
                            txtAddress.setText(place.getAddress());

//                            txtReview.setText(vcount);

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
                            System.out.println("Error: " + e.toString());
                            e.printStackTrace();
//                            new DialogInfo(context, "Không thể lấy dữ liệu từ Server!!!").show();
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
