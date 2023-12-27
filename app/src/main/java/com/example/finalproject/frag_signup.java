package com.example.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.finalproject.Utils.ApiService;
import com.example.finalproject.Utils.CustomCookieJar;
import com.google.android.material.textfield.TextInputEditText;
import com.tencent.mmkv.MMKV;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

@AndroidEntryPoint
public class frag_signup extends Fragment {
    @Inject
    CustomCookieJar cookieJar = null;
    @Inject
    ApiService apiService;
    private Button signUpButton = null;
    private TextInputEditText firstNameEditText = null;
    private TextInputEditText lastNameEditText = null;
    private TextInputEditText usernameEditText = null;
    private TextInputEditText emailEditText = null;
    private TextInputEditText passwordEditText = null;
    private TextInputEditText confirmPasswordEditText = null;

    public frag_signup() {}

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
        signUpButton.setOnClickListener(v -> {
            String firstName = firstNameEditText.getText().toString();
            String lastName = lastNameEditText.getText().toString();
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            MMKV.defaultMMKV().clearAll();
            cookieJar.clear();

            ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
            networkExecutor.execute(() -> {
                try {
                    Call<ResponseBody> authUrlCall = apiService.getAuthSession(
                            "code",
                            "openremote",
                            "https://uiot.ixxc.dev/swagger/oauth2-redirect.html");
                    try {
                        //Step 1: request HTTP GET to get cookie
                        Response<ResponseBody> AuthUrlResponse = authUrlCall.execute();
                        if (AuthUrlResponse.isSuccessful()) {
                            String HTML = AuthUrlResponse.body().string(); // Extract HTML source
                            String RegUrl = ExtractFeature(HTML, "a", "href"); // Get registration URL

                            // Step 2: request 2th HTTP GET to have submit form
                            Call<ResponseBody> RegUrlCall = apiService.getPage(RegUrl);
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
                                            "");

                                    // Step 3: Submit to form
                                    Response<ResponseBody> SubmitRespond = SubmitCall.execute();
                                    if(SubmitRespond.code() == 200){
                                        //Notification here
                                        Timber.d(String.valueOf(SubmitRespond.code()));

                                    }
                                    else if(SubmitRespond.code() == 401){
                                        //Notification here

                                    }
                                    else Timber.d(String.valueOf(SubmitRespond.code()));
                                }
                                else Timber.d("Sign up failed with unknown error!");;

                            } catch (Exception e) { Timber.d(e); }
                        }
                        else Timber.d("Executing authenticate URL fail!");

                    } catch (Exception e) { Timber.d(e); }
                } catch (Exception e) { Timber.d(e); }
            });
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