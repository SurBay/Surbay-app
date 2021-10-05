package com.pumasi.surbay.pages.signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.pages.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 300;
    public static Integer notification_type = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mContext = getApplicationContext();
        Log.d("extras", ""+getIntent().getExtras() );
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("FIREBASEMESSAGING", "Key: " + key + " Value: " + value);
                if(key.equals("type")){
                    notification_type = Integer.valueOf(value.toString());
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }


}