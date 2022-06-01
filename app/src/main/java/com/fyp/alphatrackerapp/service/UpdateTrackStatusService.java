package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.Tracking;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UpdateTrackStatusService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.UPDATE_TRACK_STATUS_URL)
    Call<Tracking> updateStatus(
            @Field("tracking_status") String tracking_status,
            @Field("tracking_fk_user_id") int tracking_fk_user_id);
}
