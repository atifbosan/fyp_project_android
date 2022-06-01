package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.util.EndPoint;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetAnnounceNotificationService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.GET_ANNOUNCE_NOTI_URL)
    Call<JsonObject> getNotifications(
            @Field("noti_forUserID") int noti_forUserID,
            @Field("noti_for") String noti_for);
}
