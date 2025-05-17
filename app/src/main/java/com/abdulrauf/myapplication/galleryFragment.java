package com.abdulrauf.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.abdulrauf.myapplication.databinding.FragmentGallery2Binding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class galleryFragment extends Fragment {

    private FragmentGallery2Binding binding;
    private final Calendar calendar = Calendar.getInstance();
    private String selectedDate = "";
    private String startTime = "";
    private String endTime = "";
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGallery2Binding.inflate(inflater, container, false);
        View view = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        // انتخاب تاریخ
        binding.btnSelectDate.setOnClickListener(v -> showDatePicker());

        // انتخاب ساعت شروع
        binding.btnStartTime.setOnClickListener(v -> showTimePicker(true));

        // انتخاب ساعت پایان
        binding.btnEndTime.setOnClickListener(v -> showTimePicker(false));

        // افزودن نوبت
        binding.btnAddSlot.setOnClickListener(v -> {
            if (selectedDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(getContext(), "لطفاً همه‌ی فیلدها را پر کنید", Toast.LENGTH_SHORT).show();
                return;
            }

            // ایجاد نقشه برای ذخیره در Firestore
            Map<String, Object> slot = new HashMap<>();
            slot.put("date", selectedDate);
            slot.put("startTime", startTime);
            slot.put("endTime", endTime);
            slot.put("isBooked", false); // نوبت هنوز رزرو نشده
            slot.put("bookedBy", null);  // هنوز کسی رزرو نکرده

            db.collection("appointments")
                    .add(slot)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "نوبت ثبت شد", Toast.LENGTH_LONG).show();
                        resetFields();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "خطا در ثبت نوبت", Toast.LENGTH_SHORT).show());
        });

        return view;
    }

    private void resetFields() {
        selectedDate = startTime = endTime = "";
        binding.tvDate.setText("تاریخ انتخاب نشده");
        binding.tvStartTime.setText("ساعت شروع انتخاب نشده");
        binding.tvEndTime.setText("ساعت پایان انتخاب نشده");
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
                    selectedDate = sdf.format(calendar.getTime());
                    binding.tvDate.setText("تاریخ: " + selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void showTimePicker(boolean isStart) {
        TimePickerDialog dialog = new TimePickerDialog(requireContext(),
                (view, hourOfDay, minute) -> {
                    String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                    if (isStart) {
                        startTime = time;
                        binding.tvStartTime.setText("شروع: " + time);
                    } else {
                        endTime = time;
                        binding.tvEndTime.setText("پایان: " + time);
                    }
                },
                10, 0, true);
        dialog.show();
    }
}
