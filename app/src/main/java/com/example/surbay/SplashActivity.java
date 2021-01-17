package com.example.surbay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId, loginPwd, name, token;
        loginId = auto.getString("inputId",null);
        loginPwd = auto.getString("inputPwd",null);
        name = auto.getString("name", null);
        token = auto.getString("token", null);
        Log.d("자동로그인", ""+loginId+loginPwd+name+token);
        if(loginId !=null && loginPwd != null) {
            Toast.makeText(this, name +"님 반갑습니다", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            UserPersonalInfo.token = token;
            startActivity(intent);
            try {
                MainActivity.getPosts();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
        else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
        finish();
    }
}