package com.example.surbay.mypage;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.surbay.R;

public class MypageSettingMain extends AppCompatActivity {
    ImageView back;
    Button account;
    Button alerm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting_main);

        getSupportActionBar().hide();

        back = findViewById(R.id.settingmain_back);
        account = findViewById(R.id.setting_account);
        alerm = findViewById(R.id.setting_alerm);

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
    }
}