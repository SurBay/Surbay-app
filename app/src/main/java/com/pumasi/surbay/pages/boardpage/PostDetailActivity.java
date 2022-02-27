package com.pumasi.surbay.pages.boardpage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.ktx.Firebase;
import com.pumasi.surbay.MyNoteActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.SurveyWebActivity;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.adapter.ReplyRecyclerViewAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.MessageDialog;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.tools.FirebaseLogging;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class PostDetailActivity extends AppCompatActivity {
    private Context context;
    private Tools tools = new Tools();
    private final int COMMENT = 0;
    private final int REPLY = 1;

    static final int START_SURVEY = 1;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    static final int FIX = 2;
    static final int FIX_DONE = 3;
    static final int REPORTED = 5;

    static final int REFRESH = 0;

    private String reply_id;
    private String reply_content;
    private int reply_mode;
    private boolean reply_done = false;
    private boolean report_done = false;
    private boolean delete_done = false;
    private boolean block_done = false;

    private TextView participants;
    private TextView participants_percent;
    TextView author;
    TextView author_info;
    TextView title;
    TextView target;
    TextView content;

    TextView est_time;
    TextView deadline;
    TextView dday;
    private EditText et_post_detail_reply;
    private LinearLayout ll_post_detail_reply;
    LinearLayout prizeLayout;
    TextView prize;

    LinearLayout partilayout;
    LinearLayout authorlayout;
    Button surveyEx;
    Button surveyEnd;

    private CustomDialog customDialog;
    private MessageDialog messageDialog;
    private ImageView iv_post_detail_reply;
    private ImageButton reply_enter_button;

    private Post post;
    private int position;
    String url;

    private ReplyRecyclerViewAdapter replyRecyclerViewAdapter;
    private static RecyclerView detail_reply_listView;
    private static ArrayList<Reply> replyArrayList;

    static Button surveyButton;

    List<Map<String, Object>> dialogItemList;


    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    int[] image = {R.drawable.kakaotalk, R.drawable.googleform};
    String[] text = {" SNS로 공유하기 ", "구글폼 url 복사하기"};
    final String[] spinner_esttime = {"1분 미만", "1~2분", "2~3분", "3~5분", "5~7분", "7~10분", "10분 초과"};

    Date today;
    private LinearLayoutManager mLayoutManager;

    public static SwipeRefreshLayout mSwipeRefreshLayout;
    postDetailHandler handler = new postDetailHandler();

    RelativeLayout loading;
    ServerTransport st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_detail);
        setResult(NOT_DONE);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        Intent intent = getIntent();
        context = PostDetailActivity.this;
        new FirebaseLogging(context).LogScreen("research_detail", "리서치");

        st = new ServerTransport(context);
        customDialog = new CustomDialog(PostDetailActivity.this);

        today = new Date();

        author = findViewById(R.id.author);
        author_info = findViewById(R.id.author_info);
        title = findViewById(R.id.title);
        target = findViewById(R.id.target);
        content = findViewById(R.id.content);;

        est_time = findViewById(R.id.detail_est_time);
        deadline = findViewById(R.id.deadline);
        dday = findViewById(R.id.detail_dday);
        participants = findViewById(R.id.participants);
        participants_percent = findViewById(R.id.participants_percent);

        ll_post_detail_reply = findViewById(R.id.ll_post_detail_reply);
        if (UserPersonalInfo.userID.equals("nonMember")) ll_post_detail_reply.setVisibility(View.GONE);
        iv_post_detail_reply = findViewById(R.id.iv_post_detail_reply);
        et_post_detail_reply = findViewById(R.id.et_post_detail_reply);
        reply_enter_button = findViewById(R.id.reply_enter_button);

        surveyButton = findViewById(R.id.surveyButton);
        prizeLayout = findViewById(R.id.prize_layout);
        prize = findViewById(R.id.prize);
        detail_reply_listView = findViewById(R.id.detail_reply_list);

        partilayout = findViewById(R.id.detail_parti_layout);
        authorlayout = findViewById(R.id.detail_author_layout);
        surveyEx = findViewById(R.id.surveyExButton);
        surveyEnd = findViewById(R.id.surveyEndButton);

        loading = findViewById(R.id.loadingPanel);
        loading.setVisibility(View.GONE);

        post = (Post)intent.getParcelableExtra("post");
        position = intent.getIntExtra("position", -1);

        loading_detail(post);
        replyArrayList = post.getComments();
        replyRecyclerViewAdapter = new ReplyRecyclerViewAdapter(PostDetailActivity.this, replyArrayList);
        replyRecyclerViewAdapter.setPost(post);
        replyRecyclerViewAdapter.setOnItemClickListener(new ReplyRecyclerViewAdapter.OnReplyClickListener() {
            @Override
            public void onReplyClick(View v, int position) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog.customRejectDialog("비회원은 이용할 수 없는 기능입니다.", "확인");
                } else {
                    if (reply_mode == COMMENT) {
                        et_post_detail_reply.post(new Runnable() {
                            @Override
                            public void run() {
                                et_post_detail_reply.setFocusableInTouchMode(true);
                                et_post_detail_reply.requestFocus();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                            }
                        });
                        Reply reply = (Reply) replyRecyclerViewAdapter.getItem(position);
                        replyRecyclerViewAdapter.Click(position);
                        reply_id = reply.getID();
                        reply_mode = REPLY;
                        setReplyVisible(true);
                    } else {
                        reply_mode = COMMENT;
                        setReplyVisible(false);
                        replyRecyclerViewAdapter.Click(-1);
                    }
                }
            }
        });
        replyRecyclerViewAdapter.setOnItemClickListener(new ReplyRecyclerViewAdapter.OnMenuClickListener() {
            @Override
            public void onMenuClick(View v, int position) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog.customRejectDialog("비회원은 이용할 수 없는 기능입니다.", "확인");
                } else {
                    Reply reply = (Reply) replyRecyclerViewAdapter.getItem(position);
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
                                    customDialog.customSimpleDialog("댓글을 삭제하시겠습니까?", "삭제", "취소", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            BackgroundDeleteThread backgroundDeleteThread = new BackgroundDeleteThread(reply.getID());
                                            backgroundDeleteThread.start();
                                            customDialog.customDialog.dismiss();
                                        }
                                    });
                                    Log.d("checkcheck", "onMenuItemClick: ");
                                    return true;
                                case R.id.report:
                                    if (reply.getReports().contains(UserPersonalInfo.email)) {
                                        customDialog.customRejectDialog("이미 신고한 댓글입니다.", "확인");
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
                                                    Toast.makeText(PostDetailActivity.this, "신고가 접수되었습니다", Toast.LENGTH_SHORT).show();
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
        detail_reply_listView.setLayoutManager(mLayoutManager);
        detail_reply_listView.setAdapter(replyRecyclerViewAdapter);


        surveyButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        String log = post.getID().replaceAll("0", "A").replaceAll("1", "B").replaceAll("2", "C").replaceAll("3", "D").replaceAll("4", "E").replaceAll("5", "F").replaceAll("6", "G")
                                .replaceAll("7", "H").replaceAll("8", "I").replaceAll("9", "J");
                        Log.d("hello", log);
                        new FirebaseLogging(context).LogScreen("participate_" + log, "참여_" + log);
                        Intent intent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                        intent.putExtra("auto", true);
                        intent.putExtra("url", url);
                        intent.putExtra("post_id", post.getID());
                        startActivityForResult(intent, START_SURVEY);
                    }
                }
        );

        setbuttonunable();

        et_post_detail_reply.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.et_post_detail_reply) {
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

        reply_enter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserPersonalInfo.userID.equals("nonMember")){
                    customDialog.customRejectDialog("비회원은 댓글을 이용하실 수 없습니다.", "확인");
                    return;
                }
                reply_content = et_post_detail_reply.getText().toString();
                et_post_detail_reply.getText().clear();

                if (reply_content.length() > 0) {
                    loading.setVisibility(View.VISIBLE);
                    BackgroundReplyThread replyThread = new BackgroundReplyThread();
                    replyThread.start();
                }
            }
        });

        et_post_detail_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserPersonalInfo.userID.equals("nonMember")){
                    customDialog.customRejectDialog("비회원은 댓글을 이용하실 수 없습니다.", "확인");
                    return;
                }
            }
        });

        surveyEx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyExDialog();
            }
        });

        surveyEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyEndDialog();
            }
        });

        dialogItemList = new ArrayList<>();

        for(int i=0;i<image.length;i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put(TAG_IMAGE, image[i]);
            itemMap.put(TAG_TEXT, text[i]);

            dialogItemList.add(itemMap);
        }


        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.post_detail_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BackgroundThread refreshThread = new BackgroundThread();
                refreshThread.start();

            }
        });
    }
    class BackgroundThread extends Thread{
        public void run() {
            st.getResearch(post.getID());
            while(st.result_research == null) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            post = st.result_research;
            replyArrayList = st.result_research.getComments();

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
                    postReply(reply_content);
                    break;
                case REPLY:
                    postPostReReply(reply_id, reply_content);
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
            postDeleteComment(id);
            while (!delete_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.getResearch(post.getID());
            while(st.result_research == null) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            post = st.result_research;
            replyArrayList = st.result_research.getComments();

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
            postReportComment(id, report_reason);
            while (!report_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.getResearch(post.getID());
            while(st.result_research == null) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {

                }
            }
            post = st.result_research;
            replyArrayList = st.result_research.getComments();

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
            while (!st.getPersonalInfoDone) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            st.getPersonalInfoDone = false;
            new BackgroundThread().start();
        }
    }



    private void postReply(String reply) {
        String requestURL = getString(R.string.server)+"/api/posts/writecomment/"+post.getID();
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
                            Log.d("response is", "" + response);
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
                                    Toast.makeText(PostDetailActivity.this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException | ParseException e) {
                                Toast.makeText(PostDetailActivity.this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }, error -> {
                        Toast.makeText(PostDetailActivity.this, "오류가 발생했습니다", Toast.LENGTH_SHORT).show();
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("result code", "result code is " +resultCode);
        switch (resultCode){
            case DONE:
                try {
                    int updatedParticipants = post.getParticipants()+1;
                    if (UserPersonalInfo.userID.equals("nonMember")) {
                        String goalpart, realpart;
                        nonMemberSurvey();
                        if(post.getParticipants()>999){
                            goalpart = "999+";
                        }else {
                            goalpart = post.getParticipants().toString();
                        }

                        if(post.getGoal_participants()>999){
                            realpart = "999+";
                        }else {
                            realpart = post.getGoal_participants().toString();
                        }
                        participants.setText(""+goalpart+"/"+realpart);

                    } else {
                        updateParticipants(updatedParticipants);
                        surveyButton.setClickable(false);
                        surveyButton.setText("이미 참여했습니다");
                        surveyButton.setBackgroundResource(R.drawable.not_round_gray_fill);
                    }
                    Intent resultIntent = new Intent(getApplicationContext(), BoardPost.class);
                    resultIntent.putExtra("position", position);
                    resultIntent.putExtra("participants", updatedParticipants);
                    setResult(DONE, resultIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            case NOT_DONE:
                setResult(NOT_DONE);
                return;
            case FIX_DONE:
                Intent resultIntent = new Intent(getApplicationContext(), BoardPost.class);
                Post post = data.getParcelableExtra("post");
                resultIntent.putExtra("post", post);
                resultIntent.putExtra("position", position);
                setResult(FIX_DONE, resultIntent);
                finish();
        }
    }
    public void setReplyVisible(boolean visible) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_post_detail_reply.getLayoutParams();
        if (visible) {
            params.width = (int) (context.getResources().getDisplayMetrics().density * 23);
            params.height = (int) (context.getResources().getDisplayMetrics().density * 23);
        } else {
            params.width = 0;
            params.height = 0;
        }
        iv_post_detail_reply.setLayoutParams(params);
    }
    private void updateParticipants(int updatedParticipants) throws Exception{
        String goalpart, realpart;
        if(post.getParticipants()>999){
            goalpart = "999+";
        }else {
            goalpart = post.getParticipants().toString();
        }

        if(post.getGoal_participants()>999){
            realpart = "999+";
        }else {
            realpart = post.getGoal_participants().toString();
        }
        participants.setText(""+goalpart+"/"+realpart);
        st.getPersonalInfo();
        st.getPersonalInfoDone = false;
    }
    private void updateReports() throws Exception {
        String requestURL = getString(R.string.server) + "/api/posts/report/" + post.getID();
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        try {
                            Log.d("response is", "" + response);
                            JSONObject res = new JSONObject(response.toString());
                            int success = res.getInt("result");
                            if (success == 1) {
                                Intent intent = new Intent(PostDetailActivity.this, BoardPost.class);
                                setResult(REPORTED, intent);
                                intent.putExtra("position", position);
                                Toast.makeText(PostDetailActivity.this, "리서치가 신고되었습니다", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if(res.getString("message").startsWith("cannot report this post")){
                                    Toast.makeText(PostDetailActivity.this, "이 게시물은 신고할 수 없습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.post_detail_bar, menu);
        Log.d("this is", "is it mine  "+UserPersonalInfo.userID+ post.getAuthor_userid()+UserPersonalInfo.userID.equals(post.getAuthor_userid()));
        if (UserPersonalInfo.userID.equals(post.getAuthor_userid())){
            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);

            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(true);
            menu.getItem(5).setVisible(false);
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(false);

            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
            menu.getItem(5).setVisible(true);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(et_post_detail_reply.getText().toString().length()>0){
                    customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
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
            case R.id.share:
                ShareDialog();
                break;
            case R.id.report:
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(PostDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    ReportDialog();
                }
                break;
            case R.id.fix:
                Date now = new Date();
                if((!post.isDone()) || !(now.after(new Date(tools.toLocal(post.getDeadline().getTime()))))) {
                    Intent intent = new Intent(this, PostWriteActivity.class);
                    intent.putExtra("purpose", PostWriteActivity.EDIT);
                    intent.putExtra("post", post);
                    startActivityForResult(intent, FIX);
                }else{
                    Toast.makeText(PostDetailActivity.this, "마감된 리서치는 수정이 불가합니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.remove:
                RemoveDialog();
                break;
            case R.id.note:
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(PostDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    messageDialog = new MessageDialog(context, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        messageDialog.setClickable(false);
                        setNote(messageDialog.note);

                        }
                    });
                    messageDialog.show();
                }
                break;
            case R.id.resultrequest:
                break;
            case R.id.block:
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog.customRejectDialog("비회원은 이용할 수 없는 기능입니다.", "확인");
                } else {
                    BackgroundBlockThread backgroundBlockThread = new BackgroundBlockThread(post.getAuthor_userid());
                    backgroundBlockThread.start();
                }


        }
        return super.onOptionsItemSelected(item);
    }

    public void setbuttonunable(){
        Date now = new Date();
        if (post.isDone()) {
            surveyButton.setClickable(false);
            surveyButton.setText("마감되었습니다");
            surveyButton.setBackgroundResource(R.drawable.not_round_gray_fill);
        }
        else if (UserPersonalInfo.userID.equals(post.getAuthor_userid())){
            partilayout.setVisibility(View.GONE);
            authorlayout.setVisibility(View.VISIBLE);
        }else if(now.after(new Date(tools.toLocal(post.getDeadline().getTime())))){
            surveyButton.setClickable(false);
            surveyButton.setText("종료된 리서치입니다");
            surveyButton.setBackgroundResource(R.drawable.not_round_gray_fill);
        }else if (UserPersonalInfo.participations.contains(post.getID())){
            surveyButton.setClickable(false);
            surveyButton.setText("이미 참여했습니다");
            surveyButton.setBackgroundResource(R.drawable.not_round_gray_fill);
        }
    }


    public int calc_dday(Date goal){
        Date dt = new Date();

        long diff = 0;
        int dday = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MM yyyy");
            SimpleDateFormat fm = new SimpleDateFormat("dd MM yyyy");
            LocalDate date1 = LocalDate.parse(fm.format(goal), dtf);
            LocalDate date2 = LocalDate.parse(fm.format(dt), dtf);
            diff = ChronoUnit.DAYS.between(date2, date1);
            dday = (int) diff;
            Log.d("dday is", "diffff"+dday);
        }
        else{
            diff = goal.getDate()-dt.getDate();
            dday = (int) diff;
        }
        return dday;
    }

    public void ReportDialog(){
        if (post.getReports().contains(UserPersonalInfo.userID)){
            Toast.makeText(PostDetailActivity.this, "이미 신고하셨습니다", Toast.LENGTH_SHORT).show();
        } else {
            customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
                @Override
                public void onClick(View v) {AlertDialog.Builder builder2 = new AlertDialog.Builder(PostDetailActivity.this);
                    builder2.setTitle("신고 사유");
                    builder2.setItems(R.array.reportreason, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                updateReports();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(PostDetailActivity.this, "신고 사유는 "+getResources().getStringArray(R.array.reportreason)[which], Toast.LENGTH_SHORT).show();
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

    private void ShareDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this, R.style.CustomDialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.share_dialog, null);


        final ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.transparent)));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setView(view, 0, 0, 0, 0);
        SimpleAdapter simpleAdapter = new SimpleAdapter(PostDetailActivity.this, dialogItemList,
                R.layout.share_listitem,
                new String[]{TAG_IMAGE, TAG_TEXT},
                new int[]{R.id.share_item_image, R.id.share_item_name});

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                        Sharing_intent.setType("text/plain");

                        String Test_Message = post.getUrl();

                        Sharing_intent.putExtra(Intent.EXTRA_TEXT, Test_Message);

                        Intent Sharing = Intent.createChooser(Sharing_intent, "공유하기");
                        startActivity(Sharing);
                        dialog.dismiss();
                        break;
                    case 1:
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", post.getUrl());
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(PostDetailActivity.this, "클립보드에 복사되었습니다", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        break;
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        Window window = dialog.getWindow();
        window.setAttributes(lp);
    }

    private void RemoveDialog() {
        customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                today = new Date();
                long diff = (today.getTime() - new Tools().toLocal(post.getDate().getTime())) / (60*1000);
                int mindiff = (int)diff;
                Log.d("todat", mindiff+ "  "+ diff + "  " + today + post.getDate());
                if (mindiff<10){
                    try {
                        deletePost();
                    } catch (Exception e) {
                        setResult(0);
                        Toast.makeText(PostDetailActivity.this, "삭제 오류", Toast.LENGTH_SHORT).show();
                        finish();
                        e.printStackTrace();
                    }

                } else {

                    CustomDialog customDialog2 = new CustomDialog(PostDetailActivity.this, null);
                    customDialog2.show();
                    customDialog2.setMessage("등록 후 10분이 경과하여 삭제할 수 없습니다");
                    customDialog2.setNegativeButton("확인");
                }
                customDialog.dismiss();
            }
        });
        customDialog.show();
        customDialog.setMessage("리서치를 삭제하더라도 리서치 작성 \n가능 횟수는 늘어나지 않습니다.\n리서치를 삭제하겠습니까?");
        customDialog.setPositiveButton("삭제");
        customDialog.setNegativeButton("취소");
    }

    public void SurveyEndDialog(){
        customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                partilayout.setVisibility(View.VISIBLE);
                authorlayout.setVisibility(View.GONE);
                surveyButton.setClickable(false);
                surveyButton.setBackgroundResource(R.drawable.not_round_gray_fill);
                surveyButton.setText("마감되었습니다");
                post.setDone(true);

                deadline.setText(tools.convertTimeZone(context, post.getDate(), "MM.dd") + " - " + tools.convertTimeZone(context, post.getDeadline(), "MM.dd. a K시"));
                try {
                    updateDeadlinePost(today);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                donepost();
                customDialog.dismiss();
            }
        });
        customDialog.show();
        customDialog.setMessage("리서치를 마감하면 더 이상 리서치 참여를 받을 수 없으며, 참여 보상이 있는 경우 리서치 참여자들에게 추첨을 통해 자동으로 지급됩니다.");
        customDialog.setPositiveButton("리서치 마감");
        customDialog.setNegativeButton("취소");
    }

    public void SurveyExDialog() {
        customDialog.customSimpleDialog("리서치를 1회(24시간) 연장하면 1크레딧이\n" + "차감됩니다.\n" + "리서치를 연장하겠습니까?", "리서치 연장", "취소", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extendPost();
                customDialog.customDialog.dismiss();
            }
        });
    }

    private void updateDeadlinePost(Date deadline) throws Exception{
        String requestURL = getString(R.string.server)+"/api/posts/updatepost/" + post.getID();
        Log.d("fix", UserPersonalInfo.name);
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS");
            fm.setTimeZone(TimeZone.getTimeZone("UTC"));
            params.put("deadline", fm.format(deadline));
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                    }, error -> {
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public void donepost(){
        String requestURL = getString(R.string.server)+"/api/posts/putdonepost";
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("post_object_id", post.getID());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                    }, error -> {
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void deletePost() throws Exception{
        String requestURL =getString(R.string.server)+ "/api/posts/deletepost/" + post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.DELETE, requestURL, null, response -> {
                            Intent intent = new Intent(PostDetailActivity.this, BoardPost.class);
                            intent.putExtra("position", position);
                            setResult(4, intent);
                            Toast.makeText(PostDetailActivity.this, "리서치가 삭제되었습니다", Toast.LENGTH_SHORT).show();
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
    private void nonMemberSurvey() {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/posts/nonmembersurvey/" + post.getID();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, null, response -> {

            }, error -> {
                        error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void extendPost() {
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server)+"/api/posts/extend/"+post.getID();
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.PUT, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);
                            boolean success = res.getBoolean("type");
                            if (success){
                                String deadline = res.getString("new_deadline");
                                extendView(deadline);
                            } else {
                                if(res.getString("message").startsWith("already")){
                                    Toast.makeText(PostDetailActivity.this, "이미 최대로 연장하셨습니다", Toast.LENGTH_SHORT).show();

                                }else if(res.getString("message").startsWith("not")){
                                    Toast.makeText(PostDetailActivity.this, "연장 크레딧이 부족합니다", Toast.LENGTH_SHORT).show();

                                }else {
                                    Toast.makeText(PostDetailActivity.this, "리서치 기간 연장에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
    public void extendView(String dl){

        Log.d("todat",dl);
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss.SSS");
        Date new_deadline = null;
        try {
            new_deadline = fm.parse(dl);
        } catch (ParseException e) {
            Log.d("parsing", "failed");
            e.printStackTrace();
        }

        deadline.setText(tools.convertTimeZone(context, post.getDate(), "MM.dd") + " - " + tools.convertTimeZone(context, post.getDeadline(), "MM.dd. a K시"));
        int dday_count = calc_dday(new_deadline);
        if (dday_count <= 2 && dday_count >= 0 ){
            if (dday_count==0){
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-Day");
            } else {
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-"+(dday_count+1));
            }
        }
        Toast.makeText(PostDetailActivity.this, "리서치가 연장되었습니다", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        setResult(position);
        if (reply_mode == COMMENT) {
            if(et_post_detail_reply.getText().toString().length()>0){

                customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
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
            setReplyVisible(false);
            replyRecyclerViewAdapter.Click(-1);
        }

    }

    private class postDetailHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("thread")) {
                case REFRESH:
                    loading_detail(post);
                    replyRecyclerViewAdapter.setItem(replyArrayList);
                    replyRecyclerViewAdapter.notifyDataSetChanged();

                    block_done = false;
                    delete_done = false;
                    reply_done = false;
                    report_done = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    try {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {

                    }
                    loading.setVisibility(View.GONE);
                    break;
                case REPLY:
                    replyRecyclerViewAdapter.setItem(replyArrayList);
                    replyRecyclerViewAdapter.notifyDataSetChanged();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    loading.setVisibility(View.GONE);
                    reply_done = false;
                    break;
                case 3:
                    replyRecyclerViewAdapter.setItem(replyArrayList);
                    replyRecyclerViewAdapter.notifyDataSetChanged();
                    report_done = false;
                    delete_done = false;
                    block_done = false;
                default:
                    break;
            }
        }
    }

    public void postPostReReply(String postcomment_object_id, String content) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/posts/comment/postreply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("postcomment_object_id", postcomment_object_id);
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
    public void postDeleteComment(String postcomment_object_id) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/posts/deletecomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("postcomment_object_id", postcomment_object_id);
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
    public void postReportComment(String postcomment_object_id, String reason) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/posts/reportcomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("postcomment_object_id", postcomment_object_id);
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

    public void setNote(String content) {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/messages/postmessage";
            JSONObject params = new JSONObject();
            params.put("from_userID", UserPersonalInfo.email);
            params.put("to_userID", post.getAuthor_userid());
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

    public void blockUser(String counter) {
        Log.d("counter", "blockUser: " + counter);
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

    @Override
    protected void onStart() {
        super.onStart();
        int work = getIntent().getIntExtra("work", 0);
        if (work == 0) {

        } else if (work == 1) {
            this.overridePendingTransition(R.anim.enter, R.anim._null);
        }
    }

    public void loading_detail(Post post){
        if(post.getAnnonymous()==true){
            author.setText("익명");
        }else{
            author.setText(post.getAuthor());
        }
        if(post.getAuthor_info().length()!=0){
            author_info.setText("("+post.getAuthor_info()+")");
            author_info.setVisibility(View.VISIBLE);
        }else{
            author_info.setVisibility(View.INVISIBLE);
        }

        est_time.setText(spinner_esttime[post.getEst_time()]);

        deadline.setText(tools.convertTimeZone(context, post.getDate(), "MM.dd") + " - " + tools.convertTimeZone(context, post.getDeadline(), "MM.dd. a K시"));
        int dday_count = calc_dday(new Date(tools.toLocal(post.getDeadline().getTime())));
        Date now = new Date();
        if(now.after(new Date(tools.toLocal(post.getDeadline().getTime()))) || post.isDone()){
            dday.setVisibility(View.VISIBLE);
            dday.setText("종료");
        } else if (dday_count <= 3 && dday_count >= 0 ){
            switch (dday_count) {
                case 0:
                    dday.setVisibility(View.VISIBLE);
                    dday.setText("D-Day");
                    break;
                default:
                    dday.setVisibility(View.VISIBLE);
                    dday.setText("D-" + dday_count);
                    break;
            }
        }
        String goalpart, realpart;
        if(post.getParticipants()>999){
            goalpart = "999+";
        }else {
            goalpart = post.getParticipants().toString();
        }

        if(post.getGoal_participants()>999){
            realpart = "999+";
        }else {
            realpart = post.getGoal_participants().toString();
        }
        participants.setText(""+goalpart+"/"+realpart);
        participants_percent.setText((100*post.getParticipants()/post.getGoal_participants())+"%");

        title.setText(post.getTitle());
        target.setText(post.getTarget());
        content.setText(post.getContent());
        if(!post.isWith_prize()){
            prizeLayout.setVisibility(View.GONE);
        }else{
            prize.setText(post.getPrize()+" ("+ post.getNum_prize()+ "명)");
        }
        url = post.getUrl();

    }

}
