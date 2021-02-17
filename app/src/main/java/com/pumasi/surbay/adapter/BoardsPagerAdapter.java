package com.pumasi.surbay.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.pumasi.surbay.BoardFragment1;
import com.pumasi.surbay.BoardFragment2;
import com.pumasi.surbay.BoardFragment3;

import java.util.ArrayList;
import java.util.List;

public class BoardsPagerAdapter extends FragmentStatePagerAdapter {

    List<Fragment> fragments=new ArrayList<>();

    public BoardsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        fragments.add(new BoardFragment1());
        fragments.add(new BoardFragment2());
        fragments.add(new BoardFragment3());
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
