package com.fyp.alphatrackerapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("user_id")
    @Expose
    private int user_id;

    @SerializedName("user_fullName")
    @Expose
    private String user_fullName;

    @SerializedName("user_email")
    @Expose
    private String user_email;

    @SerializedName("user_password")
    @Expose
    private String user_password;

    @SerializedName("user_cnic")
    @Expose
    private String user_cnic;

    @SerializedName("user_contactNo")
    @Expose
    private String user_contactNo;

    @SerializedName("user_address")
    @Expose
    private String user_address;

    @SerializedName("user_cityID")
    @Expose
    private int user_cityID;

    @SerializedName("user_lat")
    @Expose
    private double user_lat;

    @SerializedName("user_lng")
    @Expose
    private double user_lng;

    @SerializedName("user_end_lat")
    @Expose
    private double user_end_lat;

    @SerializedName("user_end_lng")
    @Expose
    private double user_end_lng;

    @SerializedName("user_isOnline")
    @Expose
    private String user_isOnline;

    @SerializedName("user_gender")
    @Expose
    private String user_gender;

    @SerializedName("user_type")
    @Expose
    private String user_type;

    @SerializedName("user_status")
    @Expose
    private String user_status;

    @SerializedName("user_profileImage")
    @Expose
    private String user_profileImage;

    @SerializedName("user_routeID")
    @Expose
    private int user_routeID;

    @SerializedName("user_vehicleID")
    @Expose
    private int user_vehicleID;


    @SerializedName("tracking_id")
    @Expose
    private int tracking_id;

    @SerializedName("tracking_lat")
    @Expose
    private double tracking_lat;

    @SerializedName("tracking_lng")
    @Expose
    private double tracking_lng;

    @SerializedName("vehicle_title")
    @Expose
    private String vehicle_title;

    @SerializedName("otp_account")
    @Expose
    private String otp_account;


    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    public User() {
    }

    public User(int user_id, String user_fullName, String user_cnic, String user_contactNo, String user_gender,
                String user_profileImage, int tracking_id, double tracking_lat,
                double tracking_lng, String vehicle_title) {
        this.user_id = user_id;
        this.user_fullName = user_fullName;
        this.user_cnic = user_cnic;
        this.user_contactNo = user_contactNo;
        this.user_gender = user_gender;
        this.user_profileImage = user_profileImage;
        this.tracking_id = tracking_id;
        this.tracking_lat = tracking_lat;
        this.tracking_lng = tracking_lng;
        this.vehicle_title=vehicle_title;
    }

    public String getVehicle_title() {
        return vehicle_title;
    }

    public void setVehicle_title(String vehicle_title) {
        this.vehicle_title = vehicle_title;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUser_fullName() {
        return user_fullName;
    }

    public void setUser_fullName(String user_fullName) {
        this.user_fullName = user_fullName;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_cnic() {
        return user_cnic;
    }

    public void setUser_cnic(String user_cnic) {
        this.user_cnic = user_cnic;
    }

    public String getUser_contactNo() {
        return user_contactNo;
    }

    public void setUser_contactNo(String user_contactNo) {
        this.user_contactNo = user_contactNo;
    }

    public String getUser_address() {
        return user_address;
    }

    public void setUser_address(String user_address) {
        this.user_address = user_address;
    }

    public int getUser_cityID() {
        return user_cityID;
    }

    public void setUser_cityID(int user_cityID) {
        this.user_cityID = user_cityID;
    }

    public double getUser_lat() {
        return user_lat;
    }

    public void setUser_lat(double user_lat) {
        this.user_lat = user_lat;
    }

    public double getUser_lng() {
        return user_lng;
    }

    public void setUser_lng(double user_lng) {
        this.user_lng = user_lng;
    }

    public double getUser_end_lat() {
        return user_end_lat;
    }

    public void setUser_end_lat(double user_end_lat) {
        this.user_end_lat = user_end_lat;
    }

    public double getUser_end_lng() {
        return user_end_lng;
    }

    public void setUser_end_lng(double user_end_lng) {
        this.user_end_lng = user_end_lng;
    }

    public String getUser_isOnline() {
        return user_isOnline;
    }

    public void setUser_isOnline(String user_isOnline) {
        this.user_isOnline = user_isOnline;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    public String getUser_profileImage() {
        return user_profileImage;
    }

    public void setUser_profileImage(String user_profileImage) {
        this.user_profileImage = user_profileImage;
    }

    public int getUser_routeID() {
        return user_routeID;
    }

    public void setUser_routeID(int user_routeID) {
        this.user_routeID = user_routeID;
    }

    public int getUser_vehicleID() {
        return user_vehicleID;
    }

    public void setUser_vehicleID(int user_vehicleID) {
        this.user_vehicleID = user_vehicleID;
    }

    public String getOtp_account() {
        return otp_account;
    }

    public void setOtp_account(String otp_account) {
        this.otp_account = otp_account;
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
}
