package com.abdulrauf.myapplication.ui.slideshow;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.abdulrauf.myapplication.databinding.FragmentSlideshowBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AppointmentAdapter adapter;
    private final ArrayList<AppointmentModel> myAppointments = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        adapter = new AppointmentAdapter(myAppointments, new AppointmentAdapter.OnAppointmentActionListener() {
            @Override
            public void onCancel(AppointmentModel appointment) {
                showCancelConfirmationDialog(appointment.getId());
            }

            @Override
            public void onEdit(AppointmentModel appointment) {
                showEditTimeDialog(appointment.getId());
            }
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);

        fetchMyAppointments();

        return root;
    }

    private void fetchMyAppointments() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("appointments")
                .whereEqualTo("bookedBy", userId)
                .whereEqualTo("isBooked", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    myAppointments.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        AppointmentModel appt = new AppointmentModel(
                                doc.getId(),
                                doc.getString("date"),
                                doc.getString("startTime"),
                                doc.getString("endTime")
                        );
                        myAppointments.add(appt);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "خطا در دریافت نوبت‌ها", Toast.LENGTH_SHORT).show());
    }

    private void showCancelConfirmationDialog(String id) {
        new AlertDialog.Builder(getContext())
                .setTitle("لغو نوبت")
                .setMessage("آیا مطمئن هستید که می‌خواهید این نوبت را لغو کنید؟")
                .setPositiveButton("بله", (dialog, which) -> cancelAppointment(id))
                .setNegativeButton("خیر", null)
                .show();
    }

    private void cancelAppointment(String id) {
        db.collection("appointments")
                .document(id)
                .update("isBooked", false, "bookedBy", null)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "نوبت لغو شد", Toast.LENGTH_SHORT).show();
                    fetchMyAppointments();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "خطا در لغو نوبت", Toast.LENGTH_SHORT).show());
    }

    private void showEditTimeDialog(String id) {
        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME);
        input.setHint("مثال: 16:30");

        new AlertDialog.Builder(getContext())
                .setTitle("ویرایش ساعت شروع")
                .setView(input)
                .setPositiveButton("ثبت", (dialog, which) -> {
                    String newTime = input.getText().toString().trim();
                    if (!newTime.isEmpty()) {
                        db.collection("appointments")
                                .document(id)
                                .update("startTime", newTime)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "ساعت ویرایش شد", Toast.LENGTH_SHORT).show();
                                    fetchMyAppointments();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "خطا در ویرایش", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("لغو", null)
                .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
