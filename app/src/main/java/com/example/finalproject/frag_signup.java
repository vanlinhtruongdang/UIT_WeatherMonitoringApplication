package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.finalproject.Utils.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;


public class frag_signup extends Fragment {

    private Button signUpButton;
    private TextInputEditText firstNameEditText;
    private TextInputEditText lastNameEditText;
    private TextInputEditText usernameEditText ;
    private TextInputEditText emailEditText ;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    public frag_signup() {
        // Required empty public constructor
    }

    public static frag_signup newInstance() {
        frag_signup fragment = new frag_signup();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpButton = view.findViewById(R.id.btn_entersignup);
        firstNameEditText = view.findViewById(R.id.et_fname);
        lastNameEditText = view.findViewById(R.id.et_lname);
        usernameEditText = view.findViewById(R.id.et_username);
        emailEditText = view.findViewById(R.id.et_email);
        passwordEditText = view.findViewById(R.id.et_password);
        confirmPasswordEditText = view.findViewById(R.id.et_cf_password);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                    .cookieJar(new com.example.finalproject.Utils.CustomCookieJar())
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
                                            if(SubmitRespond.code() == 200){
                                                //Notification here

                                                Log.d("SignUp",Integer.toString(SubmitRespond.code()));
                                            }
                                            else if(SubmitRespond.code() == 401){
                                                //Notification

                                            } else{
                                                Log.d("SignUp",SubmitRespond.message().toString());
                                            }
                                        }
                                        else Log.d("SignUp", "Sign up fail!");

                                    } catch (Exception e) { Log.d("SignIn", e.toString()); }
                                }
                                else Log.d("SignUp", "Executing authenticate URL fail!");

                            } catch (Exception e) { Log.d("SignUp", e.toString()); }
                        } catch (Exception e) { Log.d("SignUp", e.toString()); }
                    }
                });
            }
        });
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
}