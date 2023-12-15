package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.finalproject.R;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Switch btn_sw = findViewById(R.id.btn_switch);
        btn_sw.setChecked(false);
        FragmentManager fragMan = getSupportFragmentManager();
        fragMan
                .beginTransaction()
                .replace(R.id.frame_layout, frag_signin.newInstance())
                .commit();
        btn_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    FragmentManager fragMan = getSupportFragmentManager();
                    fragMan
                            .beginTransaction()
                            .replace(R.id.frame_layout, frag_signup.newInstance())
                            .commit();
                }
                else
                {
                    FragmentManager fragMan = getSupportFragmentManager();
                    fragMan
                            .beginTransaction()
                            .replace(R.id.frame_layout, frag_signin.newInstance())
                            .commit();
                }
            }
        });



    }
}