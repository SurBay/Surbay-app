package com.pumasi.surbay.pages.signup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.pumasi.surbay.R;

import java.util.ArrayList;
import java.util.List;

public class FindIdActivity extends AppCompatActivity {
    TabLayout findidtab;
    ViewPager findidview;
    public static FindIdAdapter adapter;
    public static String phone;
    public static String email;
    private FragmentTransaction fragmentTransaction;
    Boolean confirmed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_id);

        this.getSupportActionBar().hide();
        Intent intent = getIntent();
        confirmed = intent.getBooleanExtra("confirmed", false);
        findidtab = findViewById(R.id.findidtablayout);
        findidview = findViewById(R.id.findidviewpager);
        adapter = new FindIdAdapter(getSupportFragmentManager());
        findidview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        findidtab.setupWithViewPager(findidview);






    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        imm.hideSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public class FindIdAdapter extends FragmentPagerAdapter {
        public List<Fragment> fragments = new ArrayList<Fragment>();
        private String titles[] = new String[]{"???????????? ?????????"};

        public FindIdAdapter(@NonNull FragmentManager fm) {
            super(fm);
//            fragments.add(new FindIdFragment());
//                fragments.add(new ChangePwFragment());
                fragments.add(new FindPwFragment());
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}