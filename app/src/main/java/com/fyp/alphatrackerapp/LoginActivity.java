package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.manager.NetworkManager;
import com.fyp.alphatrackerapp.manager.SharedPrefManager;
import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.UserLoginService;
import com.fyp.alphatrackerapp.util.Validation;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edt_login_email, edt_login_password;
    Spinner StatusSpinnerLoginActivity;
    Button btn_login;
    TextView tv_signup, TextviewForgotPasswordLoginActivity;
    String loginAs[] = {"Login As", "User", "Driver"};
    int loginCheck = 0;
    User user;
    ProgressDialog progressDialog;
    NetworkManager manager = new NetworkManager();

    SharedPrefManager sharedPrefManager = new SharedPrefManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_login_email = findViewById(R.id.edt_login_email);
        edt_login_password = findViewById(R.id.edt_login_password);
        StatusSpinnerLoginActivity = findViewById(R.id.StatusSpinnerLoginActivity);
        btn_login = findViewById(R.id.btn_login);
        tv_signup = findViewById(R.id.tv_signup);
        TextviewForgotPasswordLoginActivity = findViewById(R.id.TextviewForgotPasswordLoginActivity);
        SpinnerWork();

        Dexter.withActivity(LoginActivity.this).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        Toast.makeText(LoginActivity.this,
                                "Permission Granted", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation()) {
                    if (manager.checkInternetConnection(LoginActivity.this)) {
                        LoginUser();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tv_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        SignupActivity.class));
            }
        });

        TextviewForgotPasswordLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        ForgotPasswordActivity.class));
            }
        });

    }

    /**
     * function that validate fields
     */
    private boolean Validation() {

        if (edt_login_email.getText().toString().isEmpty()) {
            edt_login_email.setError("Please enter your email address");
            return false;
        } else {
            edt_login_email.setError(null);
            if (!Validation.isValidEmail(edt_login_email.getText().toString(), LoginActivity.this)) {
                edt_login_email.setError("Please enter valid email address");
                return false;
            }
        }
        if (edt_login_password.getText().toString().isEmpty()) {
            edt_login_password.setError("Please enter Password");
            return false;
        }

        return true;
    }

    private void SpinnerWork() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LoginActivity.this,
                R.layout.support_simple_spinner_dropdown_item, loginAs);
        StatusSpinnerLoginActivity.setAdapter(adapter);
        StatusSpinnerLoginActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    loginCheck = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void LoginUser() {
        user = new User();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        UserLoginService service = RetrofitClient.getClient().create(UserLoginService.class);
        Call<User> call = service.user_Login(edt_login_email.getText().toString().trim(),
                edt_login_password.getText().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    user = response.body();
                    if (!user.isError()) {
                        if (user.getUser_status().equals("A")) {
                            sharedPrefManager.saveUserData(LoginActivity.this,
                                    String.valueOf(user.getUser_id()),
                                    user.getUser_fullName(),
                                    user.getUser_email(),
                                    user.getUser_cnic(),
                                    user.getUser_contactNo(),
                                    user.getUser_type(),
                                    user.getUser_status(),
                                    user.getUser_profileImage());
                            Toast.makeText(LoginActivity.this,
                                    user.getMessage(), Toast.LENGTH_SHORT).show();
                            if (user.getUser_type().equals("U")) {
                                sharedPrefManager.loginPreference(LoginActivity.this, 1);
                                startActivity(new Intent(getApplicationContext(),
                                        UserHomeActivity.class));
                                finishAffinity();
                            } else if (user.getUser_type().equals("D")) {
                                sharedPrefManager.loginPreference(LoginActivity.this, 2);
                                startActivity(new Intent(getApplicationContext(),
                                        DriverHomeActivity.class));
                                finishAffinity();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    "Admin not approved your profile yet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}