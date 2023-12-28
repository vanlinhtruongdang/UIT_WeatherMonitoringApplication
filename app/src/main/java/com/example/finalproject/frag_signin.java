package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalproject.Utils.ApiService;
import com.example.finalproject.Utils.CustomCookieJar;
import com.google.android.material.textfield.TextInputEditText;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

import com.tencent.mmkv.MMKV;

import javax.inject.Inject;


@AndroidEntryPoint
public class frag_signin extends Fragment {
    private TextInputEditText username = null, password = null;
    private Button btn_signIn = null;
    private MMKV kv = null;
    @Inject
    CustomCookieJar cookieJar = null;
    @Inject
    ApiService apiService = null;
    private LoadingDialog loadingDialog;

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
        return inflater.inflate(R.layout.frag_signin, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        username = view.findViewById(R.id.si_username);
        password = view.findViewById(R.id.si_password);
        btn_signIn = view.findViewById(R.id.btn_signin);
        loadingDialog = new LoadingDialog(requireContext());
        btn_signIn.setOnClickListener(v -> {

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    kv = MMKV.defaultMMKV();
                    kv.clearAll();
                    cookieJar.clear();
                    ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
                    networkExecutor.execute(() -> {
                        var getAuthPage = apiService.getAuthSession(
                                "code",
                                "openremote",
                                "https://uiot.ixxc.dev/swagger/oauth2-redirect.html"
                        );
                        try {
                            var authPage = getAuthPage.execute();
                            if (authPage.isSuccessful()) {
                                var authHTML = authPage.body().string(); // Extract HTML source
                                var authURL = ExtractFeature(authHTML, "form", "action"); // Get auth URL

                                var withCookies = apiService.getTokenWithCookies(
                                        authURL,
                                        username.getText().toString(),
                                        password.getText().toString()
                                );

                                var tokenCall = apiService.getTokenWithKeycloak(
                                        "openremote",
                                        username.getText().toString(),
                                        password.getText().toString(),
                                        "password"
                                );

                                withCookies.execute();

                                var token = tokenCall.execute();

                                if(token.isSuccessful()){
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(getActivity(), "Login successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    String AccessToken = token.body().getAccess_token();
                                    kv.encode("AccessToken", AccessToken);
                                    kv.encode("password",password.getText().toString());
                                    Intent intent = new Intent(getActivity(), HomeActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                    Timber.d(AccessToken);
                                }
                                else{
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            username.setText("");
                                            password.setText("");
                                            Toast.makeText(getActivity(), "Unauthorized", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    Timber.d(token.message().toString());
                                }
                            }
                        } catch (Exception e) {
                            Timber.d(e);
                        }
                    });
                    if (loadingDialog != null && loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }
                }
            },2000);
            loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            loadingDialog.show();
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