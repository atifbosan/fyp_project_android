package com.fyp.alphatrackerapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmergencyNumber {

    @SerializedName("en_id")
    @Expose
    private int en_id;

    @SerializedName("en_name")
    @Expose
    private String en_name;

    @SerializedName("en_number")
    @Expose
    private String en_number;

    @SerializedName("fk_user_id")
    @Expose
    private int fk_user_id;

    @SerializedName("code")
    @Expose
    private int code;

    @SerializedName("error")
    @Expose
    private boolean error;

    @SerializedName("message")
    @Expose
    private String message;

    public EmergencyNumber() {
    }

    public EmergencyNumber(int en_id, String en_name, String en_number) {
        this.en_id = en_id;
        this.en_name = en_name;
        this.en_number = en_number;
    }

    public int getEn_id() {
        return en_id;
    }

    public void setEn_id(int en_id) {
        this.en_id = en_id;
    }

    public String getEn_name() {
        return en_name;
    }

    public void setEn_name(String en_name) {
        this.en_name = en_name;
    }

    public String getEn_number() {
        return en_number;
    }

    public void setEn_number(String en_number) {
        this.en_number = en_number;
    }

    public int getFk_user_id() {
        return fk_user_id;
    }

    public void setFk_user_id(int fk_user_id) {
        this.fk_user_id = fk_user_id;
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
