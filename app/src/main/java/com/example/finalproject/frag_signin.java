package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.finalproject.Model.Token;
import com.example.finalproject.R;
import com.example.finalproject.Utils.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class frag_signin extends Fragment {

    private TextInputEditText username;
    private TextInputEditText password;
    private Button btn_signIn;
    public frag_signin() {
    }

    public static frag_signin newInstance() {
        frag_signin fragment = new frag_signin();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_signin, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        username = view.findViewById(R.id.si_username);
        password = view.findViewById(R.id.si_password);
        btn_signIn = view.findViewById(R.id.btn_signin);
        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
                networkExecutor.execute(new Runnable() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void run() {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://uiot.ixxc.dev")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        ApiService apiInterface = retrofit.create(ApiService.class);
                        Call<Token> call = apiInterface.getToken(
                                "openremote",
                                username.getText().toString(),
                                password.getText().toString(),
                                "password");
                        try {
                            Response<Token> response = call.execute();
                            if(response.isSuccessful()){
                                // notification successfull !!!
                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                intent.putExtra("accessToken", response.body().access_token);
                                intent.putExtra("refreshToken",response.body().refresh_token);
                                intent.putExtra("expireIn",response.body().expires_in);
                                startActivity(intent);
                                Log.d("SignIn",response.body().getAccess_token());
                            }
                            else{
                                // notification !! : "Tài khoản

                                Log.d("SignIn", response.message().toString());
                            }
                        } catch (IOException e) {
                            Log.d("SignIn","SignIn fail");
                        }
                    }
                });
            }
        });
    }
}