package com.fyp.alphatrackerapp.service;

import com.fyp.alphatrackerapp.model.Announcement;
import com.fyp.alphatrackerapp.util.EndPoint;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface GetAnnounDetService {

    @Headers({"Accept: application/json"})
    @FormUrlEncoded
    @POST(EndPoint.GET_ANNOUNCE_DET_URL)
    Call<Announcement> accounce_det(
            @Field("announcement_id") int announcement_id);
}
