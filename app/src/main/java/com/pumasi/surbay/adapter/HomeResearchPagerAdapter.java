package com.pumasi.surbay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pumasi.surbay.HomeResearchFragment1;

public class HomeResearchPagerAdapter extends FragmentPagerAdapter {


    public HomeResearchPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public HomeResearchPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return (HomeResearchFragment1.newInstance());
            case 1:
                return (HomeResearchFragment1.newInstance());
            case 2:
                return (HomeResearchFragment1.newInstance());
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}

