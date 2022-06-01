package com.fyp.alphatrackerapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.adapter.EmergencyNumberAdapter;
import com.fyp.alphatrackerapp.manager.SharedPrefManager;
import com.fyp.alphatrackerapp.model.EmergencyNumber;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.AddEmergencyNumService;
import com.fyp.alphatrackerapp.service.GetEmergencyNumberService;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmergencyNumberActivity extends AppCompatActivity {

    TextView tv_manage_enBack, tv_add_new_emergency_number;
    ListView en_listview;
    Dialog dialog;
    EditText edt_contact_name, edt_contact_number;
    Button btn_add_en;
    ProgressDialog progressDialog;
    EmergencyNumber emergencyNumber;
    int userID, pref;
    EmergencyNumberAdapter adapter;
    List<EmergencyNumber> emergencyNumberList = new ArrayList<>();
    SharedPrefManager sharedPrefManager = new SharedPrefManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_number);
        tv_manage_enBack = findViewById(R.id.tv_manage_enBack);
        tv_add_new_emergency_number = findViewById(R.id.tv_add_new_emergency_number);
        en_listview = findViewById(R.id.en_listview);
        userID = Integer.parseInt(sharedPrefManager.getID(EmergencyNumberActivity.this));
        pref=sharedPrefManager.getLoginPreference(EmergencyNumberActivity.this);
        GetNumbers(userID);
        tv_manage_enBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pref==1){
                    startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
                    finish();
                }else if(pref==2){
                    startActivity(new Intent(getApplicationContext(), DriverHomeActivity.class));
                    finish();
                }
            }
        });

        tv_add_new_emergency_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });
    }

    private void ShowDialog() {
        dialog = new Dialog(EmergencyNumberActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_number);
        edt_contact_name = dialog.findViewById(R.id.edt_contact_name);
        edt_contact_number = dialog.findViewById(R.id.edt_contact_number);
        btn_add_en = dialog.findViewById(R.id.btn_add_en);

        btn_add_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNumber(edt_contact_name.getText().toString(),
                        edt_contact_number.getText().toString(),
                        userID);
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void GetNumbers(int ID) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.show();

        GetEmergencyNumberService service = RetrofitClient.getClient().create(GetEmergencyNumberService.class);
        Call<JsonObject> call = service.getEmergencyNumbers(ID);
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

                                emergencyNumberList.add(new EmergencyNumber(data.getInt("en_id"),
                                        data.getString("en_name"),
                                        data.getString("en_number")));
                            }

                            adapter = new EmergencyNumberAdapter(emergencyNumberList, EmergencyNumberActivity.this);
                            en_listview.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EmergencyNumberActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EmergencyNumberActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void AddNumber(String contactName, String contactNumber, int UID) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("please wait..");
        progressDialog.show();
        emergencyNumber = new EmergencyNumber();

        AddEmergencyNumService service = RetrofitClient.getClient().create(AddEmergencyNumService.class);
        Call<EmergencyNumber> call = service.add_numer(contactName, contactNumber, UID);
        call.enqueue(new Callback<EmergencyNumber>() {
            @Override
            public void onResponse(Call<EmergencyNumber> call, Response<EmergencyNumber> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    emergencyNumber = response.body();

                    if (!emergencyNumber.isError()) {
                        Toast.makeText(EmergencyNumberActivity.this,
                                emergencyNumber.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EmergencyNumberActivity.this,
                                emergencyNumber.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EmergencyNumberActivity.this,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EmergencyNumber> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(EmergencyNumberActivity.this,
                        t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(pref==1){
            startActivity(new Intent(getApplicationContext(), UserHomeActivity.class));
            finish();
        }else if(pref==2){
            startActivity(new Intent(getApplicationContext(), DriverHomeActivity.class));
            finish();
        }
    }
}