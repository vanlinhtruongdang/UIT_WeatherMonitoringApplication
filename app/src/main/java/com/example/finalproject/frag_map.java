package com.example.finalproject;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.Log;
import android.widget.Button;

import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.finalproject.Utils.MyWebSocketClient;
import com.tencent.mmkv.MMKV;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class frag_map extends Fragment {

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    @Inject
    MyWebSocketClient wssClient;
    private MMKV kv = null;
    private MapView map = null;
    private long ZoomSpeed = 500;
    private double MinZoom = 18.0;
    private double MaxZoom = 21.0;
    private double ZoomLevel = 19.5;
    private GeoPoint UITLocation = new GeoPoint(10.870, 106.80324);
    private GeoPoint Station1 = new GeoPoint(10.869778736885038, 106.80280655508835);
    private GeoPoint Station2 = new GeoPoint(10.869778736885038, 106.80345028525176);
    private Button btn_center = null;
    private String accessToken = null;
    private Fragment currentFragment = null;

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
        MMKV.initialize(this.getContext());
        kv = MMKV.defaultMMKV();
        accessToken = kv.decodeString("AccessToken");
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        btn_center = view.findViewById(R.id.btn_center);
        map = view.findViewById(R.id.map);
        SetupMap(view,ctx);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        btn_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.getController().animateTo(UITLocation, ZoomLevel, ZoomSpeed);
            }
        });

    String[] Permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION};

    requestPermissionsIfNecessary(Permission);
    map.setOnTouchListener(new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (currentFragment != null) {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.remove(currentFragment).commit();
                currentFragment = null;
            }
            return false;
        }
    });
}

    @Override
    public void onResume() {
        super.onResume();
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
    //Hiển thị và thêm hiệu ứng
    private void replaceFragment(Fragment fragment, String message) {
        Bundle bundle = new Bundle();
        bundle.putString("message",message);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.move_up, 0, 0, R.anim.move_up);
        transaction.replace(R.id.map_dashboard, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
        currentFragment = fragment;
    }
    private void SetupMap(@NonNull View view, @NonNull Context ctx){
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
                map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
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
                try {
                    wssClient.getClient().send("REQUESTRESPONSE:{\"messageId\":\"read-assets:5zI6XqkQVSfdgOrZ1MyWEf:AssetEvent2\",\"event\":{\"eventType\":\"read-assets\",\"assetQuery\":{\"ids\":[\"5zI6XqkQVSfdgOrZ1MyWEf\"]}}}");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Timber.d(wssClient.uglyJson.substring(16,wssClient.uglyJson.length()));
                replaceFragment(new frag_map_dashboard1(),wssClient.uglyJson);//Hiển thị và thêm hiệu ứng
                return true;
            }
        });

        Station2Marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                map.getController().animateTo(Station2Marker.getPosition(), ZoomLevel, ZoomSpeed);
                try {
                    wssClient.getClient().send("REQUESTRESPONSE:{\"messageId\":\"read-assets:6iWtSbgqMQsVq8RPkJJ9vo:AssetEvent2\",\"event\":{\"eventType\":\"read-assets\",\"assetQuery\":{\"ids\":[\"6iWtSbgqMQsVq8RPkJJ9vo\"]}}}");
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Log.d("Mark2", wssClient.uglyJson.substring(16,wssClient.uglyJson.length()));
                replaceFragment(new frag_map_dashboard2(),wssClient.uglyJson);
                return true;
            }
        });
    }
}