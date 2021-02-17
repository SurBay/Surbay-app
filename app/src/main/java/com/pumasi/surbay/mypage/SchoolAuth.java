package com.pumasi.surbay.mypage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.pumasi.surbay.R;
import com.google.firebase.auth.FirebaseAuth;

public class SchoolAuth extends AppCompatActivity {
    final String[] spinner_email = {"이메일 선택","korea.ac.kr"};
    EditText useridEditText;
    Spinner useremailSpinner;
    TextView phone_checkTextview;
    EditText phonecheckEditText;
    TextView signupTimer;
    TextView signupPCNText;
    int posadd;

    String typesmsCode;
    String getverificationId;
    FirebaseAuth auth;

    String userid;

    CountDownTimer CDT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_auth);

        useridEditText = findViewById(R.id.schoolauth_userid);
        useremailSpinner = findViewById(R.id.schoolauth_username_email);
        phone_checkTextview = findViewById(R.id.schoolauth_userphone_check);
        phonecheckEditText = findViewById(R.id.schoolauth_phone_checknumber);
        signupTimer = findViewById(R.id.schoolauth_timer);
        signupPCNText = findViewById(R.id.schoolauth_PCNtext);

        ArrayAdapter emailadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner_email);
        emailadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        useremailSpinner.setAdapter(emailadapter);

        Button signupButton = findViewById(R.id.schoolauth_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        useremailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posadd = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void TimerStart(){
        signupTimer.setVisibility(View.VISIBLE);
        signupPCNText.setVisibility(View.VISIBLE);
        CDT = new CountDownTimer(180*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalsec = millisUntilFinished / 1000;
                long min = totalsec/60;
                long sec = totalsec - min*60;


                String minstr = String.format("%02d", min);
                String secstr = String.format("%02d", sec);
                signupTimer.setText(minstr+":"+secstr);
            }

            @Override
            public void onFinish() {
                signupPCNText.setText("인증에 실패했습니다. 인증하기를 다시 시도해주세요");
            }
        };
        CDT.start();
    }
}