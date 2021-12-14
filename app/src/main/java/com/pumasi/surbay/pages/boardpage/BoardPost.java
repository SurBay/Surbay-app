package com.pumasi.surbay.pages.boardpage;

import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.graphics.drawable.ColorDrawable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.Transaction;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.PostRecyclerViewAdapter;
import com.pumasi.surbay.adapter.RecyclerViewDdayAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
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


public class BoardPost extends Fragment {
    private ServerTransport st;

    private boolean isToast = true;

    private LinearLayoutManager mLayoutManager;
    static final int WRITE_NEWPOST = 1;
    private View view;
    private Button btn_post_sort_new;
    private Button btn_post_sort_day;
    private Button btn_post_sort_goal;
    private Button btn_research_write;
    private RelativeLayout loadingPanel;
    private RecyclerView rv_board_post;
    private static PostRecyclerViewAdapter postRecyclerViewAdapter;
    private Context context;

    public ArrayList<Post> addingPost = new ArrayList<>();
    public static ArrayList<Post> boardPostShow = new ArrayList<>();

    private CheckBox cb_hide_done;
    private TextView tv_participated_hide;
    private int visibleItemCount, pastVisiblesItems, totalItemCount;
    private static boolean isLoading = false;
    public static boolean doneInfinityPost = false;
    private SwipeRefreshLayout refresh_boards;
    private String beforePost = "";
    private int type = 0;
    private static boolean isParticipated = false;
    private CustomDialog customDialog;

