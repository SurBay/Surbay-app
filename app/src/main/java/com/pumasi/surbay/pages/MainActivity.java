package com.pumasi.surbay.pages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.pumasi.surbay.BlankFragment;
import com.pumasi.surbay.FreeBoardFragment;
import com.pumasi.surbay.MypageRenewalFragment;
import com.pumasi.surbay.ResearchBoardFragment;
import com.pumasi.surbay.HomeRenewalFragment;
import com.pumasi.surbay.VoucherBoardFragment;
import com.pumasi.surbay.classfile.MyCoupon;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.pages.homepage.NoticeActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.mypage.NotificationsActivity;
import com.pumasi.surbay.pages.signup.SplashActivity;
import com.pumasi.surbay.tools.FirebaseLogging;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ServerTransport st;
    public static float screen_ratio;
    public static int screen_width;
    public static int screen_width_px;
    private int previous = R.id.action_home;
    private CustomDialog customDialog;

    private BottomNavigationView bottomNavigationView; // 바텀 네비게이션 뷰
    private FragmentManager fm;
    private FragmentTransaction ft;

    private HomeRenewalFragment homeRenewalFragment;
    private ResearchBoardFragment researchBoardFragment;
    public static FreeBoardFragment freeBoardFragment;
    private VoucherBoardFragment voucherBoardFragment;
    private MypageRenewalFragment mypageRenewalFragment;

    public static ArrayList<Surveytip> surveytipArrayList = new ArrayList<>();
    public static ArrayList<PostNonSurvey> feedbackArrayList = new ArrayList<>();
    public static ArrayList<Notice> NoticeArrayList = new ArrayList<>();
    public static Context mContext;
    public static Integer done = 0;

    public static Date today;
    private CustomDialog endDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();

        new FirebaseLogging(mContext).LogScreen("frame", "프레임");
        Log.d("token", "onCreate: " + UserPersonalInfo.token);

        st = new ServerTransport(mContext);
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        if (display.widthPixels > display.heightPixels) {
            screen_ratio = (float) Math.round(display.widthPixels * 10000 / (double) display.heightPixels) / 10000;
        } else {
            screen_ratio = (float) Math.round(display.heightPixels * 10000 / (double) display.widthPixels) / 10000;
        }
        screen_width = (int) display.widthPixels * 160 / display.densityDpi;
        screen_width_px = display.widthPixels;
        Log.d("screen_width", "onCreate: " + screen_width);
        Log.d("screen_ratio", "onCreate: " + screen_ratio);
        bottomNavigationView = findViewById(R.id.bottomNavi);

        homeRenewalFragment= new HomeRenewalFragment();
        researchBoardFragment = new ResearchBoardFragment();
        freeBoardFragment = new FreeBoardFragment();
        voucherBoardFragment = new VoucherBoardFragment();
        mypageRenewalFragment = new MypageRenewalFragment();

        today = new Date();

        if(feedbackArrayList.size()==0) {
            getFeedbacks();
        }
        MainActivity.getSurveytips();

        Collections.reverse(MainActivity.NoticeArrayList);
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
                    case R.id.action_research_board:
                        setFrag(1);
                        break;
                    case R.id.action_free_board:
                        setFrag(2);
                        break;
                    case R.id.action_voucher:
                        setFrag(3);
                        break;
                    case R.id.action_mypage:
                        setFrag(4);
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
                previous = R.id.action_home;
                ft.replace(R.id.Main_Frame,homeRenewalFragment);
                ft.commit();
                break;

            case 1:
                previous = R.id.action_research_board;
                ft.replace(R.id.Main_Frame,researchBoardFragment);
                ft.commit();
                break;

            case 2:
                if (FreeBoardFragment.frag_position == 0) {
                    new FirebaseLogging(mContext).LogScreen("vote_board", "투표게시판");
                } else if (FreeBoardFragment.frag_position == 1) {
                    new FirebaseLogging(mContext).LogScreen("contents_board", "콘텐츠게시판");
                }
                previous = R.id.action_free_board;
                ft.replace(R.id.Main_Frame,freeBoardFragment);
                ft.commit();
                break;

            case 3:
                new FirebaseLogging(mContext).LogScreen("exchange_page", "교환페이지");
                ArrayList<String> access_users = new ArrayList<String>(Arrays.asList("specific0924@yonsei.ac.kr", "SurBay_Admin", "2018142226@yonsei.ac.kr"));
                if (access_users.contains(UserPersonalInfo.email)) {
                    previous = R.id.action_voucher;
                    ft.replace(R.id.Main_Frame, voucherBoardFragment);
                    ft.commit();
                } else {
                    customDialog = new CustomDialog(MainActivity.this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customDialog.dismiss();
                            bottomNavigationView.setSelectedItemId(previous);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://pf.kakao.com/_NIxdxks"));
                            startActivity(intent);
                        }
                    });
                    customDialog.show();
                    customDialog.setMessage("카카오톡 페이지로 이동하시겠습니까?");
                    customDialog.setPositiveButton("이동");
                    customDialog.setNegativeButton("취소");
                    customDialog.setOnCancelListener(dialog -> {
                        bottomNavigationView.setSelectedItemId(previous);
                    });
                }
                break;

            case 4:
                previous = R.id.action_mypage;
                ft.replace(R.id.Main_Frame, mypageRenewalFragment);
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
        this.finishAffinity();
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
                                    JSONObject comment = ja.getJSONObject(j);
                                    String reid = comment.getString("_id");
                                    String writer = comment.getString("writer");
                                    String contetn = comment.getString("content");
                                    Date datereply = null;
                                    try {
                                        datereply = fm.parse(post.getString("date"));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Boolean replyhide = post.getBoolean("hide");
                                    JSONArray ua = (JSONArray)comment.get("reports");

                                    ArrayList<String> replyreports = new ArrayList<String>();
                                    for (int u = 0; u<ua.length(); u++){
                                        replyreports.add(ua.getString(u));
                                    }
                                    String writer_name = null;
                                    try {
                                        writer_name = comment.getString("writer_name");
                                    }catch (Exception e){
                                        writer_name = null;
                                    }
                                    ArrayList<ReReply> reReplies = new ArrayList<>();
                                    try {
                                        JSONArray jk = (JSONArray) comment.get("reply");
                                        if (jk.length() != 0) {
                                            for (int k = 0; k < jk.length(); k++) {
                                                JSONObject reReply = jk.getJSONObject(k);
                                                String id_ = reReply.getString("_id");
                                                ArrayList<String> reports_ = new ArrayList<>();
                                                JSONArray jb = (JSONArray) reReply.get("reports");
                                                for (int b = 0; b < jb.length(); b++) {
                                                    reports_.add(jb.getString(b));
                                                }
                                                ArrayList<String> report_reasons_ = new ArrayList<>();
                                                JSONArray jc = (JSONArray) reReply.get("report_reasons");
                                                for (int c = 0; c < jc.length(); c++) {
                                                    report_reasons_.add(jc.getString(c));
                                                }
                                                boolean hide_ = reReply.getBoolean("hide");
                                                String writer_ = reReply.getString("writer");
                                                String writer_name_ = "";
                                                try {
                                                    writer_name_ = reReply.getString("writer_name");
                                                } catch (Exception e) {
                                                    writer_name_ = "익명";
                                                }
                                                String content_ = reReply.getString("content");
                                                Date date_ = fm.parse(reReply.getString("date"));
                                                String replyID_ = reReply.getString("replyID");

                                                ReReply newReReply = new ReReply(id_, reports_, report_reasons_, hide_, writer_, writer_name_, content_, date_, replyID_);
                                                reReplies.add(newReReply);
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide, writer_name, reReplies);
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

}
