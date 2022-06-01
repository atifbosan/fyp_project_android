package com.fyp.alphatrackerapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fyp.alphatrackerapp.R;
import com.fyp.alphatrackerapp.model.EmergencyNumber;

import java.util.List;

public class EmergencyNumberAdapter extends BaseAdapter {

    List<EmergencyNumber> emergencyNumberList;
    Context context;
    LayoutInflater inflater;

    public EmergencyNumberAdapter(List<EmergencyNumber> emergencyNumberList, Context context) {
        this.emergencyNumberList = emergencyNumberList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return emergencyNumberList.size();
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
        convertView = inflater.inflate(R.layout.item_emergency, parent, false);
        TextView item_en_name, item_en_number;

        item_en_name = convertView.findViewById(R.id.item_en_name);
        item_en_number = convertView.findViewById(R.id.item_en_number);

        item_en_name.setText(emergencyNumberList.get(position).getEn_name());
        item_en_number.setText(emergencyNumberList.get(position).getEn_number());

        return convertView;
    }
}
