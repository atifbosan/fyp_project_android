package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.util.EndPoint;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetDriversService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.GET_DRIVERS_URL)
    Call<JsonObject> getDrivers(
            @Field("user_routeID") int user_routeID);
}
