package com.example.surbay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class FindIdActivity extends AppCompatActivity {
    TabLayout findidtab;
    ViewPager findidview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        this.getSupportActionBar().hide();

        findidtab = findViewById(R.id.findidtablayout);
        findidview = findViewById(R.id.findidviewpager);
        FindIdAdapter adapter = new FindIdAdapter(getSupportFragmentManager());
        findidview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        findidtab.setupWithViewPager(findidview);

    }

    public class FindIdAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<Fragment>();
        private String titles[] = new String[]{"아이디 찾기", "비밀번호 재설정"};

        public FindIdAdapter(@NonNull FragmentManager fm) {
            super(fm);
            fragments.add(new FindIdFragment());
            fragments.add(new FindPwFragment());
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return this.fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}