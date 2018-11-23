package com.bkstek.stour;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bkstek.stour.adapter.AdapterCategory;
import com.bkstek.stour.adapter.AdapterHotel;
import com.bkstek.stour.adapter.AdapterRestaurant;
import com.bkstek.stour.adapter.AdapterSlider;
import com.bkstek.stour.component.DialogInfo;
import com.bkstek.stour.component.DialogSelectCategory;
import com.bkstek.stour.fragment.FragmentSlider;
import com.bkstek.stour.model.Banner;
import com.bkstek.stour.model.Category;
import com.bkstek.stour.model.History;
import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.FunctionHelper;
import com.bkstek.stour.util.VolleySingleton;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.bkstek.stour.util.CommonDefine.GET_ALL_CATEGORY;
import static com.bkstek.stour.util.CommonDefine.GET_ALL_HOTEL;
import static com.bkstek.stour.util.CommonDefine.GET_ALL_RESTAURANT;

/**
 * Created by thold on 8/1/2017.
 */

public class HomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    Toolbar toolbar;
    ViewPager vpSlides;
    RecyclerView recyclerCate, recyclerHotel, recyclerRes;
    SwipeRefreshLayout swRefresh;
    Context context;

    Menu menu;
    List<Category> categoryList;
    AdapterCategory adapterCategory;
    RecyclerView.LayoutManager layoutManager;

    List<Fragment> fragments;
    AdapterSlider adapterSlider;

    TextView[] txtDots;
    LinearLayout lnDots;

    CardView cvHistory, cvFood, cvCultural, cvFindFlight, cvFindHotel;

    AdapterHotel adapterHotel;
    AdapterRestaurant adapterRestaurant;
    private SliderLayout sldHome;

    //region search poi
    SearchView svPOI;
    int selection = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        svPOI = (SearchView) findViewById(R.id.svPOI);
//        svPOI.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));

//        vpSlides = (ViewPager) findViewById(R.id.vpSlides);
        sldHome = findViewById(R.id.sldHome);
        sldHome.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sldHome.setScrollTimeInSec(4); //set scroll delay in seconds :

        lnDots = (LinearLayout) findViewById(R.id.lnDots);
        recyclerCate = (RecyclerView) findViewById(R.id.recyclerCate);
        recyclerCate.setNestedScrollingEnabled(false);

        cvHistory = (CardView) findViewById(R.id.cvHistory);
        cvFood = (CardView) findViewById(R.id.cvFood);
        cvCultural = (CardView) findViewById(R.id.cvCultural);
        cvFindFlight = findViewById(R.id.cvFindFlight);
        cvFindHotel = findViewById(R.id.cvFindHoltel);

        recyclerHotel = (RecyclerView) findViewById(R.id.recyclerHotel);
        recyclerRes = (RecyclerView) findViewById(R.id.recyclerRes);
        swRefresh = (SwipeRefreshLayout) findViewById(R.id.swRefresh);

        context = HomeActivity.this;

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        swRefresh.setColorSchemeResources(R.color.splashbackgroudcolor);
        if (FunctionHelper.isNetworkConnected(context)) {
            GetBanner();
            GetAllCategory();
            GetAllHotel();
            GetAllRestaurant();
        } else {
            new DialogInfo(context, "Không có kết nối internet!!!").show();
        }


        cvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iHis = new Intent(context, HistoryActivity.class);
                startActivity(iHis);
            }
        });

        cvCultural.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iCul = new Intent(context, CultureActivity.class);
                startActivity(iCul);
            }
        });

        cvFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iFood = new Intent(context, FoodActivity.class);
                startActivity(iFood);
            }
        });

        cvFindFlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iFlight = new Intent(context, MapsActivity.class);
                iFlight.putExtra(CommonDefine.FUNC, "ROUTING");
                startActivity(iFlight);
            }
        });

        cvFindHotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iHotel = new Intent(context, MapsActivity.class);
                iHotel.putExtra(CommonDefine.FUNC, "SMART");
                startActivity(iHotel);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();

        swRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swRefresh.setColorSchemeResources(R.color.splashbackgroudcolor);
                GetBanner();
                GetAllCategory();
                GetAllHotel();
                GetAllRestaurant();
                swRefresh.setRefreshing(false);
            }
        });
    }


    //region get all category
    private void GetAllCategory() {
        final ProgressDialog dialog = ProgressDialog.show(context, "", "Vui lòng đợi...");

        categoryList = new ArrayList<>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_ALL_CATEGORY,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                Category category = new Category();
                                if (i == 0 ) {
                                    category.setId(object.getInt("Id"));
                                    category.setName(object.getString("Name"));
                                    category.setName("Di tích lịch sử");

                                    categoryList.add(category);
                                }
                            }
                            adapterCategory = new AdapterCategory(context, categoryList);
                            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                            recyclerCate.setLayoutManager(layoutManager);
                            recyclerCate.setAdapter(adapterCategory);

                        } catch (JSONException e) {
                            Log.d("error", "get cate " + e.toString());
                        }

                        dialog.dismiss();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.d("error", "connect get cate " + error.toString());
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        VolleySingleton.getInstance(context).addToRequestQueue(request);

    }
    //endregion

    //region get banner slider

    List<Banner> bannerList = new ArrayList<>();

    private void AddDotSlider(int currentposition) {
        txtDots = new TextView[fragments.size()];
        int count = fragments.size();

        lnDots.removeAllViews();
        for (int i = 0; i < count; i++) {
            txtDots[i] = new TextView(this);
            txtDots[i].setText(Html.fromHtml("&#8226;"));
            txtDots[i].setTextSize(40);
            txtDots[i].setTextColor(GetColor(R.color.menu_disable));
            lnDots.addView(txtDots[i]);
        }

        txtDots[currentposition].setTextColor(GetColor(R.color.white));
    }

    private int GetColor(int idColor) {
        int color = 0;
        if (Build.VERSION.SDK_INT > 21) {
            color = ContextCompat.getColor(this, idColor);
        } else {
            color = getResources().getColor(idColor);
        }
        return color;
    }

    private void GetBanner() {
        bannerList.clear();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, CommonDefine.GET_BANNER,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                Banner banner = new Banner();
                                banner.setUrlBanner(object.getString("Image"));
                                bannerList.add(banner);
                            }

                            SetBanner(bannerList);
                        } catch (JSONException e) {
                            Log.d("error", "get banner " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "connect get banner " + error.toString());
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        objectRequest.setRetryPolicy(policy);
        VolleySingleton.getInstance(context).addToRequestQueue(objectRequest);
    }

    private void SetBanner(List<Banner> linkImage) {
        fragments = new ArrayList<>();

        for (int i = 0; i < linkImage.size(); i++) {
            FragmentSlider fragmentSlider = new FragmentSlider();
            Bundle bundle = new Bundle();
            bundle.putString("linkImage", linkImage.get(i).getUrlBanner());
            fragmentSlider.setArguments(bundle);
            fragments.add(fragmentSlider);

            SliderView sliderView = new SliderView(context);
            sliderView.setImageUrl(linkImage.get(i).getUrlBanner());
            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);

            //at last add this view in your layout :
            sldHome.addSliderView(sliderView);
        }


