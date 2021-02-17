package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
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
    TextView signupTimer;
    private TextView signupPCNText;
    Button usersex_M;
    Button usersex_F;
    Spinner userage;
    int posadd;
    int posyear;

    ImageButton visibletoggle;

    private boolean MPressed, FPressed;
    String typesmsCode;
    String getverificationId;
    FirebaseAuth auth;

    Integer gender;
    Integer yearBirth = 0;
    String password;
    String name;
    String userid;
    String phoneNumber;

    private Boolean phone_check = false;

    CountDownTimer CDT;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
//    private ActivityPhoneAuthBinding mBinding;

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
        signupTimer = findViewById(R.id.signup_timer);
        signupPCNText = findViewById(R.id.signup_PCNtext);
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
                if (CheckableSignup()){
                    try {
                        makeSignupRequest(name, userid, userid, password, gender, yearBirth, phoneNumber);
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
                posadd = position;
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

        userage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                posyear = position;
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

        phone_checkTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = phonenumberEditText.getText().toString();

//                if (phone.length() == 11){
//                    PhoneAuth(콜);
//                }
//                String realphone = "+82 "+phone.substring(1, 3)+"-"+phone.substring(3,7)+"-"+phone.substring(7,11);
                String realphone = "+82"+phone.substring(1);
                Log.d("phone", "num is "+ realphone);
                PhoneAuth(realphone);
            }
        });

        phonecheckEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    typesmsCode = phonecheckEditText.getText().toString();
                    Log.d("code is", "sms code" + typesmsCode);
                    if (typesmsCode.length() == 6){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(getverificationId, typesmsCode);
                        Task<AuthResult> result = auth.signInWithCredential(credential)
                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
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

                                            signupPCNText.setText("인증에 실패했습니다. 인증하기를 다시 시도해주세요");
                                            // ...
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void makeSignupRequest(String name, String email, String username, String password, Integer gender, Integer yearBirth, String phoneNumber) throws Exception{
        try{
            String requestURL = getString(R.string.server)+"/signup";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("name", name);
            params.put("email", email);
            params.put("userID", username);
            params.put("userPassword", password);

            params.put("gender", gender);
            params.put("yearBirth", yearBirth);
            params.put("phoneNumber", phoneNumber);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            Boolean success = resultObj.getBoolean("type");
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            AlertDialog dialog;
                            if(success) {
                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(SignupActivity.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT);
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
                String userid = useridEditText.getText().toString() + "@" + spinner_email[posadd];
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String requestURL = getString(R.string.server)+"/userids/duplicate?userID="+ userid;
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        phonecheckEditText.setFocusable(false);
        phonecheckEditText.setFocusable(true);
        return true;
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
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
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
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public boolean CheckableSignup(){
        name = nameEditText.getText().toString();

        if(name.length()==0){
            Toast.makeText(getApplicationContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        userid = useridEditText.getText().toString() + "@" + spinner_email[posadd];
        if(userid.length()==0){
            Toast.makeText(getApplicationContext(), "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(pwcheckword.getVisibility() == View.GONE){
            password = passwordEditText.getText().toString();
        } else {
            return false;
        }

        if(password.length()==0){
            Toast.makeText(getApplicationContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        phoneNumber = phonenumberEditText.getText().toString();

        if (posyear != 0){
            yearBirth = Integer.valueOf(spinner_age.get(posyear));
        } else {
            Toast.makeText(getApplicationContext(), "출생연도를 선택해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(MPressed == true && FPressed == false){
            gender = 0;
        } else if (MPressed == false && FPressed == true){
            gender = 1;
        } else {
            Toast.makeText(getApplicationContext(), "성별을 선택해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(phone_check==false){
            Toast.makeText(getApplicationContext(), "번호 인증을 진행해주세요", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}