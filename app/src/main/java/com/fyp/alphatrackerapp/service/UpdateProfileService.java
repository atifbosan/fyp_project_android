package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UpdateProfileService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.UPDATE_PROFILE_URL)
    Call<User> updateProfile(
            @Field("user_id") int user_id,
            @Field("user_fullName") String user_fullName,
            @Field("user_email") String user_email,
            @Field("user_cnic") String user_cnic,
            @Field("user_contactNo") String user_contactNo);
}
