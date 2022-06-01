package com.fyp.alphatrackerapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyp.alphatrackerapp.R;
import com.fyp.alphatrackerapp.model.Announcement;
import com.fyp.alphatrackerapp.model.Notification;
import com.fyp.alphatrackerapp.remote.RetrofitClient;
import com.fyp.alphatrackerapp.service.GetAnnounDetService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAdapter extends BaseAdapter {

    List<Notification> notificationList;
    Context context;
    LayoutInflater inflater;
    Dialog dialog;
    Announcement announcement;
    ImageView img_close_dialog;
    TextView selected_announce_status, selected_announce_title, selected_announce_description;


    public NotificationAdapter(List<Notification> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_notification, parent, false);
        TextView tv_notify, tv_notify_datetime;
        tv_notify = convertView.findViewById(R.id.tv_notify);
        tv_notify_datetime = convertView.findViewById(R.id.tv_notify_datetime);
        tv_notify.setText(notificationList.get(position).getNoti_title());
        tv_notify_datetime.setText(notificationList.get(position).getCreatedDate());
        dialog = new Dialog(context, R.style.Theme_AppCompat_DayNight_NoActionBar);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog(notificationList.get(position).getNoti_fk_id());
            }
        });
        return convertView;
    }

    private void ShowDialog(int id) {
        announcement = new Announcement();
        dialog.setContentView(R.layout.dialog_selected_announcement);
        dialog.setCancelable(false);
        selected_announce_status = dialog.findViewById(R.id.selected_announce_status);
        selected_announce_title = dialog.findViewById(R.id.selected_announce_title);
        selected_announce_description = dialog.findViewById(R.id.selected_announce_description);
        img_close_dialog = dialog.findViewById(R.id.img_close_dialog);

        img_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        GetAnnounDetService service = RetrofitClient.getClient().create(GetAnnounDetService.class);
        Call<Announcement> call = service.accounce_det(id);
        call.enqueue(new Callback<Announcement>() {
            @Override
            public void onResponse(Call<Announcement> call, Response<Announcement> response) {
                if (response.isSuccessful()) {
                    announcement = response.body();
                    if (!announcement.isError()) {
                        selected_announce_status.setText(announcement.getAnnouncement_status());
                        selected_announce_title.setText(announcement.getAnnouncement_title());
                        selected_announce_description.setText(announcement.getAnnouncement_description());
                    }else{
                        Toast.makeText(context,
                                announcement.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(context,
                            response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Announcement> call, Throwable t) {
                Toast.makeText(context,
                        "", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

}