    private RecyclerHandler recyclerHandler = new RecyclerHandler();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_board_post, container, false);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = getActivity().getApplicationContext();
        st = new ServerTransport(context);

        new FirebaseLogging(context).LogScreen("research_board", "리서치게시판");

        customDialog = new CustomDialog(getActivity());
        setComponents();
        postRecyclerViewAdapter = new PostRecyclerViewAdapter(boardPostShow, context);
        mLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        rv_board_post.setAdapter(postRecyclerViewAdapter);
        rv_board_post.setLayoutManager(mLayoutManager);

        postRecyclerViewAdapter.setOnItemClickListener(new PostRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Post item = (Post) postRecyclerViewAdapter.getItem(position);
                st.getExecute(item.getID(), 0, 1);
            }
        });

        initScrollListener();
        setLoading(true);
        executeGetData();
        return view;
    }
    private void setComponents() {
        rv_board_post = view.findViewById(R.id.rv_board_post);
        refresh_boards = view.findViewById(R.id.refresh_boards);
        refresh_boards.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setClickable(false);
                beforePost = "";
                boardPostShow.clear();
                postRecyclerViewAdapter.notifyDataSetChanged();
                executeGetData();
            }
        });
        btn_post_sort_new = view.findViewById(R.id.btn_post_sort_new);
        btn_post_sort_day = view.findViewById(R.id.btn_post_sort_day);
        btn_post_sort_goal = view.findViewById(R.id.btn_post_sort_goal);

        cb_hide_done = view.findViewById(R.id.cb_hide_done);
        tv_participated_hide = view.findViewById(R.id.tv_participated_hide);
        btn_research_write = view.findViewById(R.id.btn_research_write);
        loadingPanel = view.findViewById(R.id.loadingPanel);

        btn_post_sort_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(0);
            }
        });

        btn_post_sort_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(1);
            }
        });

        btn_post_sort_goal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(2);
            }
        });

        cb_hide_done.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_participated_hide.setTextColor(Color.parseColor("#3AD1BF"));
                    setClickable(false);
                    isParticipated = true;
                } else {
                    tv_participated_hide.setTextColor(Color.parseColor("#BDBDBD"));
                    setClickable(false);
                    isParticipated = false;
                }
                beforePost = "";
                boardPostShow.clear();
                postRecyclerViewAdapter.notifyDataSetChanged();
                setLoading(true);
                setTop();
                executeGetData();
            }
        });
        btn_research_write.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(UserPersonalInfo.userID.equals("nonMember")){
                            customDialog.customSimpleDialog("게시글을 작성하기 위해서는 \n로그인이 필요합니다", "로그인 하기", "닫기", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), PostWriteActivity.class);
                            intent.putExtra("purpose", WRITE_NEWPOST);
                            startActivityForResult(intent, WRITE_NEWPOST);
                        }

                    }
                }
        );
    }
    private void setSelected(int num) {
        setLoading(true);
        type = num;
        beforePost = "";
        boardPostShow.clear();
        postRecyclerViewAdapter.notifyDataSetChanged();
        setTop();

        executeGetData();

        switch (num) {
            case 0:
                btn_post_sort_new.setBackgroundResource(R.drawable.round_border_teal_list);
                btn_post_sort_day.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_post_sort_goal.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_post_sort_new.setTextColor(getResources().getColor(R.color.teal_200));
                btn_post_sort_day.setTextColor(getResources().getColor(R.color.BDBDBD));
                btn_post_sort_goal.setTextColor(getResources().getColor(R.color.BDBDBD));
                break;
            case 1:
                btn_post_sort_new.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_post_sort_day.setBackgroundResource(R.drawable.round_border_teal_list);
                btn_post_sort_goal.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_post_sort_new.setTextColor(getResources().getColor(R.color.BDBDBD));
                btn_post_sort_day.setTextColor(getResources().getColor(R.color.teal_200));
                btn_post_sort_goal.setTextColor(getResources().getColor(R.color.BDBDBD));
                break;
            case 2:
                btn_post_sort_new.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_post_sort_day.setBackgroundResource(R.drawable.round_border_gray_list);
                btn_post_sort_goal.setBackgroundResource(R.drawable.round_border_teal_list);
                btn_post_sort_new.setTextColor(getResources().getColor(R.color.BDBDBD));
                btn_post_sort_day.setTextColor(getResources().getColor(R.color.BDBDBD));
                btn_post_sort_goal.setTextColor(getResources().getColor(R.color.teal_200));
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + num);
        }
    }

    public void getInfinityPosts(String object, int type, boolean isParticipated) {
        String user_id = "";
        if (UserPersonalInfo.userID != "nonMember") {
            user_id = UserPersonalInfo.userID;
        }
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts/infinite/?userID=" + user_id + "&type=" + type + "&post_object_id=" + object + "&participate=" + isParticipated;
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.mContext);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    JSONArray responseArray = new JSONArray(response.toString());
                    for (int i = 0; i < responseArray.length(); i++) {
                        JSONObject post = responseArray.getJSONObject(i);
                        String id = post.getString("_id");
                        String title = post.getString("title");
                        String author = post.getString("author");
                        Integer author_lvl = post.getInt("author_lvl");
                        String content = post.getString("content");
                        Integer participants = post.getInt("participants");
                        Integer goal_participants = post.getInt("goal_participants");
                        String url = post.getString("url");
                        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");
                        Date date = null;
                        Date deadline = null;
                        try {
                            date = fm.parse(post.getString("date"));
                            deadline = fm.parse(post.getString("deadline"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Boolean with_prize = post.getBoolean("with_prize");
                        Integer est_time = post.getInt("est_time");
                        String target = post.getString("target");
                        Boolean done = post.getBoolean("done");
                        Boolean hide = post.getBoolean("hide");
                        Integer extended = post.getInt("extended");
                        String author_userid = post.getString("author_userid");
                        String prize = "none";
                        Integer num_prize = 0;
                        if (with_prize) {
                            prize = post.getString("prize");
                            num_prize = post.getInt("num_prize");
                        }
                        Integer pinned = 0;
                        Boolean annonymous = false;
                        String author_info = "";
                        try {
                            pinned = post.getInt("pinned");
                            annonymous = post.getBoolean("annonymous");
                            author_info = post.getString("author_info");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        JSONArray ia = (JSONArray) post.get("participants_userids");
                        ArrayList<String> participants_userids = new ArrayList<String>();
                        for (int j = 0; j < ia.length(); j++) {
                            participants_userids.add(ia.getString(j));
                        }
                        JSONArray ka = (JSONArray) post.get("reports");
                        ArrayList<String> reports = new ArrayList<String>();
                        for (int j = 0; j < ka.length(); j++) {
                            reports.add(ka.getString(j));
                        }
                        ArrayList<Reply> comments = new ArrayList<>();
                        try{
                            JSONArray ja = (JSONArray)post.get("comments");
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
                        Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, num_prize, comments, done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info);
                        addingPost.add(newPost);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                doneInfinityPost = true;
            }, error -> {
                error.printStackTrace();
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initScrollListener() {
        rv_board_post.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    Log.d("TAG", "onScrolled: "+totalItemCount);
                    if (!isLoading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            executeGetData();
                        }
                    }
                }
            }
        });
    }

    private void setClickable(boolean clickable) {
        btn_post_sort_new.setEnabled(clickable);
        btn_post_sort_goal.setEnabled(clickable);
        btn_post_sort_day.setEnabled(clickable);
        cb_hide_done.setEnabled(clickable);

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
                if (boardPostShow.size() == 0) {
                    rv_board_post.scrollToPosition(0);
                }
            }
        }, 200);
    }

    private void executeGetData() {
        boardPostShow.add(null);
        postRecyclerViewAdapter.notifyItemInserted(boardPostShow.size() - 1);
        isLoading = true;
        setClickable(false);
        refresh_boards.setEnabled(false);
        new RecyclerThread().start();
    }
    private class RecyclerThread extends Thread {
        @Override
        public void run() {
            int counter = 0;
            getInfinityPosts(beforePost, type, isParticipated);
            while (!doneInfinityPost && counter != 3 * st.DEFAULT_NETWORK_TRY) {
                try {
                    counter += 1;
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (counter == 3 * st.DEFAULT_NETWORK_TRY) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "데이터를 받아오는데 실패하였습니다", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (addingPost.size() == 0) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "이미 모든 데이터를 받아왔습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    boardPostShow.addAll(addingPost);
                    beforePost = addingPost.get(addingPost.size() - 1).getID();
                }
            }
            addingPost.clear();
            doneInfinityPost = false;
            recyclerHandler.sendEmptyMessage(0);
        }
    }
    private class RecyclerHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            boardPostShow.remove(null);
            postRecyclerViewAdapter.setItem(boardPostShow);
            postRecyclerViewAdapter.notifyDataSetChanged();

            refresh_boards.setEnabled(true);
            refresh_boards.setRefreshing(false);
            setClickable(true);
            setLoading(false);
            isLoading = false;
        }
    }
}
