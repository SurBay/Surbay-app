package com.pumasi.surbay.pages.mypage;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.R;

public class SettingInfo extends AppCompatActivity {
    WebView wv_service_info;
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

        wv_service_info = findViewById(R.id.wv_info);
        wv_service_info.getSettings().setLoadWithOverviewMode(true);
        wv_service_info.getSettings().setUseWideViewPort(true);
        wv_service_info.loadUrl("https://docs.google.com/document/d/e/2PACX-1vQHbtAwX-Q7EvEzdIx_SmCArahMNlqANCt8I6hPF4i4B9TT9CokPJY0_4Bmo9jeUY2gDBcTsT77dk5P/pub");
    }
}