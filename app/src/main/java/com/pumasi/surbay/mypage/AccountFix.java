package com.pumasi.surbay.mypage;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class AccountFix extends AppCompatActivity {
    TextView check_name;
    Integer name_checked = 0; //0: 중복 확인중, 1: 중복임, 2: 사용가능

    ArrayList<String> spinner_age;

    EditText nameEditText;
    EditText phonenumberEditText;
    TextView phone_checkTextview;
    EditText phonecheckEditText;
    TextView signupTimer;
    TextView signupPCNText;
    Button usersex_M;
    Button usersex_F;
    Spinner userage;
    int posyear;

    public boolean MPressed = false;
    public boolean FPressed = false;
    String typesmsCode;
    String getverificationId;
    FirebaseAuth auth;

    Integer gender;
    Integer yearBirth = 0;
    String name;
    String phoneNumber;

    CountDownTimer CDT;

    ImageView back;
    private Boolean phone_check = false;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private String mVerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_fix);

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        back = findViewById(R.id.uifix_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nameEditText = findViewById(R.id.account_fix_name);
        phonenumberEditText = findViewById(R.id.account_fix_userphone);
        phone_checkTextview = findViewById(R.id.account_fix_userphone_check);
        signupTimer = findViewById(R.id.account_fix_timer);
        signupPCNText = findViewById(R.id.account_fix_PCNtext);
        phonecheckEditText = findViewById(R.id.account_fix_phone_checknumber);
        usersex_F = findViewById(R.id.account_fix_usersex_F);
        usersex_M = findViewById(R.id.account_fix_usersex_M);
        userage = findViewById(R.id.account_fix_age);

        check_name = findViewById(R.id.account_fix_check_name);

        nameEditText.setText(UserPersonalInfo.name);
        phonenumberEditText.setText(UserPersonalInfo.phoneNumber);
        if(UserPersonalInfo.gender==0){
            usersex_M.setPressed(true);
        }else if(UserPersonalInfo.gender==1){
            usersex_F.setPressed(true);
        }



        spinner_age = new ArrayList<>();
        spinner_age.add("출생연도");
        for (int i=1960;i<2020;i++){
            spinner_age.add(String.valueOf(i));
        }
        ArrayAdapter ageadapter = new ArrayAdapter(this, R.layout.simple_spinner_item, spinner_age);
        ageadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userage.setAdapter(ageadapter);
        if(UserPersonalInfo.yearBirth!=0){
            userage.setSelection(spinner_age.indexOf(UserPersonalInfo.yearBirth.toString()));
        }

        Button signupButton = findViewById(R.id.account_fix_button);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckableSignup()) {
                    updatePersonalInfo();

                }
            }
        });


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

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("name is", "username"+UserPersonalInfo.name);
                Boolean b = nameEditText.getText().toString().equals(UserPersonalInfo.name);
                Log.d("new name is", "username"+nameEditText.getText().toString()+ "is equal?  "+b);
                if(!nameEditText.getText().toString().equals(UserPersonalInfo.name)) {
                    check_name.setVisibility(View.VISIBLE);
                    check_name.setText("확인 중입니다");
                    check_name.setTextColor(getResources().getColor(R.color.teal_200));
                    try {
                        nameCheck(nameEditText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                else{
                    name_checked = 2;
                    check_name.setVisibility(View.GONE);
                }

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

        phone_checkTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = phonenumberEditText.getText().toString();
                try {
                    phoneCheck();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        phonecheckEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                typesmsCode = phonecheckEditText.getText().toString();
                Log.d("code is", "sms code" + typesmsCode);
                if (typesmsCode.length() == 6){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(getverificationId, typesmsCode);
                    Task<AuthResult> result = auth.signInWithCredential(credential)
                            .addOnCompleteListener(AccountFix.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = task.getResult().getUser();
                                        Log.d("signupauth", "signInWithCredential:success");
                                        CDT.cancel();
                                        signupPCNText.setText("인증이 완료되었습니다");
                                        phone_check = true;
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.d("signupauth", "signInWithCredential:failefail");

                                        signupPCNText.setText("인증번호가 일치하지 않습니다");
                                        // ...
                                    }
                                }
                            });
                }

            }
        });
    }

    private void nameCheck(EditText v) throws Exception{
        if (v.getText().toString().length() == 0 ){
            check_name.setVisibility(View.GONE);
        } else {
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
                                    check_name.setText("사용할 수 있는 닉네임입니다.");
                                    name_checked = 2;
                                    check_name.setTextColor(getResources().getColor(R.color.teal_200));
                                } else {
                                    check_name.setText("중복된 닉네임입니다.");
                                    name_checked = 1;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    private void phoneCheck() throws Exception{
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String requestURL = getString(R.string.server)+"/phonenumbers/duplicate?phoneNumber=" + phoneNumber;
            JSONObject params = new JSONObject();
            params.put("phoneNumber", phoneNumber);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, requestURL, params, response -> {
                        Log.d("response is", "" + response);
                        try {
                            JSONObject nameObj = new JSONObject(response.toString());
                            Boolean success = nameObj.getBoolean("type");
                            if (success) {

                                CustomDialog customDialog = new CustomDialog(AccountFix.this, null);
                                customDialog.show();
                                customDialog.setMessage("인증번호가 발송되었습니다.");
                                customDialog.setNegativeButton("확인");
                                String realphone = "+82"+phoneNumber.substring(1);
                                Log.d("phone", "num is "+ realphone);
                                PhoneAuth(realphone);
                            } else {
                                if (phoneNumber.equals(UserPersonalInfo.phoneNumber)){
                                    CustomDialog customDialog = new CustomDialog(AccountFix.this, null);
                                    customDialog.show();
                                    customDialog.setMessage("인증번호가 발송되었습니다.");
                                    customDialog.setNegativeButton("확인");
                                    String realphone = "+82"+phoneNumber.substring(1);
                                    Log.d("phone", "num is "+ realphone);
                                    PhoneAuth(realphone);
                                } else {

                                    CustomDialog customDialog = new CustomDialog(AccountFix.this, null);
                                    customDialog.show();
                                    customDialog.setMessage("이미 가입된 전화번호입니다");
                                    customDialog.setNegativeButton("확인");
                                }
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

    public void TimerStart(){
        signupTimer.setVisibility(View.VISIBLE);
        signupPCNText.setVisibility(View.VISIBLE);
        CDT = new CountDownTimer(180*1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long totalsec = millisUntilFinished / 1000;
                long min = totalsec/60;
                long sec = totalsec - min*60;


                String minstr = String.format("%02d", min);
                String secstr = String.format("%02d", sec);
                signupTimer.setText(minstr+":"+secstr);
            }

            @Override
            public void onFinish() {
                signupPCNText.setText("인증에 실패했습니다. 인증하기를 다시 시도해주세요");
            }
        };
        CDT.start();
    }

    public void PhoneAuth(String phoneNumber){
//        phoneNumber = "+1 1231231234";
        auth = FirebaseAuth.getInstance();
//        FirebaseAuth.getInstance().getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
        PhoneAuthOptions.Builder optionsBuilder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        // Save the verification id somewhere
                        // ...

                        // The corresponding whitelisted code above should be used to complete sign-in.
                        Log.d("signupauth", forceResendingToken.toString() + verificationId);
                        Log.d("signupauth", "onCodeSent:" + verificationId);
                        mVerificationId = verificationId;
                        TimerStart();
                        phonecheckEditText.setEnabled(true);
                        getverificationId = verificationId;
                        mResendToken = forceResendingToken;

                    }

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.
                        Log.d("phone auth", "onVerificationCompleted:" + credential);
                        String code = credential.getSmsCode();

                        //sometime the code is not detected automatically
                        //in this case the code will be null
                        //so user has to manually enter the code
                        if (code != null) {
                            phonecheckEditText.setText(code);
                            //verifying the code
                            verifyVerificationCode(code);
                        }

                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("phone auth", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }

                        // Show a message and update the UI
                        // ...
                    }
                });
        if(mResendToken!=null) optionsBuilder.setForceResendingToken(mResendToken);
        PhoneAuthOptions options = optionsBuilder.build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void verifyVerificationCode(String otp) {
        //creating the credential
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);

        //signing the user
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential);
    }

    public boolean CheckableSignup(){
        name = nameEditText.getText().toString();

        if(name.length()==0){
            Toast.makeText(AccountFix.this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(name_checked == 0){
            Toast.makeText(AccountFix.this, "중복 확인중입니다", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(name_checked == 1){
            Toast.makeText(AccountFix.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        phoneNumber = phonenumberEditText.getText().toString();

        if (posyear != 0){
            yearBirth = Integer.valueOf(spinner_age.get(posyear));
        } else {
            yearBirth = 0;
        }

        if(MPressed == true && FPressed == false){
            gender = 0;
        } else if (MPressed == false && FPressed == true){
            gender = 1;
        } else {
            gender = 2;
        }

        if (!phone_check){
            Toast.makeText(AccountFix.this, "번호 인증을 진행해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    private void updatePersonalInfo() {
        String token = UserPersonalInfo.token;
        try{
            JSONObject params = new JSONObject();
            params.put("name", name);
            params.put("gender", gender);
            params.put("yearBirth", yearBirth);
            params.put("phoneNumber", phoneNumber);
            String requestURL = getString(R.string.server)+"/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);
                            Boolean success = res.getBoolean("type");
                            if(success) {
                                UserPersonalInfo.name = name;
                                UserPersonalInfo.gender = gender;
                                UserPersonalInfo.yearBirth = yearBirth;
                                UserPersonalInfo.phoneNumber = phoneNumber;

                                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                SharedPreferences.Editor autoLogin = auto.edit();
                                autoLogin.putString("name", name);
                                autoLogin.commit();
                                setResult(RESULT_OK);
                                finish();
                                Toast.makeText(AccountFix.this, "회원정보가 변경되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(AccountFix.this, "오류가 발생하였습니다", Toast.LENGTH_SHORT).show();

                            }
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
                    Log.d("tokenistokenis", ""+token);
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