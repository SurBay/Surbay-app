package com.pumasi.surbay.pages.boardpage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.pumasi.surbay.ContentDetailCommentsActivity;
import com.pumasi.surbay.MyNoteActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.SurveyWebActivity;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.adapter.ReplyRecyclerViewAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.MessageDialog;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PostDetailActivity extends AppCompatActivity {
    private Context context;
    private Tools tools = new Tools();
    private final int COMMENT = 0;
    private final int REPLY = 1;
    private final int EDIT = 2;

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

    private AlertDialog dialog;
    private CustomDialog customDialog;
    private MessageDialog messageDialog;
    private ImageView iv_post_detail_reply;
    private ImageButton reply_enter_button;

    private Post post;
    private int position;
    String surveyURL;

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
    private boolean getPostDone = false;
    private boolean person_done = false;

    RelativeLayout loading;

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

        new BackgroundThread().start();

        loading_detail(post);
        replyArrayList = post.getComments();
        replyRecyclerViewAdapter = new ReplyRecyclerViewAdapter(PostDetailActivity.this, replyArrayList);
        replyRecyclerViewAdapter.setPost(post);
        replyRecyclerViewAdapter.setOnItemClickListener(new ReplyRecyclerViewAdapter.OnReplyClickListener() {
            @Override
            public void onReplyClick(View v, int position) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(PostDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
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
                    customDialog = new CustomDialog(PostDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
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
                                    customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
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
                                    et_post_detail_reply.requestFocus();
                                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                                    reply_mode = EDIT;
                                    reply_id = reply.getID();
                                    setReplyVisible(false);
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
                        Intent newIntent = new Intent(getApplicationContext(), SurveyWebActivity.class);
                        newIntent.putExtra("url", surveyURL);
                        startActivityForResult(newIntent, START_SURVEY);
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
                    CustomDialog customDialog = new CustomDialog(PostDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 댓글을 이용하실 수 없습니다");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                reply_content = et_post_detail_reply.getText().toString();
                et_post_detail_reply.getText().clear();

                if (reply_content.length() > 0 ){
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
                    CustomDialog customDialog = new CustomDialog(PostDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 댓글을 이용하실 수 없습니다");
                    customDialog.setNegativeButton("확인");
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
            getPost(post.getID());
            while(!(getPostDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
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
                case EDIT:
                    postEditComment(reply_id, reply_content);
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
            getPost(post.getID());
            while(!(getPostDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
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
            getPost(post.getID());
            while(!(getPostDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
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
            getPersonalInfo();
            while (!person_done) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
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
            Log.d("date is", ""+fm.format(date)+ "   "+ date);
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
                        Log.d("exception", "volley error");
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
    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
            return;
        }
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            Log.d("response is", ""+response);
                            JSONObject user = res.getJSONObject("data");
                            UserPersonalInfo.name = user.getString("name");
                            UserPersonalInfo.email = user.getString("email");
                            UserPersonalInfo.points = user.getInt("points");
                            UserPersonalInfo.level = user.getInt("level");
                            UserPersonalInfo.userID = user.getString("userID");
                            UserPersonalInfo.userPassword = user.getString("userPassword");
                            UserPersonalInfo.gender = user.getInt("gender");
                            UserPersonalInfo.yearBirth = user.getInt("yearBirth");
                            JSONArray ja = (JSONArray)user.get("participations");

                            ArrayList<String> partiarray = new ArrayList<String>();
                            for (int j = 0; j<ja.length(); j++){
                                partiarray.add(ja.getString(j));
                            }

                            UserPersonalInfo.participations = partiarray;
                            Log.d("partiarray", ""+UserPersonalInfo.participations.toString());

                            JSONArray ja2 = (JSONArray)user.get("prizes");
                            ArrayList<String> prizearray = new ArrayList<String>();
                            for (int j = 0; j<ja2.length(); j++){
                                prizearray.add(ja2.getString(j));
                            }
                            UserPersonalInfo.prizes = prizearray;
                            Log.d("prizearray", ""+UserPersonalInfo.prizes.toString());
                            ArrayList<Notification> notifications = new ArrayList<>();
                            try{
                                SimpleDateFormat fm = new SimpleDateFormat(getString(R.string.date_format));
                                JSONArray na = (JSONArray)user.get("notifications");
                                if (na.length() != 0){
                                    for (int j = 0; j<na.length(); j++){
                                        JSONObject notification = na.getJSONObject(j);
                                        String title = notification.getString("title");
                                        String content = notification.getString("content");
                                        String post_id = notification.getString("post_id");
                                        Date date = null;
                                        try {
                                            date = fm.parse(notification.getString("date"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Integer post_type = notification.getInt("post_type");
                                        Notification newNotification = new Notification(title, content, post_id, date, post_type);
                                        notifications.add(newNotification);
                                    }
                                }

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            UserPersonalInfo.notifications = notifications;
                            UserPersonalInfo.notificationAllow = user.getBoolean("notification_allow");
                            UserPersonalInfo.prize_check = user.getInt("prize_check");
                            try {
                                ArrayList<String> blockedUsers = new ArrayList<>();
                                JSONArray ja7 = (JSONArray)user.get("blocked_users");
                                for (int j = 0; j < ja7.length(); j++) {
                                    blockedUsers.add(ja7.getString(j));
                                }
                                UserPersonalInfo.blocked_users = blockedUsers;
                            } catch (Exception e) {
                                UserPersonalInfo.blocked_users = new ArrayList<>();
                            }
                            person_done = true;
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
        updateUserParticipation(post.getID());
        getPersonalInfo();
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
                    intent.putExtra("purpose", FIX);
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
                    customDialog = new CustomDialog(PostDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 이용할 수 없는 기능입니다.");
                    customDialog.setNegativeButton("확인");
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

    public void updateUserParticipation(String id){
        String token = UserPersonalInfo.token;
        String requestURL = getString(R.string.server)+"/user/survey";
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("post_id",id);
            params.put("userID", UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("partiarray", ""+response);
                    }, error -> {
                        Log.d("exception", "volley error");
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
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }
    public void nonMemberUpdate(String id) {
        String requestURL = getApplicationContext().getResources().getString(R.string.server) + "/api/posts/nonmembersurvey/" + id;
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, null, response -> {}, error -> {}
            );
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
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
        surveyURL = post.getUrl();

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
//        builder.setView(view);


        final ListView listview = (ListView)view.findViewById(R.id.listview_alterdialog_list);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getColor(R.color.transparent)));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

//        dialog.setCancelable(false);
//        dialog.setContentView(R.layout.share_dialog);
        dialog.setView(view, 0, 0, 0, 0);
        SimpleAdapter simpleAdapter = new SimpleAdapter(PostDetailActivity.this, dialogItemList,
                R.layout.share_listitem,
                new String[]{TAG_IMAGE, TAG_TEXT},
                new int[]{R.id.share_item_image, R.id.share_item_name});

        listview.setAdapter(simpleAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
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
                Log.d("tag", "donepost");
                donepost();
                customDialog.dismiss();
            }
        });
        customDialog.show();
        customDialog.setMessage("리서치를 마감하면 더 이상 리서치 참여를 받을 수 없으며, 참여 보상이 있는 경우 리서치 참여자들에게 추첨을 통해 자동으로 지급됩니다.");
        customDialog.setPositiveButton("리서치 마감");
        customDialog.setNegativeButton("취소");
    }

    public void SurveyExDialog(){

        customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extendPost();
                customDialog.dismiss();
            }
        });
        customDialog.show();
        customDialog.setMessage("리서치를 1회(24시간) 연장하면 1크레딧이\n" +"차감됩니다.\n"+"리서치를 연장하겠습니까?");
        customDialog.setPositiveButton("리서치 연장");
        customDialog.setNegativeButton("취소");
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
            Log.d("fix", UserPersonalInfo.name + params.toString());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("fix is", ""+response);
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
            Log.d("fix", UserPersonalInfo.name);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }


    public void donepost(){
        Log.d("tag", "donepost2");
        String requestURL = getString(R.string.server)+"/api/posts/done/"+post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }
    private void deletePost() throws Exception{
        String requestURL =getString(R.string.server)+ "/api/posts/deletepost/" + post.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.DELETE, requestURL, null, response -> {
                        Log.d("delete", ""+response);
                            Intent intent = new Intent(PostDetailActivity.this, BoardPost.class);
                            intent.putExtra("position", position);
                            setResult(4, intent);
                            Toast.makeText(PostDetailActivity.this, "리서치가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
            Log.d("fix", UserPersonalInfo.name);
        } catch (Exception e){
            Log.d("exception", "failed posting");
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
                                    Toast.makeText(PostDetailActivity.this, "이미 1회 연장하셨습니다", Toast.LENGTH_SHORT).show();

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
        } else if (reply_mode == EDIT) {
            reply_mode = COMMENT;
            Toast.makeText(context, "수정 모드가 종료되었습니다.", Toast.LENGTH_SHORT).show();
        }

    }

    private class postDetailHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.getData().getInt("thread")) {
                case REFRESH:
                    loading_detail(post);
                    Log.d("replylistis", ""+replyArrayList.size());
                    replyRecyclerViewAdapter.setItem(replyArrayList);
                    replyRecyclerViewAdapter.notifyDataSetChanged();

                    getPostDone = false;
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
                    if (reply_mode == EDIT) {
                        reply_mode = COMMENT;
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
                    getPostDone = false;
                default:
                    break;
            }
            person_done = false;
        }
    }

    private void getPost(String id) {
        try{
            String requestURL = getString(R.string.server) + "/api/posts/getpost/"+ id;
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest= new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject res = new JSONObject(response.toString());
                            String post_id = res.getString("_id");
                            String title = res.getString("title");
                            String author = res.getString("author");
                            Integer author_lvl = res.getInt("author_lvl");
                            String content = res.getString("content");
                            Integer participants = res.getInt("participants");
                            Integer goal_participants = res.getInt("goal_participants");
                            String url = res.getString("url");
                            SimpleDateFormat fm = new SimpleDateFormat(getApplicationContext().getString(R.string.date_format));
                            Date date = null;
                            try {
                                date = fm.parse(res.getString("date"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Date deadline = null;
                            try {
                                deadline = fm.parse(res.getString("deadline"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Boolean with_prize = res.getBoolean("with_prize");
                            String prize = "none";
                            Integer count= 0;
                            Integer est_time = res.getInt("est_time");
                            String target = res.getString("target");
                            Boolean done = res.getBoolean("done");
                            Boolean hide = res.getBoolean("hide");
                            Integer extended = res.getInt("extended");
                            String author_userid = res.getString("author_userid");
                            ArrayList<String> prize_urls = null;
                            if(with_prize) {
                                prize = res.getString("prize");
                                count = res.getInt("num_prize");
                                JSONArray pa = (JSONArray) res.get("prize_urls");
                                prize_urls = new ArrayList<String>();
                                for (int j = 0; j<pa.length(); j++){
                                    prize_urls.add(pa.getString(j));
                                }
                            }
                            JSONArray ia = (JSONArray)res.get("participants_userids");

                            ArrayList<String> participants_userids = new ArrayList<String>();
                            for (int j = 0; j<ia.length(); j++){
                                participants_userids.add(ia.getString(j));
                            }

                            JSONArray ka = (JSONArray)res.get("reports");

                            ArrayList<String> reports = new ArrayList<String>();
                            for (int j = 0; j<ka.length(); j++){
                                reports.add(ka.getString(j));
                            }

                            ArrayList<Reply> comments = new ArrayList<>();
                            try{
                                JSONArray ja = (JSONArray)res.get("comments");
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
                                        if (!replyhide && !replyreports.contains(UserPersonalInfo.userID)){
                                            comments.add(re);
                                        }
                                    }
                                }
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Integer pinned = 0;
                            Boolean annonymous = false;
                            String author_info = "";
                            try {
                                pinned = res.getInt("pinned");
                                annonymous = res.getBoolean("annonymous");
                                author_info = res.getString("author_info");
                            }catch (Exception e){

                            }
                            Post refreshPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, count,comments,done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info);
                            if(with_prize) refreshPost.setPrize_urls(prize_urls);

                            post = refreshPost;
                            replyArrayList = comments;
                            getPostDone = true;

                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
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

    public void postEditComment(String postcomment_object_id, String content) {
        Log.d("edit", "postEditComment: " + postcomment_object_id + ", " + content);
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/posts/updatecomment";
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
}
