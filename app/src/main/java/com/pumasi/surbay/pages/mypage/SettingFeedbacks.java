package com.pumasi.surbay.pages.mypage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.R;

public class SettingFeedbacks extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting_feedbacks);

        getSupportActionBar().hide();
    }
}
