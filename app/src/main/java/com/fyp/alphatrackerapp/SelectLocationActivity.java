package com.fyp.alphatrackerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SelectLocationActivity extends AppCompatActivity {

    GoogleMap googleMap;
    TextView tv_submit_place, tv_show_selected_place;
    ImageView map_marker_pin_img;
    MapView mMapView;
    String SelectedAddress;
    String selectedPlace;
    double selectedLat, selectedLng;
    TextView tv_search_place;
    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_CODE = 10;
    private GoogleApiClient googleApiClient;
    LatLng latLng, markerLatlng;
    Marker currentLocationMarker, newMarker;
    String location;
    PlacesClient placesClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);
        MapsInitializer.initialize(SelectLocationActivity.this);
        tv_submit_place = findViewById(R.id.tv_submit_place);
        tv_show_selected_place = findViewById(R.id.tv_show_selected_place);
        map_marker_pin_img = findViewById(R.id.map_marker_pin_img);
        map_marker_pin_img.setVisibility(View.INVISIBLE);
        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
//        LoadAddLayout();
        mMapView.onResume();// needed to get the map to display immediately
        // Create a new Places client instance.
        String apiKey = getString(R.string.google_maps_key);
        if (!Places.isInitialized()) {
            Places.initialize(SelectLocationActivity.this, apiKey);
        }

        placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.place_auto_complete_fragment);
        //autocompleteFragment.setTypeFilter(TypeFilter.CITIES);
        autocompleteFragment.setPlaceFields(Arrays.asList(com.google.android.libraries.places.api.model.Place.Field.ID,
                com.google.android.libraries.places.api.model.Place.Field.NAME,
                Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();
                final String name = place.getName();
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap googleMap) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(name);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        currentLocationMarker = googleMap.addMarker(markerOptions);
                        currentLocationMarker.setVisible(false);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

                        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                            @Override
                            public void onCameraIdle() {
                                if (googleMap != null) {
                                    googleMap.clear();
                                }
                                if (currentLocationMarker != null) {
                                    currentLocationMarker.remove();
                                }
                                map_marker_pin_img.setVisibility(View.VISIBLE);
                                LatLng center = googleMap.getCameraPosition().target;
                                newMarker = googleMap.addMarker(new MarkerOptions().position(center).title("New pos"));
                                newMarker.setVisible(false);
                                markerLatlng = newMarker.getPosition();
                                selectedLat = markerLatlng.latitude;
                                selectedLng = markerLatlng.longitude;
                                SelectedAddress = getStringAddress(markerLatlng.latitude, markerLatlng.longitude);
                                tv_show_selected_place.setText(SelectedAddress);
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(@NonNull Status status) {
            }
        });

        tv_submit_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SignupActivity.START_CLICK != 0) {
                    SignupActivity.START_POINT_ADD = SelectedAddress;
                    SignupActivity.START_LAT_ADD = selectedLat;
                    SignupActivity.START_LNG_ADD = selectedLng;
                    SignupActivity.START_CLICK = 0;
                } else if (SignupActivity.END_CLICK != 0) {
                    SignupActivity.END_POINT_ADD = SelectedAddress;
                    SignupActivity.END_LAT_ADD = selectedLat;
                    SignupActivity.END_LNG_ADD = selectedLng;
                    SignupActivity.END_CLICK = 0;
                }
                finish();
            }
        });

    }

    public String getStringAddress(Double lat, Double lng) {
        String add = "";
        String city = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(SelectLocationActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            add = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        return add + " " + city;
        return add;

    }
}