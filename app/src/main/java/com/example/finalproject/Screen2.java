package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Screen2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen2);
    }
    public void signUpClicked(View view) {
        EditText et_password = findViewById(R.id.et_password);
        String password = et_password.getText().toString();
        EditText et_cf = findViewById(R.id.et_cf_pass);
        String confirmPassword = et_cf.getText().toString();
        if (password.equals(confirmPassword)) {
            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
        }
    }
}