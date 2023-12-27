package com.example.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.finalproject.Model.User;
import com.example.finalproject.Utils.ApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class frag_user extends Fragment {

    private MMKV kv = null;
    private String accessToken;
    private TextView name;
//    private TextView email;
    private String name_call;
//    private String email_call;
    private ImageView avt;
    private int[] avtList = {R.drawable.avt11, R.drawable.avt21, R.drawable.avt31, R.drawable.avt41, R.drawable.avt51};
    private int randomInt;
    public frag_user() {}
    public static frag_user newInstance() {
        frag_user fragment = new frag_user();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MMKV.initialize(this.getContext());
        kv = MMKV.defaultMMKV();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_user, container, false);
        avt = view.findViewById(R.id.iv_avt);
        Fragment defaultFragment = new frag_user_option();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frame_user, defaultFragment)
                .commit();
        //Random ava
        Log.d("TAG", "mmkvInt: "+kv.getInt("ranNum", 7));
        int retrievedValue = kv.decodeInt("ranNum");
        try {
            if (kv.getInt("ranNum", 7)==7) {
                Random random = new Random();
                randomInt = random.nextInt(avtList.length);
                avt.setImageResource(avtList[randomInt]);
                kv.encode("ranNum",randomInt);
            } else {
                avt.setImageResource(avtList[retrievedValue]);
            }
        }catch (Exception e){
            Log.d("TAG",e.getMessage());
        }
        Log.d("TAG", "mmkvInt: "+kv.getInt("ranNum", 7));

        //BackPress
        OnBackPressedDispatcher onBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Activity activity = getActivity();
                BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
                bottomNavigationView.setSelectedItemId(R.id.fab);
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new frag_home());
                transaction.commit();
            }
        };
        onBackPressedDispatcher.addCallback(getViewLifecycleOwner(), callback);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accessToken = kv.decodeString("AccessToken");
        name = view.findViewById(R.id.tv_name);
//        email = view.findViewById(R.id.tv_mail);
        Fragment defaultFragment = new frag_user_option();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.frame_user, defaultFragment)
                .commit();
        ExecutorService networkExecutor = Executors.newSingleThreadExecutor();
        networkExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://uiot.ixxc.dev")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiInterface = retrofit.create(ApiService.class);
                Call<User> call = apiInterface.getUser("Bearer " + accessToken);
                try {
                    Response<User> response = call.execute();
                    if(response.isSuccessful()){
                        // notification successfull !!!
                        name_call = response.body().lastName + " " + response.body().firstName;
//                        email_call = response.body().email;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                name.setText(name_call);
//                                email.setText(email_call);
                            }
                        });
                    }
                    else{
                        // notification !! : "Tài khoản
                        Log.d("User",response.toString());
                    }
                } catch (IOException e) {
                    Log.d("User",e.getMessage().toString());
                }
            }
        });

    }

}