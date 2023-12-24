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

public class frag_map_dashboard2 extends Fragment {


    private TextView db_node_id;
    private TextView db_latitude;
    private TextView db_longtitude;
    private TextView db_brightness;
    private TextView db_colorTemp;


    public frag_map_dashboard2() {
    }


    public static frag_map_dashboard2 newInstance() {
        frag_map_dashboard2 fragment = new frag_map_dashboard2();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_map_dashboard2, container, false);
        String uglyJson = getArguments().getString("message");

        try{
            String formatString = uglyJson.substring(16,uglyJson.length());
            Log.d("Mark2_db",formatString);
            if (!formatString.equals("{}")) {
                JSONObject jsonObject = new JSONObject(formatString);
                String messageId = jsonObject.getString("messageId");
                if(messageId.equals("read-assets:6iWtSbgqMQsVq8RPkJJ9vo:AssetEvent2")){
                    JSONObject event = jsonObject.getJSONObject("event");
                    JSONArray assets = event.getJSONArray("assets");
                    JSONObject zero = assets.getJSONObject(0);
                    JSONObject attributes = zero.getJSONObject("attributes");
                    JSONObject location = attributes.getJSONObject("location");
                    JSONObject location_value = location.getJSONObject("value");
                    JSONArray coordinate = location_value.getJSONArray("coordinates");
                    JSONObject brightness = attributes.getJSONObject("brightness");
                    JSONObject colourTemperature = attributes.getJSONObject("colourTemperature");

                    db_node_id = view.findViewById(R.id.node_id1);
                    db_latitude = view.findViewById(R.id.latitude1);
                    db_longtitude = view.findViewById(R.id.longtitude1);
                    db_brightness = view.findViewById(R.id.brightness);
                    db_colorTemp = view.findViewById(R.id.colorTemp);

                    db_node_id.setText(zero.getString("id"));
                    db_longtitude.setText((coordinate.getString(0)));
                    db_latitude.setText(coordinate.getString(1));
                    db_brightness.setText(brightness.getString("value").concat("%"));
                    db_colorTemp.setText(colourTemperature.getString("value").concat("°K"));
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