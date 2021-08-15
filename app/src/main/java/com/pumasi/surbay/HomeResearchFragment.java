package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pumasi.surbay.adapter.HomeResearchPagerAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;

import static com.pumasi.surbay.BoardsSearchActivity.DO_SURVEY;
import static com.pumasi.surbay.adapter.HomeResearchPagerAdapter.home_research;


public class HomeResearchFragment extends Fragment {

    private int RESEARCH_ITEM_NUM;
    private View view;

    private LinearLayout ll_home_research_item1;
    private LinearLayout ll_home_research_item2;

    private TextView tv_name1;
    private TextView tv_name2;
    private TextView tv_title1;
    private TextView tv_title2;
    private TextView tv_target1;
    private TextView tv_target2;
    private ImageView iv_gift1;
    private ImageView iv_gift2;

    public HomeResearchFragment(int num) {
        this.RESEARCH_ITEM_NUM = num;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean single = (HomeResearchPagerAdapter.HOME_RESEARCH_COUNT - RESEARCH_ITEM_NUM * 2 == 1);

        view = inflater.inflate(R.layout.fragment_home_research, container, false);
        ll_home_research_item1 = view.findViewById(R.id.ll_home_research_item1);
        ll_home_research_item2 = view.findViewById(R.id.ll_home_research_item2);

        tv_name1 = view.findViewById(R.id.tv_name1);
        tv_title1 = view.findViewById(R.id.tv_title1);
        tv_target1 = view.findViewById(R.id.tv_target1);
        iv_gift1 = view.findViewById(R.id.iv_gift1);

        if (home_research.get(RESEARCH_ITEM_NUM * 2).getAnnonymous()) {
            tv_name1.setText("익명");
        } else {
            tv_name1.setText(home_research.get(RESEARCH_ITEM_NUM * 2).getAuthor().toString());
        }
        tv_title1.setText(home_research.get(RESEARCH_ITEM_NUM * 2).getTitle().toString());
        tv_target1.setText(home_research.get(RESEARCH_ITEM_NUM * 2).getTarget().toString());

        if (home_research.get(RESEARCH_ITEM_NUM * 2).getNum_prize() == 0) {
            iv_gift1.setVisibility(View.INVISIBLE);
        }

        ll_home_research_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post research = (Post) home_research.get(RESEARCH_ITEM_NUM * 2);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("post", research);
                intent.putParcelableArrayListExtra("reply", research.getComments());
                intent.putExtra("position", 2 * RESEARCH_ITEM_NUM);
                startActivityForResult(intent, DO_SURVEY);
            }
        });

        if (single) {
            ll_home_research_item2.setVisibility(View.INVISIBLE);
        } else {
            tv_name2 = view.findViewById(R.id.tv_name2);
            tv_title2 = view.findViewById(R.id.tv_title2);
            tv_target2 = view.findViewById(R.id.tv_target2);
            iv_gift2 = view.findViewById(R.id.iv_gift2);
            if (home_research.get(RESEARCH_ITEM_NUM * 2 + 1).getAnnonymous()) {
                tv_name2.setText("익명");
            } else {
                tv_name2.setText(home_research.get(RESEARCH_ITEM_NUM * 2 + 1).getAuthor().toString());
            }
            tv_title2.setText(home_research.get(RESEARCH_ITEM_NUM * 2 + 1).getTitle().toString());
            tv_target2.setText(home_research.get(RESEARCH_ITEM_NUM * 2 + 1).getTarget().toString());

            if (home_research.get(RESEARCH_ITEM_NUM * 2 + 1).getNum_prize() == 0) {
                iv_gift2.setVisibility(View.INVISIBLE);
            }
            ll_home_research_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post research = (Post) home_research.get(RESEARCH_ITEM_NUM * 2 + 1);
                    Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostDetailActivity.class);
                    intent.putExtra("post", research);
                    intent.putParcelableArrayListExtra("reply", research.getComments());
                    intent.putExtra("position", 2 * RESEARCH_ITEM_NUM + 1);
                    startActivityForResult(intent, DO_SURVEY);
                }
            });
        }
        return view;
    }
}