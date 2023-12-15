package com.example.finalproject;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.finalproject.R;
import com.tencent.mmkv.MMKV;

import java.util.Random;


public class frag_user extends Fragment {

    private ImageView avt;
    private int[] avtList = {R.drawable.avt11, R.drawable.avt21, R.drawable.avt31, R.drawable.avt41, R.drawable.avt51};
    private int randomInt;
    private MMKV mmkvInt;

    // Hàm để act có thể truyền mmkv vào frag
    public void setMMKVInt(MMKV mmkv) {
        this.mmkvInt = mmkv;

    }
    public frag_user() {

    }

    public static frag_user newInstance() {
        frag_user fragment = new frag_user();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_user, container, false);
        avt = view.findViewById(R.id.iv_avt);

        //Random ava
        Log.d("TAG", "mmkvInt: "+mmkvInt.getInt("ranNum", 7));
        int retrievedValue = mmkvInt.decodeInt("ranNum", 0);
        if (mmkvInt.getInt("ranNum", 7)==7) {
            Random random = new Random();
            randomInt = random.nextInt(avtList.length);
            avt.setImageResource(avtList[randomInt]);
            mmkvInt.encode("ranNum",randomInt);
        } else {
            avt.setImageResource(avtList[retrievedValue]);
        }
        Log.d("TAG", "mmkvInt: "+mmkvInt.getInt("ranNum", 7));


        //BackPress
        OnBackPressedDispatcher onBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, new frag_home());
                transaction.commit();
            }
        };
        onBackPressedDispatcher.addCallback(getViewLifecycleOwner(), callback);
        return view;
    }
}