package com.example.finalproject.Utils;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;

public class DateTimePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private OnDateTimeSetListener onDateTimeSetListener;
    private boolean isStartTime;

    private  Calendar datetime = Calendar.getInstance();

    public interface OnDateTimeSetListener {
        void onDateTimeSet(Calendar dateTime, boolean isStartTime);
    }

    public DateTimePickerFragment(OnDateTimeSetListener listener, boolean isStartTime) {
        this.isStartTime = isStartTime;
        this.onDateTimeSetListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int year = datetime.get(datetime.YEAR);
        int month = datetime.get(datetime.MONTH);
        int day = datetime.get(datetime.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        datetime.set(year, month, dayOfMonth);

        int hour = datetime.get(Calendar.HOUR_OF_DAY);
        int minute = datetime.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), this, hour, minute, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        datetime.set(Calendar.MINUTE, minute);

        onDateTimeSetListener.onDateTimeSet(datetime, isStartTime);
    }
}
