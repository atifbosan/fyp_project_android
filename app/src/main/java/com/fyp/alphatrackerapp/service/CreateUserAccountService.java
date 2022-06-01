package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.User;
import com.fyp.alphatrackerapp.util.EndPoint;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface CreateUserAccountService {

    @Headers({"Accept: application/json"})
    @Multipart
    @POST(EndPoint.CREATE_USER_ACCOUNT_URL)
    Call<User> create_Account(
            @Part("user_fullName") RequestBody user_fullName,
            @Part("user_email") RequestBody user_email,
            @Part("user_password") RequestBody user_password,
            @Part("user_cnic") RequestBody user_cnic,
            @Part("user_contactNo") RequestBody user_contactNo,
            @Part("user_address") RequestBody user_address,
            @Part("user_cityID") RequestBody user_cityID,
            @Part("user_lat") RequestBody user_lat,
            @Part("user_lng") RequestBody user_lng,
            @Part("user_gender") RequestBody user_gender,
            @Part MultipartBody.Part user_profileImage);
}
