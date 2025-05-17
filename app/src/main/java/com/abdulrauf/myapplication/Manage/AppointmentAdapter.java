package com.abdulrauf.myapplication.Manage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.abdulrauf.myapplication.R;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final List<String> values;

    public AppointmentAdapter(Context context, List<String> values) {
        super(context, R.layout.item_appointments, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_appointments, parent, false);
        }

        TextView tvAppointment = view.findViewById(R.id.tvAppointment);
        tvAppointment.setText(values.get(position));

        return view;
    }
}
