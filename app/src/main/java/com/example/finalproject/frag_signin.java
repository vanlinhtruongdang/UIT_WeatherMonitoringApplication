package com.example.finalproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.finalproject.Model.Token;
import com.example.finalproject.Utils.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.tencent.mmkv.MMKV;


public class frag_signin extends Fragment {
    private TextInputEditText username = null;
    private TextInputEditText password = null;
    private Button btn_signIn = null;
    private MMKV kv = null;
    public frag_signin() {
    }

    public static frag_signin newInstance() {
        frag_signin fragment = new frag_signin();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kv = MMKV.defaultMMKV();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_signin, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();

        OkHttpClient okHttpClient = mainActivity.getHTTPClient();
        Retrofit retrofit = mainActivity.getRetrofit();
        ApiService apiService = mainActivity.getAPIService();

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
                        Call<Token> call = apiService.getToken(
                                "openremote",
                                username.getText().toString(),
                                password.getText().toString(),
                                "password");
                        try {
                            Response<Token> response = call.execute();
                            if(response.isSuccessful()){
                                String AccessToken = response.body().getAccess_token();
                                kv.encode("AccessToken", AccessToken);

                                Intent intent = new Intent(getActivity(), HomeActivity.class);
                                startActivity(intent);
                                getActivity().finish();
                                Log.d("SignIn",response.body().getAccess_token());
                            }
                            else{
                                Log.d("SignIn", response.message().toString());
                            }
                        } catch (Exception e) {
                            Log.d("SignIn",e.toString());
                        }
                    }
                });
            }
        });
    }
}