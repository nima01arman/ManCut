package com.abdulrauf.myapplication.ui.slideshow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulrauf.myapplication.R;

import java.util.ArrayList;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    public interface OnAppointmentActionListener {
        void onCancel(AppointmentModel appointment);
        void onEdit(AppointmentModel appointment);
    }

    private final ArrayList<AppointmentModel> appointments;
    private final OnAppointmentActionListener listener;

    public AppointmentAdapter(ArrayList<AppointmentModel> appointments, OnAppointmentActionListener listener) {
        this.appointments = appointments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        AppointmentModel appt = appointments.get(position);
        holder.tvDate.setText("تاریخ: " + appt.getDate());
        holder.tvTime.setText("ساعت: " + appt.getStartTime() + " تا " + appt.getEndTime());

        holder.btnCancel.setOnClickListener(v -> listener.onCancel(appt));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(appt));
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTime;
        Button btnCancel, btnEdit;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}
