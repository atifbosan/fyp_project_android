package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fyp.alphatrackerapp.manager.SharedPrefManager;

public class AboutUsActivity extends AppCompatActivity {

    TextView tv_aboutUs_enBack;
    int pref;
    SharedPrefManager sharedPrefManager = new SharedPrefManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        tv_aboutUs_enBack = findViewById(R.id.tv_aboutUs_enBack);
        pref = sharedPrefManager.getLoginPreference(AboutUsActivity.this);
        tv_aboutUs_enBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pref == 1) {
                    startActivity(new Intent(getApplicationContext(),
                            UserHomeActivity.class));
                    finish();
                } else if (pref == 2) {
                    startActivity(new Intent(getApplicationContext(),
                            DriverHomeActivity.class));
                    finish();
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (pref == 1) {
            startActivity(new Intent(getApplicationContext(),
                    UserHomeActivity.class));
            finish();
        } else if (pref == 2) {
            startActivity(new Intent(getApplicationContext(),
                    DriverHomeActivity.class));
            finish();
        }
    }
}