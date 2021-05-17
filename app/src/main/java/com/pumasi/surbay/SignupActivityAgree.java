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
import android.os.SystemClock;
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
import android.widget.CheckBox;
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
import com.pumasi.surbay.mypage.SettingInfo2;

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

public class SignupActivityAgree extends AppCompatActivity {

    private Long mLastClickTime = 0L;

    TextView user_agree_info;
    TextView user_agree_info2;
    Button signup_next;

    CheckBox user_agree_check;
    CheckBox user_agree_check2;

    String userid;
    String password;
    String name;
    Integer gender;
    Integer yearBirth;

    private RelativeLayout loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_agree_layout);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        password = intent.getStringExtra("password");
        name = intent.getStringExtra("name");
        gender = intent.getIntExtra("gender", 2);
        yearBirth = intent.getIntExtra("yearBirth", 0);
        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);
        signup_next = findViewById(R.id.signup_agree_next);
        user_agree_info = findViewById(R.id.user_agree_info);
        user_agree_info2 = findViewById(R.id.user_agree_info2);
        user_agree_check = findViewById(R.id.user_agree_check);
        user_agree_check2 = findViewById(R.id.user_agree_check2);

        user_agree_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivityAgree.this, SettingInfo2.class));
            }
        });
        user_agree_info2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivityAgree.this, SettingInfo.class));
            }
        });



        signup_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 주기 1초
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    // 동의 시에만 다음 페이지로 이동
                    if (user_agree_check.isChecked() && user_agree_check2.isChecked()) {
                        Intent intent1 = new Intent(SignupActivityAgree.this, SignupActivityDone.class);
                        intent1.putExtra("userid", userid);
                        intent1.putExtra("password", password);
                        intent1.putExtra("name", name);
                        intent1.putExtra("gender", gender);
                        intent1.putExtra("yearBirth", yearBirth);
                        startActivity(intent1);
                    } else {
                        Toast.makeText(SignupActivityAgree.this, "약관을 모두 동의해주십시오", Toast.LENGTH_SHORT).show();
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                }


            }
        });







    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
}