package com.pumasi.surbay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.classfile.UserPersonalInfo;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mContext = getApplicationContext();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                String loginId, loginPwd, name, token;
                loginId = auto.getString("inputId",null);
                loginPwd = auto.getString("inputPwd",null);
                name = auto.getString("name", null);
                token = auto.getString("token", null);
                Log.d("자동로그인", ""+loginId+loginPwd+name+token);

                if(loginId !=null && loginPwd != null) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    UserPersonalInfo.token = token;
                    try {
                        MainActivity.getPosts();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);


    }
}