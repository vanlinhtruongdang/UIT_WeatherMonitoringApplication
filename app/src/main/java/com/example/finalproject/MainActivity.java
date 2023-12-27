package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.Switch;

import com.example.finalproject.Utils.ApiService;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    @Inject
    Retrofit retrofit = null;
    @Inject
    OkHttpClient okHttpClient = null;
    @Inject
    ApiService APIService = null;
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
        btn_sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FragmentManager fragMan1 = getSupportFragmentManager();
            if (isChecked)
            {
                fragMan1
                        .beginTransaction()
                        .replace(R.id.frame_layout, frag_signup.newInstance())
                        .commit();
            }
            else
            {
                fragMan1
                        .beginTransaction()
                        .replace(R.id.frame_layout, frag_signin.newInstance())
                        .commit();
            }
        });
    }
}