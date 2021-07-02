package com.pumasi.surbay.pages.signup;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pumasi.surbay.R;

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