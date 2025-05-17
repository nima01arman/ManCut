package com.abdulrauf.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ServicesFragment extends Fragment {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private ArrayList<ServiceModel> serviceList;
    private FirebaseFirestore db;

    public ServicesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewServices);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        serviceList = new ArrayList<>();
        // آداپتر فقط برای نمایش، بدون اکشن
        adapter = new ServiceAdapter(serviceList, null);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        fetchServices();

        return view;
    }

    private void fetchServices() {
        db.collection("services")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    serviceList.clear();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        ServiceModel service = document.toObject(ServiceModel.class);
                        service.setId(document.getId()); // ست کردن ID
                        serviceList.add(service);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "خطا در خواندن خدمات", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Error fetching services: " + e.getMessage());
                });
    }
}
