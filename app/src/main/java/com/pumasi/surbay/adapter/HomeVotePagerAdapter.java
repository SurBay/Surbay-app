package com.pumasi.surbay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pumasi.surbay.HomeVoteFragment;

public class HomeVotePagerAdapter extends FragmentPagerAdapter {
    public HomeVotePagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public HomeVotePagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return HomeVoteFragment.newInstance();
            case 1: return HomeVoteFragment.newInstance();
            case 2: return HomeVoteFragment.newInstance();
            case 3: return HomeVoteFragment.newInstance();
            case 4: return HomeVoteFragment.newInstance();
            default: return null;

        }
    }

    @Override
    public int getCount() {
        return 5;
    }
}
