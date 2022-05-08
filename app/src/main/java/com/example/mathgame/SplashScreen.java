package com.example.mathgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

// splash screen activity
public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // call start button activity
                Intent i = new Intent(getApplicationContext(), StartButtonActivity.class);
                startActivity(i);
                overridePendingTransition(0, android.R.anim.fade_out);
                // finish splash to prevent back button return
                finish();
            }
        }, 3000);
    }
}