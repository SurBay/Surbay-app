package com.pumasi.surbay.pages.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.UserPersonalInfo;

public class MypageSettingMain extends AppCompatActivity {
    private ImageView ib_back;
    private Button btn_setting_account;
    private Button btn_setting_alarm;
    private Button btn_setting_service_info;
    private Button btn_setting_individual_info;
    private Button btn_setting_report;
    private Button btn_setting_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting_main);

        getSupportActionBar().hide();

        ib_back = findViewById(R.id.ib_back);
        btn_setting_account = findViewById(R.id.btn_setting_account);
        btn_setting_alarm = findViewById(R.id.btn_setting_alarm);
        btn_setting_service_info = findViewById(R.id.btn_setting_service_info);
        btn_setting_individual_info = findViewById(R.id.btn_setting_individual_info);
        btn_setting_report = findViewById(R.id.btn_setting_report);
        btn_setting_feedback = findViewById(R.id.btn_setting_feedback);

        if (UserPersonalInfo.userID.equals("nonMember")) {
            btn_setting_alarm.setVisibility(View.GONE);
        }
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_setting_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, MypageSettingAccount.class);
                startActivity(intent);
            }
        });
        btn_setting_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, MypageSettingAlerm.class);
                startActivity(intent);
            }
        });
        btn_setting_service_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, SettingInfo.class);
                startActivity(intent);
            }
        });
        btn_setting_individual_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, SettingInfo2.class);
                startActivity(intent);
            }
        });
        btn_setting_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, SettingReport.class);
                startActivity(intent);
            }
        });
        btn_setting_feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, SettingFeedbacks.class);
                startActivity(intent);
            }
        });
    }
}