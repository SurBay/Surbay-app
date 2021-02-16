package com.example.surbay.mypage;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.surbay.R;
import com.example.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PwChange extends AppCompatActivity {
    String p2 = "^(?=.*[A-Za-z])(?=.*[0-9]).{6,20}$";

    EditText oripasswordEditText;
    TextView oripasswordcheckEditText;
    EditText newpasswordEditText;
    EditText newpasswordcheckEditText;
    TextView newpwchecklength;
    TextView newpwcheckword;
    ImageButton orivisibletoggle;
    ImageButton newvisibletoggle;
    Button changepw;

    boolean oripwcheck = false;
    boolean newpwcheck = false;

    String newpw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_change);

        oripasswordEditText = findViewById(R.id.pwchange_origin_password);
        orivisibletoggle = findViewById(R.id.pwchange_origin_visible_toggle);
        oripasswordcheckEditText = findViewById(R.id.pwchange_check_oripw);

        newpasswordEditText = findViewById(R.id.pwchange_new_password);
        newvisibletoggle = findViewById(R.id.pwchange_new_visible_toggle);
        newpwchecklength = findViewById(R.id.pwchange_pw_length);
        newpasswordcheckEditText = findViewById(R.id.pwchange_new_password_check);
        newpwcheckword = findViewById(R.id.pwchange_check_newpw);
        changepw = findViewById(R.id.pwchange_button);

        orivisibletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_visible(oripasswordEditText);
            }
        });
        newvisibletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_visible(newpasswordEditText);
            }
        });
        oripasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (oripasswordcheckEditText.getText().toString().length() > 0){
                        if (oripasswordEditText.getText().toString().equals(UserPersonalInfo.userPassword)){
                            oripasswordcheckEditText.setVisibility(View.GONE);
                            oripasswordcheckEditText.setTextColor(getResources().getColor(R.color.red));
                            oripwcheck = true;
                        } else {
                            oripasswordcheckEditText.setText("일치하지 않습니다");
                            oripasswordcheckEditText.setTextColor(getResources().getColor(R.color.red));
                            oripasswordcheckEditText.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });

        newpasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String pw = newpasswordEditText.getText().toString();
                    if (pw.length() < 6 || !pwChk(pw)){
                        newpwchecklength.setVisibility(View.VISIBLE);
                        newpwchecklength.setTextColor(getResources().getColor(R.color.red));
                    } else {
                        newpwchecklength.setVisibility(View.GONE);
                    }
                }
            }
        });
        newpasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                newpwchecklength.setVisibility(View.GONE);
            }
        });

        newpasswordcheckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (newpasswordcheckEditText.getText().toString().length() > 0){
                        if (newpasswordEditText.getText().toString().equals(newpasswordcheckEditText.getText().toString())){
                            newpwcheckword.setVisibility(View.GONE);
                            newpwcheckword.setTextColor(getResources().getColor(R.color.red));
                            newpwcheck = true;
                            newpw = newpasswordEditText.getText().toString();
                        } else {
                            newpwcheckword.setText("비밀번호가 불일치합니다");
                            newpwcheckword.setTextColor(getResources().getColor(R.color.red));
                            newpwcheckword.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        newpasswordcheckEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }
            @Override
            public void afterTextChanged(Editable s) {
                newpwcheckword.setVisibility(View.GONE);
            }
        });

        changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (oripwcheck&&newpwcheck){
                    try {
                        updatePw(newpw);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    public boolean pwChk(String pw){
        boolean check = false;
        Matcher m = Pattern.compile(p2).matcher(pw);
        if (m.find()){
            check = true;
        }

        return check;
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


    private void updatePw(String newpassword) throws Exception{
        String requestURL = "https://surbay-server.herokuapp.com/api/users/changepassword?phoneNumber=" + UserPersonalInfo.phoneNumber;
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userPassword", newpassword);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        finish();
                        Toast.makeText(getApplicationContext(), "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }
}