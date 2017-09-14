package com.bkstek.stour;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by thold on 8/1/2017.
 */

public class InfoActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    TabLayout tabInfo;
    ViewPager viewpagerInfo;
    Toolbar toolbar;

    ViewPagerInfo adapterInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity_layout);

        tabInfo = (TabLayout) findViewById(R.id.tabInfo);
        viewpagerInfo = (ViewPager) findViewById(R.id.viewpagerInfo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        adapterInfo = new ViewPagerInfo(getSupportFragmentManager());
        viewpagerInfo.setAdapter(adapterInfo);
        adapterInfo.notifyDataSetChanged();
        tabInfo.setupWithViewPager(viewpagerInfo);
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
