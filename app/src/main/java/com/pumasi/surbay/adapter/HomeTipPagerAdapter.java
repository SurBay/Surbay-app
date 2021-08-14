package com.pumasi.surbay.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.pumasi.surbay.HomeResearchFragment;
import com.pumasi.surbay.HomeTipFragment;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.pages.MainActivity;

import java.util.ArrayList;
import java.util.Collections;

public class HomeTipPagerAdapter extends FragmentPagerAdapter {
    public static int HOME_TIP_COUNT;
    public static ArrayList<Surveytip> home_surveytips;
    public void setSurveytips() {
        home_surveytips = MainActivity.surveytipArrayList;
    }
    public static void ShuffleHomeTip() {
        if (home_surveytips != null) {
            Collections.shuffle(home_surveytips);
        }
    }
    public HomeTipPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        setSurveytips();
        if (home_surveytips == null) {
            HOME_TIP_COUNT = 0;
        } else {
            if (home_surveytips.size() % 2 == 0) {
                HOME_TIP_COUNT = home_surveytips.size() / 2;
            } else if (home_surveytips.size() % 2 == 1) {
                HOME_TIP_COUNT = home_surveytips.size() / 2 + 1;
            }
        }
    }

    public HomeTipPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return new HomeTipFragment(position);
    }

    @Override
    public int getCount() {
        return Math.min(HOME_TIP_COUNT, 3);
    }
}
