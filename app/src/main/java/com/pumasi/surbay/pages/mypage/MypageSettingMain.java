package com.pumasi.surbay.pages.mypage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.R;

public class MypageSettingMain extends AppCompatActivity {
    ImageView back;
    Button account;
    Button alerm;
    Button info;
    Button report;
    Button feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting_main);

        getSupportActionBar().hide();

        back = findViewById(R.id.settingmain_back);
        account = findViewById(R.id.setting_account);
        alerm = findViewById(R.id.setting_alerm);
        info = findViewById(R.id.setting_info);
        report = findViewById(R.id.setting_report);
        feedback = findViewById(R.id.setting_feedbacks);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, MypageSettingAccount.class);
                startActivity(intent);
            }
        });
        alerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, MypageSettingAlerm.class);
                startActivity(intent);
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, SettingInfo.class);
                startActivity(intent);
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, SettingReport.class);
                startActivity(intent);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingMain.this, SettingFeedbacks.class);
                startActivity(intent);
            }
        });
    }
}