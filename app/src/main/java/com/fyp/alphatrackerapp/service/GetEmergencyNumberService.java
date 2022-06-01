package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.util.EndPoint;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetEmergencyNumberService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.GET_EMERGENCY_NUMBER_URL)
    Call<JsonObject> getEmergencyNumbers(
            @Field("fk_user_id") int fk_user_id);
}
