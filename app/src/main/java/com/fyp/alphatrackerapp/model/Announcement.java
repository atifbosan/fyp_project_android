package com.fyp.alphatrackerapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Announcement {

    @SerializedName("announcement_id")
    @Expose
    private int announcement_id;

    @SerializedName("announcement_title")
    @Expose
    private String announcement_title;

    @SerializedName("announcement_description")
    @Expose
    private String announcement_description;

    @SerializedName("announcement_for")
    @Expose
    private String announcement_for;

    @SerializedName("announcement_status")
    @Expose
    private String announcement_status;

    @SerializedName("createdDate")
    @Expose
    private String createdDate;

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    public Announcement() {
    }

    public int getAnnouncement_id() {
        return announcement_id;
    }

    public void setAnnouncement_id(int announcement_id) {
        this.announcement_id = announcement_id;
    }

    public String getAnnouncement_title() {
        return announcement_title;
    }

    public void setAnnouncement_title(String announcement_title) {
        this.announcement_title = announcement_title;
    }

    public String getAnnouncement_description() {
        return announcement_description;
    }

    public void setAnnouncement_description(String announcement_description) {
        this.announcement_description = announcement_description;
    }

    public String getAnnouncement_for() {
        return announcement_for;
    }

    public void setAnnouncement_for(String announcement_for) {
        this.announcement_for = announcement_for;
    }

    public String getAnnouncement_status() {
        return announcement_status;
    }

    public void setAnnouncement_status(String announcement_status) {
        this.announcement_status = announcement_status;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
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

