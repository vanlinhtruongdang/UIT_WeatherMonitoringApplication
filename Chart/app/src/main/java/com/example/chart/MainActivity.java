package com.example.chart;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements DateTimePickerFragment.OnDateTimeSetListener {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private String AccessToken;
    private ApiService apiService;
    private Spinner metricsSpinner;
    private TextView startTimeTextView;
    private LineChart lineChart;
    private TextView endTimeTextView;
    private Button generateChartButton, SignInButton;
    private Calendar startDateTime;
    private Calendar endDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CustomCookieJar())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://uiot.ixxc.dev")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        metricsSpinner = findViewById(R.id.metricsSpinner);
        startTimeTextView = findViewById(R.id.startTimeTextView);
        endTimeTextView = findViewById(R.id.endTimeTextView);
        generateChartButton = findViewById(R.id.generateChartButton);
        SignInButton = findViewById(R.id.SignIn);
        lineChart = findViewById(R.id.lineChart);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.parameter_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        metricsSpinner.setAdapter(adapter);

        startTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog(true);
            }
        });

        endTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePickerDialog(false);
            }
        });

        SignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });

        generateChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLineChart();
                generateChart();
            }
        });
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getDescription().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        CustomMarkerView markerView = new CustomMarkerView(this, R.layout.custom_marker_view);
        lineChart.setMarker(markerView);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new TimestampAxisValueFormatter());
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setLabelRotationAngle(45f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
    }

    private void populateChart(List<Point> points, String Label) {
        ArrayList<Entry> entries = new ArrayList<>();

        for (Point point : points) {
            entries.add(new Entry(point.getX(), point.getY()));
        }
        LineDataSet dataSet = new LineDataSet(entries, Label);

        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setLineWidth(2.0f);
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setCircleRadius(5f);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void SignIn(){
        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
        networkExecutor.execute(() -> {
            Call<ResponseBody> StartSignIn = apiService.SignIn(
                    "openremote",
                    "user",
                    "123",
                    "password");
            try {
                Response<ResponseBody> Response = StartSignIn.execute();
                JSONObject AccessTokenJSON = new JSONObject(Response.body().string());
                AccessToken = AccessTokenJSON.getString("access_token");
                System.out.println(AccessToken);
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    private void showDateTimePickerDialog(boolean isStartTime) {
        DateTimePickerFragment dateTimePickerFragment = new DateTimePickerFragment(this, isStartTime);
        dateTimePickerFragment.show(getSupportFragmentManager(), null);
    }
    private void generateChart() {
        if (metricsSpinner.getSelectedItem() != null && startDateTime != null && endDateTime != null) {
            ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
            networkExecutor.execute(() -> {
                try {
                    String selectedMetrics = metricsSpinner.getSelectedItem().toString();
                    selectedMetrics = selectedMetrics.toLowerCase().split(" ")[0];

                    long startTimestamp = startDateTime.getTime().getTime();
                    long endTimestamp = endDateTime.getTime().getTime();

                    JSONObject Mess = new JSONObject();
                    Mess.put("type","lttb");
                    Mess.put("fromTimestamp", startTimestamp);
                    Mess.put("toTimestamp", endTimestamp);
                    Mess.put("amountOfPoints", 100);
                    System.out.println(Mess);

                    RequestBody requestBody = RequestBody.create(JSON, Mess.toString());

                    Call<List<Point>> GetChart = apiService.GetChart("Bearer " + AccessToken,
                                                                        selectedMetrics,
                                                                        requestBody);
                    Response<List<Point>> ChartResponse = GetChart.execute();
                    List<Point> listPoint = ChartResponse.body();
                    if (!listPoint.isEmpty())
                        populateChart(listPoint, selectedMetrics);

                } catch (Exception e){
                    System.out.println(e);
                }
            });
        }
    }

    @Override
    public void onDateTimeSet(Calendar dateTime, boolean isStartTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        String formattedDateTime = dateFormat.format(dateTime.getTime());
        if (isStartTime){
            startDateTime = dateTime;
            startTimeTextView.setText(formattedDateTime);
        }
        else {
            endDateTime = dateTime;
            endTimeTextView.setText(formattedDateTime);
        }
    }
}

