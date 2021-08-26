package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;

import static com.pumasi.surbay.BoardsSearchActivity.DO_SURVEY;
import static com.pumasi.surbay.adapter.HomeVotePagerAdapter.home_votes;

public class HomeVoteFragment extends Fragment {

    private int VOTE_ITEM_NUM;
    private View view;

    private LinearLayout ll_home_vote_item;

    private TextView tv_home_vote_item_title;
    private TextView tv_home_vote_item_views;
    private TextView tv_home_vote_item_comments;
    private ImageView iv_home_vote_item_like;

    private FrameLayout fl_poll2;
    private TextView tv_content2_1;
    private TextView tv_content2_2;

    private FrameLayout fl_poll3;
    private TextView tv_content3_1;
    private TextView tv_content3_2;
    private TextView tv_content3_3;

    private FrameLayout fl_poll4;
    private TextView tv_content4_1;
    private TextView tv_content4_2;
    private TextView tv_content4_3;
    private TextView tv_content4_4;
    private TextView tv_content4_extra;

    public HomeVoteFragment(int num) {
        this.VOTE_ITEM_NUM = num;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int poll_size = home_votes.get(VOTE_ITEM_NUM).getPolls().size();
        view = inflater.inflate(R.layout.fragment_home_vote, container, false);
        ll_home_vote_item = view.findViewById(R.id.ll_home_vote_item);
        tv_home_vote_item_title = view.findViewById(R.id.tv_home_vote_item_title);
        tv_home_vote_item_views = view.findViewById(R.id.tv_home_vote_item_views);
        tv_home_vote_item_comments = view.findViewById(R.id.tv_home_vote_item_comments);
        tv_home_vote_item_comments.setText("댓글 " + home_votes.get(VOTE_ITEM_NUM).getComments().size() + "개");
        iv_home_vote_item_like = view.findViewById(R.id.iv_home_vote_item_like);

        for (String user : home_votes.get(VOTE_ITEM_NUM).getLiked_users()) {
            if (user.equals(UserPersonalInfo.email)) {
                iv_home_vote_item_like.setImageResource(R.drawable.heart_filled);
            }
        }
        if (poll_size == 2) {
            fl_poll2 = view.findViewById(R.id.fl_poll2);
            fl_poll2.setVisibility(View.VISIBLE);
            tv_content2_1 = view.findViewById(R.id.tv_content2_1);
            tv_content2_2 = view.findViewById(R.id.tv_content2_2);

            tv_content2_1.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(0).getContent());
            tv_content2_2.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(1).getContent());

        } else if (poll_size == 3) {
            fl_poll3 = view.findViewById(R.id.fl_poll3);
            fl_poll3.setVisibility(View.VISIBLE);
            tv_content3_1 = view.findViewById(R.id.tv_content3_1);
            tv_content3_2 = view.findViewById(R.id.tv_content3_2);
            tv_content3_3 = view.findViewById(R.id.tv_content3_3);

            tv_content3_1.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(0).getContent());
            tv_content3_2.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(1).getContent());
            tv_content3_3.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(2).getContent());

        } else {
            fl_poll4 = view.findViewById(R.id.fl_poll4);
            fl_poll4.setVisibility(View.VISIBLE);
            tv_content4_1 = view.findViewById(R.id.tv_content4_1);
            tv_content4_2 = view.findViewById(R.id.tv_content4_2);
            tv_content4_3 = view.findViewById(R.id.tv_content4_3);
            tv_content4_4 = view.findViewById(R.id.tv_content4_4);
            tv_content4_extra = view.findViewById(R.id.tv_content4_extra);

            tv_content4_1.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(0).getContent());
            tv_content4_2.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(1).getContent());
            tv_content4_3.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(2).getContent());
            tv_content4_4.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(3).getContent());

            if (poll_size == 4) {
                tv_content4_extra.setVisibility(View.GONE);
            } else {
                tv_content4_extra.setText("+" + String.valueOf(poll_size - 4));
            }

        }

        tv_home_vote_item_title.setText("Q. " + home_votes.get(VOTE_ITEM_NUM).getTitle());
        tv_home_vote_item_comments.setText("댓글 " + String.valueOf(home_votes.get(VOTE_ITEM_NUM).getComments().size()) + "개");

        ll_home_vote_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                General newGeneral = home_votes.get(VOTE_ITEM_NUM);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), GeneralDetailActivity.class);
                intent.putExtra("general", newGeneral);
                intent.putExtra("position", VOTE_ITEM_NUM);
                intent.putParcelableArrayListExtra("reply", newGeneral.getComments());
                intent.putParcelableArrayListExtra("polls", newGeneral.getPolls());
                startActivityForResult(intent, DO_SURVEY);
            }
        });








        return view;
    }
}