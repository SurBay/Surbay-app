package com.pumasi.surbay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pumasi.surbay.BoardPost;
import com.pumasi.surbay.BoardSurveyTip;
import com.pumasi.surbay.BoardFeedback;

import java.util.ArrayList;
import java.util.List;

public class BoardsPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments=new ArrayList<>();

    public BoardsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragments.add(new BoardPost());
        fragments.add(new BoardSurveyTip());
        fragments.add(new BoardFeedback());
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
