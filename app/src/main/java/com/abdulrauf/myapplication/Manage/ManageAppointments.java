package com.abdulrauf.myapplication.Manage;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.abdulrauf.myapplication.databinding.FragmentManageAppointmentsBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageAppointments extends Fragment {

    private FragmentManageAppointmentsBinding binding;
    private FirebaseFirestore db;
    private List<String> appointmentList;
    private List<String> appointmentIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageAppointmentsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();
        appointmentList = new ArrayList<>();
        appointmentIds = new ArrayList<>();

        loadBookedAppointments();

        binding.lvAppointments.setOnItemClickListener((parent, view1, position, id) -> {
            String appointmentId = appointmentIds.get(position);
            showEditDeleteDialog(appointmentId);
        });

        return view;
    }

    private void loadBookedAppointments() {
        db.collection("appointments")
                .whereEqualTo("isBooked", true) // فقط نوبت‌های رزرو شده
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        appointmentList.clear();
                        appointmentIds.clear();

                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String date = doc.getString("date");
                            String startTime = doc.getString("startTime");
                            String endTime = doc.getString("endTime");
                            String bookedBy = doc.getString("bookedBy");

                            appointmentList.add("📅 تاریخ: " + date +
                                    "\n⏰ شروع: " + startTime + " | پایان: " + endTime +
                                    "\n👤 رزرو توسط: " + (bookedBy != null ? bookedBy : "نامشخص"));
                            appointmentIds.add(doc.getId());
                        }

                        binding.lvAppointments.setAdapter(
                                new android.widget.ArrayAdapter<>(getContext(),
                                        android.R.layout.simple_list_item_1, appointmentList)
                        );
                    } else {
                        Toast.makeText(getContext(), "❌ خطا در دریافت نوبت‌ها", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showEditDeleteDialog(String appointmentId) {
        new AlertDialog.Builder(getContext())
                .setTitle("مدیریت نوبت رزرو شده")
                .setMessage("می‌خواهید نوبت را ویرایش یا حذف کنید؟")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("✏️ ویرایش", (dialog, which) -> editAppointment(appointmentId))
                .setNegativeButton("🗑️ حذف", (dialog, which) -> deleteAppointment(appointmentId))
                .setNeutralButton("❌ لغو", null)
                .show();
    }

    private void deleteAppointment(String appointmentId) {
        db.collection("appointments").document(appointmentId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "✅ نوبت حذف شد", Toast.LENGTH_SHORT).show();
                    loadBookedAppointments(); // لیست دوباره بارگذاری می‌شود
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "❌ خطا در حذف نوبت", Toast.LENGTH_SHORT).show());
    }

    private void editAppointment(String appointmentId) {
        // ساخت ویو سفارشی برای ورودی‌ها
        EditText etDate = new EditText(getContext());
        etDate.setHint("تاریخ جدید (yyyy/MM/dd)");
        EditText etStart = new EditText(getContext());
        etStart.setHint("ساعت شروع جدید (HH:mm)");
        EditText etEnd = new EditText(getContext());
        etEnd.setHint("ساعت پایان جدید (HH:mm)");

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 16, 32, 16);
        layout.addView(etDate);
        layout.addView(etStart);
        layout.addView(etEnd);

        new AlertDialog.Builder(getContext())
                .setTitle("ویرایش نوبت رزرو شده")
                .setView(layout)
                .setPositiveButton("ذخیره", (dialog, which) -> {
                    String newDate = etDate.getText().toString().trim();
                    String newStart = etStart.getText().toString().trim();
                    String newEnd = etEnd.getText().toString().trim();

                    if (newDate.isEmpty() || newStart.isEmpty() || newEnd.isEmpty()) {
                        Toast.makeText(getContext(), "لطفاً همه‌ی فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("date", newDate);
                    updatedData.put("startTime", newStart);
                    updatedData.put("endTime", newEnd);

                    db.collection("appointments").document(appointmentId)
                            .update(updatedData)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "✅ نوبت ویرایش شد", Toast.LENGTH_SHORT).show();
                                loadBookedAppointments();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "❌ خطا در ویرایش نوبت", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("لغو", null)
                .show();
    }
}
