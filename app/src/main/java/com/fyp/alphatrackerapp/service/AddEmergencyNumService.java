package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.EmergencyNumber;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AddEmergencyNumService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.ADD_EMERGENCY_NUMBER_URL)
    Call<EmergencyNumber> add_numer(
            @Field("en_name") String en_name,
            @Field("en_number") String en_number,
            @Field("fk_user_id") int fk_user_id);
}
