package com.fyp.alphatrackerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.directionadapter.FetchURL;
import com.fyp.alphatrackerapp.directionadapter.TaskLoadedCallback;
import com.fyp.alphatrackerapp.model.Routes;
import com.fyp.alphatrackerapp.model.Tracking;
import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.GetDriverLatLngService;
import com.fyp.alphatrackerapp.service.GetDriversService;
import com.fyp.alphatrackerapp.service.GetRoutesService;
import com.fyp.alphatrackerapp.util.HttpHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserTrackLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        TaskLoadedCallback,
        LocationListener {

    final Handler handler = new Handler();
    final int delay = 1000; // 1000 milliseconds == 1 second

    Button btn_track_driver;
    Runnable mStatusChecker;
    String url;
    TextView tv_back_to_userHome;
    Spinner search_route_spinner;
    List<Routes> routesList = new ArrayList<>();
    List<String> routeNameList = new ArrayList<>();
    int selectedRouteID;
    private static final int REQUEST_LOCATION_CODE = 10;
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    MarkerOptions place1;
    private static long INTERAl = 60 * 1000;
    private static long FAST_INTERVAL = 15 * 1000;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentLocationMarker,driverMarker;
    private LatLng latLng;
    double current_lat, current_lng;
    List<User> userList = new ArrayList<>();
    List<Double> latitudeList = new ArrayList<>();
    List<Double> longitudeList = new ArrayList<>();
    ProgressDialog progressDialog;
    private MarkerOptions place2;
    private Polyline currentPolyline;
    double getLiveLat, getliveLng;
    int getD_id;
    MarkerOptions markerOptions;

    private Handler mHandler;
    int v = 0;

    private Timer timer;
    Tracking tracking;

    private TimerTask timerTask;
//    private Handler handler = new Handler();

    double place_lat, place_lng;
    TextView tv_distance, tv_duration;
    String cal_distance;
    String cal_time, cityName;


    //To stop timer
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
    }

    //To start timer
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_track_location);
        tv_back_to_userHome = findViewById(R.id.tv_back_to_userHome);
        search_route_spinner = findViewById(R.id.search_route_spinner);
        tv_distance=findViewById(R.id.tv_distance);
        tv_duration=findViewById(R.id.tv_duration);
        btn_track_driver=findViewById(R.id.btn_track_driver);

        GetAllRoutes();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            checkSelfPermission();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        tv_back_to_userHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        UserHomeActivity.class));
                finish();
            }
        });

//        startTimer();

/*
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //your method
                if (selectedRouteID != 0) {
                    GetDrivers(selectedRouteID);
                }
                Log.d("TIMERCHECK", "run: " + selectedRouteID);

            }

        }, 0, 5000);//put here time 1000 milliseconds=1 second
*/

//        mHandler = new Handler();
        //      startRepeatingTask();

    }


    private boolean checkSelfPermission() {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }

            return false;

        } else
            return true;
    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case REQUEST_LOCATION_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {

                            buildGoogleApiClient();
                            mMap.setMyLocationEnabled(true);
                        } else {
                            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }

                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERAl);
        locationRequest.setFastestInterval(FAST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap != null) {
            mMap.clear();
        }
        mMap = googleMap;


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);

            } else {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }

            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    getD_id = Integer.parseInt(marker.getSnippet());
                    latLng = marker.getPosition();
                    place2 = new MarkerOptions().position(latLng).title(marker.getTitle());
                    place_lat = marker.getPosition().latitude;
                    place_lng = marker.getPosition().longitude;
                    btn_track_driver.setVisibility(View.VISIBLE);
                    btn_track_driver.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(getApplicationContext(),DriverLatlngActivity.class);
                            intent.putExtra("DRIVER_ID",getD_id);
                            startActivity(intent);
                        }
                    });

/*
                    new Timer().scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            //your method
                            v++;
                            trackDriverLocation();

                            Log.d("GET_TIMER", "run: " + v);//this function can change value of mInterval.

                        }
                    }, 1000, 1000);//put here time 1000 milliseconds=1 second
*/

                    new FetchURL(UserTrackLocationActivity.this).execute(getUrl(place2.getPosition(), place1.getPosition(), "driving"), "driving");
                    new GetDistanceDuration().execute();
