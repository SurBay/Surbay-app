package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pumasi.surbay.adapter.BannerViewPagerAdapter;
import com.pumasi.surbay.adapter.HomeResearchPagerAdapter;
import com.pumasi.surbay.adapter.HomeTipPagerAdapter;
import com.pumasi.surbay.adapter.HomeVotePagerAdapter;
import com.pumasi.surbay.classfile.Banner;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.homepage.NoticeActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class HomeRenewalFragment extends Fragment {

    private static Context context;
    private static FragmentManager fragmentManager;
    public RandomGetHandler randomGetHandler = new RandomGetHandler();
    public static int HOME_RESEARCH = 0;
    public static int HOME_VOTE = 1;
    public static int HOME_TIP = 2;
    private RelativeLayout rl_research;
    private RelativeLayout rl_vote;
    private RelativeLayout rl_tip;
    private LinearLayout ll_banner;
    public static ArrayList<Banner> homeBanners = new ArrayList<Banner>();
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
    private ImageView ib_bell;
    public static ArrayList<Post> randomPosts = new ArrayList<Post>();
    public static ArrayList<General> randomVotes = new ArrayList<General>();
    public static ArrayList<Surveytip> fullSurveytips = new ArrayList<Surveytip>();
    private boolean getBannerDone = false;
    private boolean getPostDone = false;
    private boolean getVoteDone = false;
    private boolean getTipDone = false;
    private int refresh_count = 0;

    private SkeletonScreen skeletonScreen;
    private SkeletonScreen skeletonScreen2;
    private SkeletonScreen skeletonScreen3;
    private SkeletonScreen skeletonScreen4;
    private SwipeRefreshLayout refresh_boards;
    private Handler handler = null;
    private Runnable update = null;
    private Timer swipeTimer = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_renewal, container, false);
        context = getActivity().getApplicationContext();
        fragmentManager = getChildFragmentManager();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        ib_bell = view.findViewById(R.id.ib_bell);
        ib_bell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NoticeActivity.class);
                startActivity(intent);
            }
        });
        rl_research = view.findViewById(R.id.rl_research);
        rl_vote = view.findViewById(R.id.rl_vote);
        rl_tip = view.findViewById(R.id.rl_tip);
        ll_banner = view.findViewById(R.id.ll_banner);

        ib_shift_home_research = view.findViewById(R.id.ib_shift_home_research);
        ib_shift_home_vote = view.findViewById(R.id.ib_shift_home_vote);
        ib_shift_home_tip = view.findViewById(R.id.ib_shift_home_tip);

        ib_home_research_shuffle = view.findViewById(R.id.ib_home_search_shuffle);
        ib_home_vote_shuffle = view.findViewById(R.id.ib_home_vote_shuffle);
        ib_home_tip_shuffle = view.findViewById(R.id.ib_home_tip_shuffle);

        iv_research_none = view.findViewById(R.id.iv_research_none);
        iv_vote_none = view.findViewById(R.id.iv_vote_none);
        iv_tip_none = view.findViewById(R.id.iv_tip_none);

        refresh_boards = view.findViewById(R.id.refresh_boards);
        refresh_boards.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                skeletonScreen = Skeleton.bind(rl_research)
                        .load(R.layout.gray)
                        .angle(0)
                        .color(R.color.white)
                        .show();
                skeletonScreen2 = Skeleton.bind(rl_vote)
                        .load(R.layout.gray)
                        .angle(0)
                        .color(R.color.white)
                        .show();
                skeletonScreen3 = Skeleton.bind(rl_tip)
                        .load(R.layout.gray)
                        .angle(0)
                        .color(R.color.white)
                        .show();
                skeletonScreen4 = Skeleton.bind(ll_banner)
                        .load(R.layout.gray)
                        .angle(0)
                        .color(R.color.teal)
                        .show();
                RefreshThread refreshThread = new RefreshThread();
                refreshThread.start();
            }
        });
        ib_home_research_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomPosts.clear();
                PostShuffleClickable(false);
                new RandomPostsThread().start();
            }
        });
        ib_home_vote_shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                randomVotes.clear();
                VoteShuffleClickable(false);
                new RandomVotesThread().start();
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

        if (homeBanners.size() == 0) {
            skeletonScreen4 = Skeleton.bind(ll_banner)
                    .load(R.layout.gray)
                    .angle(0)
                    .color(R.color.teal)
                    .show();
            new BannerThread().start();
        } else {
            bannerAdapter = new BannerViewPagerAdapter(getActivity(), homeBanners);
            vp_banner.setAdapter(bannerAdapter);
            vp_banner.setCurrentItem(Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % homeBanners.size()));

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
            if (handler == null) {
                handler = new Handler();
                update = new Runnable() {
                    public void run() {
                        vp_banner.setCurrentItem(currentPage++, true);
                    }
                };
                swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(update);
                    }
                }, 2000, 2000);
            }

        }



    }
    @SuppressLint("ResourceType")
    private void setVp_research() {
        vp_research = view.findViewById(R.id.vp_research);
        vp_research_indicator = view.findViewById(R.id.vp_research_indicator);

        if (randomPosts.size() == 0) {
            skeletonScreen = Skeleton.bind(rl_research)
                    .load(R.layout.gray)
                    .angle(0)
                    .color(R.color.white)
                    .show();
            new RandomPostsThread().start();
        } else {
            homeResearchPagerAdapter = new HomeResearchPagerAdapter(fragmentManager);
            vp_research.setAdapter(homeResearchPagerAdapter);
            vp_research_indicator.setViewPager(vp_research);
        }
    }

    private void setVp_vote() {
        vp_vote_indicator = view.findViewById(R.id.vp_vote_indicator);
        vp_vote = view.findViewById(R.id.vp_vote);

        if (randomVotes.size() == 0) {
            skeletonScreen2 = Skeleton.bind(rl_vote)
                    .load(R.layout.gray)
                    .angle(0)
                    .color(R.color.white)
                    .show();
            new RandomVotesThread().start();
        } else {
            homeVotePagerAdapter = new HomeVotePagerAdapter(fragmentManager);
            vp_vote.setAdapter(homeVotePagerAdapter);
            vp_vote_indicator.setViewPager(vp_vote);
        }
    }
    private void setVp_research_tip() {
        vp_tip_indicator = view.findViewById(R.id.vp_tip_indicator);
        vp_tip = view.findViewById(R.id.vp_tip);

        if (fullSurveytips.size() == 0) {
            skeletonScreen3 = Skeleton.bind(rl_tip)
                    .load(R.layout.gray)
                    .angle(0)
                    .color(R.color.white)
                    .show();
            new ResearchTipThread().start();

        } else {
            homeTipPagerAdapter = new HomeTipPagerAdapter(fragmentManager);
            vp_tip.setAdapter(homeTipPagerAdapter);
            vp_tip_indicator.setViewPager(vp_tip);
        }



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
    class RefreshThread extends Thread {
        public void run() {

            new BannerThread().start();
            new RandomPostsThread().start();
            new RandomVotesThread().start();
            new ResearchTipThread().start();
        }
    }
    class BannerThread extends Thread {
        public void run() {
            getBanners();
            Log.d("banner", "run: " + "banners thread1");
            while(!getBannerDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d("banner", "run: " + "banners thread2");

            randomGetHandler.sendEmptyMessage(3);
        }
    }
    class RandomPostsThread extends Thread {
        public void run() {
            getRandomPosts();
            while(!getPostDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            randomGetHandler.sendEmptyMessage(0);
        }
    }
    class RandomVotesThread extends Thread {
        public void run() {
            getRandomVotes();
            while(!getVoteDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            randomGetHandler.sendEmptyMessage(1);
        }
    }
    class ResearchTipThread extends Thread {
        public void run() {
            getSurveytips();
            while(!getTipDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            randomGetHandler.sendEmptyMessage(2);
        }
    }

    public void getRandomPosts() {
        try {
            Log.d("getRandom", "getRandomPosts: " + UserPersonalInfo.userID);
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts/random/?user_object_id=" + UserPersonalInfo.userID;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
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
                                            JSONObject comment = ja.getJSONObject(j);
                                            String reid = comment.getString("_id");
                                            String writer = comment.getString("writer");
                                            String contetn = comment.getString("content");
                                            Date datereply = null;
                                            try {
                                                datereply = fm.parse(comment.getString("date"));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Boolean replyhide = comment.getBoolean("hide");
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
                            Collections.shuffle(randomPosts);
                            getPostDone = true;

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
    public void getRandomVotes() {

        try {
            String param_email;
            if (UserPersonalInfo.userID.equals("nonMember")) {
                param_email = "null";
            } else {
                param_email = UserPersonalInfo.email;
            }
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals/random/?userID=" + param_email;
            Log.d("getRandom", "getRandomVotes: " + UserPersonalInfo.email);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
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
                                             JSONObject comment = ja.getJSONObject(j);
                                             String reid = comment.getString("_id");
                                             String writer = comment.getString("writer");
                                             String contetn = comment.getString("content");
                                             Date datereply = null;
                                             try {
                                                 datereply = fm.parse(comment.getString("date"));
                                             } catch (ParseException e) {
                                                 e.printStackTrace();
                                             }
                                             Boolean replyhide = comment.getBoolean("hide");
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
                             Collections.shuffle(randomVotes);
                             getVoteDone = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
            }, error -> {
                        error.printStackTrace();
            });
            Log.d("도대체 어디가 안되는 거야", "getRandomVotes: " + randomVotes);
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void getSurveytips(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/surveytips";
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {

                        try {
                            JSONArray resultArr = new JSONArray(response.toString());
                            Log.d("response is", ""+response);

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");
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
                                fullSurveytips.add(newSurveytip);

                            }
                            getTipDone = true;
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
    public boolean getBanners() {
    try {
        String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/banner/getbanner";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.mContext);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, requestURL, null, response -> {
            try {
                homeBanners = new ArrayList<Banner>();
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject item = jsonArray.getJSONObject(i);
                    String id = item.getString("_id");
                    int type = item.getInt("type");
                    boolean hide = item.getBoolean("hide");
                    boolean done = item.getBoolean("done");

                    // 있을 수도 없을 수도 있는 값들. 없을 때 방지로 Exception 설정.
                    String title;
                    try {
                        title = item.getString("title");
                    } catch (Exception e) {
                        title = "";
                    }
                    String author;
                    try {
                        author = item.getString("author");
                    } catch (Exception e) {
                        author = "";
                    }
                    String content;
                    try {
                        content = item.getString("content");
                    } catch (Exception e) {
                        content = "";
                    }
                    String url;
                    try {
                        url = item.getString("url");
                    } catch (Exception e) {
                        url = "";
                    }
                    // 여기까지
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
                getBannerDone = true;
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
    public void PostShuffleClickable(boolean clickable) {
        if (clickable) {
            ib_home_research_shuffle.setVisibility(View.VISIBLE);
        } else {
            ib_home_research_shuffle.setVisibility(View.GONE);
        }
    }
    public void VoteShuffleClickable(boolean clickable) {
        if (clickable) {
            ib_home_vote_shuffle.setVisibility(View.VISIBLE);
        } else {
            ib_home_vote_shuffle.setVisibility(View.GONE);
        }
    }
    private class RandomGetHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                refresh_count += 1;
                skeletonScreen.hide();
                homeResearchPagerAdapter = new HomeResearchPagerAdapter(fragmentManager);
                vp_research.setAdapter(homeResearchPagerAdapter);
                vp_research_indicator.setViewPager(vp_research);
                getPostDone = false;
                PostShuffleClickable(true);
            } else if(msg.what == 1) {
                refresh_count += 1;
                skeletonScreen2.hide();
                homeVotePagerAdapter = new HomeVotePagerAdapter(fragmentManager);
                vp_vote.setAdapter(homeVotePagerAdapter);
                vp_vote_indicator.setViewPager(vp_vote);
                getVoteDone = false;
                VoteShuffleClickable(true);
            } else if (msg.what == 2) {
                refresh_count += 1;
                skeletonScreen3.hide();
                homeTipPagerAdapter = new HomeTipPagerAdapter(fragmentManager);
                vp_tip.setAdapter(homeTipPagerAdapter);
                vp_tip_indicator.setViewPager(vp_tip);
                getTipDone = false;
            }
            else if (msg.what == 3) {
                refresh_count += 1;
                skeletonScreen4.hide();
                bannerAdapter = new BannerViewPagerAdapter(getActivity(), homeBanners);
                vp_banner.setAdapter(bannerAdapter);
                vp_banner.setCurrentItem((Integer.MAX_VALUE / 2) - ((Integer.MAX_VALUE / 2) % homeBanners.size()));
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
                if (handler == null) {
                    handler = new Handler();
                    update = new Runnable() {
                        public void run() {
                            vp_banner.setCurrentItem(currentPage++, true);
                        }
                    };
                    swipeTimer = new Timer();
                    swipeTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.post(update);
                        }
                    }, 2000, 2000);
                }

                getBannerDone = false;

            }
            if (refresh_count == 4) {
                refresh_count = 0;
                refresh_boards.setRefreshing(false);
            }
        }
    }

}
