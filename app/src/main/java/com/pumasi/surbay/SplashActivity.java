package com.pumasi.surbay;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mContext = getApplicationContext();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                String loginId, loginPwd, name, token;
                loginId = auto.getString("inputId",null);
                loginPwd = auto.getString("inputPwd",null);
                name = auto.getString("name", null);
                token = auto.getString("token", null);
                Log.d("자동로그인", ""+loginId+loginPwd+name+token);

                if(loginId !=null && loginPwd != null) {
                    try {
                        makeLoginRequest(loginId, loginPwd);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);


    }


    private void makeLoginRequest(String username, String password) throws Exception{
        try{
            String requestURL = getString(R.string.server)+"/login";
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
                            if(success) {
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

                                try {
                                    MainActivity.getNotices();
                                    getPosts();
                                } catch (Exception e) {
                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                    e.printStackTrace();
                                }


                            }else {
                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
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

    private void getPosts() throws Exception{
        try{
            Log.d("starting request", "get posts");
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts";
            RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
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
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
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