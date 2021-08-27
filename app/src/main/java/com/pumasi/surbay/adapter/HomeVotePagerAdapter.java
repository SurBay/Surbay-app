package com.pumasi.surbay.adapter;

import android.os.HandlerThread;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.database.Transaction;
import com.pumasi.surbay.HomeRenewalFragment;
import com.pumasi.surbay.HomeVoteFragment;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.pages.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class HomeVotePagerAdapter extends FragmentPagerAdapter {
    public static int HOME_VOTE_COUNT;
    public static ArrayList<General> home_votes;
    public static void setVotes() {
        home_votes = HomeRenewalFragment.randomVotes;
    }


    public HomeVotePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        setVotes();
        if (home_votes == null) {
            HOME_VOTE_COUNT = 0;
        } else {
            HOME_VOTE_COUNT = home_votes.size();
        }
        if (HOME_VOTE_COUNT == 0) {
            HomeRenewalFragment.set_invisible(HomeRenewalFragment.HOME_VOTE);
        } else {
            HomeRenewalFragment.set_visible(HomeRenewalFragment.HOME_VOTE);
        }
    }

    public HomeVotePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new HomeVoteFragment(position);
    }

    @Override
    public int getCount() {
        return Math.min(HOME_VOTE_COUNT, 5);
    }
}
