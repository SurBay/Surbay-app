package com.pumasi.surbay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeResearchFragment1 extends Fragment {

    private View view;

    public static HomeResearchFragment1 newInstance() {
        HomeResearchFragment1 homeResearchFragment1 = new HomeResearchFragment1();
        return homeResearchFragment1;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_research1, container, false);
        return view;
    }
}