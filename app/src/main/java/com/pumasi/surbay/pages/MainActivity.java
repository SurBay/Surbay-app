package com.pumasi.surbay.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pumasi.surbay.BoardsFragment;
import com.pumasi.surbay.HomeRenewalFragment;
import com.pumasi.surbay.pages.homepage.HomeFragment;
import com.pumasi.surbay.pages.homepage.NoticeActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.mypage.MypageFragment;
import com.pumasi.surbay.pages.mypage.NotificationsActivity;
import com.pumasi.surbay.pages.signup.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;

    private HomeRenewalFragment homeRenewalFragment;
    private BoardsFragment boardsFragment;
    private MypageFragment mypageFragment;

    public static ArrayList<Post> postArrayList = new ArrayList<>();
    public static ArrayList<Post> notreportedpostArrayList = new ArrayList<>();
    public static ArrayList<Post> reportpostArrayList = new ArrayList<>();

    public static ArrayList<Surveytip> surveytipArrayList = new ArrayList<>();
    public static ArrayList<PostNonSurvey> feedbackArrayList = new ArrayList<>();
    public static ArrayList<Notice> NoticeArrayList = new ArrayList<>();
    public static ArrayList<General> generalArrayList = new ArrayList<>();
    public static Context mContext;
    public static Integer done = 0;

    private static Comparator<Notice> cmpNoticeNew;

    public static int SORT = 1;

    public static Date today;
    private CustomDialog endDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = getApplicationContext();

        bottomNavigationView = findViewById(R.id.bottomNavi);

        homeRenewalFragment= new HomeRenewalFragment();
        boardsFragment = new BoardsFragment();
        mypageFragment = new MypageFragment();

        today = new Date();
        SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));

        // 받지 못한 정보가 있으면 서버에서 가져오는 함수
        if(postArrayList.size() == 0 || notreportedpostArrayList.size() == 0) {
            getPosts();
        }
        if(surveytipArrayList.size()==0) {
            getSurveytips();
        }
        if(feedbackArrayList.size()==0) {
            getFeedbacks();
        }
        if(NoticeArrayList.size()==0) {
            getNotices();
        }
        if(generalArrayList.size()==0){
            getGenerals();
        }
        getPersonalInfo();

        cmpNoticeNew = new Comparator<Notice>() {
            @Override
            public int compare(Notice o1, Notice o2) {
                int ret;
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                int compare = date1.compareTo(date2);
                if (compare > 0)
                    ret = -1;
                else if (compare == 0)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };

        Collections.sort(MainActivity.NoticeArrayList, cmpNoticeNew);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.action_home:
                        setFrag(0);
                        break;
                    case R.id.action_boards:
                        setFrag(1);
                        break;
                    case R.id.action_mypage:
                        setFrag(2);
                        break;
                }
                return true;
            }
        });
        setFrag(0); // 첫 프래그먼트 화면 지정

        if (SplashActivity.notification_type==2){
            Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
            startActivity(intent);
        } else if(SplashActivity.notification_type==1){
            Intent intent = new Intent(MainActivity.this, NoticeActivity.class);
            startActivity(intent);
        }
    }

    // 프레그먼트 교체
    private void setFrag(int n)
    {
        fm = getSupportFragmentManager();
        ft= fm.beginTransaction();
        switch (n)
        {
            case 0:
                ft.replace(R.id.Main_Frame,homeRenewalFragment);
                ft.commit();
                break;

            case 1:
                ft.replace(R.id.Main_Frame,boardsFragment);
                ft.commit();
                break;

            case 2:
                ft.replace(R.id.Main_Frame,mypageFragment);
                ft.commit();
                break;


        }
    }

    @Override
    public void onBackPressed() {
        endDialog = new CustomDialog(MainActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitProgram();

            }
        });
        endDialog.show();
        endDialog.setCanceledOnTouchOutside(true);
        endDialog.setMessage("앱을 종료하겠습니까?");
        endDialog.setPositiveButton("종료");
        endDialog.setNegativeButton("취소");
    }
    private void exitProgram() {
        if(endDialog!=null){
            endDialog.dismiss();
            endDialog = null;
        }
        moveTaskToBack(true);
//        if (Build.VERSION.SDK_INT >= 21) {
//            // 액티비티 종료 + 태스크 리스트에서 지우기
//            finishAndRemoveTask();
//        } else {
//            // 액티비티 종료
//            this.finishAffinity();
//        }
        this.finishAffinity();
    }

    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
            Log.d("getting info failed", "token is null");
            return;
        }
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            JSONObject user = res.getJSONObject("data");
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

                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.putString("name", user.getString("name"));
                            autoLogin.commit();
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
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
    public static void getPosts(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            postArrayList = new ArrayList<Post>();
                            notreportedpostArrayList = new ArrayList<Post>();
                            reportpostArrayList = new ArrayList<Post>();
                            JSONArray resultArr = new JSONArray(response.toString());
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
                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
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
                                            if ((!replyhide )&& (!replyreports.contains(UserPersonalInfo.userID))){
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
                                    reportpostArrayList.add(newPost);
                                } else if (now.after(newPost.getDeadline()) || newPost.isDone()){
                                    notreportedpostArrayList.add(newPost);
                                }
                                 else {
                                    postArrayList.add(newPost);
                                    notreportedpostArrayList.add(newPost);
                                }
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
    public static void getSurveytips(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/surveytips";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {

                        try {
                            surveytipArrayList = new ArrayList<Surveytip>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("response is", ""+response);

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String category = post.getString("category");
                                Integer likes = post.getInt("likes");
                                JSONArray ja = (JSONArray)post.get("liked_users");

                                ArrayList<String> liked_users = new ArrayList<String>();
                                for (int j = 0; j<ja.length(); j++){
                                    liked_users.add(ja.getString(j));
                                }
                                JSONArray images = (JSONArray)post.get("image_urls");
                                ArrayList<String> imagearray = new ArrayList<>();
                                if(images!=null) {
                                    imagearray = new ArrayList<String>();
                                    for (int j = 0; j < images.length(); j++) {
                                        imagearray.add(images.getString(j));
                                    }
                                }



                                String author_userid = post.getString("author_userid");



                                Surveytip newSurveytip = new Surveytip(id, title, author, author_lvl, content,  date, category, likes, liked_users);
                                newSurveytip.setAuthor_userid(author_userid);
                                if(images!=null){
                                    newSurveytip.setImage_uris(imagearray);
                                }
                                surveytipArrayList.add(newSurveytip);

                            }
//                            if(done==0){
//                                HomeFragment.receivedPosts();
//                                done = 1;
//                            }

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
    public static void getFeedbacks(){
        try{
            Log.d("starting request", "get feedbacks");
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/feedbacks";
            ArrayList<Post> list = new ArrayList<>();
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {

                        try {
                            feedbackArrayList = new ArrayList<PostNonSurvey>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("response is", ""+response);

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");

                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                    Log.d("parsing date", "success");
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Integer category = post.getInt("category");

                                ArrayList<Reply> comments = new ArrayList<>();
                                JSONArray ja = (JSONArray)post.get("comments");
                                for (int j = 0; j<ja.length(); j++){
                                    JSONObject reply = ja.getJSONObject(j);
                                    String reid = reply.getString("_id");
                                    String writer = reply.getString("writer");
                                    String contetn = reply.getString("content");
                                    Date datereply = null;
                                    try {
                                        datereply = fm.parse(post.getString("date"));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Boolean replyhide = post.getBoolean("hide");
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
                                    comments.add(re);
                                }

                                String author_userid = post.getString("author_userid");


                                PostNonSurvey newFeedback = new PostNonSurvey(id, title, author, author_lvl, content, date, category, comments);
                                newFeedback.setAuthor_userid(author_userid);
                                feedbackArrayList.add(newFeedback);
                            }
//                            Log.d("array size is",""+postArrayList.size());
//                            if(done==0){
//                                HomeFragment.receivedPosts();
//                                done = 1;
//                            }

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
    public static void getNotices(){
        try{
            Log.d("starting request", "get notices");
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/notices";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            NoticeArrayList = new ArrayList<Notice>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("response is", ""+response);

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                String content = post.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
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
                                NoticeArrayList.add(newNotice);
                            }
//                            if(done==0){
//                                HomeFragment.receivedPosts();
//                                done = 1;
//                            }

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
    public static void getGenerals(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            generalArrayList = new ArrayList<>();
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("gettinggeneralresponseis", ""+response);
                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject general = resultArr.getJSONObject(i);
                                String id = general.getString("_id");
                                String title = general.getString("title");
                                String author = general.getString("author");
                                Integer author_lvl = general.getInt("author_lvl");
                                String content = general.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(general.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Date deadline = null;
                                try {
                                    deadline = fm.parse(general.getString("deadline"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                ArrayList<Reply> comments = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("comments");
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
                                            if ((!replyhide )&& (!replyreports.contains(UserPersonalInfo.userID))){
                                                comments.add(re);
                                            }
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                Boolean done = general.getBoolean("done");
                                String author_userid = general.getString("author_userid");
                                JSONArray ka = (JSONArray)general.get("reports");
                                ArrayList<String> reports = new ArrayList<String>();
                                for (int j = 0; j<ka.length(); j++){
                                    reports.add(ka.getString(j));
                                }
                                Boolean multi_response = general.getBoolean("multi_response");
                                Integer participants = general.getInt("participants");
                                JSONArray ia = (JSONArray)general.get("participants_userids");
                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j<ia.length(); j++){
                                    participants_userids.add(ia.getString(j));
                                }
                                Boolean with_image = general.getBoolean("with_image");
                                ArrayList<Poll> polls = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("polls");
                                    if (ja.length() != 0){
                                        for (int j = 0; j<ja.length(); j++){
                                            JSONObject poll = ja.getJSONObject(j);
                                            String poll_id = poll.getString("_id");
                                            String poll_content = poll.getString("content");
                                            ArrayList<String> poll_participants_userids = new ArrayList<String>();
                                            JSONArray ua = (JSONArray)poll.get("participants_userids");
                                            for (int u = 0; u<ua.length(); u++){
                                                poll_participants_userids.add(ua.getString(u));
                                            }
                                            String image = poll.getString("image");
                                            Poll newpoll = new Poll(poll_id, poll_content, poll_participants_userids, image);
                                            polls.add(newpoll);
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                                JSONArray la = (JSONArray)general.get("liked_users");
                                ArrayList<String> liked_users = new ArrayList<String>();
                                for (int j = 0; j<la.length(); j++){
                                    liked_users.add(la.getString(j));
                                }

                                Integer likes = general.getInt("likes");

                                Boolean hide = general.getBoolean("hide");

                                General newGeneral = new General(id, title, author, author_lvl, content,
                                        date, deadline, comments, done, author_userid, reports, multi_response,
                                        participants, participants_userids, with_image, polls, liked_users, likes, hide);
                                if((!newGeneral.getReports().contains(UserPersonalInfo.userID)) && (hide!=true))
                                    generalArrayList.add(newGeneral);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            getPosts();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getPersonalInfo();
    }
}
