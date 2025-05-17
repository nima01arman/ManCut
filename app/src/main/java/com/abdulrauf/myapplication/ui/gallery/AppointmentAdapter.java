package com.abdulrauf.myapplication.ui.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnAppointmentClick {
        void onClick(AppointmentModel appointment);
    }

    private final List<AppointmentModel> list;
    private final OnAppointmentClick listener;

    public AppointmentAdapter(List<AppointmentModel> list, OnAppointmentClick listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentModel item = list.get(position);
        ((TextView) holder.itemView.findViewById(android.R.id.text1))
                .setText("تاریخ: " + item.getDate());
        ((TextView) holder.itemView.findViewById(android.R.id.text2))
                .setText("ساعت: " + item.getStartTime() + " - " + item.getEndTime());
        holder.itemView.setOnClickListener(v -> listener.onClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
