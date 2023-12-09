package com.example.finalproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Handler;
import android.os.Looper;

import com.example.finalproject.R;


public class frag_home extends Fragment {
    public frag_home() {
        // Required empty public constructor
    }

    public static frag_user newInstance() {
        frag_user fragment = new frag_user();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private TextView timeTextView, dayOfWeekTextView, dateTextView;

    // Thời gian cập nhật lại sau mỗi 1 phút
    private static final long UPDATE_INTERVAL = 60000; // 60 seconds

    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateTimeRunnable = new Runnable() {
        @Override
        public void run() {
            displayDateTimeInfo(timeTextView, dayOfWeekTextView, dateTextView);
            // Tự động cập nhật sau mỗi khoảng thời gian
            handler.postDelayed(this, UPDATE_INTERVAL);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        timeTextView = view.findViewById(R.id.tv_time);
        dayOfWeekTextView = view.findViewById(R.id.tv_dow);
        dateTextView = view.findViewById(R.id.tv_date);
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        // Bắt đầu cập nhật thời gian khi Fragment được hiển thị
        handler.post(updateTimeRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Dừng cập nhật thời gian khi Fragment không còn được hiển thị
        handler.removeCallbacks(updateTimeRunnable);
    }
    private void displayDateTimeInfo(TextView timeTextView, TextView dayOfWeekTextView, TextView dateTextView) {
        Calendar calendar = Calendar.getInstance(); // Tạo một instance của Calendar

        // Định dạng thời gian và hiển thị trong TextView thời gian
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String formattedTime = timeFormat.format(calendar.getTime());
        timeTextView.setText(formattedTime);

        // Định dạng thứ và hiển thị trong TextView thứ
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
        String formattedDayOfWeek = dayFormat.format(calendar.getTime());
        dayOfWeekTextView.setText(formattedDayOfWeek);

        // Định dạng ngày tháng năm và hiển thị trong TextView ngày tháng năm
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());
        dateTextView.setText(formattedDate);
    }
}