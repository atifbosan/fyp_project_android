package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.InsertOtpService;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPassActivity extends AppCompatActivity {

    TextView tv_forgotPass_enBack;
    LinearLayout ForgotPassLL, OTPLL;
    EditText edt_forgot_email, edt_forgot_otp;
    Button btn_forgot_pass, btn_forgot_otp;
    ProgressDialog progressDialog;
    int min = 65;
    int max = 80;
    User user;
    Random r = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        tv_forgotPass_enBack = findViewById(R.id.tv_forgotPass_enBack);
        ForgotPassLL = findViewById(R.id.ForgotPassLL);
        edt_forgot_email = findViewById(R.id.edt_forgot_email);
        btn_forgot_pass = findViewById(R.id.btn_forgot_pass);
        edt_forgot_otp = findViewById(R.id.edt_forgot_otp);
        OTPLL = findViewById(R.id.OTPLL);
        btn_forgot_otp = findViewById(R.id.btn_forgot_otp);

        tv_forgotPass_enBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),
                        LoginActivity.class));
                finish();
            }
        });

        btn_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassLL.setVisibility(View.GONE);
                OTPLL.setVisibility(View.VISIBLE);
                SendOTP();
            }
        });

    }

    private void SendOTP() {
        user = new User();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait...");
        progressDialog.show();
        int i1 = r.nextInt(max - min + 1) + min;

        InsertOtpService service = RetrofitClient.getClient().create(InsertOtpService.class);
        Call<User> call = service.send_otp(edt_forgot_email.getText().toString(), String.valueOf(i1));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    user = response.body();
                    if (!user.isError()) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage("03026444343",
                                    null, "Your OTP code for forgot Password: " +
                                            user.getOtp_account(), null, null);
                            Toast.makeText(getApplicationContext(), "SMS sent.",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                        }

                        Toast.makeText(ForgotPassActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPassActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ForgotPassActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ForgotPassActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}