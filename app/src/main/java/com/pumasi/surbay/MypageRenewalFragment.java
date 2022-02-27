package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.homepage.NoticeActivity;
import com.pumasi.surbay.pages.mypage.AccountFix;
import com.pumasi.surbay.pages.mypage.IGotGifts;
import com.pumasi.surbay.pages.mypage.MyResearchActivity;
import com.pumasi.surbay.pages.mypage.MyVoteActivity;
import com.pumasi.surbay.pages.mypage.MypageSettingMain;
import com.pumasi.surbay.pages.mypage.NotificationsActivity;
import com.pumasi.surbay.pages.mypage.SettingFeedbacks;
import com.pumasi.surbay.pages.mypage.SettingReport;
import com.pumasi.surbay.pages.signup.LoginActivity;
import com.pumasi.surbay.tools.FirebaseLogging;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MypageRenewalFragment extends Fragment {

    private View view;
    private Context context;
    private CustomDialog customDialog;
    private MyHandler handler = new MyHandler();
    private SwipeRefreshLayout refresh_boards;
    private boolean getDone = false;
    private ImageButton ib_my_setting;

    private ImageView iv_my_character;
    private TextView tv_my_name;
    private TextView tv_my_school;
    private TextView tv_my_info;
    private RelativeLayout rl_my_alarm;
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

    private RelativeLayout rl_my_research;
    private RelativeLayout rl_my_vote;
    private RelativeLayout rl_my_setting;
    private RelativeLayout rl_my_setting2;

    private LinearLayout ll_my_login;
    private TextView tv_my_login;
    private Button btn_my_login;

    public static final int UPLOADED_RESEARCH = 0;
    public static final int PARTICIPATED_RESEARCH = 1;
    public static final int UPLOADED_VOTE = 2;
    public static final int PARTICIPATED_VOTE = 3;

    public ServerTransport st;
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mypage_renewal, container, false);
        context = getActivity().getApplicationContext();

