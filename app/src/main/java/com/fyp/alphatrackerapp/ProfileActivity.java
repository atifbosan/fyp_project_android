package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fyp.alphatrackerapp.manager.SharedPrefManager;
import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.UpdateProfileService;
import com.fyp.alphatrackerapp.util.EndPoint;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    TextView tv_profile_enBack;
    int pref;
    SharedPrefManager sharedPrefManager = new SharedPrefManager();
    CircleImageView profile_img;
    EditText edt_prof_name, edt_prof_email, edt_profile_phone, edt_profile_cnic;
    Button btn_profile, btn_Update_profile;
    List<String> userData=new ArrayList<>();
    int UserID;
    ProgressDialog progressDialog;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        tv_profile_enBack = findViewById(R.id.tv_profile_enBack);
        pref = sharedPrefManager.getLoginPreference(ProfileActivity.this);
        profile_img = findViewById(R.id.profile_img);
        edt_prof_name = findViewById(R.id.edt_prof_name);
        edt_prof_email = findViewById(R.id.edt_prof_email);
        edt_profile_phone = findViewById(R.id.edt_profile_phone);
        edt_profile_cnic = findViewById(R.id.edt_profile_cnic);
        btn_profile=findViewById(R.id.btn_profile);
        btn_Update_profile=findViewById(R.id.btn_Update_profile);
        userData=sharedPrefManager.getUserData(ProfileActivity.this);
        UserID= Integer.parseInt(sharedPrefManager.getID(ProfileActivity.this));
        Glide.with(ProfileActivity.this).load(EndPoint.IMAGE_URL + userData.get(7)).into(profile_img);
        edt_prof_name.setText(userData.get(1));
        edt_prof_email.setText(userData.get(2));
        edt_profile_phone.setText(userData.get(3));
        edt_profile_cnic.setText(userData.get(6));

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_prof_name.setFocusable(true);
                edt_prof_name.setFocusableInTouchMode(true);

                //edt_prof_email.setFocusable(true);
                //edt_prof_email.setFocusableInTouchMode(true);

                edt_profile_phone.setFocusable(true);
                edt_profile_phone.setFocusableInTouchMode(true);

                edt_profile_cnic.setFocusable(true);
                edt_profile_cnic.setFocusableInTouchMode(true);
                btn_profile.setVisibility(View.GONE);
                btn_Update_profile.setVisibility(View.VISIBLE);
            }
        });

        btn_Update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_prof_name.setFocusable(false);
                edt_prof_email.setFocusable(false);
                edt_profile_phone.setFocusable(false);
                edt_profile_cnic.setFocusable(false);
                btn_Update_profile.setVisibility(View.GONE);
                btn_profile.setVisibility(View.VISIBLE);
                UpdateProfile();
            }
        });


        tv_profile_enBack.setOnClickListener(new View.OnClickListener() {
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

    private void UpdateProfile(){
        user=new User();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        UpdateProfileService service= RetrofitClient.getClient().create(UpdateProfileService.class);
        Call<User> call=service.updateProfile(UserID,edt_prof_name.getText().toString(),
                edt_prof_email.getText().toString(),
                edt_profile_cnic.getText().toString(),
                edt_profile_phone.getText().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    progressDialog.dismiss();
                    user=response.body();

                    if(!user.isError()){
                        sharedPrefManager.saveUpdatedUserData(ProfileActivity.this,
                                user.getUser_fullName(),
                                user.getUser_email(),
                                user.getUser_cnic(),
                                user.getUser_contactNo(),
                                user.getUser_profileImage());
                        Toast.makeText(ProfileActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                        if (pref == 1) {
                            startActivity(new Intent(getApplicationContext(),
                                    UserHomeActivity.class));
                            finish();
                        } else if (pref == 2) {
                            startActivity(new Intent(getApplicationContext(),
                                    DriverHomeActivity.class));
                            finish();
                        }

                    }else{
                        Toast.makeText(ProfileActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}