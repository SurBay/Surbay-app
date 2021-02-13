package com.example.surbay.mypage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.surbay.LoginActivity;
import com.example.surbay.R;
import com.example.surbay.UserPersonalInfo;
import com.example.surbay.WriteActivity;
import com.example.surbay.classfile.AccountFix;

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

        id.setText(UserPersonalInfo.userID);
        name.setText(UserPersonalInfo.name);

        uifix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageSettingAccount.this, AccountFix.class);
                startActivity(intent);
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

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MypageSettingAccount.this);
                AlertDialog dialog = builder.setMessage("로그아웃 하겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MypageSettingAccount.this, LoginActivity.class);
                                startActivity(intent);
                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = auto.edit();
                                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                                editor.clear();
                                editor.commit();
                                Toast.makeText(MypageSettingAccount.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
                dialog.show();
            }
        });
        userdelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MypageSettingAccount.this);
                AlertDialog dialog = builder.setMessage("회원 탈퇴를 하겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MypageSettingAccount.this, LoginActivity.class);
                                startActivity(intent);
                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor editor = auto.edit();
                                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                                editor.clear();
                                editor.commit();
                                Toast.makeText(MypageSettingAccount.this, "회원 탈퇴가 완료되었습니다", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
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
                dialog.show();
            }
        });
    }
}