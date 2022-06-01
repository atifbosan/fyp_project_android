package com.fyp.alphatrackerapp.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPrefManager {
    private Activity context;
    private final String PREF_FILE = "alpha_tracker";
    private final String LOGIN_PREF = "login_pref";
    private final String ID = "id";
    private final String NAME = "name";
    private final String EMAIL = "email";
    private final String CNIC = "cnic";
    private final String MOBILE_NUMBER = "phone";
    private final String TYPE = "type";
    private final String STATUS = "status";
    private final String LOC_ON = "loc_on";
    private final String PROFILE_IMG = "profile_img";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * function for saving teacher screen status
     *
     * @param _context     Activity
     * @param login_status 1 or 0
     */
    public void loginPreference(Activity _context, int login_status) {
        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(LOGIN_PREF, login_status);
        editor.apply();
        editor.commit();
    }


    public String getID(Activity _context) {
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String getId = sharedPreferences.getString(ID, "");
        return getId;
    }


    public String getLogStatus(Activity _context) {
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String getstatus = sharedPreferences.getString(STATUS, "");
        return getstatus;
    }


    public void saveStatus(Activity _context, String status) {
        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(STATUS, status);
        editor.apply();
        editor.commit();
    }

    public void saveLOC(Activity _context, String loc) {
        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(LOC_ON, loc);
        editor.apply();
        editor.commit();
    }

    public String getLOC(Activity _context) {
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String getstatus = sharedPreferences.getString(LOC_ON, "");
        return getstatus;
    }



    public void saveImage(Activity _context, String image) {
        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(PROFILE_IMG, image);
        editor.apply();
        editor.commit();
    }


    public void saveMobile(Activity activity, String mob) {
        context = activity;
        sharedPreferences = activity.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(MOBILE_NUMBER, mob);
        editor.apply();
        editor.commit();
    }


    public String getEmail(Activity _context) {
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String getEmail = sharedPreferences.getString(EMAIL, "");
        return getEmail;
    }


    /**
     * @param _context Activity
     * @return 1 or 0
     */
    public int getLoginPreference(Activity _context) {
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        int getActivityPrefConstant = sharedPreferences.getInt(LOGIN_PREF, 0);
        return getActivityPrefConstant;
    }


    public void saveUserData(Activity _context,
                             String id,
                             String name,
                             String email,
                             String cnic,
                             String phone_num,
                             String type,
                             String status,
                             String profile_img) {

        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(ID, id);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(CNIC, cnic);
        editor.putString(MOBILE_NUMBER, phone_num);
        editor.putString(TYPE, type);
        editor.putString(STATUS, status);
        editor.putString(PROFILE_IMG, profile_img);
        editor.apply();
        editor.commit();
    }



    public void saveUpdatedUserData(Activity _context,
                             String name,
                             String email,
                             String cnic,
                             String phone_num,
                             String profile_img) {

        context = _context;
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(CNIC, cnic);
        editor.putString(MOBILE_NUMBER, phone_num);
        editor.putString(PROFILE_IMG, profile_img);
        editor.apply();
        editor.commit();
    }





    /**
     * @param _context Activity
     * @return userList
     */
    public List<String> getUserData(Activity _context) {
        List<String> userList = new ArrayList<>();
        sharedPreferences = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        String id = sharedPreferences.getString(ID, "");
        String fisrt_name = sharedPreferences.getString(NAME, "");
        String email = sharedPreferences.getString(EMAIL, "");
        String mob_no = sharedPreferences.getString(MOBILE_NUMBER, "");
        String type = sharedPreferences.getString(TYPE, "");
        String cnic = sharedPreferences.getString(CNIC, "");
        String status = sharedPreferences.getString(STATUS, "");
        String profile_img = sharedPreferences.getString(PROFILE_IMG, "");
        userList.add(id);
        userList.add(fisrt_name);
        userList.add(email);
        userList.add(mob_no);
        userList.add(status);
        userList.add(type);
        userList.add(cnic);
        userList.add(profile_img);
        return userList;
    }

    public void clearAllPreference(Activity _context) {
        SharedPreferences clearSettings = _context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        clearSettings.edit().clear().apply();
    }

    public void clearPreviousData(Activity __context) {
        SharedPreferences clearData = __context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        clearData.edit().remove("address").apply();
        clearData.edit().remove("latitude").apply();
        clearData.edit().remove("longitude").apply();
    }


}
