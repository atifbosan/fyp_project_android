package com.fyp.alphatrackerapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Vehicle {

    @SerializedName("vehicle_id")
    @Expose
    private int vehicle_id;

    @SerializedName("vehicle_title")
    @Expose
    private String vehicle_title;

    @SerializedName("vehicle_status")
    @Expose
    private String vehicle_status;

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    public Vehicle() {
    }

    public Vehicle(int vehicle_id, String vehicle_title) {
        this.vehicle_id = vehicle_id;
        this.vehicle_title = vehicle_title;
    }

    public int getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(int vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getVehicle_title() {
        return vehicle_title;
    }

    public void setVehicle_title(String vehicle_title) {
        this.vehicle_title = vehicle_title;
    }

    public String getVehicle_status() {
        return vehicle_status;
    }

    public void setVehicle_status(String vehicle_status) {
        this.vehicle_status = vehicle_status;
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
