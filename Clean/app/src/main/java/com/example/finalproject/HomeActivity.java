package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.finalproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class HomeActivity extends AppCompatActivity {
    FloatingActionButton fab;
    FragmentManager fragmentManager;
    BottomNavigationView bottomNavigationView;

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
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction Transaction = fragmentManager.beginTransaction();
        Transaction.replace(R.id.frame_layout, fragment);
        Transaction.commit();
    }
}