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
    public static ArrayList<Post> home_posts;
    public void setPosts() {
        home_posts = MainActivity.postArrayList;
    }
    public static void ShuffleHomeResearch() {
        if (home_posts != null) {
            Collections.shuffle(home_posts);
        }
    }

    public HomeResearchPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        setPosts();
        if (home_posts == null) {
            HOME_RESEARCH_COUNT = 0;
        } else {
            if (home_posts.size() % 2 == 0) {
                HOME_RESEARCH_COUNT = home_posts.size() / 2;
            } else if (home_posts.size() % 2 == 1) {
                HOME_RESEARCH_COUNT = home_posts.size() / 2 + 1;
            }
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
        return Math.min(HOME_RESEARCH_COUNT, 3);
    }
}

