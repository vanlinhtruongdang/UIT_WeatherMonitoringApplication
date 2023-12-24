package com.example.finalproject.Utils;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimestampAxisValueFormatter extends ValueFormatter {

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return dateFormat.format(new Date((long) value));
    }
}