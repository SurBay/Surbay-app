package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pumasi.surbay.adapter.BannerViewPagerAdapter;
import com.pumasi.surbay.adapter.HomeResearchPagerAdapter;
import com.pumasi.surbay.adapter.HomeTipPagerAdapter;
import com.pumasi.surbay.adapter.HomeVotePagerAdapter;
import com.pumasi.surbay.pages.MainActivity;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeRenewalFragment extends Fragment {

    public static int HOME_RESEARCH = 0;
    public static int HOME_VOTE = 1;
    public static int HOME_TIP = 2;

    private static Context mContext;
    private static final Integer[] IMAGES = {R.drawable.renewal_banner, R.drawable.tutorialbanner2};
    private ArrayList<Integer> ImagesArray = new ArrayList<>();
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
    private View view;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        mContext = getActivity();
        setVp_banner();
        setVp_research();
        setVp_vote();
        setVp_research_tip();
    }
    private void setVp_banner() {
        if (view != null) {
            vp_banner = view.findViewById(R.id.vp_banner);
            for (int i = 0; i < IMAGES.length; i++) ImagesArray.add(IMAGES[i]);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}