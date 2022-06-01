package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fyp.alphatrackerapp.manager.NetworkManager;
import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.ForgotPassService;
import com.fyp.alphatrackerapp.util.Validation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText edt_forgot_email;
    Button btn_forgot_password;
    ProgressDialog progressDialog;
    User user;
    NetworkManager manager = new NetworkManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        edt_forgot_email = findViewById(R.id.edt_forgot_email);
        btn_forgot_password = findViewById(R.id.btn_forgot_password);

        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Validation()) {
                    if (manager.checkInternetConnection(ForgotPasswordActivity.this)) {
                        UpdatePass();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private boolean Validation() {
        if (edt_forgot_email.getText().toString().isEmpty()) {
            edt_forgot_email.setError("Please enter your email address");
            return false;
        } else {
            edt_forgot_email.setError(null);
            if (!Validation.isValidEmail(edt_forgot_email.getText().toString(), ForgotPasswordActivity.this)) {
                edt_forgot_email.setError("Please enter valid email address");
                return false;
            }

        }
        return true;
    }

    private void UpdatePass() {
        user = new User();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        ForgotPassService service = RetrofitClient.getClient().create(ForgotPassService.class);
        Call<User> call = service.forgot_pass(edt_forgot_email.getText().toString());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    user = response.body();
                    if (user != null) {
                        Toast.makeText(ForgotPasswordActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),
                                LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPasswordActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
/*
                Toast.makeText(ForgotPasswordActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
*/
            }
        });

    }

}