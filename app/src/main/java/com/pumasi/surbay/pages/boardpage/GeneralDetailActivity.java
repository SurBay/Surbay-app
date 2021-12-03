package com.pumasi.surbay.pages.boardpage;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.ContentDetailCommentsActivity;
import com.pumasi.surbay.MyNoteActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.GeneralReplyRecyclerViewAdapter;
import com.pumasi.surbay.adapter.PollAdapter;
import com.pumasi.surbay.adapter.PollAdapterWImage;
import com.pumasi.surbay.adapter.PollDoneAdapter;
import com.pumasi.surbay.adapter.PollDoneAdapterWImage;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.MessageDialog;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class GeneralDetailActivity extends AppCompatActivity {
    private final int COMMENT = 0;
    private final int REPLY = 1;

    static final int START_SURVEY = 1;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    static final int DO_SURVEY = 2;
    static final int LIKE = 3;
    static final int REPORTED = 5;

    static final int REFRESH = 0;
    static final int SURVEY = 2;

    private String reply_id;
    private String reply_content;
    private int reply_mode = 0;
    private boolean reply_done = false;
    private boolean report_done = false;
    private boolean delete_done = false;
    private boolean block_done = false;

    private Context context;
    private TextView participants;
    private TextView participants_percent;
    TextView author;
    TextView level;
    TextView title;
    TextView content;
    TextView date;
    TextView deadline;
    TextView multi_response;
    RecyclerView poll_recyclerview;
    RecyclerView poll_done_recyclerview;
    TextView check_results;

    Button btn_general_detail_like;

    private CustomDialog customDialog;
    private MessageDialog messageDialog;
    private ImageView iv_vote_detail_reply;
    private ImageButton reply_enter_button;
    private Button btn_general_detail_end;

    private General general;
    private int position;
    private int saveLikes;

    ArrayList<Integer> bitArray;
    
    private GeneralReplyRecyclerViewAdapter generalReplyRecyclerViewAdapter;
    private RecyclerView rv_vote_detail_comment;
    private ArrayList<Reply> replyArrayList;

    private static PollAdapter pollAdapter;
    private static PollAdapterWImage pollAdapterWImage;
    ArrayList<Poll> polls;

    static Button surveyButton;


    public static SwipeRefreshLayout mSwipeRefreshLayout;
    generalDetailHandler handler = new generalDetailHandler();

    private EditText et_vote_detail_reply;
    private PollDoneAdapter pollDoneAdapter;
    private PollDoneAdapterWImage pollDoneAdapterWImage;
    private LinearLayoutManager mLayoutManager;

    RelativeLayout loading;
    private boolean done_survey = false;
    private boolean likedselected;

    static final int LIKED = 5;
    static final int DISLIKED = 4;
    int LIKE_CHANGE = 0;
    int ORIGIN_LIKE = 0;

    private ServerTransport st;
    public static ArrayList<Bitmap> images = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_detail);
        setResult(NOT_DONE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getColor(R.color.gray_FB)));

        Intent intent = getIntent();
        context = GeneralDetailActivity.this;
        st = new ServerTransport(context);

        images = new ArrayList<>();
        btn_general_detail_end = findViewById(R.id.btn_general_detail_end);

        author = findViewById(R.id.author);
        level = findViewById(R.id.author_info);
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);;
        date = findViewById(R.id.date);
        deadline = findViewById(R.id.deadline);
        participants = findViewById(R.id.participants);
        btn_general_detail_like = findViewById(R.id.btn_general_detail_like);
        multi_response = findViewById(R.id.multi_response);
        check_results = findViewById(R.id.check_results);

        iv_vote_detail_reply = findViewById(R.id.iv_vote_detail_reply);
        et_vote_detail_reply = findViewById(R.id.et_vote_detail_reply);
        reply_enter_button = findViewById(R.id.reply_enter_button);

        surveyButton = findViewById(R.id.surveyButton);
        rv_vote_detail_comment = findViewById(R.id.rv_vote_detail_comment);

        loading = findViewById(R.id.loadingPanel);
        poll_recyclerview = findViewById(R.id.polls);
        poll_done_recyclerview = findViewById(R.id.polls_done);

        mSwipeRefreshLayout = findViewById(R.id.general_detail_swipe_container);

        general = (General) intent.getParcelableExtra("general");
        position = intent.getIntExtra("position", -1);

        Date now = new Date();
        if(general.getDone()==true || now.after(general.getDeadline())){
            setPollsDone();
            surveyButton.setVisibility(View.GONE);
            btn_general_detail_end.setVisibility(View.VISIBLE);
        }

        replyArrayList = general.getComments();
        generalReplyRecyclerViewAdapter = new GeneralReplyRecyclerViewAdapter(GeneralDetailActivity.this, replyArrayList, general.getID());
        generalReplyRecyclerViewAdapter.setPost(general);
        generalReplyRecyclerViewAdapter.setOnItemClickListener(new GeneralReplyRecyclerViewAdapter.OnReplyClickListener() {
            @Override
            public void onReplyClick(View v, int position) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(GeneralDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    if (reply_mode == COMMENT) {
                        et_vote_detail_reply.post(new Runnable() {
                        @Override
                        public void run() {
                                et_vote_detail_reply.setFocusableInTouchMode(true);
                                et_vote_detail_reply.requestFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }
                        });
                        generalReplyRecyclerViewAdapter.Click(position);
                        Reply reply = (Reply) generalReplyRecyclerViewAdapter.getItem(position);
                        reply_id = reply.getID();
                        reply_mode = REPLY;
                        setReplyVisible(true);
                    } else {
                        reply_mode = COMMENT;
                        setReplyVisible(false);
                        generalReplyRecyclerViewAdapter.Click(-1);
                    }

                }

            }
        });
        generalReplyRecyclerViewAdapter.setOnItemClickListener(new GeneralReplyRecyclerViewAdapter.OnMenuClickListener() {
            @Override
            public void onMenuClick(View v, int position) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(GeneralDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    Reply reply = (Reply) generalReplyRecyclerViewAdapter.getItem(position);
                PopupMenu popupMenu = new PopupMenu(context, v);
                popupMenu.getMenuInflater().inflate(R.menu.reply_pop_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.getMenu().getItem(2).setVisible(false);
                popupMenu.getMenu().getItem(2).setEnabled(false);
                if (UserPersonalInfo.email.equals(reply.getWriter())) {
                    popupMenu.getMenu().getItem(1).setVisible(false);
                    popupMenu.getMenu().getItem(1).setEnabled(false);
                    popupMenu.getMenu().getItem(3).setVisible(false);
                    popupMenu.getMenu().getItem(3).setEnabled(false);
                } else {
                    popupMenu.getMenu().getItem(0).setVisible(false);
                    popupMenu.getMenu().getItem(0).setEnabled(false);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                customDialog = new CustomDialog(context, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        BackgroundDeleteThread backgroundDeleteThread = new BackgroundDeleteThread(reply.getID());
                                        backgroundDeleteThread.start();
                                        customDialog.dismiss();
                                    }
                                });
                                customDialog.show();
                                customDialog.setMessage("댓글을 삭제하겠습니까?");
                                customDialog.setPositiveButton("삭제");
                                customDialog.setNegativeButton("취소");
                                return true;
                            case R.id.report:
                                if (reply.getReports().contains(UserPersonalInfo.email)) {
                                    customDialog = new CustomDialog(context, null);
                                    customDialog.show();
                                    customDialog.setMessage("이미 신고한 댓글입니다.");
                                    customDialog.setNegativeButton("확인");
                                } else {
                                    customDialog = new CustomDialog(context, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            ArrayList<String> reports = new ArrayList<>(Arrays.asList("욕설/비하", "상업적 광고 및 판매", "낚시/놀람/도배/사기", "게시판 성격에 부적절함", "기타"));
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setTitle("신고 사유");
                                            builder.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    BackgroundReportThread backgroundReportThread = new BackgroundReportThread(reply.getID(), reports.get(which));
                                                    backgroundReportThread.start();
                                                    Toast.makeText(GeneralDetailActivity.this, "신고가 접수되었습니다", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            Dialog dialog = builder.create();
                                            dialog.show();
                                            customDialog.dismiss();

                                        }
                                    });
                                    customDialog.show();
                                    customDialog.setMessage("댓글을 신고하시겠습니까?");
                                    customDialog.setPositiveButton("신고");
                                    customDialog.setNegativeButton("취소");
                                }

                                return true;
                            case R.id.edit:
                                break;
                            case R.id.block:
                                if (UserPersonalInfo.blocked_users.contains(reply.getWriter())) {
                                    customDialog = new CustomDialog(context, null);
                                    customDialog.show();
                                    customDialog.setMessage("이미 차단한 유저입니다.");
                                    customDialog.setNegativeButton("확인");
                                } else {
                                    customDialog = new CustomDialog(context, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BackgroundBlockThread backgroundBlockThread = new BackgroundBlockThread(reply.getWriter());
                                            backgroundBlockThread.start();
                                            customDialog.dismiss();
                                        }
                                    });
                                    customDialog.show();
                                    customDialog.setMessage("상대를 차단하겠습니까?\n상대를 차단하시면 더 이상 상대방이 보낸 쪽지를 볼 수 없습니다.");
                                    customDialog.setPositiveButton("차단");
                                    customDialog.setNegativeButton("취소");
                                }

                                return true;
                        }
                        return false;
                    }
                });
                }

            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        rv_vote_detail_comment.setLayoutManager(mLayoutManager);
        rv_vote_detail_comment.setAdapter(generalReplyRecyclerViewAdapter);

        polls = general.getPolls();
        bitArray = new ArrayList<>();
        for(int i=0;i<polls.size();i++){
            images.add(null);
            Poll poll = polls.get(i);
            if(poll.getParticipants_userids().contains(UserPersonalInfo.userID)) bitArray.add(1);
            else bitArray.add(0);
        }


        check_results.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPollsDone();
            }
        });


        et_vote_detail_reply.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.et_vote_detail_reply) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction()&MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        et_vote_detail_reply.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                reply_content = et_vote_detail_reply.getText().toString();
            }
        });

        reply_enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String save = et_vote_detail_reply.getText().toString();
                reply_content = et_vote_detail_reply.getText().toString();
                et_vote_detail_reply.getText().clear();
                reply_content = save;
                if (reply_content.length() > 0 ){
                    loading.setVisibility(View.VISIBLE);
                    BackgroundReplyThread replyThread = new BackgroundReplyThread();
                    replyThread.start();
                }
            }
        });



        ArrayList<String> likedlist = general.getLiked_users();
        if (likedlist.contains(UserPersonalInfo.userID)){
            likedselected = true;
            ORIGIN_LIKE = LIKED;
        } else {
            likedselected = false;
            ORIGIN_LIKE = DISLIKED;
        }
        loading_detail(general);

        saveLikes = general.getLikes();

        btn_general_detail_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserPersonalInfo.userID.equals("nonMember")){
                    CustomDialog customDialog = new CustomDialog(GeneralDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 좋아요를 하실 수 없습니다");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                if (likedselected){
                    LIKE_CHANGE = DISLIKED;
                    saveLikes -= 1;

                } else {
                    LIKE_CHANGE = LIKED;
                    saveLikes += 1;

                }
                if(general.getLikes()>99){
                    btn_general_detail_like.setText("공감 99+");
                }else {
                    btn_general_detail_like.setText("공감 " + (saveLikes));
                }
                likedselected = !likedselected;

                if (likedselected) {
                    likepost();
                } else {
                    dislikepost();
                }

                setLikesbutton();
            }
        });


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BackgroundThread refreshThread = new BackgroundThread();
                refreshThread.start();

            }
        });

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Date now = new Date();
                if(general.getDone()==true || now.after(general.getDeadline())){
                    setPollsDone();
                    surveyButton.setEnabled(false);
                }
                else if(general.getParticipants_userids().contains(UserPersonalInfo.userID)){
                    setPollsDone();
                }else{
                    setPollsNotDone();
                }
            }
        };
        for(int i = 0; i<general.getPolls().size(); i++) {
            int finalI = i;
            new Thread() {
                Message msg;

                public void run() {
                    try {
                        String uri = general.getPolls().get(finalI).getImage();
                        URL url = new
                                URL(uri);
                        URLConnection conn = url.openConnection();
                        conn.connect();
                        BufferedInputStream bis = new
                                BufferedInputStream(conn.getInputStream());
                        Bitmap bm = BitmapFactory.decodeStream(bis);
                        images.set(finalI, bm);
                        bis.close();
                        msg = handler.obtainMessage();
                        handler.sendMessage(msg);
                    } catch (IOException e) {
                    }
                }
            }.start();
        }
    }

    public void setLikesbutton(){
        if (likedselected){
            btn_general_detail_like.setBackgroundResource(R.drawable.round_border_teal_list);
            btn_general_detail_like.setTextColor(getColor(R.color.teal_200));
        } else {
            btn_general_detail_like.setBackgroundResource(R.drawable.round_border_gray_list);
            btn_general_detail_like.setTextColor(getColor(R.color.nav_gray));
        }
    }
    class BackgroundThread extends Thread{
        public void run() {
            st.getVote(general.getID());
            while(st.result_vote == null) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            general = st.result_vote;
            replyArrayList = st.result_vote.getComments();
            generalReplyRecyclerViewAdapter.setItem(replyArrayList);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", REFRESH);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
    class BackgroundReplyThread extends Thread{
        public void run() {
            switch (reply_mode) {
                case COMMENT:
                    postGeneralReply(reply_content);
                    break;
                case REPLY:
                    postGeneralReReply(reply_id, reply_content);
                    break;
            }
            while(!(reply_done)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            new BackgroundThread().start();
        }
    }
    class BackgroundDeleteThread extends Thread{
        private String id;
        public BackgroundDeleteThread(String id) {
            this.id = id;
        }
        public void run() {
            generalDeleteComment(id);
            while (!delete_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.getVote(general.getID());
            while (st.result_vote == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            general = st.result_vote;
            replyArrayList = st.result_vote.getComments();
            generalReplyRecyclerViewAdapter.setItem(replyArrayList);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 3);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
    class BackgroundReportThread extends Thread{
        private String id;
        private String report_reason;
        public BackgroundReportThread(String id, String report_reason) {
            this.id = id;
            this.report_reason = report_reason;
        }
        public void run() {
            generalReportComment(id, report_reason);
            while (!report_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.getVote(general.getID());
            while (st.result_vote == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            general = st.result_vote;
            replyArrayList = st.result_vote.getComments();
            generalReplyRecyclerViewAdapter.setItem(replyArrayList);

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", 3);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
    class BackgroundBlockThread extends Thread{
        private String counter;
        public BackgroundBlockThread(String counter) {
            this.counter = counter;
        }
        public void run() {
            blockUser(counter);
            while (!block_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.getPersonalInfo();
            while(!st.getPersonalInfoDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            new BackgroundThread().start();
        }
    }
    class BackgroundSurveyThread extends Thread{
        public void run() {
            done_general_survey(bitArray);

            while(!(done_survey)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }

            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", SURVEY);
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }


    private void postGeneralReply(String reply) {
        String requestURL = getString(R.string.server)+"/api/generals/writecomment/"+general.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("writer", UserPersonalInfo.userID);
            params.put("content",reply);

            Date date = new Date();
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss");
            fm.setTimeZone(TimeZone.getTimeZone("UTC"));
            params.put("date", fm.format(date));
            params.put("writer_name", UserPersonalInfo.name);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject resultObj = new JSONObject(response.toString());
                                Boolean success = resultObj.getBoolean("type");
                                if (success) {
                                    String id = resultObj.getString("id");
                                    String utc_date = fm.format(date);
                                    fm.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                                    Date realdate = fm.parse(utc_date);
                                    String writer_name = UserPersonalInfo.name;
                                    Reply re = new Reply(id, UserPersonalInfo.userID, reply, realdate, new ArrayList<>(), false, writer_name, new ArrayList<ReReply>());
                                    re.setWriter_name(writer_name);
                                    replyArrayList.add(re);
                                    reply_done = true;

                                } else {
                                    Toast.makeText(GeneralDetailActivity.this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | ParseException e) {
                                Toast.makeText(GeneralDetailActivity.this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }, error -> {
                        Toast.makeText(GeneralDetailActivity.this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }){

                /**
                 * Passing some request headers
                 */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json");
                    headers.put("Connection", "close");
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
            reply_done = true;
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }
    private void updateReports(){
        String requestURL = getString(R.string.server) + "/api/generals/report/" + general.getID();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            int success = res.getInt("result");
                            if (success == 1) {
                                Intent intent = new Intent(GeneralDetailActivity.this, BoardPost.class);
                                setResult(REPORTED, intent);
                                intent.putExtra("position", position);
                                Toast.makeText(GeneralDetailActivity.this, "설문이 신고되었습니다", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if(res.getString("message").startsWith("cannot report this post")){
                                    Toast.makeText(GeneralDetailActivity.this, "이 게시물은 신고할 수 없습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.general_detail_bar, menu);
        if (UserPersonalInfo.userID.equals(general.getAuthor_userid())){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(true);
        }
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                setResult(position);
                if(et_vote_detail_reply.getText().toString().length()>0){
                    customDialog = new CustomDialog(GeneralDetailActivity.this, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();

                            customDialog.dismiss();
                        }
                    });
                    customDialog.show();
                    customDialog.setMessage("댓글 작성을 취소하겠습니까?");
                    customDialog.setPositiveButton("확인");
                    customDialog.setNegativeButton("취소");
                }else{
                    finish();
                }
                break;
            case R.id.report:
                //select back button
                ReportDialog();
                break;
            case R.id.remove:
                RemoveDialog();
                break;
            case R.id.note:
                messageDialog = new MessageDialog(GeneralDetailActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageDialog.setClickable(false);
                        setNote(messageDialog.note);
                    }
                });
                messageDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }
    private void done_general_survey(ArrayList<Integer> bitArray){
        String requestURL = getString(R.string.server)+"/api/generals/survey/"+general.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            JSONArray bitArray_json = new JSONArray();
            for(int i=0;i<bitArray.size();i++){
                bitArray_json.put(bitArray.get(i));
            }
            params.put("userID", UserPersonalInfo.userID);
            params.put("bitarray", bitArray_json);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        done_survey = true;
                    }, error -> {
                        error.printStackTrace();
                        done_survey = true;
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loading_detail(General general){
        author.setText(general.getAuthor());
        level.setText("(Lv "+general.getAuthor_lvl()+")");
        SimpleDateFormat fm;
        if(general.getMulti_response()) fm = new SimpleDateFormat(" / MM.dd kk:mm 투표 마감", Locale.KOREA);
        else fm = new SimpleDateFormat("MM.dd kk:mm 투표 마감", Locale.KOREA);

        deadline.setText(fm.format(general.getDeadline()));
        date.setText(new SimpleDateFormat("MM.dd kk:mm / ").format(general.getDate()));
        participants.setText(""+ general.getParticipants()+"명 참여");

        title.setText("Q. "+ general.getTitle());
        content.setText(general.getContent());
        if(general.getLikes()>99){
            btn_general_detail_like.setText("공감 99+");
        }else{
            btn_general_detail_like.setText("공감 "+general.getLikes());
        }
        if(ORIGIN_LIKE==LIKED && LIKE_CHANGE==DISLIKED){
            if(general.getLikes()>99){
                btn_general_detail_like.setText("공감 99+");
            }else{
                btn_general_detail_like.setText("공감 "+(general.getLikes()-1));
            }
        }
        else if(ORIGIN_LIKE==DISLIKED && LIKE_CHANGE==LIKED) {
            if(general.getLikes()>99){
                btn_general_detail_like.setText("공감 99+");
            }else {
                btn_general_detail_like.setText("공감 " + (general.getLikes() + 1));
            }
        }
        setLikesbutton();
        if(general.getMulti_response()) multi_response.setVisibility(View.VISIBLE);
        else multi_response.setVisibility(View.GONE);

        Date now = new Date();
        if(general.getDone()==true || now.after(general.getDeadline())){
            setPollsDone();
            surveyButton.setEnabled(false);
        }
        else if(general.getParticipants_userids().contains(UserPersonalInfo.userID)){
            setPollsDone();
        }else{
            setPollsNotDone();
        }

    }
    public void setReplyVisible(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_vote_detail_reply.getLayoutParams();
        if (visible) {
            params.width = (int) (context.getResources().getDisplayMetrics().density * 23);
            params.height = (int) (context.getResources().getDisplayMetrics().density * 23);
        } else {
            params.width = 0;
            params.height = 0;
        }
        iv_vote_detail_reply.setLayoutParams(params);
    }
    public void setPollsNotDone(){
        check_results.setVisibility(View.VISIBLE);
        poll_done_recyclerview.setVisibility(View.GONE);
        poll_recyclerview.setVisibility(View.VISIBLE);
        if(!general.getWith_image()) {
            pollAdapter = new PollAdapter(GeneralDetailActivity.this, general.getPolls(), bitArray, general.getMulti_response());
            poll_recyclerview.setLayoutManager(new LinearLayoutManager(this));
            poll_recyclerview.setAdapter(pollAdapter);
        }else{
            pollAdapterWImage = new PollAdapterWImage(GeneralDetailActivity.this, general.getPolls(), bitArray, general.getMulti_response());
            poll_recyclerview.setLayoutManager(new LinearLayoutManager(this));
            poll_recyclerview.setAdapter(pollAdapterWImage);
        }
        surveyButton.setText("투표하기");

        surveyButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {

                        if(general.getWith_image()==false)
                            bitArray = pollAdapter.bitArray;
                        else
                            bitArray = pollAdapterWImage.bitArray;
                        if(bitArray.stream().mapToInt(a->a).sum()==0){
                            return;
                        }
                        setResult(DONE);
                        loading.setVisibility(View.VISIBLE);
                        BackgroundSurveyThread surveyThread = new BackgroundSurveyThread();
                        surveyThread.start();
                    }
                }
        );

    }
    public void setPollsDone(){
        check_results.setVisibility(View.GONE);
        poll_recyclerview.setVisibility(View.GONE);
        poll_done_recyclerview.setVisibility(View.VISIBLE);
        if(general.getWith_image()==false) {
            pollDoneAdapter = new PollDoneAdapter(GeneralDetailActivity.this, general.getPolls(), bitArray, general.getMulti_response());
            poll_done_recyclerview.setLayoutManager(new LinearLayoutManager(this));
            poll_done_recyclerview.setAdapter(pollDoneAdapter);
        }else{
            pollDoneAdapterWImage = new PollDoneAdapterWImage(GeneralDetailActivity.this, general.getPolls(), bitArray, general.getMulti_response());
            poll_done_recyclerview.setLayoutManager(new LinearLayoutManager(this));
            poll_done_recyclerview.setAdapter(pollDoneAdapterWImage);
        }
        surveyButton.setText("다시 투표하기");
        surveyButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        setResult(DONE);
                        if(general.getWith_image()==false)
                            bitArray = pollDoneAdapter.bitArray;
                        else
                            bitArray = pollDoneAdapterWImage.bitArray;
                        setPollsNotDone();
                    }
                }
        );
    }
    public void ReportDialog(){
        if (general.getReports().contains(UserPersonalInfo.userID)){
            Toast.makeText(GeneralDetailActivity.this, "이미 신고하셨습니다", Toast.LENGTH_SHORT).show();
        } else {
            customDialog = new CustomDialog(GeneralDetailActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {AlertDialog.Builder builder2 = new AlertDialog.Builder(GeneralDetailActivity.this);
                    builder2.setTitle("신고 사유");
                    builder2.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                updateReports();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(GeneralDetailActivity.this, "신고 사유는 "+getResources().getStringArray(R.array.reportreason)[which], Toast.LENGTH_SHORT).show();
                        }
                    });
                    Dialog dialog2 = builder2.create();
                    dialog2.show();
                    customDialog.dismiss();
                }
            });
            customDialog.show();
            customDialog.setMessage("신고는 반대의견을 나타내는 기능이 아닙니다. 신고 사유에 맞지 않는 신고의 경우, 해당 신고는 처리되지 않습니다.");
            customDialog.setPositiveButton("확인");
            customDialog.setNegativeButton("취소");
        }
    }
    private void RemoveDialog() {
        customDialog = new CustomDialog(GeneralDetailActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    deletePost();
                } catch (Exception e) {
                    setResult(0);
                    Toast.makeText(GeneralDetailActivity.this, "삭제 오류", Toast.LENGTH_SHORT).show();
                    finish();
                    e.printStackTrace();
                }
                customDialog.dismiss();
            }
        });
        customDialog.show();
        customDialog.setMessage("글을 삭제하겠습니까?");
        customDialog.setPositiveButton("삭제");
        customDialog.setNegativeButton("취소");
    }
    private void deletePost() throws Exception{
        String requestURL =getString(R.string.server)+ "/api/generals/deletepost/" + general.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.DELETE, requestURL, null, response -> {
                        Intent intent = new Intent(GeneralDetailActivity.this, BoardPost.class);
                        intent.putExtra("position", position);
                        setResult(4, intent);
                        Toast.makeText(GeneralDetailActivity.this, "설문이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }, error -> {
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        setResult(position);
        if (reply_mode == COMMENT) {
            if (et_vote_detail_reply.getText().toString().length() > 0) {
                customDialog = new CustomDialog(GeneralDetailActivity.this, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();

                        customDialog.dismiss();
                    }
                });
                customDialog.show();
                customDialog.setMessage("댓글 작성을 취소하겠습니까?");
                customDialog.setPositiveButton("확인");
                customDialog.setNegativeButton("취소");
            } else {
                finish();
            }
        } else if (reply_mode == REPLY) {
            reply_mode = COMMENT;
            generalReplyRecyclerViewAdapter.Click(-1);
            setReplyVisible(false);
        }
    }
    public void likepost(){
        String requestURL = getString(R.string.server)+"/api/generals/like/"+general.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID",UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                    }, error -> {
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    public void dislikepost(){
        String requestURL = getString(R.string.server)+"/api/generals/dislike/"+general.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID",UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                    }, error -> {
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private class generalDetailHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("thread")) {
                case REFRESH:
                    polls = general.getPolls();
                    bitArray = new ArrayList<>();
                    for(int i=0;i<polls.size();i++){
                        Poll poll = polls.get(i);
                        if(poll.getParticipants_userids().contains(UserPersonalInfo.userID)) bitArray.add(1);
                        else bitArray.add(0);
                    }
                    loading_detail(general);
                    generalReplyRecyclerViewAdapter.setItem(replyArrayList);
                    generalReplyRecyclerViewAdapter.notifyDataSetChanged();
                    loading.setVisibility(View.GONE);
                    reply_done = false;
                    delete_done = false;
                    block_done = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                    break;
                case REPLY:

                    generalReplyRecyclerViewAdapter.setItem(replyArrayList);
                    generalReplyRecyclerViewAdapter.notifyDataSetChanged();
                    InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm2.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    loading.setVisibility(View.GONE);
                    reply_done = false;
                    reply_content = null;
                    et_vote_detail_reply.setText(null);
                    break;
                case SURVEY:
                    loading_detail(general);
                    loading.setVisibility(View.GONE);
                    done_survey = false;
                    setPollsDone();
                    break;
                case 3:
                    generalReplyRecyclerViewAdapter.setItem(replyArrayList);
                    generalReplyRecyclerViewAdapter.notifyDataSetChanged();
                    report_done = false;
                    delete_done = false;
                    break;
                default:
                    break;
            }
            generalReplyRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void postGeneralReReply(String generalcomment_object_id, String content) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/comment/postreply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("generalcomment_object_id", generalcomment_object_id);
            params.put("content", content);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        reply_done = true;
            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void generalDeleteComment(String generalcomment_object_id) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/deletecomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("generalcomment_object_id", generalcomment_object_id);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                delete_done = true;
            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void generalReportComment(String generalcomment_object_id, String reason) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/reportcomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("generalcomment_object_id", generalcomment_object_id);
            params.put("reason", reason);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                report_done = true;
            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void blockUser(String counter) {
        String token = UserPersonalInfo.token;
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/users/blockuser";
            JSONObject params = new JSONObject();
            params.put("userID", counter);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                        block_done = true;
            }, error -> {
                        error.printStackTrace();
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }
    public void setNote(String content) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/messages/postmessage";
            JSONObject params = new JSONObject();
            params.put("from_userID", UserPersonalInfo.email);
            params.put("to_userID", general.getAuthor_userid());
            params.put("content", content);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, requestURL, params, response -> {
                Intent intent = new Intent(context, MyNoteActivity.class);
                messageDialog.dismiss();
                startActivity(intent);
            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        int work = getIntent().getIntExtra("work", 0);
        if (work == 0) {

        } else if (work == 1) {
            this.overridePendingTransition(R.anim.enter, R.anim._null);
        }
    }
}
