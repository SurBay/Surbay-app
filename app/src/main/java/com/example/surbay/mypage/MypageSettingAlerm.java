package com.example.surbay.mypage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import com.example.surbay.R;

public class MypageSettingAlerm extends AppCompatActivity {

    ImageView back;
    Switch main;
    Switch sound;
    Switch vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting_alerm);

        back = findViewById(R.id.alerm_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        main = findViewById(R.id.alerm_main_switch);
        sound = findViewById(R.id.alerm_sound_switch);
        vibe = findViewById(R.id.alerm_vibe_switch);
    }
}