//                    new GetDistanceDuration().execute();



                }
            });

        }
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    protected Marker createMarker(double latitude, double longitude, String name, String v_name, int UID) {


        if (latLng != null) {

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title("My Location");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            currentLocationMarker = mMap.addMarker(markerOptions);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

        }


        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.2f, 0.2f)
                .title(v_name)
                .snippet(String.valueOf(UID))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carplaceholder)));

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        current_lat = lastLocation.getLatitude();
        current_lng = lastLocation.getLongitude();
        place1 = new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("My Location");
        latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
         refreshMapPosition(latLng, 90);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("My Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocationMarker = mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }


    private void GetAllRoutes() {
        GetRoutesService service = RetrofitClient.getClient().create(GetRoutesService.class);
        Call<JsonObject> call = service.getRoutes();
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

                                routesList.add(new Routes(
                                        data.getInt("route_id"),
                                        data.getString("route_title")));

                                routeNameList.add(data.getString("route_title"));

                            }

                            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(UserTrackLocationActivity.this,
                                    R.layout.support_simple_spinner_dropdown_item, routeNameList);
                            search_route_spinner.setAdapter(routeAdapter);
                            search_route_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedRouteID = routesList.get(position).getRoute_id();
                                    if (userList != null) {
                                        userList.clear();
                                    }
                                    if (latitudeList != null) {
                                        latitudeList.clear();
                                    }
                                    if (longitudeList != null) {
                                        longitudeList.clear();
                                    }
                                    GetDrivers(selectedRouteID);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    Toast.makeText(UserTrackLocationActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UserTrackLocationActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetDrivers(int id) {
        if (userList != null) {
            userList.clear();
        }
        if (latitudeList != null) {
            latitudeList.clear();
        }
        if (longitudeList != null) {
            longitudeList.clear();
        }
        GetDriversService service = RetrofitClient.getClient().create(GetDriversService.class);
        Call<JsonObject> call = service.getDrivers(id);
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

                                userList.add(new User(data.getInt("user_id"),
                                        data.getString("user_fullName"),
                                        data.getString("user_cnic"),
                                        data.getString("user_contactNo"),
                                        data.getString("user_gender"),
                                        data.getString("user_profileImage"),
                                        data.getInt("tracking_id"),
                                        data.getDouble("tracking_lat"),
                                        data.getDouble("tracking_lng"),
                                        data.getString("vehicle_title")));
                                latitudeList.add(data.getDouble("tracking_lat"));
                                longitudeList.add(data.getDouble("tracking_lng"));
                                createMarker(latitudeList.get(i),
                                        longitudeList.get(i),
                                        userList.get(i).getUser_fullName(),
                                        userList.get(i).getVehicle_title(),
                                        userList.get(i).getUser_id());
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(UserTrackLocationActivity.this,
                                response.message(), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(UserTrackLocationActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(UserTrackLocationActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void refreshMapPosition(LatLng pos, float angle) {
        CameraPosition.Builder positionBuilder = new CameraPosition.Builder();
        positionBuilder.target(pos);
        positionBuilder.zoom(16f);
        //positionBuilder.bearing(angle);
        //positionBuilder.tilt(90);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(positionBuilder.build()));
    }



    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }

    private void trackDriverLocation() {
        tracking = new Tracking();
        GetDriverLatLngService service = RetrofitClient.getClient().create(GetDriverLatLngService.class);
        Call<Tracking> call = service.getTracking(getD_id);
        call.enqueue(new Callback<Tracking>() {
            @Override
            public void onResponse(Call<Tracking> call, Response<Tracking> response) {
                if (response.isSuccessful()) {
                    tracking = response.body();
                    if (!tracking.isError()) {
                        onMapReady(mMap);
                        getLiveLat = tracking.getTracking_lat();
                        getliveLng = tracking.getTracking_lng();

                        System.out.println("Location: Lat: "+getLiveLat + " "+ "Lng: "+getliveLng);
                        loadMap(getLiveLat, getliveLng);

                    }


                } else {
                    Toast.makeText(UserTrackLocationActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tracking> call, Throwable t) {
                Toast.makeText(UserTrackLocationActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMap(double lat, double lng) {
        if (markerOptions != null) {
            driverMarker.remove();

        }
        latLng = new LatLng(lat, lng);
        Log.d("CheckLoc", "onCreate: " + lat + "/" + lng);
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(" Tracking");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        driverMarker = mMap.addMarker(markerOptions);

        refreshMapPosition(latLng, 90);

    }

    /**
     * function to calculate distance between two points
     * Async task class to get json by making HTTP call
     */
    private class GetDistanceDuration extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + current_lat + "," + current_lng +
                    "&destination=" + place_lat + "," + place_lng + "&mode=driving" +
                    "&key=" + getText(R.string.google_maps_key).toString();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e("JSONSTR", "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray routes = jsonObj.getJSONArray("routes");

                    for (int i = 0; i < routes.length(); i++) {
                        JSONArray legs = ((JSONObject) routes.get(i)).getJSONArray("legs");

                        for (int j = 0; j < legs.length(); j++) {

                            /** Getting distance from the json data */
                            JSONObject jDistance = ((JSONObject) legs.get(j)).getJSONObject("distance");
                            cal_distance = jDistance.getString("text");

                            /** Getting duration from the json data */
                            JSONObject jDuration = ((JSONObject) legs.get(j)).getJSONObject("duration");
                            cal_time = jDuration.getString("text");
                        }
                    }
                }

 /*                   viewPagerAdapter = new ViewPagerAdapter(SelectedPlaceDetailActivity.this, piclist);
                    viewPager.setAdapter(viewPagerAdapter);
                    dotsIndicator.setViewPager(viewPager);
*/ catch (final JSONException e) {
                    Log.e("ERROR", "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e("SERVER ERROR", "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            String distanceText = "Distance: " + cal_distance;
            String durationText = "Duration: " + cal_time;
            tv_distance.setText(distanceText);
            tv_duration.setText(durationText);
        }
    }

}