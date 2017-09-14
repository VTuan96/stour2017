package com.bkstek.stour;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen_layout);

        imageView = (ImageView) findViewById(R.id.imageView3);
        // animation
        Animation rotateAnimation = AnimationUtils.loadAnimation(SplashScreenActivity.this,R.anim.rotate);
        rotateAnimation.setRepeatCount(100);
        imageView.startAnimation(rotateAnimation);

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                }catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent homeScreen = new Intent(SplashScreenActivity.this, HomeScreenActivity.class);

                    startActivity(homeScreen);
                }
            }
        });
        thread.start();
    }
}
