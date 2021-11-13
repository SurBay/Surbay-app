package com.pumasi.surbay.pages.signup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SignupActivityExtra extends AppCompatActivity {

    TextView check_name;
    EditText nameEditText;
    Button signup_next;
    Button usersex_M;
    Button usersex_F;
    Spinner userage;
    int posadd;
    int posyear;

    private boolean MPressed, FPressed;

    ArrayList<String> spinner_age;

    String userid;
    String password;

    Boolean dup_check = false;

    private RelativeLayout loading;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_extra_layout);
        getSupportActionBar().hide();

        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        password = intent.getStringExtra("password");

        nameEditText = findViewById(R.id.name);
        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);
        signup_next = findViewById(R.id.signup_extra_next);
        usersex_F = findViewById(R.id.usersex_F);
        usersex_M = findViewById(R.id.usersex_M);
        userage = findViewById(R.id.signup_age);
        check_name = findViewById(R.id.signup_check_name);

        spinner_age = new ArrayList<>();
        spinner_age.add("출생연도");
        for (int i=1985;i<2005;i++){
            spinner_age.add(String.valueOf(i));
        }
        ArrayAdapter ageadapter = new ArrayAdapter(this, R.layout.simple_spinner_item, spinner_age);
        ageadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userage.setAdapter(ageadapter);
        userage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView text = view.findViewById(android.R.id.text1);
                if(position==0) text.setTextColor(getColor(R.color.nav_gray));
                else text.setTextColor(getColor(R.color.text_black));

                Log.d("press", "pressed "+ usersex_M.isPressed() + usersex_F.isPressed() + MPressed + FPressed);
                usersex_M.setBackground(getDrawable(R.drawable.sexselector_m));
                usersex_F.setBackground(getDrawable(R.drawable.sexselector_m));
                if(MPressed) usersex_M.setPressed(true);
                else if(FPressed) usersex_F.setPressed(true);
                posyear = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("pres2", "pressed "+ usersex_M.isPressed() + usersex_F.isPressed()+ MPressed + FPressed);
                usersex_M.setBackground(getDrawable(R.drawable.sexselector_m));
                usersex_F.setBackground(getDrawable(R.drawable.sexselector_m));
                if(MPressed) usersex_M.setPressed(true);
                else if(FPressed) usersex_F.setPressed(true);
            }
        });
        userage.setOnTouchListener(new View.OnTouchListener() { //스피너 누를시 버튼 눌림 해제되는 버그때문에 넣음
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("press3", "pressed "+ usersex_M.isPressed() + usersex_F.isPressed()+ MPressed + FPressed);
                if(MPressed) usersex_M.setBackgroundColor(Color.parseColor("#3AD1BF"));
                else if(FPressed) usersex_F.setBackgroundColor(Color.parseColor("#3AD1BF"));
                return false;
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

        nameEditText.setFilters(new InputFilter[]{filterKoEnNumSpe});


        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                check_name.setVisibility(View.VISIBLE);
                check_name.setText("중복 확인 중입니다.");
                check_name.setTextColor(getResources().getColor(R.color.red));
                try {
                    nameCheck(nameEditText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        signup_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                if(name.length()==0 || dup_check==false){
                    CustomDialog customDialog = new CustomDialog(SignupActivityExtra.this, null);
                    customDialog.show();
                    customDialog.setMessage("닉네임을 확인해주세요");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                int gender;
                if(MPressed == true && FPressed == false){
                    gender = 0;
                } else if (MPressed == false && FPressed == true){
                    gender = 1;
                } else {
                    gender = 2;
                }
                int yearBirth;
                if (posyear != 0){
                    yearBirth = Integer.valueOf(spinner_age.get(posyear));
                } else {
                    yearBirth = 0;
                }
                Intent intent1 = new Intent(SignupActivityExtra.this, SignupActivityAgree.class);
                intent1.putExtra("userid", userid);
                intent1.putExtra("password", password);
                intent1.putExtra("name", name);
                intent1.putExtra("gender", gender);
                intent1.putExtra("yearBirth", yearBirth);
                startActivity(intent1);
            }
        });




    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    protected InputFilter filterKoEnNumSpe = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣!_@$%^&+=\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    private void nameCheck(EditText v) throws Exception{
        if (v.getText().toString().length() == 0 ){
            check_name.setVisibility(View.GONE);
        } else if(v.getText().toString().length()<2 || v.getText().toString().length()>10){
            check_name.setText("한글/영문/숫자(2~10자)로만 이루어져야 합니다");
            check_name.setTextColor(getResources().getColor(R.color.red));
        }
        else {
            try {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String temp_name = v.getText().toString();
                String requestURL = getString(R.string.server)+"/names/duplicate?name=" + temp_name;
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
                                    dup_check = true;
                                    check_name.setTextColor(getResources().getColor(R.color.blue));
                                } else {
                                    check_name.setText("이미 사용 중인 닉네임입니다.");
                                    dup_check = false;
                                    check_name.setTextColor(getResources().getColor(R.color.red));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                            Log.d("exception", "volley error");
                            error.printStackTrace();
                        });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                requestQueue.add(jsonObjectRequest);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}