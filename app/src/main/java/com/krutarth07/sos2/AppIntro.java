package com.krutarth07.sos2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Admin on 23-03-2017.
 */
/////aaaaaaaaaaaaaaaaaaaaaaaaaa
public class AppIntro extends com.github.paolorotolo.appintro.AppIntro {

    @Override
    public void init(Bundle savedInstanceState) {

        // Add your slide's fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        /*ddSlide(first_fragment);
        addSlide(second_fragment);
        addSlide(third_fragment);
        addSlide(fourth_fragment);*/

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("SOS APP", "Your personal informer in your pockets.", R.drawable.press, Color.parseColor("#b60113")));
        addSlide(AppIntroFragment.newInstance("Emergency Contacts", "The people who care for you.", R.drawable.home, Color.parseColor("#14a804")));
        addSlide(AppIntroFragment.newInstance("Panic Message", "We take care of your choices.", R.drawable.message, Color.parseColor("#b60113")));
        addSlide(AppIntroFragment.newInstance("Remote Location Keyword", "You're never alone.", R.drawable.keywords, Color.parseColor("#14a804")));


        // OPTIONAL METHODS
        // Override bar/separator color.
        /*setBarColor(Color.parseColor("#03A9F4"));
        setSeparatorColor(Color.parseColor("#03A9F4"));*/

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permisssion in Manifest.

    }

    @Override
    public void onSkipPressed() {
        // Do something when users tap on Skip button.
        Intent intent = new Intent(AppIntro.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDonePressed() {
        // Do something when users tap on Done button.
        Intent intent = new Intent(AppIntro.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSlideChanged() {
        // Do something when the slide changes.
    }

    @Override
    public void onNextPressed() {
        // Do something when users tap on Next button.
    }
}
