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

    ArrayList<Surveytip> surveytipArrayList;
    public HomeTipPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public HomeTipPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return (HomeTipFragment.newInstance(0));
            case 1:
                return (HomeTipFragment.newInstance(1));
            case 2:
                return (HomeTipFragment.newInstance(2));
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
