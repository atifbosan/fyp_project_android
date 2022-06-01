package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface InsertOtpService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.INSERT_OTP_URL)
    Call<User> send_otp(
            @Field("user_email") String user_email,
            @Field("otp_account") String otp_account);

}
