package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_signup = findViewById(R.id.btn_signup);
        Button btn_signin = findViewById(R.id.btn_signin);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Screen2.class);
                startActivity(intent);
            }
        });
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Screen3.class);
                startActivity(intent);
            }
        });
        Animation animaAlpha = AnimationUtils.loadAnimation(this,R.anim.anim_alpha);

        ImageView logoUIT = findViewById(R.id.logo_uit);
        logoUIT.startAnimation(animaAlpha);

        TextView appName = findViewById(R.id.appName);
        appName.startAnimation(animaAlpha);
    }
}