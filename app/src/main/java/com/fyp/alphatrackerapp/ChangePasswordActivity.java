package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.manager.SharedPrefManager;
import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.ChangePasswordService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText edt_current_pass, edt_new_pass, edt_confirm_pass;
    Button btn_changePass_submit;
    TextView tv_changePass_enBack;
    ProgressDialog progressDialog;
    int UserID, pref;
    User user;
    SharedPrefManager sharedPrefManager = new SharedPrefManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        edt_current_pass = findViewById(R.id.edt_current_pass);
        edt_new_pass = findViewById(R.id.edt_new_pass);
        edt_confirm_pass = findViewById(R.id.edt_confirm_pass);
        tv_changePass_enBack=findViewById(R.id.tv_changePass_enBack);
        btn_changePass_submit = findViewById(R.id.btn_changePass_submit);

        btn_changePass_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation()) {
                    ChangePassword();
                }
            }
        });

        UserID = Integer.parseInt(sharedPrefManager.getID(ChangePasswordActivity.this));
        pref=sharedPrefManager.getLoginPreference(ChangePasswordActivity.this);

        tv_changePass_enBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref==1){
                    startActivity(new Intent(getApplicationContext(),
                            UserHomeActivity.class));
                    finish();
                }else if(pref==2){
                    startActivity(new Intent(getApplicationContext(),
                            DriverHomeActivity.class));
                    finish();
                }
            }
        });

    }

    private boolean Validation() {
        if (edt_current_pass.getText().toString().isEmpty()) {
            edt_current_pass.setError("fill this field");
            return false;
        }

        if (edt_new_pass.getText().toString().isEmpty()) {
            edt_new_pass.setError("fill this field");
            return false;
        }

        if (edt_confirm_pass.getText().toString().isEmpty()) {
            edt_confirm_pass.setError("fill this field");
            return false;
        }

        if (!edt_confirm_pass.equals(edt_new_pass)) {
            edt_confirm_pass.setError("password not matched");
            return false;
        }
        return true;
    }

    private void ChangePassword() {
        user=new User();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait.....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        ChangePasswordService service = RetrofitClient.getClient().create(ChangePasswordService.class);
        Call<User> call = service.updatePassword(edt_current_pass.getText().toString(),
                edt_new_pass.getText().toString(),UserID);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()){
                    progressDialog.dismiss();
                    user=response.body();
                    if(!user.isError()){
                        Toast.makeText(ChangePasswordActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        finishAffinity();
                    }else{
                        Toast.makeText(ChangePasswordActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }else{
                    progressDialog.dismiss();
                    Toast.makeText(ChangePasswordActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ChangePasswordActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(pref==1){
            startActivity(new Intent(getApplicationContext(),
                    UserHomeActivity.class));
            finish();
        }else if(pref==2){
            startActivity(new Intent(getApplicationContext(),
                    DriverHomeActivity.class));
            finish();
        }

    }
}