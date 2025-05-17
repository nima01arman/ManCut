package com.abdulrauf.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private ArrayList<ServiceModel> services;
    private OnServiceActionListener listener;

    public interface OnServiceActionListener {
        void onEdit(ServiceModel service);
        void onDelete(ServiceModel service);
    }

    public ServiceAdapter(ArrayList<ServiceModel> services, OnServiceActionListener listener) {
        this.services = services;
        this.listener = listener;
    }

    @Override
    public ServiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ServiceViewHolder holder, int position) {
        ServiceModel service = services.get(position);
        holder.name.setText(service.getServiceName());
        holder.price.setText("قیمت: " + service.getServicePrice() + " تومان");

        if (listener != null) {
            // دکمه‌ها قابل مشاهده باشند
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);

            holder.btnEdit.setOnClickListener(v -> listener.onEdit(service));
            holder.btnDelete.setOnClickListener(v -> listener.onDelete(service));
        } else {
            // برای مشتری: دکمه‌ها مخفی شوند
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;
        ImageButton btnEdit, btnDelete;

        public ServiceViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvServiceName);
            price = itemView.findViewById(R.id.tvServicePrice);
            btnEdit = itemView.findViewById(R.id.btnEditService);
            btnDelete = itemView.findViewById(R.id.btnDeleteService);
        }
    }
}
