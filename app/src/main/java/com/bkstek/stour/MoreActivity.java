package com.bkstek.stour;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by thold on 8/1/2017.
 */

public class MoreActivity extends AppCompatActivity {

    Toolbar toolbar;

    CardView cvWeather;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.more_activity_layout);

        initWidgets();

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        cvWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoreActivity.this, WeatherActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initWidgets(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        cvWeather = findViewById(R.id.cvWeather);
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

}
