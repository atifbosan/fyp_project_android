package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.util.EndPoint;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface GetCitiesService {

    @Headers({"Accept: application/json"})
    @GET(EndPoint.GET_CITIES_URL)
    Call<JsonObject> getCities();

}
