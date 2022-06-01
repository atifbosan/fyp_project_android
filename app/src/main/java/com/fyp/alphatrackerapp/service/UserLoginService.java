package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserLoginService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.LOGIN_USER_URL)
    Call<User> user_Login(
            @Field("user_email") String user_email,
            @Field("user_password") String user_password);
}