//        adapterSlider = new AdapterSlider(getSupportFragmentManager(), fragments);
//        vpSlides.setAdapter(adapterSlider);
//        adapterSlider.notifyDataSetChanged();
//        AddDotSlider(0);
//        vpSlides.addOnPageChangeListener(this);

    }

    //endregion

    //region get all hotel
    private void GetAllHotel() {
        final List<History> historyListHotel = new ArrayList<>();
        historyListHotel.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_ALL_HOTEL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                History hotel = new History();
                                boolean isActive = object.getBoolean("isActive");
                                if (isActive) {
                                    hotel.setId(object.getInt("Id"));
                                    hotel.setName(object.getString("Name"));
                                    hotel.setAvatar(object.getString("Avatar"));
                                    historyListHotel.add(hotel);
                                }
                            }

                            adapterHotel = new AdapterHotel(context, historyListHotel);
                            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                            recyclerHotel.setLayoutManager(layoutManager);
                            recyclerHotel.setAdapter(adapterHotel);
                            adapterHotel.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.d("error", "get all hotel " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "connect get hotel " + error.toString());
            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        VolleySingleton.getInstance(context).addToRequestQueue(request);

    }
    //endregion

    //region get all restaurant
    private void GetAllRestaurant() {
        final List<History> historyListRes = new ArrayList<>();
        historyListRes.clear();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, GET_ALL_RESTAURANT,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Data");
                            int count = array.length();
                            for (int i = 0; i < count; i++) {
                                JSONObject object = array.getJSONObject(i);
                                History res = new History();
                                boolean isActive = object.getBoolean("isActive");
                                if (isActive) {
                                    res.setId(object.getInt("Id"));
                                    res.setName(object.getString("Name"));
                                    res.setAvatar(object.getString("Avatar"));
                                    historyListRes.add(res);
                                }
                            }

                            adapterRestaurant = new AdapterRestaurant(context, historyListRes);
                            layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                            recyclerRes.setLayoutManager(layoutManager);
                            recyclerRes.setAdapter(adapterRestaurant);
                            adapterRestaurant.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Log.d("error", "get all res " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", "connect get res " + error.toString());
            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        request.setRetryPolicy(policy);

        VolleySingleton.getInstance(context).addToRequestQueue(request);

    }
    //endregion

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mnu_search){
            final DialogSelectCategory selectCategory = new DialogSelectCategory(context,selection);
            selectCategory.show();

            selectCategory.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    selection = selectCategory.getSelection();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        AddDotSlider(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            ((HomeScreenActivity) HomeScreenActivity.context).finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getString(R.string.exit_confirm),
                Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }
}
