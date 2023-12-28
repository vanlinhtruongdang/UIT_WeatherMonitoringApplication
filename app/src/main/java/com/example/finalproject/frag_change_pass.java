package com.example.finalproject;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalproject.Utils.ApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.tencent.mmkv.MMKV;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class frag_change_pass extends Fragment {
    private Button btn_back = null, btn_save = null;
    private TextInputEditText password = null, newPassword = null, confirmPassword = null;
    private MMKV kv = null;
    @Inject
    ApiService apiService = null;

    public frag_change_pass() { }

    public static frag_change_pass newInstance() {
        frag_change_pass fragment = new frag_change_pass();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_change_pass, container, false);
        password = view.findViewById(R.id.et_password);
        newPassword = view.findViewById(R.id.et_newPass);
        confirmPassword = view.findViewById(R.id.et_confirm);

        btn_back = view.findViewById(R.id.btn_back);
        btn_save = view.findViewById(R.id.btn_saveAcc);
        kv = MMKV.defaultMMKV();
        btn_save.setOnClickListener(v -> {
            String act_password = kv.decodeString("password");
            String pass = password.getText().toString();
            String newPass = newPassword.getText().toString();
            String confPass = confirmPassword.getText().toString();
            if(pass.equals("") || newPass.equals("") || confPass.equals("")){
                ToastMessage("Please fill all the field");
            } else if(!pass.equals(act_password)){
                ToastMessage("Password not correct");
            } else if (!newPass.equals(confPass)) {
                ToastMessage("Password confirm not match");
            } else {
                var networkExecutor = Executors.newSingleThreadExecutor();
                var getPasswordPageCall = apiService.getPasswordPage();
                networkExecutor.execute(() -> {
                    try {
                        var getPasswordPageExecuter = getPasswordPageCall.execute();
                        var changePasswordHTML = getPasswordPageExecuter.body().string();
                        var stateChecker = ExtractStateChecker(changePasswordHTML);

                        var changePasswordCall = apiService.changePassword(
                                stateChecker,
                                pass,
                                newPass,
                                confPass,
                                "Save"
                        );
                        changePasswordCall.execute();
                        ToastMessage("Password change successfully");
                    } catch (Exception e){
                        Timber.d(e);
                    }
                });
            }
        });

        btn_back.setOnClickListener(v -> {
            Fragment parent = getParentFragment();
            Fragment newFragment = new frag_user_option();
            parent.getChildFragmentManager().beginTransaction()
                    .replace(R.id.frame_user, newFragment)
                    .addToBackStack(null)
                    .commit();
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
    protected String ExtractStateChecker(String html) {
        Document document = Jsoup.parse(html);
        Element foundElement = document.getElementById("stateChecker");

        if (foundElement != null) {
            String elementValue = foundElement.attr("value");
            return elementValue;
        } else {
            return null;
        }
    }
    private void ToastMessage(String message){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                password.setText("");
                newPassword.setText("");
                confirmPassword.setText("");
            }
        });
    }
}