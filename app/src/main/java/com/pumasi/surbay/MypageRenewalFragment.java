package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.homepage.NoticeActivity;
import com.pumasi.surbay.pages.mypage.MyResearchActivity;
import com.pumasi.surbay.pages.mypage.MyVoteActivity;
import com.pumasi.surbay.pages.mypage.MypageSettingMain;
import com.pumasi.surbay.pages.mypage.SettingFeedbacks;
import com.pumasi.surbay.pages.mypage.SettingReport;

public class MypageRenewalFragment extends Fragment {

    private View view;
    private Context context;
    private ImageButton ib_my_setting;

    private TextView tv_my_name;
    private TextView tv_my_school;
    private TextView tv_my_credit;

    private LinearLayout ll_my_research_uploaded;
    private LinearLayout ll_my_research_participated;
    private LinearLayout ll_my_vote_uploaded;
    private LinearLayout ll_my_vote_participated;

    private TextView tv_my_research_uploaded;
    private TextView tv_my_research_participated;
    private TextView tv_my_vote_uploaded;
    private TextView tv_my_vote_participated;

    private ImageButton ib_my_exchange;
    private ImageButton ib_my_gift;
    private ImageButton ib_my_note;
    private ImageButton ib_my_announce;
    private ImageButton ib_my_suggest;
    private ImageButton ib_my_report;

    public static final int UPLOADED_RESEARCH = 0;
    public static final int PARTICIPATED_RESEARCH = 1;
    public static final int UPLOADED_VOTE = 2;
    public static final int PARTICIPATED_VOTE = 3;


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mypage_renewal, container, false);
        context = getActivity().getApplicationContext();

        tv_my_name = view.findViewById(R.id.tv_my_name);
        tv_my_school = view.findViewById(R.id.tv_my_school);
        tv_my_credit = view.findViewById(R.id.tv_my_credit);
        tv_my_name.setText(UserPersonalInfo.name);
        tv_my_school.setText(UserPersonalInfo.email);
        tv_my_credit.setText(String.valueOf(UserPersonalInfo.points) + " 크레딧");



        tv_my_research_uploaded = view.findViewById(R.id.tv_my_research_uploaded);
        tv_my_research_participated = view.findViewById(R.id.tv_my_research_participated);
        tv_my_vote_uploaded = view.findViewById(R.id.tv_my_vote_uploaded);
        tv_my_vote_participated = view.findViewById(R.id.tv_my_vote_participated);

        tv_my_research_uploaded.setText(String.valueOf(UserPersonalInfo.my_posts.size()) + "개");
        tv_my_research_participated.setText(String.valueOf(UserPersonalInfo.participations.size()) + "개");
        tv_my_vote_uploaded.setText(String.valueOf(UserPersonalInfo.my_generals.size()) + "개");
        tv_my_vote_participated.setText(String.valueOf(UserPersonalInfo.general_participations.size()) + "개");


        ib_my_setting = view.findViewById(R.id.ib_my_setting);
        ib_my_exchange = view.findViewById(R.id.ib_my_exchange);
        ib_my_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyExchangeActivity.class);
                startActivity(intent);
            }
        });
        ib_my_gift = view.findViewById(R.id.ib_my_gift);

        ib_my_note = view.findViewById(R.id.ib_my_note);
        ib_my_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyNoteActivity.class);
                startActivity(intent);
            }
        });
        ib_my_announce = view.findViewById(R.id.ib_my_announce);
        ib_my_announce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NoticeActivity.class);
                startActivity(intent);
            }
        });
        ib_my_suggest = view.findViewById(R.id.ib_my_suggest);
        ib_my_suggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingFeedbacks.class);
                startActivity(intent);
            }
        });
        ib_my_report = view.findViewById(R.id.ib_my_report);
        ib_my_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SettingReport.class);
                startActivity(intent);
            }
        });


        ll_my_research_uploaded = view.findViewById(R.id.ll_my_research_uploaded);
        ll_my_research_participated = view.findViewById(R.id.ll_my_research_participated);
        ll_my_vote_uploaded = view.findViewById(R.id.ll_my_vote_uploaded);
        ll_my_vote_participated = view.findViewById(R.id.ll_my_vote_participated);
        ll_my_research_uploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyResearchActivity.class);
                intent.putExtra("type", UPLOADED_RESEARCH);
                startActivity(intent);
            }
        });
        ll_my_research_participated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyResearchActivity.class);
                intent.putExtra("type", PARTICIPATED_RESEARCH);
                startActivity(intent);
            }
        });
        ll_my_vote_uploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyVoteActivity.class);
                intent.putExtra("type", UPLOADED_VOTE);
                startActivity(intent);
            }
        });
        ll_my_vote_participated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MyVoteActivity.class);
                intent.putExtra("type", PARTICIPATED_VOTE);
                startActivity(intent);
            }
        });
        ib_my_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MypageSettingMain.class);
                startActivity(intent);
            }
        });





        return view;
    }
}