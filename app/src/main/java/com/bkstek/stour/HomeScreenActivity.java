package com.bkstek.stour;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.bkstek.stour.util.CommonDefine;
import com.bkstek.stour.util.FunctionHelper;

@SuppressWarnings("deprecation")
public class HomeScreenActivity extends TabActivity implements View.OnClickListener {


    RelativeLayout tab_home, tab_datcho, tab_info, tab_account;
    ImageView iv_menu_home, iv_menu_datcho, iv_menu_info, iv_menu_account;
    TextView tv_menu_home, tv_menu_datcho, tv_menu_info, tv_menu_account;

    Context context;

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_activity);

        initView();

        context = HomeScreenActivity.this;

        initTabHost();
        initTabBottom();
    }

    private void initView() {
        tab_home = (RelativeLayout) findViewById(R.id.tab_home);
        tab_datcho = (RelativeLayout) findViewById(R.id.tab_datcho);
        tab_info = (RelativeLayout) findViewById(R.id.tab_info);
        tab_account = (RelativeLayout) findViewById(R.id.tab_account);

        iv_menu_home = (ImageView) findViewById(R.id.iv_menu_home);
        iv_menu_datcho = (ImageView) findViewById(R.id.iv_menu_datcho);
        iv_menu_info = (ImageView) findViewById(R.id.iv_menu_info);
        iv_menu_account = (ImageView) findViewById(R.id.iv_menu_account);

        tv_menu_home = (TextView) findViewById(R.id.tv_menu_home);
        tv_menu_datcho = (TextView) findViewById(R.id.tv_menu_datcho);
        tv_menu_info = (TextView) findViewById(R.id.tv_menu_info);
        tv_menu_account = (TextView) findViewById(R.id.tv_menu_account);
    }

    private void initTabHost() {

        tabHost = getTabHost();

        // Tab for home
        TabHost.TabSpec homeSpec = tabHost.newTabSpec("Home");
        homeSpec.setIndicator("Home",
                getResources().getDrawable(R.drawable.ic_home_white_24dp));
        Intent iHome = new Intent(this, HomeActivity.class);
        homeSpec.setContent(iHome);

        // Tab for placehold
        TabHost.TabSpec placeSpec = tabHost.newTabSpec("PlaceHold");
        placeSpec.setIndicator("PlaceHold",
                getResources().getDrawable(R.drawable.ic_map));
        Intent iPlace = new Intent(this, MapsActivity.class);
        placeSpec.setContent(iPlace);

        // Tab for info
        TabHost.TabSpec infoSpec = tabHost.newTabSpec("Info");
        infoSpec.setIndicator("Info",
                getResources().getDrawable(R.drawable.ic_error_white_24dp));
        Intent iInfo = new Intent(this, InfoActivity.class);
        infoSpec.setContent(iInfo);

        // Tab for account
        TabHost.TabSpec accSpec = tabHost.newTabSpec("Account");
        accSpec.setIndicator("Account",
                getResources().getDrawable(R.drawable.ic_more_horiz_white_24dp));
        Intent iAccount = new Intent(this, MoreActivity.class);
        accSpec.setContent(iAccount);

        tabHost.addTab(homeSpec);
        tabHost.addTab(placeSpec);
        tabHost.addTab(infoSpec);
        tabHost.addTab(accSpec);
    }

    private void initTabBottom() {
        tab_home.setOnClickListener(this);
        tab_datcho.setOnClickListener(this);
        tab_info.setOnClickListener(this);
        tab_account.setOnClickListener(this);

        FunctionHelper.UpdateTabClick(context, CommonDefine.HOME);

        setHightLightClick();
        tab_home.performClick();

    }

    private void setHightLightClick() {
        tv_menu_home.setTextColor(context.getResources().getColor(
                R.color.menu_disable));

        tv_menu_datcho.setTextColor(context.getResources().getColor(
                R.color.menu_disable));

        tv_menu_info.setTextColor(context.getResources().getColor(
                R.color.menu_disable));

        tv_menu_account.setTextColor(context.getResources().getColor(
                R.color.menu_disable));

        iv_menu_home.setImageResource(R.drawable.ic_home2);
        iv_menu_home.setColorFilter(context.getResources().getColor(R.color.menu_disable));

        iv_menu_datcho.setImageResource(R.drawable.ic_map2);
        iv_menu_datcho.setColorFilter(context.getResources().getColor(R.color.menu_disable));

        iv_menu_info.setImageResource(R.drawable.ic_info);
        iv_menu_info.setColorFilter(context.getResources().getColor(R.color.menu_disable));

        iv_menu_account.setImageResource(R.drawable.ic_dots_v);
        iv_menu_account.setColorFilter(context.getResources().getColor(R.color.menu_disable));

        String tabName = FunctionHelper.GetNameTabClick(context);

        switch (tabName) {
            case CommonDefine.HOME:
                tv_menu_home.setTextColor(context.getResources().getColor(
                        R.color.menu_enable));
                iv_menu_home.setImageResource(R.drawable.ic_home2);
                iv_menu_home.setColorFilter(context.getResources().getColor(R.color.menu_enable));
                break;
            case CommonDefine.PLACEHOLD:
                tv_menu_datcho.setTextColor(context.getResources().getColor(
                        R.color.menu_enable));
                iv_menu_datcho.setImageResource(R.drawable.ic_map2);
                iv_menu_datcho.setColorFilter(context.getResources().getColor(R.color.menu_enable));
                break;
            case CommonDefine.INFO:
                tv_menu_info.setTextColor(context.getResources().getColor(
                        R.color.menu_enable));
                iv_menu_info.setImageResource(R.drawable.ic_info);
                iv_menu_info.setColorFilter(context.getResources().getColor(R.color.menu_enable));
                break;
            case CommonDefine.ACCOUNT:
                tv_menu_account.setTextColor(context.getResources().getColor(
                        R.color.menu_enable));
                iv_menu_account.setImageResource(R.drawable.ic_dots_v);
                iv_menu_account.setColorFilter(context.getResources().getColor(R.color.menu_enable));
                break;
        }


    }

    private void ClickHome() {
        FunctionHelper.UpdateTabClick(context, CommonDefine.HOME);
        setHightLightClick();
        tabHost.setCurrentTab(0);
    }

    private void ClickPlaceHold() {
        FunctionHelper.UpdateTabClick(context, CommonDefine.PLACEHOLD);
        setHightLightClick();
        tabHost.setCurrentTab(1);
    }

    private void ClickInfo() {
        FunctionHelper.UpdateTabClick(context, CommonDefine.INFO);
        setHightLightClick();
        tabHost.setCurrentTab(2);
    }

    private void ClickAccount() {
        FunctionHelper.UpdateTabClick(context, CommonDefine.ACCOUNT);
        setHightLightClick();
        tabHost.setCurrentTab(3);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.tab_home:
                ClickHome();
                break;
            case R.id.tab_datcho:
                ClickPlaceHold();
                break;
            case R.id.tab_info:
                ClickInfo();
                break;
            case R.id.tab_account:
                ClickAccount();
                break;
        }

    }
}
