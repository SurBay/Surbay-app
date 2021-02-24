package com.pumasi.surbay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.CustomDialog;
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
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;

    CheckBox auto_login_check;
    CheckBox save_id_check;

    ImageButton visibletoggle;

    private EditText usernameEditText;
    private EditText passwordEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        Log.d("user info start is ", ""+UserPersonalInfo.userID);
        save_id_check = findViewById(R.id.id_save_check);
        auto_login_check = findViewById(R.id.auto_login_check);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
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
                if(username.length()==0){Toast.makeText(LoginActivity.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();}
                else if(password.length()==0){Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();}
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
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
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
                Intent intent = new Intent(LoginActivity.this, FindIdActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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

    private void makeLoginRequest(String username, String password) throws Exception{
        try{
            String requestURL = getString(R.string.server)+"/login";
            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
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
                                    UserPersonalInfo.phoneNumber = user.getString("phoneNumber");
                                    JSONArray ja = (JSONArray)user.get("participations");

                                    ArrayList<String> partiarray = new ArrayList<String>();
                                    for (int j = 0; j<ja.length(); j++){
                                        partiarray.add(ja.getString(j));
                                    }

                                    UserPersonalInfo.participations = partiarray;
                                    Log.d("partiarray", ""+UserPersonalInfo.participations.toString());

                                    JSONArray ja2 = (JSONArray)user.get("prizes");
                                    ArrayList<String> prizearray = new ArrayList<String>();
                                    for (int j = 0; j<ja2.length(); j++){
                                        prizearray.add(ja2.getString(j));
                                    }
                                    UserPersonalInfo.prizes = prizearray;
                                    if (auto_login_check.isChecked()){
                                        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                                        SharedPreferences.Editor autoLogin = auto.edit();
                                        autoLogin.putString("inputId", username);
                                        autoLogin.putString("inputPwd", password);
                                        autoLogin.putString("token", token);
                                        autoLogin.putString("name", user.getString("name"));
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

                                    Log.d("starting main", "myname is "+UserPersonalInfo.userID);
                                    try {
                                        MainActivity.getNotices();
                                        getPosts();

                                    } catch (Exception e) {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        LoginActivity.this.startActivity(intent);
                                        e.printStackTrace();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(intent);
                                }


                            }else {
                                CustomDialog customDialog = new CustomDialog(LoginActivity.this, null);
                                customDialog.show();
                                customDialog.setMessage("아이디나 비밀번호가 일치하지 않습니다");
                                customDialog.setNegativeButton("다시시도");
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

    private void getPosts() throws Exception{
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
                                            Log.d("start app comment", ""+datereply.toString());
                                            Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
                                            Log.d("start app reply", ""+re.getDate().toString());
                                            if (!replyhide && !replyreports.contains(UserPersonalInfo.userID)){
                                                comments.add(re);
                                            }
                                        }
                                    }
                                    Log.d("start app", "getpost comment"+comments.size()+"");

                                } catch (Exception e){
                                    e.printStackTrace();
                                    Log.d("parsing date", "non reply");
                                }
                                Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids, reports, hide, author_userid);
                                Log.d("start app", "newpost comments"+newPost.getComments().size()+"");
                                Date now = new Date();
                                Log.d(title+"reported by", ""+reports+"  "+UserPersonalInfo.userID+reports.contains(UserPersonalInfo.userID));
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
                            Log.d("array size is",""+MainActivity.postArrayList.size());
                            Log.d("finisharray size is",""+ MainActivity.notreportedpostArrayList.size());
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}