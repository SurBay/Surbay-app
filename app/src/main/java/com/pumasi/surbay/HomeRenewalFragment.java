package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pumasi.surbay.adapter.BannerViewPagerAdapter;
import com.pumasi.surbay.adapter.HomeResearchPagerAdapter;
import com.pumasi.surbay.adapter.HomeTipPagerAdapter;
import com.pumasi.surbay.adapter.HomeVotePagerAdapter;
import com.pumasi.surbay.classfile.Banner;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeRenewalFragment extends Fragment {

    public static int HOME_RESEARCH = 0;
    public static int HOME_VOTE = 1;
    public static int HOME_TIP = 2;
    public static ArrayList<Banner> homeBanners = new ArrayList<Banner>();
    private static final Integer[] IMAGES = {R.drawable.renewal_banner, R.drawable.tutorialbanner2};
    private ViewPager vp_banner;
    private static ViewPager vp_research;
    private static ViewPager vp_vote;
    private static ViewPager vp_tip;
    private ImageButton ib_shift_home_research;
    private ImageButton ib_shift_home_vote;
    private ImageButton ib_shift_home_tip;
    private ImageButton ib_home_research_shuffle;
    private ImageButton ib_home_vote_shuffle;
    private ImageButton ib_home_tip_shuffle;
    private int currentPage;
    private static View view;
    private static HomeResearchPagerAdapter homeResearchPagerAdapter;
    private static HomeVotePagerAdapter homeVotePagerAdapter;
    private HomeTipPagerAdapter homeTipPagerAdapter;
    private static BannerViewPagerAdapter bannerAdapter;
    private static DotsIndicator vp_research_indicator;
    private static DotsIndicator vp_vote_indicator;
    private static DotsIndicator vp_tip_indicator;
    private static ImageView iv_research_none;
    private static ImageView iv_vote_none;
    private static ImageView iv_tip_none;
    private ArrayList<String> ImagesArray = new ArrayList<>();
    public static ArrayList<Post> randomPosts = new ArrayList<Post>();
    public static ArrayList<General> randomVotes = new ArrayList<General>();
    private boolean doneBanners = true;
    public static boolean doneResearch = false;
    public static boolean doneVote = false;
    public static boolean shuffleResearchDone = false;
    public static boolean shuffleVoteDone = false;
    private Handler handler;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        doneVote = false;
        doneBanners = false;
        view = inflater.inflate(R.layout.fragment_home_renewal, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        ib_shift_home_research = view.findViewById(R.id.ib_shift_home_research);
        ib_shift_home_vote = view.findViewById(R.id.ib_shift_home_vote);
        ib_shift_home_tip = view.findViewById(R.id.ib_shift_home_tip);

        ib_home_research_shuffle = view.findViewById(R.id.ib_home_search_shuffle);
        ib_home_vote_shuffle = view.findViewById(R.id.ib_home_vote_shuffle);
        ib_home_tip_shuffle = view.findViewById(R.id.ib_home_tip_shuffle);

        iv_research_none = view.findViewById(R.id.iv_research_none);
        iv_vote_none = view.findViewById(R.id.iv_vote_none);
        iv_tip_none = view.findViewById(R.id.iv_tip_none);

        ib_home_research_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRandomPosts();
            }
        });
        ib_home_vote_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRandomVotes();
            }
        });
        ib_home_tip_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeTipPagerAdapter.ShuffleHomeTip();
                vp_tip.setAdapter(homeTipPagerAdapter);
                vp_tip_indicator.setViewPager(vp_tip);
            }
        });
        ib_shift_home_research.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_research_board);
            }
        });
        ib_shift_home_vote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottomNavi);
                bottomNavigationView.setSelectedItemId(R.id.action_free_board);
                FreeBoardFragment.free_position = 0;

            }
        });
        ib_shift_home_tip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SurveyTipContainer.class);
                startActivity(intent);
            }
        });

        setView();
        return view;
    }
    private void setView() {
        setVp_banner();
        setVp_vote();
        setVp_research();
        setVp_research_tip();
    }
    private void setVp_banner() {

        vp_banner = view.findViewById(R.id.vp_banner);
        for (int i = 0; i < homeBanners.size(); i++) ImagesArray.add(homeBanners.get(i).getImage_url());
        bannerAdapter = new BannerViewPagerAdapter(getActivity().getApplicationContext(), ImagesArray);
        vp_banner.setAdapter(bannerAdapter);

        vp_banner.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
           // 원리 시간 날 때 찾아볼 것!!
        int NUM_PAGES = IMAGES.length;

           final Handler handler = new Handler();
           final Runnable Update = new Runnable() {
               public void run() {
                   if (currentPage == NUM_PAGES) {
                       currentPage = 0;
                   }
                   vp_banner.setCurrentItem(currentPage++, true);
               }
           };
           Timer swipeTimer = new Timer();
           swipeTimer.schedule(new TimerTask() {
               @Override
               public void run() {
                   handler.post(Update);
               }
           }, 30000, 30000);


    }
    private void setVp_research() {
        vp_research = view.findViewById(R.id.vp_research);
        vp_research_indicator = view.findViewById(R.id.vp_research_indicator);
        if (randomPosts.size() == 0) {
            getRandomPosts();
        } else {
            homeResearchPagerAdapter = new HomeResearchPagerAdapter(getChildFragmentManager());
            vp_research.setAdapter(homeResearchPagerAdapter);
            vp_research_indicator.setViewPager(vp_research);
        }


    }

    private void setVp_vote() {
        vp_vote_indicator = view.findViewById(R.id.vp_vote_indicator);
        vp_vote = view.findViewById(R.id.vp_vote);
        if (randomVotes.size() == 0) {
            getRandomVotes();
        } else {
            homeVotePagerAdapter = new HomeVotePagerAdapter(getChildFragmentManager());
            vp_vote.setAdapter(homeVotePagerAdapter);
            vp_vote_indicator.setViewPager(vp_vote);
        }



    }
    private void setVp_research_tip() {
        vp_tip_indicator = view.findViewById(R.id.vp_tip_indicator);
        vp_tip = view.findViewById(R.id.vp_tip);

        homeTipPagerAdapter = new HomeTipPagerAdapter(getChildFragmentManager());

        vp_tip.setAdapter(homeTipPagerAdapter);
        vp_tip_indicator.setViewPager(vp_tip);


    }
    public static void set_invisible(int pos) {
        if (pos == HOME_RESEARCH) {
            iv_research_none.setVisibility(View.VISIBLE);
            vp_research.setVisibility(View.INVISIBLE);
            vp_research_indicator.setVisibility(View.INVISIBLE);
        } else if (pos == HOME_VOTE) {
            iv_vote_none.setVisibility(View.VISIBLE);
            vp_vote.setVisibility(View.INVISIBLE);
            vp_vote_indicator.setVisibility(View.INVISIBLE);
        } else if (pos == HOME_TIP) {
            iv_tip_none.setVisibility(View.VISIBLE);
            vp_tip.setVisibility(View.INVISIBLE);
            vp_tip_indicator.setVisibility(View.INVISIBLE);
        }
    }
    public static void set_visible(int pos) {
        if (pos == HOME_RESEARCH) {
            iv_research_none.setVisibility(View.INVISIBLE);
            vp_research.setVisibility(View.VISIBLE);
            vp_research_indicator.setVisibility(View.VISIBLE);
        } else if (pos == HOME_VOTE) {
            iv_vote_none.setVisibility(View.INVISIBLE);
            vp_vote.setVisibility(View.VISIBLE);
            vp_vote_indicator.setVisibility(View.VISIBLE);
        } else if (pos == HOME_TIP) {
            iv_tip_none.setVisibility(View.INVISIBLE);
            vp_tip.setVisibility(View.VISIBLE);
            vp_tip_indicator.setVisibility(View.VISIBLE);
        }
    }
    public void getRandomPosts() {
        try {
            Log.d("getRandom", "getRandomPosts: " + UserPersonalInfo.userID);
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts/random/?user_object_id=" + UserPersonalInfo.userID;
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                        try {
                            randomPosts = new ArrayList<Post>();
                            JSONArray responseArray = new JSONArray(response.toString());
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject post = responseArray.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");
                                Integer participants = post.getInt("participants");
                                Integer goal_participants = post.getInt("goal_participants");
                                String url = post.getString("url");
                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");
                                Date date = null;
                                Date deadline = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                    deadline = fm.parse(post.getString("deadline"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Boolean with_prize = post.getBoolean("with_prize");
                                Integer est_time = post.getInt("est_time");
                                String target = post.getString("target");
                                Boolean done = post.getBoolean("done");
                                Boolean hide = post.getBoolean("hide");
                                Integer extended = post.getInt("extended");
                                String author_userid = post.getString("author_userid");
                                String prize = "none";
                                Integer visit = post.getInt("visit");
                                Double almost = post.getDouble("almost");

                                Integer num_prize = 0;
                                if (with_prize) {
                                    prize = post.getString("prize");
                                    num_prize = post.getInt("num_prize");
                                }
                                Integer pinned = 0;
                                Boolean annonymous = false;
                                String author_info = "";
                                try {
                                    pinned = post.getInt("pinned");
                                    annonymous = post.getBoolean("annonymous");
                                    author_info = post.getString("author_info");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                JSONArray ia = (JSONArray) post.get("participants_userids");
                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j < ia.length(); j++) {
                                    participants_userids.add(ia.getString(j));
                                }
                                JSONArray ka = (JSONArray) post.get("reports");
                                ArrayList<String> reports = new ArrayList<String>();
                                for (int j = 0; j < ka.length(); j++) {
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
                                Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, num_prize, comments, done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info, visit, almost);
                                randomPosts.add(newPost);
                                Log.d("어?", "getRandomPosts: " + newPost);
                            }
                            homeResearchPagerAdapter = new HomeResearchPagerAdapter(getChildFragmentManager());
                            vp_research.setAdapter(homeResearchPagerAdapter);
                            vp_research_indicator.setViewPager(vp_research);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
            }, error -> {
                        error.printStackTrace();
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
            doneResearch = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getRandomVotes() {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals/random/?userID=" + UserPersonalInfo.email;
            Log.d("getRandom", "getRandomVotes: " + UserPersonalInfo.email);
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                        try {
                             randomVotes = new ArrayList<General>();
                             JSONArray responseArray = new JSONArray(response.toString());
                             for (int i = 0; i < responseArray.length(); i++) {
                                 JSONObject general = responseArray.getJSONObject(i);
                                 String id = general.getString("_id");
                                 String title = general.getString("title");
                                 String author = general.getString("author");
                                 Integer author_lvl = general.getInt("author_lvl");
                                 String content = general.getString("content");
                                 SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");
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
                                 randomVotes.add(newGeneral);
                             }
                            homeVotePagerAdapter = new HomeVotePagerAdapter(getChildFragmentManager());
                            vp_vote.setAdapter(homeVotePagerAdapter);
                            vp_vote_indicator.setViewPager(vp_vote);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
            }, error -> {
                        error.printStackTrace();
            });
            Log.d("도대체 어디가 안되는 거야", "getRandomVotes: " + randomVotes);
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
            doneVote = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static boolean getBanners() {
    try {
        String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/banner/getbanner";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.mContext);
        Log.d("fuck", "getBanners: " );
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, requestURL, null, response -> {
            try {
                HomeRenewalFragment.homeBanners = new ArrayList<Banner>();
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String id = item.getString("_id");
                    int type = item.getInt("type");
                    boolean hide = item.getBoolean("hide");
                    boolean done = item.getBoolean("done");
                    String title = item.getString("title");
                    String author = item.getString("author");
                    String content = item.getString("content");
                    String url = item.getString("url");
                    String image_url = item.getString("image_url");
                    SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");
                    Date date = null;
                    try {
                        date = fm.parse(item.getString("date"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Banner banner = new Banner(id, type, hide, done, title, author, content, url, date, image_url);
                    homeBanners.add(banner);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
                Log.d("exception", "JSON error");

            }
        }, error -> {
            error.printStackTrace();
            Log.d("exception", "volley error");

        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    } catch (Exception e) {
        e.printStackTrace();
        Log.d("exception", "failed getting response");

    }
    return true;

}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }



}
