package com.pumasi.surbay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pumasi.surbay.HomeResearchFragment;
import com.pumasi.surbay.HomeTipFragment;
import com.pumasi.surbay.classfile.Surveytip;

import java.util.ArrayList;

public class HomeTipPagerAdapter extends FragmentPagerAdapter {

    public HomeTipPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        HomeTipFragment homeTipFragment = new HomeTipFragment(0);
        homeTipFragment.setSurveytips();
        homeTipFragment.Shuffle();
    }

    public HomeTipPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:

                return new HomeTipFragment(0);
            case 1:
                return new HomeTipFragment(1);
            case 2:
                return new HomeTipFragment(2);
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
