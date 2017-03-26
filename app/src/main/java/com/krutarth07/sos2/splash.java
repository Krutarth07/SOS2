package com.krutarth07.sos2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

/**
 * Created by Krutarth on 27-01-2017.
 */

public class splash extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Shimmer shimmer;
        ShimmerTextView t = (ShimmerTextView)findViewById(R.id.shimmer_tv);
        shimmer = new Shimmer();
        shimmer.start(t);


        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(2000);


                    // After 5 seconds redirect to another intent
                    Intent i = new Intent(splash.this, AppIntro.class);
                    startActivity(i);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();

    }
}