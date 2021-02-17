package com.pumasi.surbay;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
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
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.classfile.loadingProgress;
import com.pumasi.surbay.mypage.MypageFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;
    private HomeFragment homeFragment;
    private BoardsFragment boardsFragment;
    private MypageFragment mypageFragment;
    public static ArrayList<Post> postArrayList = new ArrayList<>();
    public static ArrayList<Post> finishpostArrayList = new ArrayList<>();
    public static ArrayList<Surveytip> surveytipArrayList = new ArrayList<>();
    public static ArrayList<PostNonSurvey> feedbackArrayList = new ArrayList<>();
    public static ArrayList<Notice> NoticeArrayList = new ArrayList<>();
    private static Context mContext;
    private static Integer done = 0;

    public static int SORT = 1;

    public static Date today;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getApplicationContext();

        today = new Date();
        SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));

        loadingProgress loadingProgress = new loadingProgress();

        if(postArrayList.size() == 0 || finishpostArrayList.size() == 0) {
            try {
                getPosts();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(surveytipArrayList.size()==0) {
            try {
                getSurveytips();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(postArrayList.size()==0) {
            try {
                getFeedbacks();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(postArrayList.size()==0) {
            try {
                getNotices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        getPersonalInfo();

        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.bottomNavi);
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

        homeFragment=new HomeFragment();
        boardsFragment = new BoardsFragment();
        mypageFragment = new MypageFragment();
        setFrag(0); // 첫 프래그먼트 화면 지정
    }

    // 프레그먼트 교체
    private void setFrag(int n)
    {
        fm = getSupportFragmentManager();
        ft= fm.beginTransaction();
        switch (n)
        {
            case 0:
                ft.replace(R.id.Main_Frame,homeFragment);
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

    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
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
                            Log.d("response is", ""+response);
                            JSONObject user = res.getJSONObject("data");
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
                            Log.d("prizearray", ""+UserPersonalInfo.prizes.toString());


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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }

    public static void getPosts() throws Exception{
        try{
            Log.d("starting request", "get posts");
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts";
            RequestQueue requestQueue = Volley.newRequestQueue(mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            postArrayList = new ArrayList<Post>();
                            finishpostArrayList = new ArrayList<Post>();
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
                                SimpleDateFormat fm = new SimpleDateFormat(mContext.getString(R.string.date_format));
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
                                Integer extended = post.getInt("extended");
                                if(with_prize) {
                                    prize = post.getString("prize");
                                    count = post.getInt("num_prize");
                                }
                                JSONArray ia = (JSONArray)post.get("participants_userids");

                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j<ia.length(); j++){
                                    participants_userids.add(ia.getString(j));
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
                                            Log.d("start app comment", ""+datereply.toString());
                                            Reply re = new Reply(reid, writer, contetn, datereply);
                                            Log.d("start app reply", ""+re.getDate().toString());
                                            comments.add(re);
                                        }
                                    }
                                    Log.d("start app", "getpost comment"+comments.size()+"");
                                } catch (Exception e){
                                    e.printStackTrace();
                                    Log.d("parsing date", "non reply");
                                }
                                Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids);
                                Log.d("start app", "newpost comments"+newPost.getComments().size()+"");
                                if (!newPost.isDone()) {
                                    finishpostArrayList.add(newPost);
                                    postArrayList.add(newPost);
                                } else {
                                    postArrayList.add(newPost);
                                }
                            }
                            Log.d("array size is",""+postArrayList.size());
                            Log.d("finisharray size is",""+finishpostArrayList.get(0).toString());
                            if(done==0){
                                HomeFragment.receivedPosts();
                                done = 1;
                            }
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
    public static void getSurveytips() throws Exception{
        try{
            Log.d("starting request", "get surveytips");
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
                                    Log.d("parsing date", "success");
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

                                Surveytip newSurveytip = new Surveytip(id, title, author, author_lvl, content,  date, category, likes, liked_users);
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
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
    public static void getFeedbacks() throws Exception{
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
                                    Reply re = new Reply(reid, writer, contetn, datereply);
                                    comments.add(re);
                                }


                                PostNonSurvey newFeedback = new PostNonSurvey(id, title, author, author_lvl, content, date, category, comments);
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
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }


    public static void getNotices() throws Exception{
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

                                Notice newNotice = new Notice(id, title, author, content, date);
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
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}
