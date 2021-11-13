package com.pumasi.surbay.pages.boardpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.ContentDetailActivity;
import com.pumasi.surbay.adapter.VoteRecyclerViewAdapter;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.signup.LoginActivity;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class BoardGeneral extends Fragment// Fragment 클래스를 상속받아야한다
{ // 게시판

    private LinearLayoutManager mLayoutManager;
    private CustomDialog customDialog;
    private View view;
    static final int WRITE_NEWPOST = 1;
    static final int DO_SURVEY = 2;

    private final int REQUEST_BOARD = 1;
    private int type = 0;
    private static boolean isOriginal = false;
    private int visibleItemCount, pastVisiblesItems, totalItemCount;
    private SwipeRefreshLayout refresh_boards;
    private boolean isLoading = false;

    private Button btn_vote_new_sort;
    private ImageButton ib_vote_like_sort;
    private ImageButton ib_vote_many_sort;
    private Button btn_vote_write;
    private CheckBox cb_collect;
    private TextView tv_collect;
    private Context context;
    private RelativeLayout loadingPanel;
    private RecyclerView rv_board_vote;
    private VoteRecyclerViewAdapter voteRecyclerViewAdapter;
    public static ArrayList<General> boardVoteShow = new ArrayList<General>();

    private String check = "";
    private String beforeVote = "";
    private ServerTransport st;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_board_general, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = getActivity().getApplicationContext();
        st = new ServerTransport(context);

        setComponents();

        setLoading(true);
        setClickable(false);


        voteRecyclerViewAdapter = new VoteRecyclerViewAdapter(boardVoteShow, context);
        mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv_board_vote.setAdapter(voteRecyclerViewAdapter);
        rv_board_vote.setLayoutManager(mLayoutManager);

        voteRecyclerViewAdapter.setOnItemClickListener(new VoteRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
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
                    General general = (General) voteRecyclerViewAdapter.getItem(position);
                    st.getExecute(general.getID(), 1, 1);
                }

            }
        });
        initScrollListener();
        getInfinityVotes(beforeVote, type, isOriginal);

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result code is", ""+requestCode+"   "+resultCode);
    }

    private void setComponents() {
        rv_board_vote = view.findViewById(R.id.rv_board_vote);
        refresh_boards = view.findViewById(R.id.refresh_boards);
        refresh_boards.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setClickable(false);
                check = "";
                beforeVote = "";
                boardVoteShow.clear();
                voteRecyclerViewAdapter.notifyDataSetChanged();
                getInfinityVotes(beforeVote, type, isOriginal);
            }
        });
        btn_vote_new_sort = view.findViewById(R.id.btn_vote_new_sort);
        ib_vote_like_sort = view.findViewById(R.id.ib_vote_like_sort);
        ib_vote_many_sort = view.findViewById(R.id.ib_vote_many_sort);

        cb_collect = view.findViewById(R.id.cb_collect);
        tv_collect = view.findViewById(R.id.tv_collect);
        btn_vote_write = view.findViewById(R.id.btn_vote_write);
        loadingPanel = view.findViewById(R.id.loadingPanel);

        btn_vote_new_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(0);
            }
        });
        ib_vote_like_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(1);
            }
        });
        ib_vote_many_sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(2);
            }
        });

        cb_collect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_collect.setTextColor(context.getResources().getColor(R.color.teal_200));
                    isOriginal = true;
                } else {
                    tv_collect.setTextColor(Color.parseColor("#BDBDBD"));
                    isOriginal = false;
                }
                setLoading(true);
                setClickable(false);
                check = "";
                beforeVote = "";
                boardVoteShow.clear();
                voteRecyclerViewAdapter.notifyDataSetChanged();
                getInfinityVotes(beforeVote, type, isOriginal);
            }
        });


        btn_vote_write.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (UserPersonalInfo.userID.equals("nonMember")) {
                            CustomDialog customDialog = new CustomDialog(getActivity(), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                            customDialog.show();
                            customDialog.setMessage("게시글을 작성하기 위해서는 \n로그인이 필요합니다");
                            customDialog.setPositiveButton("로그인 하기");
                            customDialog.setNegativeButton("닫기");
                        } else {
                            Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), GeneralWriteActivity.class);
                            intent.putExtra("purpose", WRITE_NEWPOST);
                            startActivityForResult(intent, WRITE_NEWPOST);
                        }

                    }
                }
        );


    }

    public void setSelected(int num) {
        setClickable(false);
        setLoading(true);
        type = num;
        check = "";
        beforeVote = "";
        boardVoteShow.clear();
        voteRecyclerViewAdapter.notifyDataSetChanged();
        getInfinityVotes(beforeVote, type, isOriginal);
        setTop();

        switch (num) {
            case 0:
                btn_vote_new_sort.setBackgroundResource(R.drawable.round_border_teal_list);
                ib_vote_like_sort.setBackgroundResource(R.drawable.round_border_gray_list);
                ib_vote_many_sort.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_vote_new_sort.setTextColor(getResources().getColor(R.color.teal_200));
                ib_vote_like_sort.setImageDrawable(getTintedDrawable(getResources(), R.drawable.general_likes, R.color.BDBDBD));
                ib_vote_many_sort.setImageDrawable(getTintedDrawable(getResources(), R.drawable.general_participants, R.color.BDBDBD));
                break;
            case 1:
                btn_vote_new_sort.setBackgroundResource(R.drawable.round_border_gray_list);
                ib_vote_like_sort.setBackgroundResource(R.drawable.round_border_teal_list);
                ib_vote_many_sort.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_vote_new_sort.setTextColor(getResources().getColor(R.color.BDBDBD));
                ib_vote_like_sort.setImageDrawable(getTintedDrawable(getResources(), R.drawable.general_likes, R.color.teal_200));
                ib_vote_many_sort.setImageDrawable(getTintedDrawable(getResources(), R.drawable.general_participants, R.color.BDBDBD));

                break;
            case 2:
                btn_vote_new_sort.setBackgroundResource(R.drawable.round_border_gray_list);
                ib_vote_like_sort.setBackgroundResource(R.drawable.round_border_gray_list);
                ib_vote_many_sort.setBackgroundResource(R.drawable.round_border_teal_list);
                btn_vote_new_sort.setTextColor(getResources().getColor(R.color.BDBDBD));
                ib_vote_like_sort.setImageDrawable(getTintedDrawable(getResources(), R.drawable.general_likes, R.color.BDBDBD));
                ib_vote_many_sort.setImageDrawable(getTintedDrawable(getResources(), R.drawable.general_participants, R.color.teal_200));

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + num);
        }
    }
    public void getInfinityVotes(String object, int type, boolean isOriginal) {
        check = object;
        isLoading = true;
        Log.d("hey2", "getInfinityPosts: "+ object + ", " + type + ", " + isOriginal);
        String userID;
        if (UserPersonalInfo.email == null) {
            userID = "";
        } else {
            userID = UserPersonalInfo.email;
        }

        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals/infinite/?userID=" + userID + "&general_object_id=" + object + "&type=" + type + "&original=" + isOriginal;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONArray responseArray = new JSONArray(response.toString());
                            Log.d("voteInfinityCount", "getInfinityVotes: " + response.length());
                            for (int i = 0; i < responseArray.length(); i++) {
                                JSONObject general = responseArray.getJSONObject(i);
                                String id = general.getString("_id");
                                String title = general.getString("title");
                                String author = general.getString("author");
                                Integer author_lvl = general.getInt("author_lvl");
                                String content = general.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(general.getString("date"));
                                    Log.d("note excepted", "getInfinityVotes: " + date);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    Log.d("date excepted", "getInfinityVotes: " + date);
                                }
                                Date deadline = null;
                                try {
                                    deadline = fm.parse(general.getString("deadline"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                ArrayList<Reply> comments = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("comments");
                                    if (ja.length() != 0){
                                        for (int j = 0; j<ja.length(); j++){
                                            JSONObject comment = ja.getJSONObject(j);
                                            String reid = comment.getString("_id");
                                            String writer = comment.getString("writer");
                                            String contetn = comment.getString("content");
                                            Date datereply = null;
                                            try {
                                                datereply = fm.parse(comment.getString("date"));
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }
                                            Boolean replyhide = comment.getBoolean("hide");
                                            JSONArray ua = (JSONArray)comment.get("reports");


                                            ArrayList<String> replyreports = new ArrayList<String>();
                                            for (int u = 0; u<ua.length(); u++){
                                                replyreports.add(ua.getString(u));
                                            }
                                            String writer_name = null;
                                            try {
                                                writer_name = comment.getString("writer_name");
                                            }catch (Exception e){
                                                writer_name = null;
                                            }
                                            ArrayList<ReReply> reReplies = new ArrayList<>();
                                            try {
                                                JSONArray jk = (JSONArray) comment.get("reply");
                                                if (jk.length() != 0) {
                                                    for (int k = 0; k < jk.length(); k++) {
                                                        JSONObject reReply = jk.getJSONObject(k);
                                                        String id_ = reReply.getString("_id");
                                                        ArrayList<String> reports_ = new ArrayList<>();
                                                        JSONArray jb = (JSONArray) reReply.get("reports");
                                                        for (int b = 0; b < jb.length(); b++) {
                                                            reports_.add(jb.getString(b));
                                                        }
                                                        ArrayList<String> report_reasons_ = new ArrayList<>();
                                                        JSONArray jc = (JSONArray) reReply.get("report_reasons");
                                                        for (int c = 0; c < jc.length(); c++) {
                                                            report_reasons_.add(jc.getString(c));
                                                        }
                                                        boolean hide_ = reReply.getBoolean("hide");
                                                        String writer_ = reReply.getString("writer");
                                                        String writer_name_ = "";
                                                        try {
                                                            writer_name_ = reReply.getString("writer_name");
                                                        } catch (Exception e) {
                                                            writer_name_ = "익명";
                                                        }
                                                        String content_ = reReply.getString("content");
                                                        Date date_ = fm.parse(reReply.getString("date"));
                                                        String replyID_ = reReply.getString("replyID");

                                                        ReReply newReReply = new ReReply(id_, reports_, report_reasons_, hide_, writer_, writer_name_, content_, date_, replyID_);
                                                        reReplies.add(newReReply);
                                                    }
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide, writer_name, reReplies);
                                            re.setWriter_name(writer_name);
                                            if ((!replyhide )&& (!replyreports.contains(UserPersonalInfo.userID))){
                                                comments.add(re);
                                            }
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                Boolean done = general.getBoolean("done");
                                String author_userid = general.getString("author_userid");
                                JSONArray ka = (JSONArray)general.get("reports");
                                ArrayList<String> reports = new ArrayList<String>();
                                for (int j = 0; j<ka.length(); j++){
                                    reports.add(ka.getString(j));
                                }
                                Boolean multi_response = general.getBoolean("multi_response");
                                Integer participants = general.getInt("participants");
                                JSONArray ia = (JSONArray)general.get("participants_userids");
                                ArrayList<String> participants_userids = new ArrayList<String>();
                                for (int j = 0; j<ia.length(); j++){
                                    participants_userids.add(ia.getString(j));
                                }
                                Boolean with_image = general.getBoolean("with_image");
                                ArrayList<Poll> polls = new ArrayList<>();
                                try{
                                    JSONArray ja = (JSONArray)general.get("polls");
                                    if (ja.length() != 0){
                                        for (int j = 0; j<ja.length(); j++){
                                            JSONObject poll = ja.getJSONObject(j);
                                            String poll_id = poll.getString("_id");
                                            String poll_content = poll.getString("content");
                                            ArrayList<String> poll_participants_userids = new ArrayList<String>();
                                            JSONArray ua = (JSONArray)poll.get("participants_userids");
                                            for (int u = 0; u<ua.length(); u++){
                                                poll_participants_userids.add(ua.getString(u));
                                            }
                                            String image = poll.getString("image");
                                            Poll newpoll = new Poll(poll_id, poll_content, poll_participants_userids, image);
                                            polls.add(newpoll);
                                        }
                                    }

                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                                JSONArray la = (JSONArray)general.get("liked_users");
                                ArrayList<String> liked_users = new ArrayList<String>();
                                for (int j = 0; j<la.length(); j++){
                                    liked_users.add(la.getString(j));
                                }

                                Integer likes = general.getInt("likes");

                                Boolean hide = general.getBoolean("hide");

                                General newGeneral = new General(id, title, author, author_lvl, content,
                                        date, deadline, comments, done, author_userid, reports, multi_response,
                                        participants, participants_userids, with_image, polls, liked_users, likes, hide);
                                boardVoteShow.add(newGeneral);
                            }


                            boardVoteShow.remove(null);
                            voteRecyclerViewAdapter.notifyItemRemoved(boardVoteShow.size());
                            if (boardVoteShow == null || boardVoteShow.size() == 0) {
                                beforeVote = "";
                            } else if (boardVoteShow.get(boardVoteShow.size() - 1) != null) {
                                beforeVote = boardVoteShow.get(boardVoteShow.size() - 1).getID();
                            }
                            voteRecyclerViewAdapter.notifyDataSetChanged();
                            setClickable(true);
                            setLoading(false);
                            refresh_boards.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

            }, error -> {
                        error.printStackTrace();
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
            isLoading = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initScrollListener() {
        rv_board_vote.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 0) {
                    visibleItemCount = mLayoutManager.getChildCount();
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    totalItemCount = mLayoutManager.getItemCount();

                    if (!isLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading();
                        }
                    }
                }
            }
        });
    }

    private void loading() {
        if (!check.equals(beforeVote)) {
            setClickable(false);
            boardVoteShow.add(null);
            voteRecyclerViewAdapter.notifyItemInserted(boardVoteShow.size() - 1);
            getInfinityVotes(beforeVote, type, isOriginal);
        }

    }

    private void setClickable(boolean clickable) {
        btn_vote_new_sort.setEnabled(clickable);
        ib_vote_like_sort.setEnabled(clickable);
        ib_vote_many_sort.setEnabled(clickable);
        cb_collect.setEnabled(clickable);
    }
    private void setLoading(boolean show) {
        if (show) {
            loadingPanel.setVisibility(View.VISIBLE);
        } else if (!show) {
            loadingPanel.setVisibility(View.GONE);
        }
    }
    private void setTop() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (boardVoteShow.size() == 0) {
                    rv_board_vote.scrollToPosition(0);
                }
            }
        }, 200);
    }
    public Drawable getTintedDrawable(Resources res,
                                      @DrawableRes int drawableResId, @ColorRes int colorResId) {
        Drawable drawable = res.getDrawable(drawableResId);
        int color = res.getColor(colorResId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

}
