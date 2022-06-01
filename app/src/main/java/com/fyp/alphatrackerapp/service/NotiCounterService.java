package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.Notification;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotiCounterService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.GET_NOTI_COUNTER_URL)
    Call<Notification> getCounter(
            @Field("noti_forUserID") int noti_forUserID,
            @Field("noti_for") String noti_for);
}
