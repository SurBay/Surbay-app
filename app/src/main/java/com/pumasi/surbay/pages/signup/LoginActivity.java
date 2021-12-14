package com.pumasi.surbay.pages.signup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pumasi.surbay.AnalyticsApplication;
import com.pumasi.surbay.HomeRenewalFragment;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.MyCoupon;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.pumasi.surbay.tools.FirebaseLogging;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;
    private Context context;
    private AnalyticsApplication application;
    private CheckBox cb_auto_login;
    private CheckBox cb_save_id;

    ImageButton visibletoggle;

    private EditText et_id;
    private EditText et_password;
    private boolean loginDone = false;

    RelativeLayout loading;

    loginHandler handler = new loginHandler();

    TextView nonMemberLogin;

    String fcm_token;

    boolean notice_done = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        context = getApplicationContext();

        new FirebaseLogging(context).LogScreen("login_page", "로그인_페이지");

        try {
            MainActivity.getSurveytips();
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView tv_find_pw = findViewById(R.id.findidorpw);
        TextView tv_problem = findViewById(R.id.tv_problem);
        Button bt_login = findViewById(R.id.login);
        Button bt_sign_up = findViewById(R.id.sign_up);

        cb_save_id = findViewById(R.id.id_save_check);
        cb_auto_login = findViewById(R.id.auto_login_check);

        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);

        visibletoggle = findViewById(R.id.visible_toggle_login);

        nonMemberLogin = findViewById(R.id.non_member_login);

        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);

        SharedPreferences autologin = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String loginId, loginPwd, name, token;
        loginId = autologin.getString("inputId", null);
        loginPwd = autologin.getString("inputPwd", null);
        name = autologin.getString("name", null);
        token = autologin.getString("token", null);
        Log.d("자동로그인", "" + loginId + loginPwd + name + token);
        fcm_token = null;
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FIREBASEMESSAGING", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("tokenis", token);
                        fcm_token = token;
                        if (loginId != null && loginPwd != null) {
                            loading.setVisibility(View.VISIBLE);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                    makeLoginRequest(loginId, loginPwd);
                                    while (!(loginDone)) {
                                        try {
                                            Thread.sleep(10);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    getNotices();
                                    while (!notice_done) {
                                        try {
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    Message message = handler.obtainMessage();
                                    handler.sendMessage(message);
                                }
                            }).start();
                        }

//                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });




        visibletoggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_visible(et_password, visibletoggle);
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_password.getText().length() != 0) {
                    visibletoggle.setVisibility(View.VISIBLE);
                } else {
                    visibletoggle.setVisibility(View.INVISIBLE);
                }
            }
        });
        nonMemberLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserPersonalInfo.token = null;
                UserPersonalInfo.name = "비회원";
                UserPersonalInfo.email = null;
                UserPersonalInfo.points = 0;
                UserPersonalInfo.level = 0;
                UserPersonalInfo.userID = "nonMember";
                UserPersonalInfo.participations = new ArrayList<>();
                UserPersonalInfo.prizes = new ArrayList<>();
                UserPersonalInfo.prize_check = 0;
                UserPersonalInfo.general_participations = new ArrayList<>();
                UserPersonalInfo.my_generals = new ArrayList<>();
                UserPersonalInfo.my_posts = new ArrayList<>();
                UserPersonalInfo.prizes = new ArrayList<>();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_id.getText().toString();
                String password = et_password.getText().toString();
                if (username.length() == 0) {
                    Toast.makeText(LoginActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else if (password.length() == 0) {
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {
                    loading.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            makeLoginRequest(username, password);
                            while (!(loginDone)) {
                                try {
                                    Thread.sleep(10);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            getNotices();
                            while(!notice_done) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            Message message = handler.obtainMessage();
                            handler.sendMessage(message);
                        }
                    }).start();
                }
            }
        });
        bt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivityEmail.class);
                startActivity(intent);
            }
        });
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
        String auto_id = auto.getString("inputId", null);
        if (auto_id != null) {
            et_id.setText(auto_id);
        }

        tv_find_pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ChangePwActivity.class);
                startActivity(intent);
            }
        });
        tv_problem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ProblemActivity.class);
                startActivity(intent);
            }
        });


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                        Log.d("dynamiclinkis", "" + deepLink);
                        if(autologin.getString("temp_email", null)!=null && deepLink!=null && (!deepLink.toString().contains("passwordchange")))
                            confirmemail(autologin.getString("temp_email", null));
                        else if(autologin.getString("pwdchangeemail", null)!=null && deepLink!=null && deepLink.toString().contains("passwordchange")){
                            confirmemailpasswordchange(autologin.getString("pwdchangeemail", null));
                            Intent intent = new Intent(LoginActivity.this, ChangePwActivity.class);
                            intent.putExtra("confirmed", true);
                            startActivity(intent);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("dynamictest", "getDynamicLink:onFailure", e);
                    }
                });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    @Override
    public void onBackPressed() {
        CustomDialog customDialog = new CustomDialog(LoginActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitProgram();
            }
        });
        customDialog.show();
        customDialog.setMessage("앱을 종료하겠습니까?");
        customDialog.setPositiveButton("종료");
        customDialog.setNegativeButton("취소");
    }

    private void exitProgram() {
        moveTaskToBack(true);
        if (Build.VERSION.SDK_INT >= 21) {
            // 액티비티 종료 + 태스크 리스트에서 지우기
            finishAndRemoveTask();
        } else {
            // 액티비티 종료
            finish();
        }

        System.exit(0);
    }

    private class loginHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (UserPersonalInfo.userID != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                loading.setVisibility(View.GONE);
            }
        }
    }

    private void confirmemail(String email) {
        String requestURL = getString(R.string.server) + "/api/users/confirmemail";
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        JSONObject params = new JSONObject();
        try {
            params.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.PUT, requestURL, params, response -> {
                Toast.makeText(LoginActivity.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
            }, error -> {
                Log.d("exception", "volley error");
                CustomDialog customDialog = new CustomDialog(LoginActivity.this, null);
                customDialog.show();
                customDialog.setMessage("오류가 발생했습니다");
                customDialog.setNegativeButton("다시시도");
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new

    DefaultRetryPolicy(20*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
    }
    private void confirmemailpasswordchange(String email) {
        String requestURL = getString(R.string.server) + "/api/users/confirmemailpassword";
        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        JSONObject params = new JSONObject();
        try {
            params.put("email",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.PUT, requestURL, params, response -> {
                }, error -> {
                    Log.d("exception", "volley error");
                    CustomDialog customDialog = new CustomDialog(LoginActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("오류가 발생했습니다");
                    customDialog.setNegativeButton("다시시도");
                    error.printStackTrace();
                });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }
    private void makeLoginRequest(String username, String password){
        try{
            String requestURL = getString(R.string.server)+"/login";
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

            JSONObject params = new JSONObject();
            params.put("userID", username);
            params.put("userPassword", password);
            Log.d("logintoken", username + ", " + password);
            Log.d("logintoken", "" + fcm_token);
            params.put("fcm_token", fcm_token);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            Boolean success = resultObj.getBoolean("type");
                            if(success) {
                                try {
                                    String token = resultObj.getString("token");
                                    JSONObject user = resultObj.getJSONObject("data");
                                    UserPersonalInfo.token = token;
                                    UserPersonalInfo.name = user.getString("name");
                                    UserPersonalInfo.email = user.getString("email");
                                    UserPersonalInfo.points = user.getInt("points");
                                    UserPersonalInfo.level = user.getInt("level");
                                    UserPersonalInfo.userID = user.getString("userID");
                                    UserPersonalInfo.userPassword = user.getString("userPassword");
                                    UserPersonalInfo.gender = user.getInt("gender");
                                    UserPersonalInfo.yearBirth = user.getInt("yearBirth");

                                    JSONArray ja = (JSONArray)user.get("participations");
                                    ArrayList<String> partiResearch = new ArrayList<String>();
                                    for (int j = 0; j<ja.length(); j++){
                                        partiResearch.add(ja.getString(j));
                                    }
                                    UserPersonalInfo.participations = partiResearch;

                                    JSONArray ja3 = (JSONArray)user.get("my_posts");
                                    ArrayList<String> userResearch = new ArrayList<String>();
                                    for (int j = 0; j < ja3.length(); j++) {
                                        userResearch.add(ja3.getString(j));
                                    }
                                    UserPersonalInfo.my_posts = userResearch;

                                    JSONArray ja4 = (JSONArray)user.get("general_participations");
                                    ArrayList<String> partiGeneral = new ArrayList<String>();
                                    for (int j = 0; j <ja4.length(); j++) {
                                        partiGeneral.add(ja4.getString(j));
                                    }
                                    UserPersonalInfo.general_participations = partiGeneral;

                                    JSONArray ja5 = (JSONArray)user.get("my_generals");
                                    ArrayList<String> userGeneral = new ArrayList<String>();
                                    for (int j = 0; j <ja5.length(); j++) {
                                        userGeneral.add(ja5.getString(j));
                                    }
                                    UserPersonalInfo.my_generals = userGeneral;

                                    JSONArray ja2 = (JSONArray)user.get("prizes");
                                    ArrayList<String> prizearray = new ArrayList<String>();
                                    for (int j = 0; j<ja2.length(); j++){
                                        prizearray.add(ja2.getString(j));
                                    }

                                    UserPersonalInfo.prizes = prizearray;
                                    try {
                                        ArrayList<String> blockedUsers = new ArrayList<>();
                                        JSONArray ja7 = (JSONArray)user.get("blocked_users");
                                        for (int j = 0; j < ja7.length(); j++) {
                                            blockedUsers.add(ja7.getString(j));
                                        }
                                        UserPersonalInfo.blocked_users = blockedUsers;
                                    } catch (Exception e) {
                                        UserPersonalInfo.blocked_users = new ArrayList<>();
                                    }

                                    ArrayList<Notification> notifications = new ArrayList<>();
                                    try{
                                        SimpleDateFormat fm = new SimpleDateFormat(getString(R.string.date_format));
                                        JSONArray na = (JSONArray)user.get("notifications");
                                        if (na.length() != 0){
                                            for (int j = 0; j<na.length(); j++){
                                                JSONObject notification = na.getJSONObject(j);
                                                String title = notification.getString("title");
                                                String content = notification.getString("content");
                                                String post_id = notification.getString("post_id");
                                                Date date = null;
                                                try {
                                                    date = fm.parse(notification.getString("date"));
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                Log.d("case_m", "makeLoginRequest: " + date);
                                                Integer post_type = notification.getInt("post_type");
                                                Notification newNotification = new Notification(title, content, post_id, date, post_type);
                                                notifications.add(newNotification);
                                            }
                                        }

                                    } catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    UserPersonalInfo.notifications = notifications;
                                    UserPersonalInfo.notificationAllow = user.getBoolean("notification_allow");
                                    UserPersonalInfo.prize_check = user.getInt("prize_check");
                                    
                                    try {
                                        ArrayList<MyCoupon> myCoupons = new ArrayList<>();
                                        JSONArray ja6 = (JSONArray) user.get("coupons");
                                        Log.d("ja6", "makeLoginRequest: " + ja6);
                                        for (int j = 0; j < ja6.length(); j++) {
                                            JSONObject coupon= ja6.getJSONObject(j);
                                            String coupon__id = coupon.getString("_id");
                                            String coupon_id = coupon.getString("coupon_id");
                                            boolean coupon_used = coupon.getBoolean("used");
                                            SimpleDateFormat fm = new SimpleDateFormat(getResources().getString(R.string.date_format));
                                            Date coupon_used_date = null;
                                            String coupon_date = coupon.getString("date");
                                            try {
                                                coupon_used_date = fm.parse(coupon.getString("used_date"));
                                            } catch (ParseException e) {
                                                coupon_used_date = null;
                                            }
                                            Date coupon_due_date = null;
                                            try {
                                                coupon_due_date = fm.parse(coupon.getString("due_date"));
                                            } catch (ParseException e) {
                                                coupon_due_date = null;
                                            }
                                            ArrayList<String> coupon_image_urls = new ArrayList<>();
                                            JSONArray ka = coupon.getJSONArray("image_urls");
                                            for (int k = 0; k < ka.length(); k++) {
                                                coupon_image_urls.add(ka.getString(k));
                                            }
                                            String coupon_store = coupon.getString("store");
                                            String coupon_menu = coupon.getString("menu");
                                            String coupon_content = coupon.getString("content");
                                            myCoupons.add(new MyCoupon(coupon__id, coupon_id, coupon_used, coupon_used_date, coupon_date, coupon_due_date, coupon_image_urls, coupon_store, coupon_menu, coupon_content));
                                        }
                                        UserPersonalInfo.coupons = myCoupons;
                                        Log.d("userCoupons", "makeLoginRequest: " + myCoupons);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    
                                    if (cb_auto_login.isChecked()){
                                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor autoLogin = auto.edit();
                                        autoLogin.putString("inputId", username);
                                        autoLogin.putString("inputPwd", password);
                                        autoLogin.putString("token", token);
                                        autoLogin.putString("name", user.getString("name"));
                                        autoLogin.commit();
                                    } else {
                                        if (cb_save_id.isChecked()){
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
                                    loginDone = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                if(resultObj.getString("data").startsWith("email")){
                                    CustomDialog customDialog = new CustomDialog(LoginActivity.this, null);
                                    customDialog.show();
                                    customDialog.setMessage("메일 인증을 진행해주세요");
                                    customDialog.setNegativeButton("다시시도");
                                }else{
                                    CustomDialog customDialog = new CustomDialog(LoginActivity.this, null);
                                    customDialog.show();
                                    customDialog.setMessage("아이디나 비밀번호가 일치하지 않습니다");
                                    customDialog.setNegativeButton("다시시도");
                                }
                                loginDone = true;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loginDone = true;
                    }, error -> {
                        Log.d("exception", "volley error");
                        CustomDialog customDialog = new CustomDialog(LoginActivity.this, null);
                        customDialog.show();
                        customDialog.setMessage("오류가 발생했습니다");
                        customDialog.setNegativeButton("다시시도");
                        loginDone = true;
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    private void change_visible(EditText v, ImageButton visibletoggle){
        if (v.getTag() == "0"){
            v.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            v.setSelection(v.getText().length());
            visibletoggle.setImageResource(R.drawable.ic_unvisibletoggle);
            v.setTag("1");
        } else {
            v.setTransformationMethod(PasswordTransformationMethod.getInstance());
            v.setSelection(v.getText().length());
            visibletoggle.setImageResource(R.drawable.visibletoggle);
            v.setTag("0");
        }
    }
    public void getNotices(){
        try{
            Log.d("starting request", "get notices");
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/notices";
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            MainActivity.NoticeArrayList = new ArrayList<Notice>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("response is", ""+response);

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                String content = post.getString("content");
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat fm = new SimpleDateFormat(getApplicationContext().getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                    Log.d("parsing date", "success");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                JSONArray images = (JSONArray)post.get("image_urls");
                                ArrayList<String> imagearray = new ArrayList<>();
                                if(images!=null) {
                                    imagearray = new ArrayList<String>();
                                    for (int j = 0; j < images.length(); j++) {
                                        imagearray.add(images.getString(j));
                                    }
                                }
                                Notice newNotice = new Notice(id, title, author, content, date);

                                if(images!=null){
                                    newNotice.setImages(imagearray);
                                }
                                MainActivity.NoticeArrayList.add(newNotice);
                                notice_done = true;
                            }
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}