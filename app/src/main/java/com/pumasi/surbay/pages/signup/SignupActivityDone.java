package com.pumasi.surbay.pages.signup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.tools.FirebaseLogging;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivityDone extends AppCompatActivity {

    private Context context;
    TextView user_agree_info;
    Button signup_next;

    String userid;
    String password;
    String name;
    Integer gender;
    Integer yearBirth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_done_layout);
        getSupportActionBar().hide();
        context = getApplicationContext();

        new FirebaseLogging(context).LogScreen("signup5", "회원가입_5단계");
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        password = intent.getStringExtra("password");
        name = intent.getStringExtra("name");
        gender = intent.getIntExtra("gender", 2);
        yearBirth = intent.getIntExtra("yearBirth", 0);
        signup_next = findViewById(R.id.signup_done_next);
        signup_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(SignupActivityDone.this, LoginActivity.class);
                startActivity(intent1);
            }
        });

        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        SharedPreferences.Editor autoLogin = auto.edit();
        autoLogin.putString("temp_email", userid);
        autoLogin.commit();
        makeSignupRequest(name, userid, userid, password, gender, yearBirth);
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://main.d2w6n37lfl2sg2.amplifyapp.com/?email=" + userid)
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                "com.pumasi.surbay",
                                true, /* installIfNotAvailable */
                                "29"    /* minimumVersion */)
                        .build();
        Log.d("url", userid);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendSignInLinkToEmail(userid, actionCodeSettings)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("email", "Email sent.");
                        }
                    }
                });







    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    private void makeSignupRequest(String name, String email, String username, String password, Integer gender, Integer yearBirth){
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

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            Boolean success = resultObj.getBoolean("type");
                            if(!success) {
                                CustomDialog customDialog = new CustomDialog(SignupActivityDone.this, null);
                                customDialog.show();
                                customDialog.setMessage("중복된 아이디입니다");
                                customDialog.setNegativeButton("확인");
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
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}