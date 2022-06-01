package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ChangePasswordService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.CHANGE_PASSWORD_URL)
    Call<User> updatePassword(
            @Field("current_pass") String current_pass,
            @Field("new_password") String new_password,
            @Field("user_id") int user_id);
}
