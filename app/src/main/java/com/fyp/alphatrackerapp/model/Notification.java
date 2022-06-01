package com.fyp.alphatrackerapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification {

    @SerializedName("noti_id")
    @Expose
    private int noti_id;

    @SerializedName("noti_title")
    @Expose
    private String noti_title;

    @SerializedName("noti_type")
    @Expose
    private String noti_type;

    @SerializedName("noti_for")
    @Expose
    private String noti_for;

    @SerializedName("noti_fk_id")
    @Expose
    private int noti_fk_id;

    @SerializedName("noti_forUserID")
    @Expose
    private int noti_forUserID;

    @SerializedName("noti_status")
    @Expose
    private String noti_status;

    @SerializedName("createdDate")
    @Expose
    private String createdDate;

    @SerializedName("count")
    @Expose
    private int count;

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    public Notification() {
    }

    public Notification(int noti_id, String noti_title, int noti_fk_id, String createdDate) {
        this.noti_id = noti_id;
        this.noti_title = noti_title;
        this.noti_fk_id = noti_fk_id;
        this.createdDate = createdDate;
    }

    public int getNoti_id() {
        return noti_id;
    }

    public void setNoti_id(int noti_id) {
        this.noti_id = noti_id;
    }

    public String getNoti_title() {
        return noti_title;
    }

    public void setNoti_title(String noti_title) {
        this.noti_title = noti_title;
    }

    public String getNoti_type() {
        return noti_type;
    }

    public void setNoti_type(String noti_type) {
        this.noti_type = noti_type;
    }

    public String getNoti_for() {
        return noti_for;
    }

    public void setNoti_for(String noti_for) {
        this.noti_for = noti_for;
    }

    public int getNoti_fk_id() {
        return noti_fk_id;
    }

    public void setNoti_fk_id(int noti_fk_id) {
        this.noti_fk_id = noti_fk_id;
    }

    public int getNoti_forUserID() {
        return noti_forUserID;
    }

    public void setNoti_forUserID(int noti_forUserID) {
        this.noti_forUserID = noti_forUserID;
    }

    public String getNoti_status() {
        return noti_status;
    }

    public void setNoti_status(String noti_status) {
        this.noti_status = noti_status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
