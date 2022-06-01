package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.Tracking;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface TrackingService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.INSERT_TRACKING_URL)
    Call<Tracking> insertTrack(
            @Field("tracking_lat") double tracking_lat,
            @Field("tracking_lng") double tracking_lng,
            @Field("tracking_fk_user_id") int tracking_fk_user_id);

}
