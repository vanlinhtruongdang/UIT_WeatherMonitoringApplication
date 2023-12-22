package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.finalproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.tencent.mmkv.MMKV;

public class HomeActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

    private MMKV mmkvInt;
    private MMKV checkedMmkv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        MMKV.initialize(this);
//        mmkvInt = MMKV.defaultMMKV();
//        mmkvInt.encode("ranNum", 7);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setSelectedItemId(R.id.fab);

//        Log.d("TAG_home", "mmkvInt: "+mmkvInt.getInt("ranNum", 7));
//        Fragment frag_user = new frag_user();
//        ((com.example.finalproject.frag_user) frag_user).setMMKVInt(mmkvInt);


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.user) {
                    replaceFragment(new frag_user());
                    return true;
                } else if (itemId == R.id.graph) {
                    replaceFragment(new frag_graph());
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
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Xóa dữ liệu mmkv
        mmkvInt.clearAll();
        Log.d("TAG", "mmkvInt: "+mmkvInt.getInt("ranNum", 7));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction Transaction = fragmentManager.beginTransaction();
        Transaction.replace(R.id.frame_layout, fragment);
        Transaction.commit();
    }
}