package com.example.finalproject;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.net.URISyntaxException;

public class frag_map extends Fragment {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    MyWebSocketClient client = null;
    private MapView map = null;
    private long ZoomSpeed = 500;
    private double MinZoom = 18.0;
    private double MaxZoom = 21.0;
    private double ZoomLevel = 19.5;
    private GeoPoint UITLocation = new GeoPoint(10.870, 106.80324);
    private GeoPoint Station1 = new GeoPoint(10.869778736885038, 106.80280655508835);
    private GeoPoint Station2 = new GeoPoint(10.869778736885038, 106.80345028525176);
    public static frag_map newInstance() {
        frag_map fragment = new frag_map();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.frag_map, container, false);
        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        FrameLayout frameLayout = view.findViewById(R.id.map_dashboard);

        // Tạo Fragment map
        frag_map_dashboard map_dashboard = new frag_map_dashboard();


        // Thay thế Fragment hiện tại trong FrameLayout bằng Fragment map
        getChildFragmentManager()
                .beginTransaction()
                .replace(frameLayout.getId(), map_dashboard)
                .commit();

        String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJoREkwZ2hyVlJvaE5zVy1wSXpZeDBpT2lHMzNlWjJxV21sRk4wWGE1dWkwIn0.eyJleHAiOjE3MDIxODgzMDUsImlhdCI6MTcwMjExMzQ3NywiYXV0aF90aW1lIjoxNzAyMTAxOTA1LCJqdGkiOiI5Mzk4YTAzYS1kNDE0LTQ4Y2QtYWYwOS05OGNlOWJmZWZhNTgiLCJpc3MiOiJodHRwczovL3Vpb3QuaXh4Yy5kZXYvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjRlM2E0NDk2LTJmMTktNDgxMy1iZjAwLTA5NDA3ZDFlZThjYiIsInR5cCI6IkJlYXJlciIsImF6cCI6Im9wZW5yZW1vdGUiLCJub25jZSI6ImY1ZDQ2NDYxLTNmNzEtNGJkMC1iNzQ4LTkzYTdkYTk3YzBkYiIsInNlc3Npb25fc3RhdGUiOiJiZDM4MzZjMy04YjZjLTQ0YTctOWQ2Yy00OWU3NTk4NTVjYzciLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vdWlvdC5peHhjLmRldiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiZGVmYXVsdC1yb2xlcy1tYXN0ZXIiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsib3BlbnJlbW90ZSI6eyJyb2xlcyI6WyJyZWFkOm1hcCIsInJlYWQ6cnVsZXMiLCJyZWFkOmluc2lnaHRzIiwicmVhZDphc3NldHMiXX0sImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoib3BlbmlkIHByb2ZpbGUgZW1haWwiLCJzaWQiOiJiZDM4MzZjMy04YjZjLTQ0YTctOWQ2Yy00OWU3NTk4NTVjYzciLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsIm5hbWUiOiJGaXJzdCBOYW1lIExhc3QgbmFtZSIsInByZWZlcnJlZF91c2VybmFtZSI6InVzZXIiLCJnaXZlbl9uYW1lIjoiRmlyc3QgTmFtZSIsImZhbWlseV9uYW1lIjoiTGFzdCBuYW1lIiwiZW1haWwiOiJ1c2VyQGl4eGMuZGV2In0.KvHDuS_1JAp8Cvqekc8TJzairrkVOpId8HK6vMsjML1f5CvBsmHqchSGzRLCKPi_Mi_-W4bZxJZnwKU75M-Ql-hStSHcIP4wazYNwgsWg6EJs7hR3KllCNOusfELHepXL1HxVfSvkhTrp8NofYVbgRcm5eAgzhiHUsr5DhCchK3bIEi6FVQM6fvtvR3C3XhLL4z0dZsblAv5UZ1whLQxG3bQPD5_4egaD2Rn5UFrcsFtaGR1dLS-KsUCe2bAha7lmqr7AkUuts9vL9u5o3oLNkAUab-i771ykuFBPi5MeUXlHCkjzA9z3zlhdhgF1EbyEI0r-hx0g1VzOTAfo7RPpA";
        try{
            client = new MyWebSocketClient("wss://uiot.ixxc.dev/websocket/events?Realm=master&Authorization=Bearer%20"+token);
            client.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        map = view.findViewById(R.id.map);
        ColorMatrix inverseMatrix = new ColorMatrix(new float[] {
                -1.0f, 0.0f, 0.0f, 0.0f, 255f,
                0.0f, -1.0f, 0.0f, 0.0f, 255f,
                0.0f, 0.0f, -1.0f, 0.0f, 255f,
                0.0f, 0.0f, 0.0f, 1.0f, 0.0f
        });

        int destinationColor = Color.parseColor("#1c2334");
        float lr = (255.0f - Color.red(destinationColor))/255.0f;
        float lg = (255.0f - Color.green(destinationColor))/255.0f;
        float lb = (255.0f - Color.blue(destinationColor))/255.0f;
        ColorMatrix grayscaleMatrix = new ColorMatrix(new float[] {
                lr, lg, lb, 0, 0,
                lr, lg, lb, 0, 0,
                lr, lg, lb, 0, 0,
                0, 0, 0, 0, 255,
        });
        grayscaleMatrix.preConcat(inverseMatrix);
        int dr = Color.red(destinationColor);
        int dg = Color.green(destinationColor);
        int db = Color.blue(destinationColor);
        float drf = dr / 255f;
        float dgf = dg / 255f;
        float dbf = db / 255f;
        ColorMatrix tintMatrix = new ColorMatrix(new float[] {
                drf, 0, 0, 0, 0,
                0, dgf, 0, 0, 0,
                0, 0, dbf, 0, 0,
                0, 0, 0, 1, 0,
        });
        tintMatrix.preConcat(grayscaleMatrix);
        float lDestination = drf * lr + dgf * lg + dbf * lb;
        float scale = 1f - lDestination;
        float translate = 1 - scale * 0.5f;
        ColorMatrix scaleMatrix = new ColorMatrix(new float[] {
                scale, 0, 0, 0, dr * translate,
                0, scale, 0, 0, dg * translate,
                0, 0, scale, 0, db * translate,
                0, 0, 0, 1, 0,
        });
        scaleMatrix.preConcat(tintMatrix);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(scaleMatrix);

        Marker Station1Marker = new Marker(map);
        Station1Marker.setPosition(Station1);
        Station1Marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Station1Marker.setIcon(ctx.getDrawable(R.drawable.cloud));

        Marker Station2Marker = new Marker(map);
        Station2Marker.setPosition(Station2);
        Station2Marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        Station2Marker.setIcon(ctx.getDrawable(R.drawable.cloud));

        map.addOnFirstLayoutListener(new MapView.OnFirstLayoutListener() {
            @Override
            public void onFirstLayout(View v, int left, int top, int right, int bottom) {
                map.setTileSource(TileSourceFactory.MAPNIK );
                map.setMultiTouchControls(true);
                map.setMinZoomLevel(MinZoom);
                map.setMaxZoomLevel(MaxZoom);
                map.getController().setZoom(ZoomLevel);
                map.setScrollableAreaLimitLatitude(10.875, 10.865, 100);
                map.setScrollableAreaLimitLongitude(106.800, 106.806, 100);
                map.getController().setCenter(UITLocation);
                map.getOverlayManager().getTilesOverlay().setColorFilter(filter);
                map.getOverlays().add(Station1Marker);
                map.getOverlays().add(Station2Marker);
                map.invalidate();
            }
        });

        Station1Marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                map.getController().animateTo(Station1Marker.getPosition(), ZoomLevel, ZoomSpeed);
                client.send("REQUESTRESPONSE:{\"messageId\":\"read-assets:5zI6XqkQVSfdgOrZ1MyWEf:AssetEvent2\",\"event\":{\"eventType\":\"read-assets\",\"assetQuery\":{\"ids\":[\"5zI6XqkQVSfdgOrZ1MyWEf\"]}}}");
                return true;
            }
        });

        Station2Marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                map.getController().animateTo(Station2Marker.getPosition(), ZoomLevel, ZoomSpeed);
                return true;
            }
        });

        String[] Permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION};

        requestPermissionsIfNecessary(Permission);

        return view;
    }
    public void onResume() {
        super.onResume();

        //Backpressed
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().beginTransaction().remove(frag_map.this).commit();
            }
        });


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Configuration.getInstance().load(requireContext(), prefs);
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        Configuration.getInstance().save(requireContext(), prefs);
        if (map != null) {
            map.onPause();
        }
    }


    public class MyWebSocketClient extends WebSocketClient {

        public MyWebSocketClient(String serverUrl) throws URISyntaxException {
            super(new URI(serverUrl));
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            Log.d("WebSocket","On open");

        }

        @Override
        public void onMessage(String message) {
            // Handle incoming messages from the server.
            Log.d("WebSocket","On message");
            Log.d("WebSocket",message);
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            // WebSocket connection is closed.
            // Perform any necessary cleanup here.
            Log.d("WebSocket","On close");
        }

        @Override
        public void onError(Exception ex) {
            // Handle any errors that occur during the WebSocket connection.
            Log.d("WebSocket","On error");
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permissions[i]);
            }
        }

        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
}