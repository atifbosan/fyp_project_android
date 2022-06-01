package com.fyp.alphatrackerapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Tracking {

    @SerializedName("tracking_id")
    @Expose
    private int tracking_id;

    @SerializedName("tracking_lat")
    @Expose
    private double tracking_lat;

    @SerializedName("tracking_lng")
    @Expose
    private double tracking_lng;

    @SerializedName("tracking_fk_user_id")
    @Expose
    private int tracking_fk_user_id;

    @SerializedName("tracking_status")
    @Expose
    private String tracking_status;

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    public Tracking() {
    }

    public int getTracking_id() {
        return tracking_id;
    }

    public void setTracking_id(int tracking_id) {
        this.tracking_id = tracking_id;
    }

    public double getTracking_lat() {
        return tracking_lat;
    }

    public void setTracking_lat(double tracking_lat) {
        this.tracking_lat = tracking_lat;
    }

    public double getTracking_lng() {
        return tracking_lng;
    }

    public void setTracking_lng(double tracking_lng) {
        this.tracking_lng = tracking_lng;
    }

    public int getTracking_fk_user_id() {
        return tracking_fk_user_id;
    }

    public void setTracking_fk_user_id(int tracking_fk_user_id) {
        this.tracking_fk_user_id = tracking_fk_user_id;
    }

    public String getTracking_status() {
        return tracking_status;
    }

    public void setTracking_status(String tracking_status) {
        this.tracking_status = tracking_status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
