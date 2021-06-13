package com.pumasi.surbay.pages.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.R;
import com.pumasi.surbay.pages.mypage.SettingInfo;
import com.pumasi.surbay.pages.mypage.SettingInfo2;

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