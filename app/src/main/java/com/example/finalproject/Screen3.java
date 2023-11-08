package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Screen3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen3);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }
    public void resetClick(View view)
    {
        Intent intent = new Intent(this, Screen4.class);
        startActivity(intent);
    }
}