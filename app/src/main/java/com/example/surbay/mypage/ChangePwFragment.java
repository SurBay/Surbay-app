package com.example.surbay.mypage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.surbay.FindIdActivity;
import com.example.surbay.R;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePwFragment extends Fragment {
    private View view;
    String p2 = "^(?=.*[A-Za-z])(?=.*[0-9]).{6,20}$";
    EditText passwordEditText;
    EditText passwordcheckEditText;
    TextView pwchecklength;
    TextView pwcheckword;
    ImageButton visibletoggle;
    Button button;
    String password;
    String phone;

    public static ChangePwFragment newInstance() {
        return new ChangePwFragment();
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.changepw_fragment, container, false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Bundle bundle = getArguments();

        if(bundle != null){
            phone = bundle.getString("phoneNumber");

        }

        passwordEditText = view.findViewById(R.id.changepw_password);
        passwordcheckEditText = view.findViewById(R.id.changepw_password_check);
        pwcheckword = view.findViewById(R.id.changepw_check_pw);
        pwchecklength = view.findViewById(R.id.changepw_pw_length);
        visibletoggle = (ImageButton)view.findViewById(R.id.changepw_visible_toggle);
        button = view.findViewById(R.id.changepw_button);
        button.setClickable(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updatePw(passwordEditText.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText((AppCompatActivity)getActivity(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        });

        visibletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_visible(passwordEditText);
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pw = passwordEditText.getText().toString();
                    if (pw.length() < 6 || !pwChk(pw)){
                        pwchecklength.setVisibility(View.VISIBLE);
                        pwchecklength.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        pwchecklength.setVisibility(View.GONE);
                    }
                }
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                pwchecklength.setVisibility(View.GONE);
            }
        });

        passwordcheckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (passwordcheckEditText.getText().toString().length() > 0){
                        if (passwordEditText.getText().toString().equals(passwordcheckEditText.getText().toString())){
                            pwcheckword.setVisibility(View.GONE);
                            pwcheckword.setTextColor(getResources().getColor(R.color.red));
                            button.setClickable(true);
                        } else {
                            pwcheckword.setText("비밀번호가 불일치합니다");
                            pwcheckword.setTextColor(getResources().getColor(R.color.red));
                            pwcheckword.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        passwordcheckEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                pwcheckword.setVisibility(View.GONE);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void change_visible(EditText v){
        if (v.getTag() == "0"){
            v.setTransformationMethod(null);
            v.setTag("1");
        } else {
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setTag("0");
        }
    }


    public boolean pwChk(String pw){
        boolean check = false;
        Matcher m = Pattern.compile(p2).matcher(pw);
        if (m.find()){
            check = true;
        }

        return check;
    }


    private void updatePw(String newpassword) throws Exception{
        String requestURL = "https://surbay-server.herokuapp.com/api/users/changepassword?phoneNumber=" + FindIdActivity.phone;
        try{
            RequestQueue requestQueue = Volley.newRequestQueue((AppCompatActivity)getActivity());
            JSONObject params = new JSONObject();
            params.put("userPassword", newpassword);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }
}
