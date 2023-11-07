package com.example.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.Executors;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.internal.http2.Header;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    protected String ExtractFeature(String html, String tag, String attribute) {
        Document document = Jsoup.parse(html);
        Element foundElement = document.select(tag).first();

        if (foundElement != null) {
            String elementValue = foundElement.attr(attribute);
            return elementValue;
        } else {
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signUpButton = findViewById(R.id.signUpButton);
        EditText firstNameEditText = findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = findViewById(R.id.lastNameEditText);
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy dữ liệu từ các trường EditText
                String firstName = firstNameEditText.getText().toString();
                String lastName = lastNameEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String confirmPassword = confirmPasswordEditText.getText().toString();
                String register = "";

                ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
                networkExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                    .cookieJar(new CustomCookieJar())
                                    .build();

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://uiot.ixxc.dev/")
                                    .client(okHttpClient)
                                    .build();

                            ApiService apiService = retrofit.create(ApiService.class);
                            Call<ResponseBody> authUrlCall = apiService.getAuthUrl(
                                    "code",
                                    "openremote",
                                    "https://uiot.ixxc.dev/swagger/oauth2-redirect.html");
                            try {
                                //Step 1: request HTTP GET to get cookie
                                Response<ResponseBody> AuthUrlResponse = authUrlCall.execute();
                                if (AuthUrlResponse.isSuccessful()) {
                                    String HTML = AuthUrlResponse.body().string(); // Extract HTML source
                                    Headers Header = AuthUrlResponse.headers();
                                    String RegUrl = ExtractFeature(HTML, "a", "href"); // Get registration URL

                                    // Step 2: request 2th HTTP GET to have submit form
                                    Call<ResponseBody> RegUrlCall = apiService.getRegisterPage(RegUrl);
                                    try {
                                        Response<ResponseBody> RegUrlResponse = RegUrlCall.execute();
                                        if (RegUrlResponse.isSuccessful())
                                        {
                                            HTML = RegUrlResponse.body().string();

                                            String FormSubmitURL = ExtractFeature(HTML, "form", "action"); // Get registration submit form
                                            Call<ResponseBody> SubmitCall = apiService.signUp(
                                                    FormSubmitURL,
                                                    firstName,
                                                    lastName,
                                                    username,
                                                    email,
                                                    password,
                                                    confirmPassword,
                                                    register);

                                            // Step 3: Submit to form
                                            Response<ResponseBody> SubmitRespond = SubmitCall.execute();
                                            Log.d("SignIn", SubmitRespond.body().string());
                                        }
                                        else Log.d("SignIn", "Sign up fail!");

                                    } catch (Exception e) { Log.d("SignIn", e.toString()); }
                                }
                                else Log.d("SignIn", "Executing authenticate URL fail!");

                            } catch (Exception e) { Log.d("SignIn", e.toString()); }
                        } catch (Exception e) { Log.d("SignIn", e.toString()); }
                    }
                });
            }
        });
    }
}

