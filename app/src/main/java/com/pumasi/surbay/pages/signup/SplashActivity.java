package com.pumasi.surbay.pages.signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.LoginActivity;
import com.pumasi.surbay.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 300;
    public static Integer notification_type = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mContext = getApplicationContext();
        Log.d("extras", ""+getIntent().getExtras() );
        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("FIREBASEMESSAGING", "Key: " + key + " Value: " + value);
                if(key.equals("type")){
                    notification_type = Integer.valueOf(value.toString());
                }
            }
        }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIME_OUT);


    }


//    private void makeLoginRequest(String username, String password) throws Exception{
//        try{
//            String requestURL = getString(R.string.server)+"/login";
//            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
//            JSONObject params = new JSONObject();
//            params.put("userID", username);
//            params.put("userPassword", password);
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                    (Request.Method.POST, requestURL, params, response -> {
//                        Log.d("response is", ""+response);
//                        try {
//                            JSONObject resultObj = new JSONObject(response.toString());
//                            Boolean success = resultObj.getBoolean("type");
//                            if(success) {
//                                String token = resultObj.getString("token");
//                                JSONObject user = resultObj.getJSONObject("data");
//                                UserPersonalInfo.token = token;
//                                UserPersonalInfo.name = user.getString("name");
//                                UserPersonalInfo.email = user.getString("email");
//                                UserPersonalInfo.points = user.getInt("points");
//                                UserPersonalInfo.level = user.getInt("level");
//                                UserPersonalInfo.userID = user.getString("userID");
//                                UserPersonalInfo.userPassword = user.getString("userPassword");
//                                UserPersonalInfo.gender = user.getInt("gender");
//                                UserPersonalInfo.yearBirth = user.getInt("yearBirth");
//                                JSONArray ja = (JSONArray)user.get("participations");
//
//                                ArrayList<String> partiarray = new ArrayList<String>();
//                                for (int j = 0; j<ja.length(); j++){
//                                    partiarray.add(ja.getString(j));
//                                }
//
//                                UserPersonalInfo.participations = partiarray;
//                                Log.d("partiarray", ""+UserPersonalInfo.participations.toString());
//
//                                JSONArray ja2 = (JSONArray)user.get("prizes");
//                                ArrayList<String> prizearray = new ArrayList<String>();
//                                for (int j = 0; j<ja2.length(); j++){
//                                    prizearray.add(ja2.getString(j));
//                                }
//                                UserPersonalInfo.prizes = prizearray;
//
//                                try {
//                                    MainActivity.getNotices();
//                                    getPosts();
//                                } catch (Exception e) {
//                                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                    e.printStackTrace();
//                                }
//
//
//                            }else {
//                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }, error -> {
//                        Log.d("exception", "volley error");
//                        error.printStackTrace();
//                    });
//            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(jsonObjectRequest);
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }

//    private void getPosts(){
//        try{
//            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts";
//            RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
//            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
//                    (Request.Method.GET, requestURL, null, response -> {
//                        try {
//                            MainActivity.postArrayList = new ArrayList<Post>();
//                            MainActivity.notreportedpostArrayList = new ArrayList<Post>();
//                            MainActivity.reportpostArrayList = new ArrayList<Post>();
//                            JSONArray resultArr = new JSONArray(response.toString());
//                            for (int i = 0; i < resultArr.length(); i++) {
//                                JSONObject post = resultArr.getJSONObject(i);
//                                String id = post.getString("_id");
//                                String title = post.getString("title");
//                                String author = post.getString("author");
//                                Integer author_lvl = post.getInt("author_lvl");
//                                String content = post.getString("content");
//                                Integer participants = post.getInt("participants");
//                                Integer goal_participants = post.getInt("goal_participants");
//                                String url = post.getString("url");
//                                SimpleDateFormat fm = new SimpleDateFormat(getString(R.string.date_format));
//                                Date date = null;
//                                try {
//                                    date = fm.parse(post.getString("date"));
//                                    Log.d("parsing date", "success");
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                Date deadline = null;
//                                try {
//                                    deadline = fm.parse(post.getString("deadline"));
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                Boolean with_prize = post.getBoolean("with_prize");
//                                String prize = "none";
//                                Integer count= 0;
//                                Integer est_time = post.getInt("est_time");
//                                String target = post.getString("target");
//                                Boolean done = post.getBoolean("done");
//                                Boolean hide = post.getBoolean("hide");
//                                Integer extended = post.getInt("extended");
//                                String author_userid = post.getString("author_userid");
//                                if(with_prize) {
//                                    prize = post.getString("prize");
//                                    count = post.getInt("num_prize");
//                                }
//                                JSONArray ia = (JSONArray)post.get("participants_userids");
//
//                                ArrayList<String> participants_userids = new ArrayList<String>();
//                                for (int j = 0; j<ia.length(); j++){
//                                    participants_userids.add(ia.getString(j));
//                                }
//
//                                JSONArray ka = (JSONArray)post.get("reports");
//
//                                ArrayList<String> reports = new ArrayList<String>();
//                                for (int j = 0; j<ka.length(); j++){
//                                    reports.add(ka.getString(j));
//                                }
//
//                                ArrayList<Reply> comments = new ArrayList<>();
//                                try{
//                                    JSONArray ja = (JSONArray)post.get("comments");
//                                    if (ja.length() != 0){
//                                        for (int j = 0; j<ja.length(); j++){
//                                            JSONObject reply = ja.getJSONObject(j);
//                                            String reid = reply.getString("_id");
//                                            String writer = reply.getString("writer");
//                                            String contetn = reply.getString("content");
//                                            Date datereply = null;
//                                            try {
//                                                datereply = fm.parse(reply.getString("date"));
//                                            } catch (ParseException e) {
//                                                e.printStackTrace();
//                                            }
//                                            Boolean replyhide = reply.getBoolean("hide");
//                                            JSONArray ua = (JSONArray)reply.get("reports");
//
//                                            ArrayList<String> replyreports = new ArrayList<String>();
//                                            for (int u = 0; u<ua.length(); u++){
//                                                replyreports.add(ua.getString(u));
//                                            }
//                                            String writer_name = null;
//                                            try {
//                                                writer_name = reply.getString("writer_name");
//                                            }catch (Exception e){
//                                                writer_name = null;
//                                            }
//                                            Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
//                                            re.setWriter_name(writer_name);
//                                            if (!replyhide && !replyreports.contains(UserPersonalInfo.userID)){
//                                                comments.add(re);
//                                            }
//                                        }
//                                    }
//
//                                } catch (Exception e){
//                                    e.printStackTrace();
//                                }
//                                Integer pinned = post.getInt("pinned");
//                                Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids, reports, hide, author_userid);
//                                newPost.setPinned(pinned);
//                                Date now = new Date();
//                                if(reports.contains(UserPersonalInfo.userID) || hide) {
//                                    MainActivity.reportpostArrayList.add(newPost);
//                                } else if (now.after(newPost.getDeadline()) || newPost.isDone()){
//                                    MainActivity.notreportedpostArrayList.add(newPost);
//                                }
//                                else {
//                                    MainActivity.postArrayList.add(newPost);
//                                    MainActivity.notreportedpostArrayList.add(newPost);
//                                }
//                            }
//                            Log.d("startmainactivity", "posts are"+MainActivity.postArrayList.size()+" "+MainActivity.notreportedpostArrayList.size());
//
//                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                            startActivity(intent);
//                        } catch (JSONException e) {
//                            Log.d("exception", "JSON error");
//                            e.printStackTrace();
//                        }
//                    }, error -> {
//                        Log.d("exception", "volley error");
//                        error.printStackTrace();
//                        try {
//                            throw new Exception();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    });
//            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//            requestQueue.add(jsonArrayRequest);
//        } catch (Exception e){
//            Log.d("exception", "failed getting response");
//            e.printStackTrace();
//        }
//    }
}