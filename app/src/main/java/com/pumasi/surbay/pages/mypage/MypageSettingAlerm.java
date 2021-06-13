package com.pumasi.surbay.pages.mypage;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.util.HashMap;
import java.util.Map;

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
//        mainselect = alerm.getBoolean("mainselect", true);
//        soundselect = alerm.getBoolean("soundselect", true);
//        vibeselect = alerm.getBoolean("vibeselect", true);
        mainselect = UserPersonalInfo.notificationAllow;
        soundselect = UserPersonalInfo.notificationAllow;
        vibeselect = UserPersonalInfo.notificationAllow;


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
                    UserPersonalInfo.notificationAllow = false;

                    sound.setChecked(false);
                    vibe.setChecked(false);

                    disallownotifications();
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
                    UserPersonalInfo.notificationAllow = true;

                    sound.setChecked(true);
                    vibe.setChecked(true);

                    allownotifications();

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
    private void allownotifications(){
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server)+"/api/users/allownotifications";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.PUT, requestURL, null, response -> {
                    }, error -> {
                        error.printStackTrace();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
    private void disallownotifications(){
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server)+"/api/users/disallownotifications";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.PUT, requestURL, null, response -> {
                    }, error -> {
                        error.printStackTrace();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}