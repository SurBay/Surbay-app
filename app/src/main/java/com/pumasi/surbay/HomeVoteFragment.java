package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;
import com.pumasi.surbay.pages.signup.LoginActivity;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Inet4Address;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pumasi.surbay.BoardsSearchActivity.DO_SURVEY;
import static com.pumasi.surbay.adapter.HomeVotePagerAdapter.home_votes;

public class HomeVoteFragment extends Fragment {

    private int VOTE_ITEM_NUM;
    private Context context;
    private String vote_id;
    private View view;
    private CustomDialog customDialog;

    private LinearLayout ll_home_vote_item;

    private TextView tv_home_vote_item_title;
    private TextView tv_home_vote_item_votes;
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

    private ServerTransport st;

    public HomeVoteFragment(int num) {
        this.VOTE_ITEM_NUM = num;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int poll_size = home_votes.get(VOTE_ITEM_NUM).getPolls().size();
        vote_id = home_votes.get(VOTE_ITEM_NUM).getID();
        context = getActivity().getApplicationContext();
        st = new ServerTransport(context);
        view = inflater.inflate(R.layout.fragment_home_vote, container, false);
        ll_home_vote_item = view.findViewById(R.id.ll_home_vote_item);
        tv_home_vote_item_title = view.findViewById(R.id.tv_home_vote_item_title);
        tv_home_vote_item_votes = view.findViewById(R.id.tv_home_vote_item_votes);
        tv_home_vote_item_votes.setText("투표 " + home_votes.get(VOTE_ITEM_NUM).getParticipants() + "명");
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
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    customDialog.show();
                    customDialog.setMessage("투표게시판을 이용하기 위해서는 \n로그인이 필요합니다.");
                    customDialog.setPositiveButton("로그인 하기");
                    customDialog.setNegativeButton("닫기");
                } else {
                    General newGeneral = home_votes.get(VOTE_ITEM_NUM);
                    st.getExecute(newGeneral.getID(), 1, 0);
                }
            }
        });
        return view;
    }

}