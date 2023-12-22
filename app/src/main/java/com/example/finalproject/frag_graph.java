package com.example.finalproject;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import com.example.finalproject.Model.Point;
import com.example.finalproject.Utils.ApiService;
import com.example.finalproject.Utils.CustomCookieJar;
import com.example.finalproject.Utils.CustomMarkerView;
import com.example.finalproject.Utils.DateTimePickerFragment;
import com.example.finalproject.Utils.TimestampAxisValueFormatter;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONObject;
import org.osmdroid.config.Configuration;


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


import com.example.finalproject.R;
import com.tencent.mmkv.MMKV;


public class frag_graph extends Fragment implements DateTimePickerFragment.OnDateTimeSetListener{
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private MMKV kv = null;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private ApiService apiService;
    private Spinner metricsSpinner;
    private TextView startTimeTextView;
    private LineChart lineChart;
    private TextView endTimeTextView;
    private Button generateChartButton;
    private Calendar startDateTime;
    private Calendar endDateTime;
    String accessToken;


    public frag_graph() {
        // Required empty public constructor
    }

    public static frag_graph newInstance() {
        frag_graph fragment = new frag_graph();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_graph, container, false);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        MMKV.initialize(this.getContext());
        kv = MMKV.defaultMMKV();
        accessToken = kv.decodeString("AccessToken");
        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CustomCookieJar())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://uiot.ixxc.dev")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        metricsSpinner = view.findViewById(R.id.metricsSpinner);
        startTimeTextView = view.findViewById(R.id.startTimeTextView);
        endTimeTextView = view.findViewById(R.id.endTimeTextView);
        generateChartButton = view.findViewById(R.id.generateChartButton);
//        SignInButton = view.findViewById(R.id.SignIn);
        lineChart = view.findViewById(R.id.lineChart);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                view.getContext(), R.array.parameter_array, android.R.layout.simple_spinner_item);
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

//        SignInButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SignIn();
//            }
//        });

        generateChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupLineChart(ctx);
                generateChart();
            }
        });
    }

    private void setupLineChart(Context ctx) {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setTouchEnabled(true);
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getDescription().setTextColor(Color.WHITE);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setDoubleTapToZoomEnabled(false);

        CustomMarkerView markerView = new CustomMarkerView(ctx, R.layout.custom_marker_view);
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

//    private void SignIn(){
//        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
//        networkExecutor.execute(() -> {
//            Call<ResponseBody> StartSignIn = apiService.SignIn(
//                    "openremote",
//                    "user",
//                    "123",
//                    "password");
//            try {
//                Response<ResponseBody> Response = StartSignIn.execute();
//                JSONObject AccessTokenJSON = new JSONObject(Response.body().string());
//                AccessToken = AccessTokenJSON.getString("access_token");
//                System.out.println(AccessToken);
//            } catch (Exception e) {
//                System.out.println(e);
//            }
//        });
//    }

    private void showDateTimePickerDialog(boolean isStartTime) {
        DateTimePickerFragment dateTimePickerFragment = new DateTimePickerFragment(this, isStartTime);
        dateTimePickerFragment.show(getParentFragmentManager(), null);
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

                    Call<List<Point>> GetChart = apiService.GetChart("Bearer " + accessToken,
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