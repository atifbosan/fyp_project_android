package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ForgotPassService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.FORGOT_PASSWORD_URL)
    Call<User> forgot_pass(
            @Field("user_email") String user_email);
}
