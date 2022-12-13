package com.android.alertup_user;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.android.alertup_user.databinding.ActivityFullscreenBinding;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    ImageView splash;
    private static final boolean AUTO_HIDE = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);


        splash = findViewById(R.id.image);
        splash.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_clockwise));
        // Delayed removal of status and navigation bar

        Handler h = new Handler();
        // The Runnable will be executed after the given delay time
        h.postDelayed(r, 2000); // will be delayed for 1.5 seconds

    }

    Runnable r = new Runnable() {
        @Override
        public void run() {

                //String welcome_boy = "Welcome   "+ name;
                // welcome.setText(welcome_boy);
                Intent amphibiansActivityIntent = new Intent(FullscreenActivity.this, MapsActivity.class);
                //  Toast.makeText(Splash_screen.this, name,  Toast.LENGTH_LONG).show();
                startActivity(amphibiansActivityIntent);


        }
    };









}