//        new FirebaseLogging(context).LogScreen("my_page", "마이페이지");
        st = new ServerTransport(context);
        try {
            userControl(UserPersonalInfo.userID.equals("nonMember"));
            if (UserPersonalInfo.userID.equals("nonMember")) {
                rl_my_research.setVisibility(View.GONE);
            }

            refresh_boards = view.findViewById(R.id.refresh_boards);
            refresh_boards.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    GetPersonalInfoThread getPersonalInfoThread = new GetPersonalInfoThread();
                    getPersonalInfoThread.start();
                }
            });
            iv_my_character = view.findViewById(R.id.iv_my_character);
            String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying", "specific0924@yonsei.ac.kr"};
            boolean isAdmin = false;
            for (String admin : adminsList) {
                if (UserPersonalInfo.userID.equals(admin)) {
                    iv_my_character.setImageResource(R.drawable.surbay_logo_transparent);
                    isAdmin = true;
                }
            }
            if (UserPersonalInfo.userID.equals("nonMember")) {
                iv_my_character.setImageResource(R.drawable.anonymous);
            } else {
                if (!isAdmin) {
                    if (UserPersonalInfo.level <= 1) {
                        iv_my_character.setImageResource(R.drawable.lv1_trans);
                    } else if (UserPersonalInfo.level <= 2) {
                        iv_my_character.setImageResource(R.drawable.lv2_trans);
                    } else if (UserPersonalInfo.level <= 3) {
                        iv_my_character.setImageResource(R.drawable.lv3_trans);
                    } else {
                        iv_my_character.setImageResource(R.drawable.lv4_trans);
                    }
                }
            }
        } catch (Exception e) {
            Log.d("why_null", "onCreateView: " + UserPersonalInfo.userID);
        }


        tv_my_name = view.findViewById(R.id.tv_my_name);
        tv_my_school = view.findViewById(R.id.tv_my_school);
        tv_my_credit = view.findViewById(R.id.tv_my_credit);
        tv_my_info = view.findViewById(R.id.tv_my_info);
        rl_my_alarm = view.findViewById(R.id.rl_my_alarm);
        tv_my_name.setText(UserPersonalInfo.name);
        tv_my_school.setText(UserPersonalInfo.email);
        if (UserPersonalInfo.userID.equals("nonMember")) tv_my_info.setVisibility(View.GONE);
        tv_my_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AccountFix.class);
                startActivity(intent);
            }
        });
        rl_my_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, NotificationsActivity.class);
                    startActivity(intent);
                }

            }
        });
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
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, MyExchangeActivity.class);
                    startActivity(intent);
                }
            }
        });
        ib_my_gift = view.findViewById(R.id.ib_my_gift);
        ib_my_gift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, IGotGifts.class);
                    startActivity(intent);
                }

            }
        });
        ib_my_note = view.findViewById(R.id.ib_my_note);
        ib_my_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, MyNoteActivity.class);
                    startActivity(intent);
                }

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
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, MyResearchActivity.class);
                    intent.putExtra("type", UPLOADED_RESEARCH);
                    startActivity(intent);
                }
            }
        });
        ll_my_research_participated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, MyResearchActivity.class);
                    intent.putExtra("type", PARTICIPATED_RESEARCH);
                    startActivity(intent);
                }
            }
        });
        ll_my_vote_uploaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, MyVoteActivity.class);
                    intent.putExtra("type", UPLOADED_VOTE);
                    startActivity(intent);
                }
            }
        });
        ll_my_vote_participated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Intent intent = new Intent(context, MyVoteActivity.class);
                    intent.putExtra("type", PARTICIPATED_VOTE);
                    startActivity(intent);
                }
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
    class GetPersonalInfoThread extends Thread {
        public void run() {
            st.getPersonalInfo();
            while (!st.getPersonalInfoDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.getPersonalInfoDone = false;
            handler.sendEmptyMessage(0);
        }
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            tv_my_name.setText(UserPersonalInfo.name);
            tv_my_school.setText(UserPersonalInfo.email);
            tv_my_credit.setText(String.valueOf(UserPersonalInfo.points) + " 크레딧");
            tv_my_research_uploaded.setText(String.valueOf(UserPersonalInfo.my_posts.size()) + "개");
            tv_my_research_participated.setText(String.valueOf(UserPersonalInfo.participations.size()) + "개");
            tv_my_vote_uploaded.setText(String.valueOf(UserPersonalInfo.my_generals.size()) + "개");
            tv_my_vote_participated.setText(String.valueOf(UserPersonalInfo.general_participations.size()) + "개");
            String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
            boolean isAdmin = false;
            for (String admin : adminsList) {
                if (UserPersonalInfo.userID.equals(admin)) {
                    iv_my_character.setImageResource(R.drawable.surbay_logo_transparent);
                    isAdmin = true;
                }
            }
            if (UserPersonalInfo.userID.equals("nonMember")) {
                iv_my_character.setImageResource(R.drawable.anonymous);
            } else {
                if (!isAdmin) {
                    if (UserPersonalInfo.level <= 1) {
                        iv_my_character.setImageResource(R.drawable.lv1_trans);
                    } else if (UserPersonalInfo.level <= 2) {
                        iv_my_character.setImageResource(R.drawable.lv2_trans);
                    } else if (UserPersonalInfo.level <= 3) {
                        iv_my_character.setImageResource(R.drawable.lv3_trans);
                    } else {
                        iv_my_character.setImageResource(R.drawable.lv4_trans);
                    }
                }
            }

            getDone = false;
            refresh_boards.setRefreshing(false);
        }
    }

    private void userControl(boolean isAnonymous) {
        if (isAnonymous) {
            rl_my_research = view.findViewById(R.id.rl_my_research);
            rl_my_vote = view.findViewById(R.id.rl_my_vote);
            rl_my_setting = view.findViewById(R.id.rl_my_setting);
            rl_my_setting2 = view.findViewById(R.id.rl_my_setting2);
            rl_my_research.setVisibility(View.GONE);
            rl_my_vote.setVisibility(View.GONE);
            rl_my_setting.setVisibility(View.GONE);
            rl_my_setting2.setVisibility(View.GONE);

            ll_my_login = view.findViewById(R.id.ll_my_login);
            tv_my_login = view.findViewById(R.id.tv_my_login);
            btn_my_login = view.findViewById(R.id.btn_my_login);

            ll_my_login.setVisibility(View.VISIBLE);
            btn_my_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            });


        }
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume", "onResume: my_page_resume!!");
        new FirebaseLogging(context).LogScreen("my_page", "마이페이지");
    }
}