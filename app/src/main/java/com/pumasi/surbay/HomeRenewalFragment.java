package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

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
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static ArrayList<Banner> homeBanners = new ArrayList<>();
    private static Context mContext;
    private static final Integer[] IMAGES = {R.drawable.renewal_banner, R.drawable.tutorialbanner2};
    private ArrayList<String> ImagesArray = new ArrayList<>();
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
    private HomeResearchPagerAdapter homeResearchPagerAdapter;
    private HomeVotePagerAdapter homeVotePagerAdapter;
    private HomeTipPagerAdapter homeTipPagerAdapter;
    private static BannerViewPagerAdapter bannerAdapter;
    private static DotsIndicator vp_research_indicator;
    private static DotsIndicator vp_vote_indicator;
    private static DotsIndicator vp_tip_indicator;
    private static ImageView iv_research_none;
    private static ImageView iv_vote_none;
    private static ImageView iv_tip_none;
    public static ArrayList<Post> randomPosts;
    public static ArrayList<General> randomVotes;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_home_renewal, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        MainActivity.getBanners();
        getRandomPosts();

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
                HomeResearchPagerAdapter.ShuffleHomeResearch();
                vp_research.setAdapter(homeResearchPagerAdapter);
            }
        });
        ib_home_vote_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeVotePagerAdapter.ShuffleHomeVote();
                vp_vote.setAdapter(homeVotePagerAdapter);
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
        setVp_research();
        setVp_vote();
        setVp_research_tip();
    }
    private void setVp_banner() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (homeBanners.size() == 0) {
                    try {
                        Log.d("fuck", "run: ");
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        vp_banner = view.findViewById(R.id.vp_banner);
        for (int i = 0; i < homeBanners.size(); i++) ImagesArray.add(homeBanners.get(i).getImage_url());
        bannerAdapter = new BannerViewPagerAdapter(getActivity().getApplicationContext(), ImagesArray);
        vp_banner.setAdapter(bannerAdapter);

        // 원리 시간 날 때 찾아볼 것!!
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

        homeResearchPagerAdapter = new HomeResearchPagerAdapter(getChildFragmentManager());

        vp_research.setAdapter(homeResearchPagerAdapter);
        vp_research_indicator.setViewPager(vp_research);

    }
    private void setVp_vote() {
        vp_vote_indicator = view.findViewById(R.id.vp_vote_indicator);
        vp_vote = view.findViewById(R.id.vp_vote);

        homeVotePagerAdapter = new HomeVotePagerAdapter(getChildFragmentManager());

        vp_vote.setAdapter(homeVotePagerAdapter);
        vp_vote_indicator.setViewPager(vp_vote);

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
    public static void getRandomPosts() {
        try {
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
                                Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, num_prize, comments, done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info);
                                randomPosts.add(newPost);
                                Log.d("어?", "getRandomPosts: ");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
            }, error -> {
                        error.printStackTrace();
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    public static void getRandomVotes() {
//        try {
//            String requestURL = R.string.server + "/api/generals/random";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setVp_banner();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}