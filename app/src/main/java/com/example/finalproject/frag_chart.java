package com.example.finalproject;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalproject.Model.Point;
import com.example.finalproject.Utils.ApiService;
import com.example.finalproject.Utils.CustomCookieJar;
import com.example.finalproject.Utils.CustomMarkerView;
import com.example.finalproject.Utils.DateTimePickerFragment;
import com.example.finalproject.Utils.TimestampAxisValueFormatter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public class frag_chart extends Fragment implements DateTimePickerFragment.OnDateTimeSetListener {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private String AccessToken;
    private ApiService apiService;
    private TextView startTimeTextView;
    private LineChart lineChart;
    private TextView endTimeTextView;
    private Button generateChartButton, SignInButton;
    private Calendar startDateTime;
    private Calendar endDateTime;
    private TextInputLayout textInputLayout;
    private String selectedMetrics = "";

    public frag_chart() {
    }

    public static frag_chart newInstance() {
        frag_chart fragment = new frag_chart();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chart, container, false);
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CustomCookieJar())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://uiot.ixxc.dev")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.autoComplete);


        startTimeTextView = view.findViewById(R.id.startTimeTextView);
        endTimeTextView = view.findViewById(R.id.endTimeTextView);
        generateChartButton = view.findViewById(R.id.generateChartButton);
        lineChart = view.findViewById(R.id.lineChart);
        textInputLayout = view.findViewById(R.id.testss);
        SignIn();
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


        generateChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLineChart();
                generateChart();
            }
        });
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMetrics = parent.getItemAtPosition(position).toString();
                Log.d("TAG", "selectedMetrics: "+selectedMetrics);
            }
        });

        //backPressed
        OnBackPressedDispatcher onBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new frag_home());
                transaction.commit();
            }
        };
        onBackPressedDispatcher.addCallback(getViewLifecycleOwner(), callback);
        return view;
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

        CustomMarkerView markerView = new CustomMarkerView(requireContext(), R.layout.custom_marker_view);
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
        dataSet.setColor(Color.argb(255,14,226,255));
        dataSet.setCircleColor(Color.argb(255,14,226,255));
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
        dateTimePickerFragment.show(requireActivity().getSupportFragmentManager(), null);
    }
    private void generateChart() {
        if (textInputLayout != null && startDateTime != null && endDateTime != null) {
            ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
            networkExecutor.execute(() -> {
                try {
                    Log.d("Tag", "Value of intValue: " + selectedMetrics);

                    selectedMetrics = selectedMetrics.toLowerCase().split(" ")[0];
                    Log.d("Tag", "Value of intValue: " + selectedMetrics);


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

