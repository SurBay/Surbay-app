package com.pumasi.surbay;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeVoteFragment extends Fragment {

    private int VOTE_ITEM_NUM;
    private View view;

    public HomeVoteFragment(int num) {
        this.VOTE_ITEM_NUM = num;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_vote, container, false);
        return view;
    }
}