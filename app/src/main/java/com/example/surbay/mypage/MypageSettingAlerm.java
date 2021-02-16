package com.example.surbay.mypage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.surbay.R;

public class MypageSettingAlerm extends AppCompatActivity {

    ImageView back;
    Switch main;
    Switch sound;
    Switch vibe;

    boolean mainselect;
    boolean soundselect;
    boolean vibeselect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting_alerm);

        getSupportActionBar().hide();

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

        SharedPreferences alerm = getSharedPreferences("alerm", Activity.MODE_PRIVATE);
        mainselect = alerm.getBoolean("mainselect", true);
        soundselect = alerm.getBoolean("soundselect", true);
        vibeselect = alerm.getBoolean("vibeselect", true);

        main.setChecked(mainselect);
        sound.setChecked(soundselect);
        vibe.setChecked(vibeselect);

        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!main.isChecked()){
                    mainselect = false;
                    soundselect = false;
                    vibeselect = false;

                    sound.setChecked(false);
                    vibe.setChecked(false);

                    SharedPreferences alerm = getSharedPreferences("alerm", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor alermedit = alerm.edit();
                    alermedit.putBoolean("mainselect", mainselect);
                    alermedit.putBoolean("soundselect", soundselect);
                    alermedit.putBoolean("token", vibeselect);
                    alermedit.commit();
                } else {
                    mainselect = true;
                    soundselect = true;
                    vibeselect = true;

                    sound.setChecked(true);
                    vibe.setChecked(true);

                    SharedPreferences alerm = getSharedPreferences("alerm", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor alermedit = alerm.edit();
                    alermedit.putBoolean("mainselect", mainselect);
                    alermedit.putBoolean("soundselect", soundselect);
                    alermedit.putBoolean("vibeselect", vibeselect);
                    alermedit.commit();
                }
            }
        });
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundselect = !soundselect;

                sound.setChecked(soundselect);

                SharedPreferences alerm = getSharedPreferences("alerm", Activity.MODE_PRIVATE);
                SharedPreferences.Editor alermedit = alerm.edit();
                alermedit.putBoolean("soundselect", soundselect);
                alermedit.commit();
            }
        });
        vibe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibeselect = !vibeselect;

                vibe.setChecked(vibeselect);

                SharedPreferences alerm = getSharedPreferences("alerm", Activity.MODE_PRIVATE);
                SharedPreferences.Editor alermedit = alerm.edit();
                alermedit.putBoolean("vibeselect", vibeselect);
                alermedit.commit();
            }
        });
    }
}