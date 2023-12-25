package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.finalproject.Utils.MyWebSocketClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.tencent.mmkv.MMKV;

import java.net.URISyntaxException;

public class HomeActivity extends MainActivity {
    private MyWebSocketClient wssClient = null;
    private MMKV kv = null;
    private String accessToken = null;
    private FloatingActionButton fab = null;
    private FragmentManager fragmentManager = null;
    private BottomNavigationView bottomNavigationView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.fab);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.user) {
                    replaceFragment(new frag_user());
                    return true;
                } else if (itemId == R.id.graph) {
                    replaceFragment(new frag_chart());
                    return true;
                }
                return false;
            }
        });
        fragmentManager = getSupportFragmentManager();
        replaceFragment(new frag_home());

        fab = findViewById(R.id.btn_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new frag_home());
                bottomNavigationView.setSelectedItemId(R.id.fab);
            }
        });

        kv = MMKV.defaultMMKV();
        accessToken = kv.decodeString("AccessToken");
        try{
            wssClient = new MyWebSocketClient("wss://uiot.ixxc.dev/websocket/events?Realm=master&Authorization=Bearer%20"+accessToken);
            wssClient.connect();

            if (wssClient.isOpen())
                Log.d("WebSocket","Successfully!");

        } catch (URISyntaxException e) {
            Log.d("WebSocket","Failed");
        }
    }


    private void replaceFragment(Fragment fragment) {
        FragmentTransaction Transaction = fragmentManager.beginTransaction();
        Transaction.replace(R.id.frame_layout, fragment);
        Transaction.commit();
    }

    public MyWebSocketClient getSocket(){
        return wssClient;
    }
}