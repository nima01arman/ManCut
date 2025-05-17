package com.abdulrauf.myapplication.ui.gallery;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abdulrauf.myapplication.databinding.FragmentGalleryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AppointmentAdapter adapter;
    private final ArrayList<AppointmentModel> appointmentList = new ArrayList<>();
    private final Calendar calendar = Calendar.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        adapter = new AppointmentAdapter(appointmentList, appointment -> {
            db.collection("appointments")
                    .document(appointment.getId())
                    .update("isBooked", true, "bookedBy", mAuth.getCurrentUser().getUid())
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "نوبت رزرو شد", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "خطا در رزرو نوبت", Toast.LENGTH_SHORT).show());
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        return root;
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String selectedDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                            .format(calendar.getTime());
                    fetchAppointments(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void fetchAppointments(String date) {
        appointmentList.clear();
        db.collection("appointments")
                .whereEqualTo("date", date)
                .whereEqualTo("isBooked", false)
                .get()
                .addOnSuccessListener(result -> {
                    for (QueryDocumentSnapshot doc : result) {
                        AppointmentModel appt = new AppointmentModel(
                                doc.getId(),
                                doc.getString("date"),
                                doc.getString("startTime"),
                                doc.getString("endTime")
                        );
                        appointmentList.add(appt);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "خطا در دریافت نوبت‌ها", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
