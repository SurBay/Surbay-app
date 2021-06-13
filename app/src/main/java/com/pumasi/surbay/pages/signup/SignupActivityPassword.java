package com.pumasi.surbay.pages.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivityPassword extends AppCompatActivity {


    String p2 = "^(?=.*[A-Za-z])(?=.*[0-9]).{6,20}$";
    EditText passwordEditText;
    EditText passwordcheckEditText;
    TextView pwchecklength;
    TextView pwcheckword;
    Button signup_next;

    String userid;
    String password;

    ImageButton visibletoggle;

    private RelativeLayout loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_password_layout);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");

        passwordEditText = findViewById(R.id.password);
        passwordcheckEditText = findViewById(R.id.password_check);
        pwcheckword = findViewById(R.id.signup_check_pw);
        pwchecklength = findViewById(R.id.signup_pw_length);
        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);
        visibletoggle = findViewById(R.id.visible_toggle);
        signup_next = findViewById(R.id.signup_password_next);

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                String pw = passwordEditText.getText().toString();
                if(pw.length()!=0){
                    visibletoggle.setVisibility(View.VISIBLE);
                }else{
                    visibletoggle.setVisibility(View.INVISIBLE);
                }
                if (pw.length() < 6 || !pwChk(pw)){
                    pwchecklength.setVisibility(View.VISIBLE);
                    pwchecklength.setTextColor(getResources().getColor(R.color.red));
                } else {
                    pwchecklength.setVisibility(View.GONE);
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
                if (passwordcheckEditText.getText().toString().length() > 0){
                    if (passwordEditText.getText().toString().equals(passwordcheckEditText.getText().toString())){
                        pwcheckword.setVisibility(View.GONE);
                        pwcheckword.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        pwcheckword.setText("비밀번호가 불일치합니다");
                        pwcheckword.setTextColor(getResources().getColor(R.color.red));
                        pwcheckword.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        visibletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_visible(passwordEditText, visibletoggle);
            }
        });

        signup_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pwcheckword.getVisibility() == View.GONE){
                    password = passwordEditText.getText().toString();
                } else {
                    password = "";
                }

                if(password.length()==0){
                    CustomDialog customDialog = new CustomDialog(SignupActivityPassword.this, null);
                    customDialog.show();
                    customDialog.setMessage("비밀번호를 입력해주세요");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                Intent intent1 = new Intent(SignupActivityPassword.this, SignupActivityExtra.class);
                intent1.putExtra("userid", userid);
                intent1.putExtra("password", password);
                startActivity(intent1);

            }
        });




    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void change_visible(EditText v, ImageButton visibletoggle){
        if (v.getTag() == "0"){
            v.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            v.setSelection(v.getText().length());
            visibletoggle.setImageResource(R.drawable.ic_unvisibletoggle);
            v.setTag("1");
        } else {
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setSelection(v.getText().length());
            visibletoggle.setImageResource(R.drawable.visibletoggle);
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
}