package com.fyp.alphatrackerapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Routes {

    @SerializedName("route_id")
    @Expose
    private int route_id;

    @SerializedName("route_title")
    @Expose
    private String route_title;

    @SerializedName("route_status")
    @Expose
    private String route_status;

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    public Routes() {
    }

    public Routes(int route_id, String route_title) {
        this.route_id = route_id;
        this.route_title = route_title;
    }

    public int getRoute_id() {
        return route_id;
    }

    public void setRoute_id(int route_id) {
        this.route_id = route_id;
    }

    public String getRoute_title() {
        return route_title;
    }

    public void setRoute_title(String route_title) {
        this.route_title = route_title;
    }

    public String getRoute_status() {
        return route_status;
    }

    public void setRoute_status(String route_status) {
        this.route_status = route_status;
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
