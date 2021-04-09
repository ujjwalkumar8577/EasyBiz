package com.ujjwalkumar.easybiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private Timer timer = new Timer();
    private TimerTask Splash;
    private Intent in = new Intent();

    LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Splash = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        in.setAction(Intent.ACTION_VIEW);
                        in.setClass(getApplicationContext(), MenuActivity.class);
                        in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(in);
                        finish();
                    }
                });
            }
        };
        timer.schedule(Splash, (int)1500);

    }
}