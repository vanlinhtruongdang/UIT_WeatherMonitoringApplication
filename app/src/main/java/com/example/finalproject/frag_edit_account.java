package com.example.finalproject;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalproject.Utils.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;
@AndroidEntryPoint
public class frag_edit_account extends Fragment {

    private Button btn_back = null,btn_save = null;
    private TextInputEditText ed_email = null, ed_firstName = null, ed_lastName = null;

    @Inject
    ApiService apiService = null;
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
        btn_save = view.findViewById(R.id.btn_saveAcc);
        ed_email = view.findViewById(R.id.et_email);
        ed_firstName = view.findViewById(R.id.et_fname);
        ed_lastName = view.findViewById(R.id.et_lname);
        btn_save.setOnClickListener(v -> {
            String email = ed_email.getText().toString();
            String fisrtName = ed_firstName.getText().toString();
            String lastName = ed_lastName.getText().toString();
            if(email.equals("") || fisrtName.equals("") || lastName.equals("")){
                ToastMessage("Please fill all the field");
            } else if (!isValidEmail(email)){
                ToastMessage("Email not valid");
            } else {
                var networkExecutor = Executors.newSingleThreadExecutor();
                var getAccountPageCall = apiService.getAccountPage();
                networkExecutor.execute(() -> {
                    try {
                        var getAccountPageExecuter = getAccountPageCall.execute();
                        var changePasswordHTML = getAccountPageExecuter.body().string();
                        var stateChecker = ExtractStateChecker(changePasswordHTML);

                        var changeAccountCall = apiService.changeAccount(
                                stateChecker,
                                email,
                                fisrtName,
                                lastName,
                                "Save"
                        );
                        changeAccountCall.execute();
                        ToastMessage("Account have been updated");

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
                ed_email.setText("");
                ed_firstName.setText("");
                ed_lastName.setText("");
            }
        });
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}