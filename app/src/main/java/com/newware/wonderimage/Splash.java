package com.newware.wonderimage;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;

public class Splash extends AppCompatActivity {
    ImageView imageView;
    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

//        Objects.requireNonNull(getSupportActionBar()).hide();

        imageView = findViewById(R.id.iv_animation);
        imageView.setBackgroundResource(R.drawable.animation_laoding);
        animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();


        SplashLauncher splashLauncher = new SplashLauncher();
        splashLauncher.start();
    }

    private class SplashLauncher extends Thread {
        public void run() {
            try {

                sleep(1800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Splash.this, OnBoardScreen.class);
            startActivity(intent);
            Splash.this.finish();
        }

    }
}
