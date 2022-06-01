package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.model.Cities;
import com.fyp.alphatrackerapp.model.Routes;
import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.model.Vehicle;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.CreateUserAccountService;
import com.fyp.alphatrackerapp.service.DriverSignupService;
import com.fyp.alphatrackerapp.service.GetCitiesService;
import com.fyp.alphatrackerapp.service.GetRoutesService;
import com.fyp.alphatrackerapp.service.GetVehicleService;
import com.fyp.alphatrackerapp.util.Validation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity implements
        LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    EditText edt_signup_fullname, edt_signup_email, edt_signup_password,
            edt_signup_Confirmpassword, edt_signup_contact, edt_signup_cnic;
    RadioButton radioMale, radioFemale;
    Spinner City_signup_spinner, role_spinner;
    Button btn_signup;
    CircleImageView profile_signup_image;
    ProgressDialog progressDialog;
    LinearLayout DriverSignupLL;
    Spinner signup_Vehicle_spinner, signup_Route_spinner;
    TextView tv_start_point, tv_end_point;
    String rolearray[] = {"Select Your Role", "User", "Driver"};
    String spinnerSelectedValue;
    int rolePos;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_CAPTURE_PHONE = 2;
    private Uri filePath, cameraImageURI;
    File file;
    boolean isCameraCaptured = false;
    boolean isProfileImgClicked = false;
    boolean isGalleryCaptured = false;
    String latitude, longitude;
    private static long INTERVAl = 60 * 1000;
    private static long FAST_INTERVAL = 15 * 1000;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    double a_latitude = 0.0, a_longitude = 0.0;
    String add, city;
    List<Cities> citiesList = new ArrayList<>();
    List<String> cityNameList = new ArrayList<>();
    List<Routes> routesList = new ArrayList<>();
    List<String> routeNameList = new ArrayList<>();
    List<Vehicle> vehicleList = new ArrayList<>();
    List<String> vehicleNameList = new ArrayList<>();
    int selectedCityID, selectedRouteID, selectedVehicleID, radio_check;
    RequestBody requestFile;
    User user;
    String address, getSelectedCityName;
    public static String START_POINT_ADD = "";
    public static double START_LAT_ADD = 0.0;
    public static double START_LNG_ADD = 0.0;
    public static String END_POINT_ADD = "";
    public static double END_LAT_ADD = 0.0;
    public static double END_LNG_ADD = 0.0;
    public static int START_CLICK = 0;
    public static int END_CLICK = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edt_signup_fullname = findViewById(R.id.edt_signup_fullname);
        edt_signup_email = findViewById(R.id.edt_signup_email);
        edt_signup_password = findViewById(R.id.edt_signup_password);
        edt_signup_Confirmpassword = findViewById(R.id.edt_signup_Confirmpassword);
        edt_signup_contact = findViewById(R.id.edt_signup_contact);
        edt_signup_cnic = findViewById(R.id.edt_signup_cnic);
        radioMale = findViewById(R.id.radioMale);
        radioFemale = findViewById(R.id.radioFemale);
        City_signup_spinner = findViewById(R.id.City_signup_spinner);
        role_spinner = findViewById(R.id.role_spinner);
        btn_signup = findViewById(R.id.btn_signup);
        profile_signup_image = findViewById(R.id.profile_signup_image);
        DriverSignupLL = findViewById(R.id.DriverSignupLL);
        signup_Vehicle_spinner = findViewById(R.id.signup_Vehicle_spinner);
        signup_Route_spinner = findViewById(R.id.signup_Route_spinner);
        tv_start_point = findViewById(R.id.tv_start_point);
        tv_end_point = findViewById(R.id.tv_end_point);
        SetRoleSpinner();
        GetAllCities();

        tv_start_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_start_point.setText("Selected");
                START_CLICK++;
                startActivity(new Intent(SignupActivity.this,
                        SelectLocationActivity.class));
            }
        });

        tv_end_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_end_point.setText("Ending Point");
                END_CLICK++;
                startActivity(new Intent(SignupActivity.this,
                        SelectLocationActivity.class));
            }
        });

        profile_signup_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isProfileImgClicked = true;
                showImagePickerDialog();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(INTERVAl)
                .setFastestInterval(FAST_INTERVAL);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rolePos == 1) {
                    if (Validation()) {
                        CreateUserAccount();
                    }
                } else if (rolePos == 2) {
                    if (Validation()) {
                        CreateDriverAccount();
                    }
                }
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
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            latitude = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());
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
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        address = getStringAddress(location.getLatitude(), location.getLongitude());
    }


    /**
     * dialog for show image picker option in android
     */
    private void showImagePickerDialog() {
        Dialog dialog = new Dialog(SignupActivity.this, R.style.TranslucentDialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_pic_picker);
        dialog.setCancelable(true);

        ImageView imgGallery = dialog.findViewById(R.id.img_camera);
        imgGallery.setOnClickListener(v -> {
            loadGalleryImage();
            dialog.dismiss();
        });

        ImageView imageCamera = dialog.findViewById(R.id.img_gallery);
        imageCamera.setOnClickListener(v -> {
            loadTakePicture();
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * load gallery function
     */
    private void loadGalleryImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * load camera function
     */
    private void loadTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraImageURI = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "pic_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        );
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageURI);
            startActivityForResult(pictureIntent,
                    REQUEST_IMAGE_CAPTURE_PHONE);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:

                    if (isProfileImgClicked) {

                        if (profile_signup_image != null) {
                            profile_signup_image.setBackground(null);
                        }
                        Uri selectedImage = data.getData();
                        try {
                            isGalleryCaptured = true;
                            filePath = data.getData();
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                            profile_signup_image.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Log.i("TAG", "Some exception " + e);
                        }
                    }

                    break;

                case REQUEST_IMAGE_CAPTURE_PHONE:

                    if (isProfileImgClicked) {

                        if (profile_signup_image != null) {
                            profile_signup_image.setBackground(null);
                        }

                        if (data != null && data.getExtras() != null) {

                            isCameraCaptured = true;

                            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                            if (imageBitmap != null) {
                                filePath = getImageUri(imageBitmap);
                                profile_signup_image.setImageBitmap(imageBitmap);
                            }

                        }
                    }
                    break;
            }
    }

    /*
     * This method is fetching the absolute path of the image file
     * if you want to upload other kind of files like .pdf, .docx
     * you need to make changes on this method only
     * Rest part will be the same
     * */
    private String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            System.out.println("column_index of selected image is:" + column_index);
            cursor.moveToFirst();
            System.out.println("selected image path is:" + cursor.getString(column_index));
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    public String getCameraRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private boolean Validation() {

        if (edt_signup_fullname.getText().toString().isEmpty()) {
            edt_signup_fullname.setError("Please enter your full name");
            return false;
        }

        if (edt_signup_email.getText().toString().isEmpty()) {
            edt_signup_email.setError("Please enter your email address");
            return false;
        } else {
            edt_signup_email.setError(null);
            if (!Validation.isValidEmail(edt_signup_email.getText().toString(), SignupActivity.this)) {
                edt_signup_email.setError("Please enter valid email address");
                return false;
            }
        }
        if (edt_signup_password.getText().toString().isEmpty()) {
            edt_signup_password.setError("Please enter Password");
            return false;
        } else {
            if (!Validation.validiate2(edt_signup_password.getText().toString(), SignupActivity.this)) {
                edt_signup_password.setError("Enter correct password");
                return false;
            }
        }

        if (edt_signup_Confirmpassword.getText().toString().isEmpty()) {
            edt_signup_Confirmpassword.setError("Please enter Re-Password");
            return false;
        } else {
            if (!Validation.isPassSame(edt_signup_password.getText().toString(), edt_signup_Confirmpassword.getText().toString(), SignupActivity.this)) {
                edt_signup_Confirmpassword.setError("Password not matched");
                return false;
            }
        }

        if (edt_signup_contact.getText().toString().isEmpty()) {
            edt_signup_contact.setError("Please Enter Your Phone Number");
            return false;

        } else {
            if (!edt_signup_contact.getText().toString().matches("^((\\+92)|(0092))-{0,1}\\d{3}-{0,1}\\d{7}$|^\\d{11}$|^\\d{4}-\\d{7}$")) {
                edt_signup_contact.setError("Invalid Phone Number");
                return false;
            }
        }

        if (edt_signup_cnic.getText().toString().isEmpty()) {
            edt_signup_cnic.setError("Please Enter Your Cnic Number");
        } else {
            if (edt_signup_cnic.getText().toString().length() != 13) {
                edt_signup_cnic.setError("Invalid Cnic Number");
                return false;
            }
        }
        if (spinnerSelectedValue.matches("") || spinnerSelectedValue.matches("Select Your Role")) {
            Toast.makeText(this,
                    "warning , Please select role", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (DriverSignupLL.getVisibility() == View.VISIBLE) {
        }

        return true;
    }


    private void SetRoleSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupActivity.this,
                R.layout.support_simple_spinner_dropdown_item, rolearray);
        role_spinner.setAdapter(adapter);
        role_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelectedValue = adapterView.getItemAtPosition(i).toString();
                if (spinnerSelectedValue.matches("Driver")) {
                    rolePos = i;
                    DriverSignupLL.setVisibility(View.VISIBLE);
                    GetAllRoutes();
                    GetAllVehicle();

//                    getCatelist();
                } else {
                    rolePos = i;
                    DriverSignupLL.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void GetAllVehicle() {

        GetVehicleService service = RetrofitClient.getClient().create(GetVehicleService.class);
        Call<JsonObject> call = service.getVehicle();
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
                                vehicleList.add(new Vehicle(
                                        data.getInt("vehicle_id"),
                                        data.getString("vehicle_title")));

                                vehicleNameList.add(data.getString("vehicle_title"));
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SignupActivity.this,
                                    R.layout.support_simple_spinner_dropdown_item,
                                    vehicleNameList);
                            signup_Vehicle_spinner.setAdapter(adapter);
                            signup_Vehicle_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedVehicleID = vehicleList.get(position).getVehicle_id();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this,
                                response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SignupActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    public String getStringAddress(Double lat, Double lng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(SignupActivity.this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            add = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return add;
    }

    private void GetAllCities() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("please wait..");
        progressDialog.setCancelable(false);
        progressDialog.show();


        GetCitiesService service = RetrofitClient.getClient().create(GetCitiesService.class);
        Call<JsonObject> call = service.getCities();
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().getAsJsonObject().toString());

                            JSONArray jsonArray = jsonObject.getJSONArray("records");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject data = jsonArray.getJSONObject(i);

                                citiesList.add(new Cities(data.getInt("id"),
                                        data.getString("city")));

                                cityNameList.add(data.getString("city"));
                            }

                            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(SignupActivity.this,
                                    R.layout.support_simple_spinner_dropdown_item,
                                    cityNameList);

                            City_signup_spinner.setAdapter(cityAdapter);

                            City_signup_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedCityID = citiesList.get(position).getId();
                                    getSelectedCityName = parent.getItemAtPosition(position).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }


                    } else {
                        Toast.makeText(SignupActivity.this,
                                response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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

                            ArrayAdapter<String> routeAdapter = new ArrayAdapter<String>(SignupActivity.this,
                                    R.layout.support_simple_spinner_dropdown_item, routeNameList);
                            signup_Route_spinner.setAdapter(routeAdapter);
                            signup_Route_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    selectedRouteID = routesList.get(position).getRoute_id();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SignupActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void CreateUserAccount() {
        user = new User();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        if (radioMale.isChecked()) {
            radio_check = 1;
        } else if (radioFemale.isChecked()) {
            radio_check = 2;
        }

        if (filePath != null) {
            //creating request body for file
            if (isCameraCaptured) {
                file = new File(getCameraRealPathFromURI(filePath));
            } else if (isGalleryCaptured) {
                file = new File(getRealPathFromURI(filePath));
            }
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        //creating request body for name
        RequestBody _name = RequestBody.create(MediaType.parse("text/plain"), edt_signup_fullname.getText().toString());

        //creating request body for email
        RequestBody _email = RequestBody.create(MediaType.parse("text/plain"), edt_signup_email.getText().toString());

        //creating request body for password
        RequestBody _password = RequestBody.create(MediaType.parse("text/plain"), edt_signup_password.getText().toString());

        //creating request body for cnic
        RequestBody _cnic = RequestBody.create(MediaType.parse("text/plain"), edt_signup_cnic.getText().toString());

        //creating request body for contact1
        RequestBody _contact1 = RequestBody.create(MediaType.parse("text/plain"), edt_signup_contact.getText().toString());

        //creating request body for address
        RequestBody _address = RequestBody.create(MediaType.parse("text/plain"), getSelectedCityName);

        //creating request body for province_id
        RequestBody _fk_city_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedCityID));

        //creating request body for city_id
        RequestBody _latitude = RequestBody.create(MediaType.parse("text/plain"), latitude);

        //creating request body for city_id
        RequestBody _longitude = RequestBody.create(MediaType.parse("text/plain"), longitude);

        //creating request body for city_id
        RequestBody _gender = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(radio_check));

        MultipartBody.Part body;
        body = MultipartBody.Part.createFormData("user_profileImage", file.getName(), requestFile);


        CreateUserAccountService service = RetrofitClient.getClient().create(CreateUserAccountService.class);
        Call<User> call = service.create_Account(_name, _email, _password,
                _cnic, _contact1, _address, _fk_city_id, _latitude, _longitude, _gender, body);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    user = response.body();
                    if (!user.isError()) {
                        Toast.makeText(SignupActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(SignupActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void CreateDriverAccount() {
        user = new User();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        if (radioMale.isChecked()) {
            radio_check = 1;
        } else if (radioFemale.isChecked()) {
            radio_check = 2;
        }

        if (filePath != null) {
            //creating request body for file
            if (isCameraCaptured) {
                file = new File(getCameraRealPathFromURI(filePath));
            } else if (isGalleryCaptured) {
                file = new File(getRealPathFromURI(filePath));
            }
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);

        //creating request body for name
        RequestBody _name = RequestBody.create(MediaType.parse("text/plain"), edt_signup_fullname.getText().toString());

        //creating request body for email
        RequestBody _email = RequestBody.create(MediaType.parse("text/plain"), edt_signup_email.getText().toString());

        //creating request body for password
        RequestBody _password = RequestBody.create(MediaType.parse("text/plain"), edt_signup_password.getText().toString());

        //creating request body for cnic
        RequestBody _cnic = RequestBody.create(MediaType.parse("text/plain"), edt_signup_cnic.getText().toString());

        //creating request body for contact1
        RequestBody _contact1 = RequestBody.create(MediaType.parse("text/plain"), edt_signup_contact.getText().toString());

        //creating request body for address
        RequestBody _address = RequestBody.create(MediaType.parse("text/plain"), START_POINT_ADD);

        //creating request body for province_id
        RequestBody _fk_city_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedCityID));

        //creating request body for city_id
        RequestBody _latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(START_LAT_ADD));

        //creating request body for city_id
        RequestBody _longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(START_LNG_ADD));

        //creating request body for city_id
        RequestBody end_latitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(END_LAT_ADD));

        //creating request body for city_id
        RequestBody end_longitude = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(END_LNG_ADD));


        //creating request body for city_id
        RequestBody _gender = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(radio_check));

        //creating request body for city_id
        RequestBody _route = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedRouteID));

        RequestBody _vehicle = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(selectedVehicleID));


        MultipartBody.Part body;
        body = MultipartBody.Part.createFormData("user_profileImage", file.getName(), requestFile);

        DriverSignupService service = RetrofitClient.getClient().create(DriverSignupService.class);
        Call<User> call = service.user_account(_name, _email, _password,
                _cnic, _contact1, _address, _fk_city_id, _latitude, _longitude,
                end_latitude, end_longitude, _gender, _route, _vehicle, body);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    user = response.body();
                    if (!user.isError()) {
                        Toast.makeText(SignupActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        finishAffinity();
                    } else {
                        Toast.makeText(SignupActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SignupActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SignupActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}