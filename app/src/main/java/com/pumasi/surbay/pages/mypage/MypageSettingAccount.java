package com.pumasi.surbay.pages.mypage;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.pages.signup.LoginActivity;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MypageSettingAccount extends AppCompatActivity {
    TextView id;
    TextView name;
    TextView phone;
    TextView sex;
    TextView birth;
    TextView schoolmail;
    TextView schoolname;

    Button uifix;
    Button schoolAuth;
    Button pwchange;
    Button logout;
    Button userdelect;

    ImageView back;

    LinearLayout memberView;
    LinearLayout nonMemberView;

    Button gotoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage_setting_account);

        getSupportActionBar().hide();

        back = findViewById(R.id.account_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        id = findViewById(R.id.account_UI_id);
        name = findViewById(R.id.account_UI_name);
        phone = findViewById(R.id.account_UI_phone);
        sex = findViewById(R.id.account_UI_sex);
        birth = findViewById(R.id.account_UI_birth);
        schoolmail = findViewById(R.id.account_SH_EM);
        schoolname = findViewById(R.id.account_SH_name);

        uifix = findViewById(R.id.account_UI_fix);
        schoolAuth = findViewById(R.id.account_SH_Auth);
        pwchange = findViewById(R.id.account_pwchange);
        logout = findViewById(R.id.account_logout);
        userdelect = findViewById(R.id.alerm_userdelect);

        memberView = findViewById(R.id.memberView);
        nonMemberView = findViewById(R.id.nonMemberView);

        id.setText(UserPersonalInfo.userID);
        name.setText(UserPersonalInfo.name);
//        phone.setText(UserPersonalInfo.phoneNumber);
        if(UserPersonalInfo.userID.equals("nonMember")){
            memberView.setVisibility(View.GONE);
            nonMemberView.setVisibility(View.VISIBLE);
            gotoLogin = findViewById(R.id.go_to_login);
            gotoLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MypageSettingAccount.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            return;

        }
        if (UserPersonalInfo.gender == 0){
            sex.setText("??????");
        } else if(UserPersonalInfo.gender == 1){
            sex.setText("??????");
        } else{
            sex.setText("????????????");
        }
        if(UserPersonalInfo.yearBirth==0){
            birth.setText("????????????");
        }else {
            birth.setText(UserPersonalInfo.yearBirth.toString());
        }
        uifix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingAccount.this, AccountFix.class);
                startActivityForResult(intent, 40);
            }
        });
        schoolAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        pwchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingAccount.this, PwChange.class);
                startActivity(intent);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(MypageSettingAccount.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        logoutRequest();
                    }
                });
                customDialog.show();
                customDialog.setMessage("???????????? ????????????????");
                customDialog.setPositiveButton("????????????");
                customDialog.setNegativeButton("??????");
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(MypageSettingAccount.this);
                AlertDialog dialog = builder.setMessage("???????????? ????????????????")
                        .setPositiveButton("????????????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MypageSettingAccount.this, LoginActivity.class);
                                startActivity(intent);
                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = auto.edit();
                                //editor.clear()??? auto??? ???????????? ?????? ????????? ???????????? ????????????.
                                editor.clear();
                                editor.commit();
                                Toast.makeText(MypageSettingAccount.this, "???????????? ???????????????", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onShow(DialogInterface arg0) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.teal_200);
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.gray);
                    }
                });
                dialog.show();*/
            }
        });
        userdelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog customDialog = new CustomDialog(MypageSettingAccount.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deletePersonalInfo();
                        Intent intent = new Intent(MypageSettingAccount.this, LoginActivity.class);
                        startActivity(intent);
                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = auto.edit();
                        //editor.clear()??? auto??? ???????????? ?????? ????????? ???????????? ????????????.
                        editor.clear();
                        editor.commit();
                        Toast.makeText(MypageSettingAccount.this, "?????? ????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
                customDialog.show();
                customDialog.setMessage("?????? ????????? ????????????????");
                customDialog.setPositiveButton("????????????");
                customDialog.setNegativeButton("??????");
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==40 && resultCode==RESULT_OK){

            id.setText(UserPersonalInfo.userID);
            name.setText(UserPersonalInfo.name);
//            phone.setText(UserPersonalInfo.phoneNumber);
            if (UserPersonalInfo.gender == 0){
                sex.setText("??????");
            } else {
                sex.setText("??????");
            }
            birth.setText(UserPersonalInfo.yearBirth.toString());
        }
    }

    private void deletePersonalInfo() {
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server)+"/api/users/delete";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.DELETE, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);

                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.clear();
                            autoLogin.commit();
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
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
    private void logoutRequest(){
        try{
            String requestURL = getString(R.string.server)+"/api/users/logout";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.userID);
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        try {
                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = auto.edit();
                            //editor.clear()??? auto??? ???????????? ?????? ????????? ???????????? ????????????.
                            editor.clear();
                            editor.commit();
                            SharedPreferences tempWrite = getSharedPreferences("tempWrite", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor tempWriteedit = tempWrite.edit();
                            tempWriteedit.clear();
                            tempWriteedit.commit();

                            UserPersonalInfo.clearInfo();
                            MainActivity.surveytipArrayList = new ArrayList<>();
                            MainActivity.feedbackArrayList = new ArrayList<>();
                            MainActivity.NoticeArrayList = new ArrayList<>();
                            MainActivity.done = 0;
                            Toast.makeText(MypageSettingAccount.this, "???????????? ???????????????", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MypageSettingAccount.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}