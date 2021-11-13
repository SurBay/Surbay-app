package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.HomeResearchPagerAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pumasi.surbay.BoardsSearchActivity.DO_SURVEY;
import static com.pumasi.surbay.adapter.HomeResearchPagerAdapter.home_research;


public class HomeResearchFragment extends Fragment {
    private Context context;

    private int RESEARCH_ITEM_NUM;
    private View view;

    private Tools tools = new Tools();
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
    private TextView tv_day1;
    private TextView tv_day2;

    private ServerTransport st;
    public HomeResearchFragment(int num) {
        this.RESEARCH_ITEM_NUM = num;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        st = new ServerTransport(context);
        boolean single = (HomeResearchPagerAdapter.HOME_RESEARCH_COUNT - RESEARCH_ITEM_NUM * 2 == 1);

        view = inflater.inflate(R.layout.fragment_home_research, container, false);
        ll_home_research_item1 = view.findViewById(R.id.ll_home_research_item1);
        ll_home_research_item2 = view.findViewById(R.id.ll_home_research_item2);

        tv_name1 = view.findViewById(R.id.tv_name1);
        tv_title1 = view.findViewById(R.id.tv_title1);
        tv_target1 = view.findViewById(R.id.tv_target1);
        iv_gift1 = view.findViewById(R.id.iv_gift1);
        tv_day1 = view.findViewById(R.id.tv_day1);

        Post home_item = home_research.get(RESEARCH_ITEM_NUM * 2);

        if (home_item.getAnnonymous()) {
            tv_name1.setText("익명");
        } else {
            tv_name1.setText(home_item.getAuthor().toString());
        }
        tv_title1.setText(home_item.getTitle().toString());
        tv_target1.setText(home_item.getTarget().toString());

        if (home_item.getNum_prize() == 0) {
            iv_gift1.setVisibility(View.INVISIBLE);
        }
        Date date = new Date(System.currentTimeMillis());
        Log.d("time checking", "onCreateView: " + new SimpleDateFormat("dd.HH.mm").format(date) + ", deadline: " + new SimpleDateFormat("dd.HH.mm").format(home_item.getDeadline()));
        if (tools.toUTC(date.getTime()) - home_item.getDeadline().getTime() > 0) {
            tv_day1.setText("종료");
            tv_day1.setTextColor(Color.parseColor("#C4C4C4"));
        } else if (tools.compareDay(new Date(tools.toLocal(home_item.getDeadline().getTime()))) == 0) {
            tv_day1.setText("D-DAY");
        } else if (tools.toUTC(date.getTime()) - home_item.getDate().getTime() < tools.time_day) {
            tv_day1.setText("NEW");
        } else {
            tv_day1.setText("D-" + tools.compareDay(new Date(tools.toLocal(home_item.getDeadline().getTime()))));
        }
        ll_home_research_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post research = home_item;
                st.getExecute(research.getID(), 0, 0);
            }
        });

        if (single) {
            ll_home_research_item2.setVisibility(View.INVISIBLE);
        } else {
            tv_name2 = view.findViewById(R.id.tv_name2);
            tv_title2 = view.findViewById(R.id.tv_title2);
            tv_target2 = view.findViewById(R.id.tv_target2);
            iv_gift2 = view.findViewById(R.id.iv_gift2);
            tv_day2 = view.findViewById(R.id.tv_day2);

            Post home_item2 = home_research.get(RESEARCH_ITEM_NUM * 2 + 1);

            if (home_item2.getAnnonymous()) {
                tv_name2.setText("익명");
            } else {
                tv_name2.setText(home_item2.getAuthor().toString());
            }
            tv_title2.setText(home_item2.getTitle().toString());
            tv_target2.setText(home_item2.getTarget().toString());

            if (home_item2.getNum_prize() == 0) {
                iv_gift2.setVisibility(View.INVISIBLE);
            }
            if (tools.toUTC(date.getTime()) - home_item2.getDeadline().getTime() > 0) {
                tv_day2.setText("종료");
                tv_day2.setTextColor(Color.parseColor("#C4C4C4"));
            } else if (tools.compareDay(new Date(tools.toLocal(home_item2.getDeadline().getTime()))) == 0) {
                tv_day2.setText("D-DAY");
            } else if (tools.toUTC(date.getTime()) - home_item2.getDate().getTime() < tools.time_day) {
                tv_day2.setText("NEW");
            } else {
                tv_day2.setText("D-" + tools.compareDay(new Date(tools.toLocal(home_item2.getDeadline().getTime()))));
            }
            ll_home_research_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post research = home_item2;
                    st.getExecute(research.getID(), 0, 0);
                }
            });
        }
        return view;
    }
}