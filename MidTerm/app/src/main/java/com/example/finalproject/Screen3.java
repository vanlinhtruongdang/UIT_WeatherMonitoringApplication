package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Model.Token;
import com.google.android.material.textfield.TextInputLayout;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Screen3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);
        Button SignIn = findViewById(R.id.btn_entersignin);
        SignIn.setOnClickListener(this::onSignInClick);
        findViewById(R.id.btn_reset).setOnClickListener(v ->
            startActivity(new Intent(this, Screen4.class)));
    }

    private void onSignInClick(View view) {
        TextInputLayout user = findViewById(R.id.et_username3);
        TextInputLayout pass = findViewById(R.id.et_password3);
        user.setError(null);
        pass.setError(null);
        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
        networkExecutor.execute(() -> {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://uiot.ixxc.dev")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiService apiInterface = retrofit.create(ApiService.class);
            Call<Token> call = apiInterface.getToken("openremote", user.getEditText().getText().toString(),pass.getEditText().getText().toString(), "password");
            Response<Token> response = null;
            try {
                response = call.execute();
                if(response.isSuccessful()){
                    // notification successfull !!!
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Login successfully!", Toast.LENGTH_SHORT).show());
                    Log.d("SignIn",response.body().toString());
                } else {
                    throw new Exception("Login unsuccessful: " + response.errorBody().string());
                }
            } catch (Exception e) {
                runOnUiThread(() -> {
                    user.setError("Something was wrong");
                    pass.setError("Something was wrong");
                });
                Log.d("SignIn", response != null ? response.message().toString() : e.toString());
            }
        });
    }
}