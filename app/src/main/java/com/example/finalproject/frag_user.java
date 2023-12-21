package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.finalproject.Model.Token;
import com.example.finalproject.Model.User;
import com.example.finalproject.R;
import com.example.finalproject.Utils.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class frag_user extends Fragment {

    private MMKV kv = null;
    private String accessToken;
    private TextView name;
    private TextView email;
    private String name_call;
    private String email_call;
    private Button btn_logout;
    public frag_user() {
        // Required empty public constructor
    }

    public static frag_user newInstance() {
        frag_user fragment = new frag_user();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.frag_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MMKV.initialize(this.getContext());
        kv = MMKV.defaultMMKV();
        accessToken = kv.decodeString("AccessToken");
        name = view.findViewById(R.id.tv_name);
        email = view.findViewById(R.id.tv_mail);
        btn_logout = view.findViewById(R.id.btn_logout);
        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
        networkExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://uiot.ixxc.dev")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiInterface = retrofit.create(ApiService.class);
                Call<User> call = apiInterface.getUser("Bearer " + accessToken);
                try {
                    Response<User> response = call.execute();
                    if(response.isSuccessful()){
                        // notification successfull !!!
                        name_call = response.body().lastName + " " + response.body().firstName;
                        email_call = response.body().email;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(name_call);
                                email.setText(email_call);
                            }
                        });
                    }
                    else{
                        // notification !! : "Tài khoản
                        Log.d("User",response.toString());
                    }
                } catch (IOException e) {
                    Log.d("User",e.getMessage().toString());
                }
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

}