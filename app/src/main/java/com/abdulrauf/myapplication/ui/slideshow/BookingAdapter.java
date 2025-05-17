package com.abdulrauf.myapplication.ui.slideshow;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulrauf.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<DocumentSnapshot> bookings;
    private OnBookingActionListener listener;

    public BookingAdapter(List<DocumentSnapshot> bookings, OnBookingActionListener listener) {
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        DocumentSnapshot booking = bookings.get(position);

        // گرفتن اطلاعات نوبت از Firestore و بررسی اینکه آیا اطلاعات null است
        try {
            // اطلاعات نوبت از Firestore
            final String name = booking.getString("name") != null ? booking.getString("name") : "نام مشخص نشده";
            final String date = booking.getString("date") != null ? booking.getString("date") : "تاریخ مشخص نشده";
            final String time = booking.getString("time") != null ? booking.getString("time") : "زمان مشخص نشده";
            final String id = booking.getId();  // این متغیر باید final باشد

            // تنظیم مقادیر به TextViews
            holder.nameTextView.setText(name);
            holder.dateTextView.setText(date);
            holder.timeTextView.setText(time);

            // چاپ اطلاعات برای دیباگ
            Log.d("BookingAdapter", "Booking ID: " + id + " Name: " + name + " Date: " + date + " Time: " + time);

            // هنگامی که کاربر روی آیتم کلیک می‌کند، دیالوگ ویرایش باز شود
            holder.itemView.setOnClickListener(v -> listener.onEdit(id, name, date, time));

            // هنگام نگه داشتن آیتم، آن را حذف می‌کند
            holder.itemView.setOnLongClickListener(v -> {
                listener.onDelete(id);
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace(); // در صورت بروز خطا در خواندن داده‌ها، خطا را چاپ می‌کنیم
        }
    }



    @Override
    public int getItemCount() {
        return bookings.size();
    }

    // کلاس ViewHolder برای هر نوبت
    public static class BookingViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView dateTextView;
        TextView timeTextView;

        public BookingViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textBookingName);
            dateTextView = itemView.findViewById(R.id.textBookingDate);
            timeTextView = itemView.findViewById(R.id.textBookingTime);
        }
    }

    // این اینترفیس برای مدیریت عملیات ویرایش و حذف است
    public interface OnBookingActionListener {
        void onDelete(String id);
        void onEdit(String id, String name, String date, String time);
    }
}
