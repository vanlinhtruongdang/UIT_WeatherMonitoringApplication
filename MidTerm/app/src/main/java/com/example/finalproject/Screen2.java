package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Screen2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen2);
        findViewById(R.id.btn_entersigup).setOnClickListener(this::onSignUpClick);
    }

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

    private void onSignUpClick(View view) {
        // Lấy dữ liệu từ các trường EditText
        TextInputLayout firstNameEditText = findViewById(R.id.et_fname);
        TextInputLayout lastNameEditText = findViewById(R.id.et_lname);
        TextInputLayout usernameEditText = findViewById(R.id.et_username);
        TextInputLayout emailEditText = findViewById(R.id.et_email);
        TextInputLayout passwordEditText = findViewById(R.id.et_password);
        TextInputLayout confirmPasswordEditText = findViewById(R.id.et_cf_pass);

        firstNameEditText.setError(null);
        lastNameEditText.setError(null);
        usernameEditText.setError(null);
        emailEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);

        String firstName = firstNameEditText.getEditText().getText().toString();
        String lastName = lastNameEditText.getEditText().getText().toString();
        String username = usernameEditText.getEditText().getText().toString();
        String email = emailEditText.getEditText().getText().toString();
        String password = passwordEditText.getEditText().getText().toString();
        String confirmPassword = confirmPasswordEditText.getEditText().getText().toString();
        String register = "";

        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
        networkExecutor.execute(() -> {
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
                                    String submitHTML = SubmitRespond.body().string();
                                    Log.d("SignUp",submitHTML);
                                    runOnUiThread(() ->
                                            Toast.makeText(getApplicationContext(), "Sign up successfully!", Toast.LENGTH_SHORT).show());
                                    //notification
                                    runOnUiThread(() -> {
                                        if(submitHTML.contains("id=\"username\" class=\"validate invalid\"") || username.isEmpty()){
                                            usernameEditText.setError("Invalid user");
                                        }
                                        if(submitHTML.contains("Invalid email address.") || email.isEmpty()){
                                            emailEditText.setError("Invalid email");
                                        }
                                        if(!password.equals(confirmPassword)){
                                            confirmPasswordEditText.setError("Password confirmation doesn't match.");
                                        }
                                        if(password.isEmpty() || confirmPassword.isEmpty()){
                                            passwordEditText.setError("Password is empty");
                                            confirmPasswordEditText.setError("Confirm password is empty");
                                        }
                                    });
                                }
                                else Log.d("SignUp", "Sign up fail!");

                            } catch (Exception e) { Log.d("SignIn", e.toString()); }
                        }
                        else Log.d("SignUp", "Executing authenticate URL fail!");

                    } catch (Exception e) { Log.d("SignUp", e.toString()); }
                } catch (Exception e) { Log.d("SignUp", e.toString()); }
        });
    }
}