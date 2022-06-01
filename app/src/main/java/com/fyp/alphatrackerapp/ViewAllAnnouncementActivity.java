package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.adapter.NotificationAdapter;
import com.fyp.alphatrackerapp.manager.SharedPrefManager;
import com.fyp.alphatrackerapp.model.Notification;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.GetAnnounceNotificationService;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllAnnouncementActivity extends AppCompatActivity {

    TextView tv_back_to_Home, tv_no_announcement;
    ListView announcement_listview;
    int pref;
    int userID;
    SharedPrefManager sharedPrefManager = new SharedPrefManager();
    List<Notification> notificationList = new ArrayList<>();
    ProgressDialog progressDialog;
    NotificationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_announcement);
        tv_back_to_Home = findViewById(R.id.tv_back_to_Home);
        tv_no_announcement = findViewById(R.id.tv_no_announcement);
        announcement_listview = findViewById(R.id.announcement_listview);
        pref = sharedPrefManager.getLoginPreference(ViewAllAnnouncementActivity.this);
        userID = Integer.parseInt(sharedPrefManager.getID(ViewAllAnnouncementActivity.this));
        if(pref==1){
            GetAnnouncement(userID,"U");
        }else if(pref==2){
            GetAnnouncement(userID,"D");
        }
    }

    private void GetAnnouncement(int id, String type) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("please wait...");
        progressDialog.show();

        GetAnnounceNotificationService service = RetrofitClient.getClient().create(GetAnnounceNotificationService.class);
        Call<JsonObject> call = service.getNotifications(id, type);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().getAsJsonObject().toString());

                            JSONArray jsonArray = jsonObject.getJSONArray("records");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject data = jsonArray.getJSONObject(i);

                                notificationList.add(new Notification(
                                        data.getInt("noti_id"),
                                        data.getString("noti_title"),
                                        data.getInt("noti_fk_id"),
                                        data.getString("createdDate")));
                            }
                            adapter = new NotificationAdapter(notificationList, ViewAllAnnouncementActivity.this);
                            announcement_listview.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(ViewAllAnnouncementActivity.this,
                                "Record not found", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(ViewAllAnnouncementActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ViewAllAnnouncementActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}