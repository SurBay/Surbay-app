package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;

    CheckBox auto_login_check;
    CheckBox save_id_check;

    ImageButton visibletoggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();



        save_id_check = findViewById(R.id.id_save_check);
        auto_login_check = findViewById(R.id.auto_login_check);

        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);
        TextView findidorpw = findViewById(R.id.findidorpw);

        visibletoggle = findViewById(R.id.visible_toggle_login);

        visibletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_visible(passwordEditText);
            }
        });

        Button login = findViewById(R.id.login);
        Button signup = findViewById(R.id.sign_up);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                if(username.length()==0){Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();}
                else if(password.length()==0){Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();}
                else{
                    try {
                        makeLoginRequest(username, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(intent);
            }
        });

        save_id_check.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });
        auto_login_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String auto_id = auto.getString("inputId", null);
        if (auto_id != null){
            usernameEditText.setText(auto_id);
        }

        findidorpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindIdActivity.class);
                startActivity(intent);
            }
        });
    }

    private void makeLoginRequest(String username, String password) throws Exception{
        try{
            String requestURL = getString(R.string.server)+"/login";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID", username);
            params.put("userPassword", password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            Boolean success = resultObj.getBoolean("type");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            if(success) {
                                dialog = builder.setMessage("로그인에 성공했습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                LoginActivity.this.startActivity(intent);
                                                try {

                                                    String token = resultObj.getString("token");
                                                    UserPersonalInfo.token = token;

                                                    if (auto_login_check.isChecked()){
                                                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                                        SharedPreferences.Editor autoLogin = auto.edit();
                                                        autoLogin.putString("inputId", username);
                                                        autoLogin.putString("inputPwd", password);

                                                        autoLogin.putString("token", token);
                                                        autoLogin.commit();
                                                    } else {
                                                        if (save_id_check.isChecked()){
                                                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                                            SharedPreferences.Editor autoLogin = auto.edit();


                                                            autoLogin.putString("inputId", username);
                                                            autoLogin.remove("inputPwd");
                                                            autoLogin.remove("token");
                                                            autoLogin.commit();
                                                        } else {
                                                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                                            SharedPreferences.Editor autoLogin = auto.edit();


                                                            autoLogin.remove("inputId");
                                                            autoLogin.remove("inputPwd");
                                                            autoLogin.remove("token");
                                                            autoLogin.commit();
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                finish();
                                            }///로그인 화면이 스택으로 남아있는지?
                                        })
                                        .create();
                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onShow(DialogInterface arg0) {
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.black);
                                    }
                                });
                                dialog.show();
                            }else {
                                dialog = builder.setMessage("아이디나 비밀번호가 일치하지 않습니다.")
                                        .setNegativeButton("다시시도", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        })
                                        .create();
                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @SuppressLint("ResourceAsColor")
                                    @Override
                                    public void onShow(DialogInterface arg0) {
                                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(R.color.black);
                                    }
                                });
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    private void change_visible(EditText v){
        if (v.getTag() == "0"){
            v.setTransformationMethod(null);
            v.setTag("1");
        } else {
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setTag("0");
        }
    }
}