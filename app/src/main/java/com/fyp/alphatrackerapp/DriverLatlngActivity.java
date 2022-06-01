package com.fyp.alphatrackerapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.fyp.alphatrackerapp.directionadapter.FetchURL;
import com.fyp.alphatrackerapp.model.Tracking;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.GetDriverLatLngService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverLatlngActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int REQUEST_LOCATION_CODE = 10;
    private GoogleApiClient googleApiClient;
    private GoogleMap mMap;
    Tracking tracking;
    private Timer timer;
    double getLiveLat, getliveLng;
    MarkerOptions markerOptions, currentmarkeroption;
    private LatLng latLng;
    private Marker currentLocationMarker, driverMarker;
    boolean hideMarker;
    int id;
    private Handler mHandler;
    int v = 0;
    private static long INTERAl = 2000;
    private static long FAST_INTERVAL = 1000;
    private int mInterval = 2000; // 5 seconds by default, can be changed later
    private LocationRequest mLocationRequest;
    private Location lastLocation;
    private LocationRequest locationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_latlng);
        id = getIntent().getIntExtra("DRIVER_ID", 0);

        Dexter.withActivity(DriverLatlngActivity.this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (googleApiClient == null) {


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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driverlatmap);
        mapFragment.getMapAsync(this);

/*
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //your method
                trackDriverLocation();
                //trackDriverLocation();
                System.out.println("working");
                //Log.d("GET_TIMER", "run: " + v);//this function can change value of mInterval.

            }
        }, 5000, 5000);
*/

        mHandler = new Handler();
        startRepeatingTask();

    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    //----------------------GOOGLE CALLBACKS--------------------------//

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;




    /*    schoolMarkerOptions = new MarkerOptions();
        schoolMarkerOptions.position(new LatLng(getSat, getSLng));
        schoolMarkerOptions.title(schoolName);
        schoolMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        schoolMarker = mMap.addMarker(schoolMarkerOptions);
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(getSat, getSLng))
                .radius(20)
                .strokeWidth(.5f)
                .strokeColor(Color.parseColor("#2271cce7"))
                .fillColor(Color.parseColor("#2271cce7")));
*/

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_json));

                    if (!success) {
                        Log.e("", "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e("error", "Can't find style. Error: ", e);
                }

                buildGoogleApiClient();


                mMap.setMyLocationEnabled(true);
            }

        } else {

            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.style_json));

                if (!success) {
                    Log.e("", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("error", "Can't find style. Error: ", e);
            }
            buildGoogleApiClient();

            mMap.setMyLocationEnabled(true);


        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        currentmarkeroption = new MarkerOptions();
        currentmarkeroption.position(new LatLng(location.getLatitude(), location.getLongitude()));
        currentmarkeroption.title("Current location");
        currentmarkeroption.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        currentLocationMarker = mMap.addMarker(currentmarkeroption);
/*
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(getPLat, getPLng))
                .radius(20)
                .strokeWidth(.5f)
                .strokeColor(Color.parseColor("#2271cce7"))
                .fillColor(Color.parseColor("#2271cce7")));
*/

    }

    private void trackDriverLocation() {
        tracking = new Tracking();
        GetDriverLatLngService service = RetrofitClient.getClient().create(GetDriverLatLngService.class);
        Call<Tracking> call = service.getTracking(id);
        call.enqueue(new Callback<Tracking>() {
            @Override
            public void onResponse(Call<Tracking> call, Response<Tracking> response) {
                if (response.isSuccessful()) {
                    tracking = response.body();
                    if (!tracking.isError()) {
                        onMapReady(mMap);
                        getLiveLat = tracking.getTracking_lat();
                        getliveLng = tracking.getTracking_lng();

                        System.out.println("Location: Lat: " + getLiveLat + " " + "Lng: " + getliveLng);
                        loadMap(getLiveLat, getliveLng);

                    }


                } else {
                    Toast.makeText(DriverLatlngActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tracking> call, Throwable t) {
                Toast.makeText(DriverLatlngActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                v++;
                trackDriverLocation();
                Log.d("GET_TIMER", "run: " + v);//this function can change value of mInterval.
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private void refreshMapPosition(LatLng pos, float angle) {
        CameraPosition.Builder positionBuilder = new CameraPosition.Builder();
        positionBuilder.target(pos);
        positionBuilder.zoom(16f);
        //positionBuilder.bearing(angle);
        //positionBuilder.tilt(90);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(positionBuilder.build()));
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
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.carplaceholder));

//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        driverMarker = mMap.addMarker(markerOptions);

        refreshMapPosition(latLng, 90);

/*
        CheckHomeRadius(lat, lng);
        CheckSchoolRadius(lat, lng);
*/

    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }




/*
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
                }
            });

        }
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
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void loadMap(double lat, double lng) {
        //driverMarker.remove();
        //hideMarker = markerOptions == null;

        if (markerOptions!=null){
            driverMarker.remove();
        }

        latLng = new LatLng(lat, lng);
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(latLng);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;


        Log.d("CheckLoc", "onCreate: " + lat + "/" + lng);
        markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(" Tracking");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        driverMarker = mMap.addMarker(markerOptions);

        refreshMapPosition(latLng, 90);

    }

    private void refreshMapPosition(LatLng pos, float angle) {
        CameraPosition.Builder positionBuilder = new CameraPosition.Builder();
        positionBuilder.target(pos);
        positionBuilder.zoom(16f);
        //positionBuilder.bearing(angle);
        //positionBuilder.tilt(90);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(positionBuilder.build()));
    }

    private void trackDriverLocation() {
        tracking = new Tracking();
        GetDriverLatLngService service = RetrofitClient.getClient().create(GetDriverLatLngService.class);
        Call<Tracking> call = service.getTracking(id);
        call.enqueue(new Callback<Tracking>() {
            @Override
            public void onResponse(Call<Tracking> call, Response<Tracking> response) {
                if (response.isSuccessful()) {
                    tracking = response.body();
                    if (!tracking.isError()) {
                        onMapReady(mMap);
                        getLiveLat = tracking.getTracking_lat();
                        getliveLng = tracking.getTracking_lng();

                        System.out.println("Location: Lat: " + getLiveLat + " " + "Lng: " + getliveLng);
                        loadMap(getLiveLat, getliveLng);

                    }


                } else {
                    Toast.makeText(DriverLatlngActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tracking> call, Throwable t) {
                Toast.makeText(DriverLatlngActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
*/

}