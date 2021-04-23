package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.app.MediaRouteButton;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.DomainSearchDialog;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.mypage.SettingInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;
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