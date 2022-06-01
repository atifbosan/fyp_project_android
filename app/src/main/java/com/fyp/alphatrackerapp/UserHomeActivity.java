package com.fyp.alphatrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.alphatrackerapp.adapter.EmergencyNumberAdapter;
import com.fyp.alphatrackerapp.manager.SharedPrefManager;
import com.fyp.alphatrackerapp.model.EmergencyNumber;
import com.fyp.alphatrackerapp.model.Notification;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.GetEmergencyNumberService;
import com.fyp.alphatrackerapp.service.NotiCounterService;
import com.fyp.alphatrackerapp.service.UpdateNotiCountService;
import com.fyp.alphatrackerapp.util.EndPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeActivity extends AppCompatActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    CardView user_track_location_card, User_notification_cardView, usr_send_msg;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navView;
    SharedPrefManager sharedPrefManager = new SharedPrefManager();
    Notification notification;
    TextView tv_user_noti_counter;
    int User_ID;
    TextView navUserName;
    ImageView nav_img;
    List<EmergencyNumber> emergencyNumberList = new ArrayList<>();
    List<String> userData = new ArrayList<>();
    double latitude, longitude;
    private static long INTERVAl = 60 * 1000;
    private static long FAST_INTERVAL = 15 * 1000;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        user_track_location_card = findViewById(R.id.user_track_location_card);
        User_notification_cardView = findViewById(R.id.User_notification_cardView);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navView = findViewById(R.id.nav_view_menue);
        drawer = findViewById(R.id.drawer_layout);
        usr_send_msg = findViewById(R.id.usr_send_msg);
        toggle = new ActionBarDrawerToggle(UserHomeActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        tv_user_noti_counter = findViewById(R.id.tv_user_noti_counter);
        User_ID = Integer.parseInt(sharedPrefManager.getID(UserHomeActivity.this));
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilemenue:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.changepasswordmenue:
                        startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.aboutmenu:
                        startActivity(new Intent(getApplicationContext(),
                                AboutUsActivity.class));
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.emergencynumbermenu:
                        startActivity(new Intent(getApplicationContext(),
                                EmergencyNumberActivity.class));
                        drawer.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.logoutmenu:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
                        drawer.closeDrawer(GravityCompat.START);
                        sharedPrefManager.clearAllPreference(UserHomeActivity.this);
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        finishAffinity();
                        break;
                }
                return false;
            }
        });

        usr_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNumbers(User_ID);
            }
        });

        navView.removeHeaderView(navView.getHeaderView(0));
        View header = navView.inflateHeaderView(R.layout.nav_header);

        userData = sharedPrefManager.getUserData(UserHomeActivity.this);

        navUserName = header.findViewById(R.id.nav_userName);
        nav_img = header.findViewById(R.id.nav_img);

        navUserName.setText(userData.get(1));
        Glide.with(UserHomeActivity.this).
                load(EndPoint.IMAGE_URL + userData.get(7)).
                into(nav_img);


        user_track_location_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        UserTrackLocationActivity.class));
            }
        });
        User_notification_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateNotification(User_ID, "U");
                startActivity(new Intent(getApplicationContext(),
                        ViewAllAnnouncementActivity.class));
            }
        });

        GetNotiCounter(User_ID, "U");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(INTERVAl)
                .setFastestInterval(FAST_INTERVAL);

    }


    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            Toast.makeText(this, latitude + " WORKS " + longitude + "", Toast.LENGTH_LONG).show();


        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, 1000);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }
    private void GetNotiCounter(int userID, String userType) {
        notification = new Notification();
        NotiCounterService service = RetrofitClient.getClient().create(NotiCounterService.class);
        Call<Notification> call = service.getCounter(userID, userType);
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    notification = response.body();
                    if (!notification.isError()) {
                        tv_user_noti_counter.setText(String.valueOf(notification.getCount()));
                    } else {
                        Toast.makeText(UserHomeActivity.this,
                                notification.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(UserHomeActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void UpdateNotification(int userID, String userType) {
        notification = new Notification();
        UpdateNotiCountService service = RetrofitClient.getClient().create(UpdateNotiCountService.class);
        Call<Notification> call = service.update_notify(userID, userType);
        call.enqueue(new Callback<Notification>() {
            @Override
            public void onResponse(Call<Notification> call, Response<Notification> response) {
                if (response.isSuccessful()) {
                    notification = response.body();
                    if (!notification.isError()) {
                        Toast.makeText(UserHomeActivity.this,
                                notification.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserHomeActivity.this,
                                notification.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(UserHomeActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetNumbers(int ID) {

        GetEmergencyNumberService service = RetrofitClient.getClient().create(GetEmergencyNumberService.class);
        Call<JsonObject> call = service.getEmergencyNumbers(ID);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().getAsJsonObject().toString());

                            JSONArray jsonArray = jsonObject.getJSONArray("records");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject data = jsonArray.getJSONObject(i);

                                emergencyNumberList.add(new EmergencyNumber(data.getInt("en_id"),
                                        data.getString("en_name"),
                                        data.getString("en_number")));
                            }
                            for (int j = 0; j < emergencyNumberList.size(); j++) {
                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(emergencyNumberList.get(j).getEn_number(),
                                            null, "http://maps.google.com/maps?saddr=" + latitude + "," + longitude, null, null);
                                    Toast.makeText(getApplicationContext(), "SMS sent.",
                                            Toast.LENGTH_LONG).show();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    Toast.makeText(getApplicationContext(),
                                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                } else {
                    Toast.makeText(UserHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UserHomeActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}