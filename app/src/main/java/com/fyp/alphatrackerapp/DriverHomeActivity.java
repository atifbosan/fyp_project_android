package com.fyp.alphatrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.alphatrackerapp.manager.SharedPrefManager;
import com.fyp.alphatrackerapp.model.EmergencyNumber;
import com.fyp.alphatrackerapp.model.Notification;
import com.fyp.alphatrackerapp.model.Tracking;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.GetEmergencyNumberService;
import com.fyp.alphatrackerapp.service.NotiCounterService;
import com.fyp.alphatrackerapp.service.TrackingService;
import com.fyp.alphatrackerapp.service.UpdateNotiCountService;
import com.fyp.alphatrackerapp.service.UpdateTrackStatusService;
import com.fyp.alphatrackerapp.util.EndPoint;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverHomeActivity extends AppCompatActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Location dest;
    boolean hideMarker;
    double distance, convrtedDistance;
    private static long INTERAl = 2000;
    private static long FAST_INTERVAL = 1000;
    private static final int REQUEST_LOCATION_CODE = 10;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    //    LocationDriver driver;
    int counter = 0;
    //    private List<LocationParent> locationParentList = new ArrayList<>();
//    LocationParent locationParent;
//    Marker currentLocationMarker, parentMarkers, schoolMarkers;
    private Location lastLocation;
    //    private GoogleMap mMap;
    private int code;
    //  private NetworkManager networkManager = new NetworkManager();
    MarkerOptions markerOptions, parentMarkerOptions, schoolMarkerOptions;
    List<Double> getParentLat = new ArrayList();
    List<Double> getParentLng = new ArrayList();
    List<Double> getSchoolLat = new ArrayList();
    List<Double> getSchoolLng = new ArrayList();
    List<String> getChildNames = new ArrayList<>();
    List<String> getParentNames = new ArrayList<>();
    List<String> getScoolNames = new ArrayList<>();
    private LatLng latLng, parentLatLng, schoolLatLng;
    private LocationRequest locationRequest;
    private static final String LOGSERVICE = "#######";
    String fk_driver_id;
    SharedPrefManager sharedPrefManager = new SharedPrefManager();
    double radiusInMeters = 0.02 * 1000.0; //5 km
    private double getLat, getLng;


    Toolbar toolbar;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    //    SharedPrefManager sharedPrefManager = new SharedPrefManager();
    TextView tv_driver_noti_counter;
    int User_ID;
    ProgressDialog progressDialog;
    Notification notification;
    CardView driver_notify_cardview;
    Tracking tracking;
    String latitude, longitude;
    long INTERVAl = 10;
    /*
        long FAST_INTERVAL = 10;
        GoogleApiClient mGoogleApiClient;
        LocationRequest mLocationRequest;
    */
    SwitchCompat switchlocation;
    TextView navUserName;
    ImageView nav_img;
    List<EmergencyNumber> emergencyNumberList = new ArrayList<>();
    List<String> userData = new ArrayList<>();
    CardView send_message_cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        send_message_cardView = findViewById(R.id.send_message_cardView);

        Dexter.withActivity(DriverHomeActivity.this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();


        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
        } else {
            showGPSDisabledAlertToUser();
        }

        navigationView = findViewById(R.id.Nav_view_Driver);
        drawerLayout = findViewById(R.id.layout_drawer_driver);
        toggle = new ActionBarDrawerToggle(DriverHomeActivity.this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        switchlocation = findViewById(R.id.switchlocation);
        tv_driver_noti_counter = findViewById(R.id.tv_driver_noti_counter);
        driver_notify_cardview = findViewById(R.id.driver_notify_cardview);
        User_ID = Integer.parseInt(sharedPrefManager.getID(DriverHomeActivity.this));
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.profilemenue:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.changepasswordmenue:
                        startActivity(new Intent(getApplicationContext(), ChangePasswordActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.aboutmenu:
                        startActivity(new Intent(getApplicationContext(),
                                AboutUsActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.emergencynumbermenu:
                        startActivity(new Intent(getApplicationContext(),
                                EmergencyNumberActivity.class));
                        drawerLayout.closeDrawer(GravityCompat.START);
                        break;

                    case R.id.logoutmenu:
                        Toast.makeText(getApplicationContext(), "Logout", Toast.LENGTH_LONG).show();
                        drawerLayout.closeDrawer(GravityCompat.START);
                        sharedPrefManager.clearAllPreference(DriverHomeActivity.this);
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        break;
                }
                return false;
            }
        });
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        View header = navigationView.inflateHeaderView(R.layout.nav_header);

        userData = sharedPrefManager.getUserData(DriverHomeActivity.this);

        navUserName = header.findViewById(R.id.nav_userName);
        nav_img = header.findViewById(R.id.nav_img);

        navUserName.setText(userData.get(1));
        Glide.with(DriverHomeActivity.this).load(EndPoint.IMAGE_URL + userData.get(7)).into(nav_img);

        send_message_cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNumbers(User_ID);
            }
        });

        GetNotiCounter(User_ID, "D");
        driver_notify_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateNotification(User_ID, "D");
                startActivity(new Intent(getApplicationContext(),
                        ViewAllAnnouncementActivity.class));
            }
        });


        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(INTERVAl)
                .setFastestInterval(FAST_INTERVAL);

        switchlocation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPrefManager.saveLOC(DriverHomeActivity.this, "1");
                    UpdateTrackingStatus("ON", User_ID);
                } else {
                    sharedPrefManager.saveLOC(DriverHomeActivity.this, "0");

                    UpdateTrackingStatus("OFF", User_ID);
                }
            }
        });


     /*   handler.postDelayed(new Runnable() {
            public void run() {
                if (sharedPrefManager.getLOC(DriverHomeActivity.this).equals("1")) {
                    //UpdateLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), User_ID);
                }
                System.out.println("myHandler: here!"); // Do your work here
                handler.postDelayed(this, delay);
            }
        }, delay);*/

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    private void UpdateTrackingStatus(String status, int uID) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        UpdateTrackStatusService service = RetrofitClient.getClient().create(UpdateTrackStatusService.class);
        Call<Tracking> call = service.updateStatus(status, uID);
        call.enqueue(new Callback<Tracking>() {
            @Override
            public void onResponse(Call<Tracking> call, Response<Tracking> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    tracking = response.body();
                    if (!tracking.isError()) {
                        Toast.makeText(DriverHomeActivity.this,
                                tracking.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DriverHomeActivity.this,
                                tracking.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DriverHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tracking> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(DriverHomeActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAl);
        mLocationRequest.setFastestInterval(FAST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mGoogleApiClient.isConnected()) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
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
/*
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        UpdateLocation(location.getLatitude(),location.getLongitude(),User_ID);
        Log.d("LOCATIONU", "onLocationChanged: " + latitude + " " + longitude);
*/
        counter++;
        lastLocation = location;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());

        getLat = lastLocation.getLatitude();
        getLng = lastLocation.getLongitude();

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();




        //animateMarker(currentLocationMarker, latLng, false);
/*
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        // markerOptions.draggable(true);
        markerOptions.title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));


        currentLocationMarker = mMap.addMarker(markerOptions);*/

        UpdateLocation(location.getLatitude(), location.getLongitude(), User_ID);
        Log.d("LOCATIONU", "onLocationChanged: " + getLat + " " + getLng);


//        sendDriverLatLng(getLat, getLng);


//        System.out.println("Changes Updated");
        /*Toast.makeText(this,
                "Changes Updated" + latitude + " " + longitude, Toast.LENGTH_SHORT).show();*/
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
                        tv_driver_noti_counter.setText(String.valueOf(notification.getCount()));
                    } else {
                        Toast.makeText(DriverHomeActivity.this,
                                notification.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DriverHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(DriverHomeActivity.this,
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
                        Toast.makeText(DriverHomeActivity.this,
                                notification.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DriverHomeActivity.this,
                                notification.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DriverHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Notification> call, Throwable t) {
                Toast.makeText(DriverHomeActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void UpdateLocation(double lat, double lng, int userID) {
        TrackingService service = RetrofitClient.getClient().create(TrackingService.class);
        Call<Tracking> call = service.insertTrack(lat, lng, userID);
        call.enqueue(new Callback<Tracking>() {
            @Override
            public void onResponse(Call<Tracking> call, Response<Tracking> response) {
                if (response.isSuccessful()) {
                    tracking = response.body();
                    if (!tracking.isError()) {
                        Log.d("LOCATION", lat + " , " + lng + "," + userID + " " + tracking.getMessage());
                    } else {
                        Toast.makeText(DriverHomeActivity.this,
                                tracking.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(DriverHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tracking> call, Throwable t) {
                Toast.makeText(DriverHomeActivity.this,
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
                    Toast.makeText(DriverHomeActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(DriverHomeActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}