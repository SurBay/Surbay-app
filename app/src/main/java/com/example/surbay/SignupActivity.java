package com.example.surbay;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    TextView check_id;
    TextView check_name;

    String p2 = "^(?=.*[A-Za-z])(?=.*[0-9]).{6,20}$";
    final String[] spinner_email = {"이메일 선택","korea.ac.kr"};
    ArrayList<String> spinner_age;

    EditText nameEditText;
    EditText useridEditText;
    Spinner useremailSpinner;
    EditText passwordEditText;
    EditText passwordcheckEditText;
    TextView pwchecklength;
    TextView pwcheckword;
    EditText phonenumberEditText;
    TextView phone_checkTextview;
    EditText phonecheckEditText;
    Button usersex_M;
    Button usersex_F;
    Spinner userage;
    int pos;

    ImageButton visibletoggle;

    private boolean MPressed, FPressed;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();



        nameEditText = findViewById(R.id.name);
        useridEditText = findViewById(R.id.userid);
        useremailSpinner = findViewById(R.id.username_email);
        passwordEditText = findViewById(R.id.password);
        passwordcheckEditText = findViewById(R.id.password_check);
        pwcheckword = findViewById(R.id.signup_check_pw);
        pwchecklength = findViewById(R.id.signup_pw_length);
        phonenumberEditText = findViewById(R.id.userphone);
        phone_checkTextview = findViewById(R.id.userphone_check);
        phonecheckEditText = findViewById(R.id.phone_checknumber);
        usersex_F = findViewById(R.id.usersex_F);
        usersex_M = findViewById(R.id.usersex_M);
        userage = findViewById(R.id.signup_age);

        visibletoggle = (ImageButton)findViewById(R.id.visible_toggle);
        check_id = findViewById(R.id.signup_check_id);
        check_name = findViewById(R.id.signup_check_name);

        ArrayAdapter emailadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner_email);
        emailadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        useremailSpinner.setAdapter(emailadapter);

        spinner_age = new ArrayList<>();
        spinner_age.add("출생연도");
        for (int i=1960;i<2020;i++){
            spinner_age.add(String.valueOf(i));
        }
        ArrayAdapter ageadapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinner_age);
        ageadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userage.setAdapter(ageadapter);

        visibletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_visible(passwordEditText);
            }
        });
        Button signupButton = findViewById(R.id.sign_up_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String userid = useridEditText.getText().toString() + "@" + spinner_email[pos];
                String password = passwordEditText.getText().toString();
                if(name.length()==0){Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();}
                else if(userid.length()==0){Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();}
                else if(password.length()==0){Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();}
                else{
                    try {
                        makeSignupRequest(name, userid, userid, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        useridEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    check_id.setVisibility(View.VISIBLE);
                    check_id.setText("중복 확인 중입니다.");
                    check_id.setTextColor(getResources().getColor(R.color.red));
                    try {
                        idCheck((EditText)v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        useridEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                check_id.setVisibility(View.GONE);
                check_name.setTextColor(getResources().getColor(R.color.red));
            }
        });

        useremailSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                if (useridEditText.getText().toString().length() > 0 ){
                    check_id.setVisibility(View.VISIBLE);
                    check_id.setText("중복 확인 중입니다.");
                    check_id.setTextColor(getResources().getColor(R.color.red));
                    try {
                        idCheck(useridEditText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    check_name.setVisibility(View.VISIBLE);
                    check_name.setText("중복 확인 중입니다.");
                    check_name.setTextColor(getResources().getColor(R.color.red));
                    try {
                        nameCheck((EditText)v);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                check_name.setVisibility(View.GONE);
                check_name.setTextColor(getResources().getColor(R.color.red));
            }
        });

        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pw = passwordEditText.getText().toString();
                    if (pw.length() < 6 || !pwChk(pw)){
                        pwchecklength.setVisibility(View.VISIBLE);
                        pwchecklength.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        pwchecklength.setVisibility(View.GONE);
                    }
                }
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                pwchecklength.setVisibility(View.GONE);
            }
        });

        passwordcheckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (passwordcheckEditText.getText().toString().length() > 0){
                        if (passwordEditText.getText().toString().equals(passwordcheckEditText.getText().toString())){
                            pwcheckword.setVisibility(View.GONE);
                            pwcheckword.setTextColor(getResources().getColor(R.color.red));
                        } else {
                            pwcheckword.setText("비밀번호가 불일치합니다");
                            pwcheckword.setTextColor(getResources().getColor(R.color.red));
                            pwcheckword.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        passwordcheckEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                pwcheckword.setVisibility(View.GONE);
            }
        });

        usersex_M.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setPressed(true);
                usersex_F.setPressed(false);
                MPressed = true;
                FPressed = false;

                return true;
            }
        });
        usersex_F.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setPressed(true);
                usersex_M.setPressed(false);
                MPressed = false;
                FPressed = true;

                return true;
            }
        });
    }

    private void makeSignupRequest(String name, String email, String username, String password) throws Exception{
        try{
            String requestURL = "https://surbay-server.herokuapp.com/signup";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("name", name);
            params.put("email", email);
            params.put("userID", username);
            params.put("userPassword", password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            Boolean success = resultObj.getBoolean("type");
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            AlertDialog dialog;
                            if(success) {
                                dialog = builder.setMessage("회원가입이 완료되었습니다")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                                startActivity(intent);
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
                                dialog = builder.setMessage("중복된 아이디입니다.")
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

    private void nameCheck(EditText v) throws Exception{
        if (v.getText().toString().length() == 0 ){
            check_name.setVisibility(View.GONE);
        } else {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String temp_name = v.getText().toString();
                String requestURL = "https://surbay-server.herokuapp.com/names/duplicate?name=" + temp_name;
                JSONObject params = new JSONObject();
                params.put("name", temp_name);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, requestURL, params, response -> {
                            Log.d("response is", "" + response);
                            try {
                                JSONObject nameObj = new JSONObject(response.toString());
                                Boolean success = nameObj.getBoolean("type");
                                if (success) {
                                    check_name.setText("사용 가능한 닉네임입니다.");
                                    check_name.setTextColor(getResources().getColor(R.color.blue));
                                } else {
                                    check_name.setText("이미 사용 중인 닉네임입니다.");
                                    check_name.setTextColor(getResources().getColor(R.color.red));
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void idCheck(EditText s) throws Exception{
        if (s.getText().toString().length() == 0) {
            check_id.setVisibility(View.GONE);
        } else {
            try {
                String userid = useridEditText.getText().toString() + "@" + spinner_email[pos];
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String requestURL = "https://surbay-server.herokuapp.com/userids/duplicate?userID="+ userid;
                JSONObject params = new JSONObject();
                params.put("userID", userid);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, requestURL, params, response -> {
                            Log.d("response is", ""+response);
                            try {
                                JSONObject idObj = new JSONObject(response.toString());
                                Boolean success = idObj.getBoolean("type");
                                if (success){
                                    check_id.setText("사용 가능한 아이디입니다.");
                                    check_id.setTextColor(getResources().getColor(R.color.blue));
                                } else {
                                    check_id.setText("이미 가입된 이메일입니다.");
                                    check_id.setTextColor(getResources().getColor(R.color.red));
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
    private void change_visible(EditText v){
        if (v.getTag() == "0"){
            v.setTransformationMethod(null);
            v.setTag("1");
        } else {
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setTag("0");
        }
    }

    public boolean pwChk(String pw){
        boolean check = false;
        Matcher m = Pattern.compile(p2).matcher(pw);
        if (m.find()){
            check = true;
        }

        return check;
    }
}
