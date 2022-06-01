package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.util.EndPoint;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface GetVehicleService {

    @Headers({"Accept: application/json"})
    @GET(EndPoint.GET_VEHICLE_URL)
    Call<JsonObject> getVehicle();
}
