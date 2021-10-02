package com.pumasi.surbay.pages.mypage;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.R;

public class SettingInfo2 extends AppCompatActivity {
    WebView wv_individual_info;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_info);

        getSupportActionBar().hide();
        back = findViewById(R.id.setting_info_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        wv_individual_info = findViewById(R.id.wv_info);
        wv_individual_info.getSettings().setLoadWithOverviewMode(true);
        wv_individual_info.getSettings().setUseWideViewPort(true);
        wv_individual_info.loadUrl("https://docs.google.com/document/d/e/2PACX-1vRGSj0KruvDK0y6V0s-AFVtTgEQj3Y4g9Ud0xioVZUhoClKDMOmO-HqTKCcQ-1SG9NGpo5TLnUQxFC5/pub");
    }
}