package com.pumasi.surbay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pumasi.surbay.HomeRenewalFragment;
import com.pumasi.surbay.HomeResearchFragment;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.pages.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class HomeResearchPagerAdapter extends FragmentPagerAdapter {
    public static int HOME_RESEARCH_COUNT;
    public static ArrayList<Post> home_research;
    public static void setPosts() {
        home_research = MainActivity.postArrayList;
    }
    public static void ShuffleHomeResearch() {
        setPosts();
        if (home_research != null) {
            Collections.shuffle(home_research);
        }
    }

    public HomeResearchPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        setPosts();
        if (home_research == null) {
            HOME_RESEARCH_COUNT = 0;
        } else {
            HOME_RESEARCH_COUNT = home_research.size();
        }

        if (HOME_RESEARCH_COUNT == 0) {
            HomeRenewalFragment.set_invisible(HomeRenewalFragment.HOME_RESEARCH);
        } else {
            HomeRenewalFragment.set_visible(HomeRenewalFragment.HOME_RESEARCH);
        }

    }

    public HomeResearchPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new HomeResearchFragment(position);
    }

    @Override
    public int getCount() {
        int page = HOME_RESEARCH_COUNT / 2;
        if (HOME_RESEARCH_COUNT % 2 == 1) {
            page += 1;
        }
        return Math.min(page, 3);
    }
}

