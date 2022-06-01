package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fyp.alphatrackerapp.manager.NetworkManager;
import com.fyp.alphatrackerapp.manager.SharedPrefManager;

public class MainActivity extends AppCompatActivity {

    Thread timer;
    int pref;
    NetworkManager manager = new NetworkManager();
    SharedPrefManager sharedPrefManager = new SharedPrefManager();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = sharedPrefManager.getLoginPreference(MainActivity.this);
        if (manager.checkInternetConnection(MainActivity.this)) {
            timer = new Thread() {
                @Override
                public void run() {
                    try {
                        synchronized (this) {
                            wait(2500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        if (pref == 1) {
                            Intent intent = new Intent(MainActivity.this, UserHomeActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else if (pref == 2) {
                            Intent intent = new Intent(MainActivity.this, DriverHomeActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                }
            };
            timer.start();
        } else {
            Toast.makeText(this,
                    "Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
}