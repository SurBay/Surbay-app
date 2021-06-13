package com.pumasi.surbay;

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
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.signup.SignupActivityEmail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;

    private CheckBox cb_auto_login;
    private CheckBox cb_save_id;

    ImageButton visibletoggle;

    private EditText et_id;
    private EditText et_password;
    private boolean loginDone = false;

    RelativeLayout loading;

    loginHandler handler = new loginHandler();
    private boolean getPostDone = false;

    TextView nonMemberLogin;

    String fcm_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        try {
            MainActivity.getNotices();
            getPosts();
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
//        FirebaseMessaging.getInstance().subscribeToTopic("surbay");
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
                                    while (!(loginDone && getPostDone)) {
                                        try {
                                            Thread.sleep(100);
                                        } catch (Exception e) {
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
                            while (!(loginDone && getPostDone)) {
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {
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
//                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
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
        jsonObjectRequest.setRetryPolicy(new

                DefaultRetryPolicy(20*1000,DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonObjectRequest);
    }

    private void makeLoginRequest(String username, String password){
        try{
            String requestURL = getString(R.string.server)+"/login";
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);

            JSONObject params = new JSONObject();
            params.put("userID", username);
            params.put("userPassword", password);
            Log.d("logintoken", "" + fcm_token);
            params.put("fcm_token", fcm_token);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, requestURL, params, response -> {
                        try {
                            JSONObject resultObj = new JSONObject(response.toString());
                            Boolean success = resultObj.getBoolean("type");
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
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

                                    ArrayList<String> partiarray = new ArrayList<String>();
                                    for (int j = 0; j<ja.length(); j++){
                                        partiarray.add(ja.getString(j));
                                    }

                                    UserPersonalInfo.participations = partiarray;

                                    JSONArray ja2 = (JSONArray)user.get("prizes");
                                    ArrayList<String> prizearray = new ArrayList<String>();
                                    for (int j = 0; j<ja2.length(); j++){
                                        prizearray.add(ja2.getString(j));
                                    }
                                    UserPersonalInfo.prizes = prizearray;
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

    private void getPosts(){
        try{
            Log.d("starting request", "get posts");
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts";
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            MainActivity.postArrayList = new ArrayList<Post>();
                            MainActivity.notreportedpostArrayList = new ArrayList<Post>();
                            MainActivity.reportpostArrayList = new ArrayList<Post>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("getpost", ""+response+"\n");
                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");
                                Integer participants = post.getInt("participants");
                                Integer goal_participants = post.getInt("goal_participants");
                                String url = post.getString("url");
                                SimpleDateFormat fm = new SimpleDateFormat(getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                    Log.d("parsing date", "success");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date deadline = null;
                                try {
                                    deadline = fm.parse(post.getString("deadline"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Boolean with_prize = post.getBoolean("with_prize");
                                String prize = "none";
                                Integer count= 0;
                                Integer est_time = post.getInt("est_time");
                                String target = post.getString("target");
                                Boolean done = post.getBoolean("done");
                                Boolean hide = post.getBoolean("hide");
                                Integer extended = post.getInt("extended");
                                String author_userid = post.getString("author_userid");
                                if(with_prize) {
                                    prize = post.getString("prize");
                                    count = post.getInt("num_prize");
                                }
                                JSONArray ia = (JSONArray)post.get("participants_userids");

                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j<ia.length(); j++){
                                    participants_userids.add(ia.getString(j));
                                }

                                JSONArray ka = (JSONArray)post.get("reports");

                                ArrayList<String> reports = new ArrayList<String>();
                                for (int j = 0; j<ka.length(); j++){
                                    reports.add(ka.getString(j));
                                }

                                ArrayList<Reply> comments = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)post.get("comments");
                                    if (ja.length() != 0){
                                        for (int j = 0; j<ja.length(); j++){
                                            JSONObject reply = ja.getJSONObject(j);
                                            String reid = reply.getString("_id");
                                            String writer = reply.getString("writer");
                                            String contetn = reply.getString("content");
                                            Date datereply = null;
                                            try {
                                                datereply = fm.parse(reply.getString("date"));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Boolean replyhide = reply.getBoolean("hide");
                                            JSONArray ua = (JSONArray)reply.get("reports");

                                            ArrayList<String> replyreports = new ArrayList<String>();
                                            for (int u = 0; u<ua.length(); u++){
                                                replyreports.add(ua.getString(u));
                                            }
                                            String writer_name = null;
                                            try {
                                                writer_name = reply.getString("writer_name");
                                            }catch (Exception e){
                                                writer_name = null;
                                            }
                                            Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                            re.setWriter_name(writer_name);
                                            if (!replyhide && !replyreports.contains(UserPersonalInfo.userID)){
                                                comments.add(re);
                                            }
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                Integer pinned = 0;
                                Boolean annonymous = false;
                                String author_info = "";
                                try {
                                    pinned = post.getInt("pinned");
                                    annonymous = post.getBoolean("annonymous");
                                    author_info = post.getString("author_info");
                                }catch (Exception e){

                                }
                                Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info);

                                Date now = new Date();
                                if(reports.contains(UserPersonalInfo.userID) || hide) {
                                    MainActivity.reportpostArrayList.add(newPost);
                                } else if (now.after(newPost.getDeadline()) || newPost.isDone()){
                                    MainActivity.notreportedpostArrayList.add(newPost);
                                }
                                else {
                                    MainActivity.postArrayList.add(newPost);
                                    MainActivity.notreportedpostArrayList.add(newPost);
                                }
                            }
                            getPostDone = true;
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