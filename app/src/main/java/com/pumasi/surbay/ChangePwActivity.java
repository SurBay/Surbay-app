package com.pumasi.surbay;

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

import java.util.ArrayList;
import java.util.List;

public class ChangePwActivity extends AppCompatActivity {
    private FragmentManager fm;
    private FragmentTransaction ft;
    private FindPwFragment findPwFragment;
    public static String email;
    Boolean confirmed = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pw_only);

        this.getSupportActionBar().hide();
        Intent intent = getIntent();
        confirmed = intent.getBooleanExtra("confirmed", false);
        findPwFragment = new FindPwFragment();

        fm = getSupportFragmentManager();
        ft= fm.beginTransaction();
        ft.replace(R.id.Main_Frame,findPwFragment);
        ft.commit();
    }
}