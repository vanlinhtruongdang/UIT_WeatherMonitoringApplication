package com.example.finalproject;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import com.example.finalproject.Utils.MyWebSocketClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tencent.mmkv.MMKV;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class HomeActivity extends MainActivity {
    private MMKV kv = null;
    private String accessToken = null;
    private FloatingActionButton fab = null;
    private FragmentManager fragmentManager = null;
    private BottomNavigationView bottomNavigationView = null;

    @Inject
    public MyWebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.fab);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.user) {
                replaceFragment(new frag_user(),"frame_user");
                return true;
            } else if (itemId == R.id.graph) {
                replaceFragment(new frag_chart(),"frame_chart");
                return true;
            }
            return false;
        });
        fragmentManager = getSupportFragmentManager();
        replaceFragment(new frag_home(),"frame_home");

        fab = findViewById(R.id.btn_home);
        fab.setOnClickListener(v -> {
            replaceFragment(new frag_home(),"frame_home");
            bottomNavigationView.setSelectedItemId(R.id.fab);
        });

        kv = MMKV.defaultMMKV();
        accessToken = kv.decodeString("AccessToken");
        try{
            webSocketClient.setServerUrl("wss://uiot.ixxc.dev/websocket/events?Realm=master&Authorization=Bearer%20"+accessToken);
            webSocketClient.getClient().connect();
            if (webSocketClient.getClient().isOpen())
                Timber.d("Websocket open successfully!");
        } catch (Exception e) {
            Timber.d(e);
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentTransaction Transaction = fragmentManager.beginTransaction();
        Transaction.replace(R.id.frame_layout, fragment, tag);
        Transaction.commit();
    }
}