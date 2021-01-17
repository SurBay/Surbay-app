package com.example.surbay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();


        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);

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
    }

    private void makeLoginRequest(String username, String password) throws Exception{
        try{
            String requestURL = "https://surbay-server.herokuapp.com/login";
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
                                                    SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                                    SharedPreferences.Editor autoLogin = auto.edit();
                                                    autoLogin.putString("inputId", username);
                                                    autoLogin.putString("inputPwd", password);

                                                    String token = resultObj.getString("token");
                                                    autoLogin.putString("token", token);
                                                    autoLogin.commit();
                                                    UserPersonalInfo.token = token;
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                finish();
                                            }
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
}