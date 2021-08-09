package com.pumasi.surbay;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pumasi.surbay.adapter.BannerViewPagerAdapter;
import com.pumasi.surbay.adapter.HomeResearchPagerAdapter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeRenewalFragment extends Fragment {

    private static Context mContext;
    private static final Integer[] IMAGES= {R.drawable.renewal_banner, R.drawable.tutorialbanner2};
    private ArrayList<Integer> ImagesArray = new ArrayList<>();
    private ViewPager vp_banner;
    private ViewPager vp_research;
    private ViewPager vp_vote;
    private ViewPager vp_research_tip;
    private int currentPage;
    private View view;
    private HomeResearchPagerAdapter homeResearchAdapter;
    private static BannerViewPagerAdapter bannerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_renewal, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        mContext = getActivity();
        vp_vote = view.findViewById(R.id.vp_vote);
        vp_research_tip = view.findViewById(R.id.vp_research_tip);


        setVp_banner();
        setVp_research();
        return view;
    }
    private void setVp_banner() {
        if (view != null) {
            vp_banner = view.findViewById(R.id.vp_banner);
            for (int i=0;i<IMAGES.length;i++) ImagesArray.add(IMAGES[i]);
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
        homeResearchAdapter = new HomeResearchPagerAdapter(getFragmentManager());
        vp_research.setAdapter(homeResearchAdapter);
    }

    private void setVp_research_tip() {
        vp_research_tip = view.findViewById(R.id.vp_research_tip);
    }
}