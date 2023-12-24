package com.example.finalproject;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class frag_map_dashboard1 extends Fragment {

    private TextView db_node_id;
    private TextView db_latitude;
    private TextView db_longtitude;
    private TextView db_rainfall;
    private TextView db_temperature;
    private TextView db_humidity;
    private TextView db_wind_speed;
    private TextView db_wind_direction;

    public frag_map_dashboard1() {
        // Required empty public constructor
    }

    public static frag_map_dashboard1 newInstance() {
        frag_map_dashboard1 fragment = new frag_map_dashboard1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_map_dashboard1, container, false);
        String uglyJson = getArguments().getString("message");


        try{
            String formatString = uglyJson.substring(16,uglyJson.length());
            Log.d("Mark1_db",formatString);
            if (!formatString.equals("{}")) {
                JSONObject jsonObject = new JSONObject(formatString);
                String messageId = jsonObject.getString("messageId");
                if(messageId.equals("read-assets:5zI6XqkQVSfdgOrZ1MyWEf:AssetEvent2")){
                    JSONObject event = jsonObject.getJSONObject("event");
                    JSONArray assets = event.getJSONArray("assets");
                    JSONObject zero = assets.getJSONObject(0);
                    JSONObject attributes = zero.getJSONObject("attributes");
                    JSONObject location = attributes.getJSONObject("location");
                    JSONObject location_value = location.getJSONObject("value");
                    JSONArray coordinate = location_value.getJSONArray("coordinates");
                    JSONObject rainfall = attributes.getJSONObject("rainfall");
                    JSONObject temperature = attributes.getJSONObject("temperature");
                    JSONObject humidity = attributes.getJSONObject("humidity");
                    JSONObject windDirection = attributes.getJSONObject("windDirection");
                    JSONObject windSpeed = attributes.getJSONObject("windSpeed");

                    db_node_id = view.findViewById(R.id.node_id);
                    db_latitude = view.findViewById(R.id.latitude);
                    db_longtitude = view.findViewById(R.id.longtitude);
                    db_rainfall = view.findViewById(R.id.rainFall);
                    db_wind_direction = view.findViewById(R.id.windDirect);
                    db_wind_speed = view.findViewById(R.id.windSpeed);
                    db_humidity = view.findViewById(R.id.humidity);
                    db_temperature = view.findViewById(R.id.temperature);

                    db_node_id.setText(zero.getString("id"));
                    db_rainfall.setText(rainfall.getString("value").concat("mm"));
                    db_temperature.setText(temperature.getString("value").concat("°C"));
                    db_humidity.setText(humidity.getString("value").concat("%"));
                    db_wind_direction.setText(windDirection.getString("value"));
                    db_wind_speed.setText(windSpeed.getString("value").concat(" km/h"));
                    db_longtitude.setText((coordinate.getString(0)));
                    db_latitude.setText(coordinate.getString(1));
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        //BackPressed
        OnBackPressedDispatcher onBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new frag_map());
                transaction.commit();
            }
        };
        onBackPressedDispatcher.addCallback(getViewLifecycleOwner(), callback);

        return view;
    }
}