package com.bkstek.stour;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.bkstek.stour.model.Banner;
import com.bkstek.stour.model.Place;
import com.bkstek.stour.util.FunctionHelper;
import com.bkstek.stour.util.VolleySingleton;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_FOOD_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_PLACE_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_HOTEL_DETAIL;
import static com.bkstek.stour.util.CommonDefine.GET_RESTAURANT_DETAIL;

/**
 * Created by acebk on 8/3/2017.
 */

public class DetailActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ImageView imBack;
    TextView txtPlaceName, txtReview, txtdistince, txtDes, txtAddress;
    RatingBar rbStarRate;
    String url;
    int locationID;
    String TAG;
    Toolbar toolbar;
    ViewPager vpSlides;

    ProgressBar pgOne, pgTwo, pgThree, pgFour, pgFive;
    RelativeLayout youtube;
    TextView txtLink;

    Context context;
    List<Banner> bannerList = new ArrayList<>();

    List<Fragment> fragments;
    AdapterSlider adapterSlider;

    String video_id = "uYOMT3VYxvA";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_layout);

        imBack = (ImageView) findViewById(R.id.imBack);
        // imPlace = (ImageView) findViewById(R.id.imPlace);
        txtPlaceName = (TextView) findViewById(R.id.txtPlaceName);
        txtReview = (TextView) findViewById(R.id.txtReview);
        txtdistince = (TextView) findViewById(R.id.txtdistince);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rbStarRate = (RatingBar) findViewById(R.id.rbStarRate);
        txtDes = (TextView) findViewById(R.id.txtDes);
        txtAddress = (TextView) findViewById(R.id.txtAddress);
        vpSlides = (ViewPager) findViewById(R.id.vpSlides);

        pgOne = (ProgressBar) findViewById(R.id.pgOne);
        pgTwo = (ProgressBar) findViewById(R.id.pgTwo);
        pgThree = (ProgressBar) findViewById(R.id.pgThree);
        pgFour = (ProgressBar) findViewById(R.id.pgFour);
        pgFive = (ProgressBar) findViewById(R.id.pgFive);

        youtube = (RelativeLayout) findViewById(R.id.youtube);
        txtLink = (TextView) findViewById(R.id.txtLink);

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

        context = DetailActivity.this;

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        locationID = intent.getIntExtra("PlaceID", 0);
        TAG = intent.getStringExtra("TAG");

        if (FunctionHelper.isNetworkConnected(context)) {
            GetData(locationID);
        } else {
            new DialogInfo(context, "Không có kết nối internet!!!").show();
        }


        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iYoutube = new Intent(context, YoutubeActivity.class);
                iYoutube.putExtra("VIDEO_ID", video_id);
                iYoutube.putExtra("locationID", locationID);
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

                            JSONArray array = object.getJSONArray("FileAttachs");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                Banner banner = new Banner();
                                banner.setUrlBanner(jsonObject.getString("FileUrl"));
                                bannerList.add(banner);
                            }

                            SetBanner(bannerList);

                            txtPlaceName.setText(place.getName());
                            rbStarRate.setNumStars(place.getStar());

                            String vcount = String.valueOf(place.getViewCount()) + " Reviews";
                            txtAddress.setText(place.getAddress());
                            txtReview.setText(vcount);
                            txtDes.setText(Html.fromHtml(place.getLongDes()));

                        } catch (JSONException e) {
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


        adapterSlider = new AdapterSlider(getSupportFragmentManager(), fragments);
        vpSlides.setAdapter(adapterSlider);
        adapterSlider.notifyDataSetChanged();

        vpSlides.addOnPageChangeListener(this);

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
}
