package com.example.surbay.mypage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.surbay.LoginActivity;
import com.example.surbay.MainActivity;
import com.example.surbay.R;
import com.example.surbay.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PwChange extends AppCompatActivity {
    String p2 = "^(?=.*[A-Za-z])(?=.*[0-9]).{6,20}$";

    EditText oripasswordEditText;
    TextView oripasswordcheckEditText;
    EditText newpasswordEditText;
    EditText newpasswordcheckEditText;
    TextView newpwchecklength;
    TextView newpwcheckword;
    ImageButton orivisibletoggle;
    ImageButton newvisibletoggle;

    String oripassword;
    String newpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_change);

        oripasswordEditText = findViewById(R.id.pwchange_origin_password);
        orivisibletoggle = findViewById(R.id.pwchange_origin_visible_toggle);
        oripasswordcheckEditText = findViewById(R.id.pwchange_check_oripw);

        newpasswordEditText = findViewById(R.id.pwchange_new_password);
        newvisibletoggle = findViewById(R.id.pwchange_new_visible_toggle);
        newpwchecklength = findViewById(R.id.pwchange_pw_length);
        newpasswordcheckEditText = findViewById(R.id.pwchange_new_password_check);
        newpwcheckword = findViewById(R.id.pwchange_check_newpw);

        oripasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (oripasswordcheckEditText.getText().toString().length() > 0){
                        if (oripasswordEditText.getText().toString().equals(oripassword)){
                            oripasswordcheckEditText.setVisibility(View.GONE);
                            oripasswordcheckEditText.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            oripasswordcheckEditText.setText("일치하지 않습니다");
                            oripasswordcheckEditText.setTextColor(getResources().getColor(R.color.red));
                            oripasswordcheckEditText.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        newpasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pw = newpasswordEditText.getText().toString();
                    if (pw.length() < 6 || !pwChk(pw)){
                        newpwchecklength.setVisibility(View.VISIBLE);
                        newpwchecklength.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        newpwchecklength.setVisibility(View.GONE);
                    }
                }
            }
        });
        newpasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                newpwchecklength.setVisibility(View.GONE);
            }
        });

        newpasswordcheckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (newpasswordcheckEditText.getText().toString().length() > 0){
                        if (newpasswordEditText.getText().toString().equals(newpasswordcheckEditText.getText().toString())){
                            newpwcheckword.setVisibility(View.GONE);
                            newpwcheckword.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            newpwcheckword.setText("비밀번호가 불일치합니다");
                            newpwcheckword.setTextColor(getResources().getColor(R.color.red));
                            newpwcheckword.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        newpasswordcheckEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                newpwcheckword.setVisibility(View.GONE);
            }
        });

    }

    public boolean pwChk(String pw){
        boolean check = false;
        Matcher m = Pattern.compile(p2).matcher(pw);
        if (m.find()){
            check = true;
        }

        return check;
    }
}