package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.pumasi.surbay.pages.mypage.MyResearch;
import com.pumasi.surbay.pages.mypage.MyVote;
import com.pumasi.surbay.pages.mypage.MypageSettingMain;

public class MypageRenewalFragment extends Fragment {

    private View view;
    private ImageButton ib_my_setting;

    private LinearLayout ll_my_research_uploaded;
    private LinearLayout ll_my_research_participated;
    private LinearLayout ll_my_vote_uploaded;
    private LinearLayout ll_my_vote_participated;

    private ImageButton ib_my_exchange;
    private ImageButton ib_my_gift;
    private ImageButton ib_my_note;
    private ImageButton ib_my_announce;
    private ImageButton ib_my_suggest;
    private ImageButton ib_my_report;

    private final int UPLOADED = 0;
    private final int PARTICIPATED = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mypage_renewal, container, false);
        ib_my_setting = view.findViewById(R.id.ib_my_setting);
        ib_my_exchange = view.findViewById(R.id.ib_my_exchange);
        ib_my_gift = view.findViewById(R.id.ib_my_gift);
        ib_my_note = view.findViewById(R.id.ib_my_note);
        ib_my_announce = view.findViewById(R.id.ib_my_announce);
        ib_my_suggest = view.findViewById(R.id.ib_my_suggest);
        ib_my_report = view.findViewById(R.id.ib_my_report);


        ll_my_research_uploaded = view.findViewById(R.id.ll_my_research_uploaded);
        ll_my_research_participated = view.findViewById(R.id.ll_my_research_participated);
        ll_my_vote_uploaded = view.findViewById(R.id.ll_my_vote_uploaded);
        ll_my_vote_participated = view.findViewById(R.id.ll_my_vote_participated);
        ll_my_research_uploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MyResearch.class);
                intent.putExtra("type", UPLOADED);
                startActivity(intent);
            }
        });
        ll_my_research_participated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MyResearch.class);
                intent.putExtra("type", PARTICIPATED);
                startActivity(intent);
            }
        });
        ll_my_vote_uploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MyVote.class);
                intent.putExtra("type", UPLOADED);
                startActivity(intent);
            }
        });
        ll_my_vote_participated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MyVote.class);
                intent.putExtra("type", PARTICIPATED);
                startActivity(intent);
            }
        });
        ib_my_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), MypageSettingMain.class);
                startActivity(intent);
            }
        });

        return view;
    }
}