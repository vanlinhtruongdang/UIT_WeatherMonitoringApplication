package com.example.finalproject;

import android.app.Activity;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class frag_edit_account extends Fragment {
    private Button btn_back;
    public frag_edit_account() {
    }


    public static frag_edit_account newInstance() {
        frag_edit_account fragment = new frag_edit_account();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_edit_account, container, false);
        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment parent = getParentFragment();
                Fragment newFragment = new frag_user_option();
                parent.getChildFragmentManager().beginTransaction()
                        .replace(R.id.frame_user, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        //BackPress
        OnBackPressedDispatcher onBackPressedDispatcher = requireActivity().getOnBackPressedDispatcher();
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Fragment parent = getParentFragment();
                Fragment newFragment = new frag_user_option();
                parent.getChildFragmentManager().beginTransaction()
                        .replace(R.id.frame_user, newFragment)
                        .addToBackStack(null)
                        .commit();
            }
        };
        onBackPressedDispatcher.addCallback(getViewLifecycleOwner(), callback);
        return view;

    }

}