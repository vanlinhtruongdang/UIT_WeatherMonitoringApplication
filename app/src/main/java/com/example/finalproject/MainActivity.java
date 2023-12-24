package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.finalproject.Utils.ApiService;
import com.example.finalproject.Utils.CustomCookieJar;
import com.tencent.mmkv.MMKV;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    protected Retrofit retrofit = null;
    protected OkHttpClient okHttpClient = null;
    protected ApiService APIService = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MMKV.initialize(this.getBaseContext());

        okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CustomCookieJar())
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl("https://uiot.ixxc.dev/")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService = retrofit.create(ApiService.class);

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
                FragmentManager fragMan = getSupportFragmentManager();
                if (isChecked)
                {
                    fragMan
                            .beginTransaction()
                            .replace(R.id.frame_layout, frag_signup.newInstance())
                            .commit();
                }
                else
                {
                    fragMan
                            .beginTransaction()
                            .replace(R.id.frame_layout, frag_signin.newInstance())
                            .commit();
                }
            }
        });
    }

    public Retrofit getRetrofit(){
        return retrofit;
    }

    public OkHttpClient getHTTPClient(){
        return okHttpClient;
    }

    public ApiService getAPIService(){
        return APIService;
    }
}