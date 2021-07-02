package com.pumasi.surbay.pages.boardpage;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.pumasi.surbay.R;
import com.pumasi.surbay.SurveyWebActivity;
import com.pumasi.surbay.adapter.ReplyListViewAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class PostDetailActivity extends AppCompatActivity {
    static final int START_SURVEY = 1;
    static final int DONE = 1;
    static final int NOT_DONE = 0;
    static final int FIX = 2;
    static final int FIX_DONE = 3;
    static final int REPORTED = 5;

    static final int REFRESH = 0;
    static final int REPLY = 1;

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
    EditText reply_enter;
    LinearLayout prizeLayout;
    TextView prize;

    LinearLayout partilayout;
    LinearLayout authorlayout;
    Button surveyEx;
    Button surveyEnd;

    private AlertDialog dialog;
    private CustomDialog customDialog;
    ImageButton reply_enter_button;

    private Post post;
    private int position;
    String surveyURL;


    private static ReplyListViewAdapter detail_reply_Adapter;
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

    String newReply = null;
    private boolean postReplyDone = false;

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

        reply_enter = findViewById(R.id.reply_enter);
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
        detail_reply_Adapter = new ReplyListViewAdapter(PostDetailActivity.this, replyArrayList);
        detail_reply_Adapter.setPost(post);
        mLayoutManager = new LinearLayoutManager(this);
        detail_reply_listView.setLayoutManager(mLayoutManager);
        detail_reply_listView.setAdapter(detail_reply_Adapter);



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

        reply_enter.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.reply_enter) {
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
                newReply = reply_enter.getText().toString();
                if (newReply.length() > 0 ){
                    loading.setVisibility(View.VISIBLE);
                    BackgroundReplyThread replyThread = new BackgroundReplyThread();
                    replyThread.start();
                }
            }
        });

        reply_enter.setOnClickListener(new View.OnClickListener() {
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

        Log.d("reports", post.getReports().toString());

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
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
            postReply(newReply);
            while(!(postReplyDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            getPost(post.getID());
            while(!(getPostDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putInt("thread", REPLY);
            message.setData(bundle);
            handler.sendMessage(message);
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
                                    Reply re = new Reply(id, UserPersonalInfo.userID, reply, realdate, new ArrayList<>(), false);
                                    re.setWriter_name(writer_name);
                                    replyArrayList.add(re);
                                    postReplyDone = true;

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
            postReplyDone = true;
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
                    updateParticipants(updatedParticipants);
                    surveyButton.setClickable(false);
                    surveyButton.setText("이미 참여했습니다");
                    surveyButton.setBackgroundResource(R.drawable.not_round_gray_fill);
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
    private void getPersonalInfo() {
        if (UserPersonalInfo.token == null) {
            return;
        }
        String token = UserPersonalInfo.token;
        try{
            String requestURL = getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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



                            SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor autoLogin = auto.edit();
                            autoLogin.putString("name", user.getString("name"));
                            autoLogin.commit();
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
                                Toast.makeText(PostDetailActivity.this, "설문이 신고되었습니다", Toast.LENGTH_SHORT).show();
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
        } else {
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);

            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if(reply_enter.getText().toString().length()>0){
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
                //select back button
                ReportDialog();
                break;
            case R.id.fix:
                Date now = new Date();
                if((!post.isDone()) || !(now.after(post.getDeadline()))) {
                    Intent intent = new Intent(this, PostWriteActivity.class);
                    intent.putExtra("purpose", FIX);
                    intent.putExtra("post", post);
                    startActivityForResult(intent, FIX);
                }else{
                    Toast.makeText(PostDetailActivity.this, "마감된 설문은 수정이 불가합니다", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.remove:
                RemoveDialog();
                break;
            case R.id.note:
                break;
            case R.id.resultrequest:
                break;

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
        }else if(now.after(post.getDeadline())){
            surveyButton.setClickable(false);
            surveyButton.setText("종료된 설문입니다");
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
            Log.d("exception", "failed posting");
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
        Log.d("deadline is", ""+post.getDeadline());
        Log.d("formatted deadline is", ""+new SimpleDateFormat("MM.dd a K시", Locale.KOREA).format(post.getDeadline()));

        deadline.setText(new SimpleDateFormat("MM.dd").format(post.getDate()) + " - " + new SimpleDateFormat("MM.dd a K시", Locale.KOREA).format(post.getDeadline()));
        int dday_count = calc_dday(post.getDeadline());
        Date now = new Date();
        if(now.after(post.getDeadline()) || post.isDone()){
            dday.setVisibility(View.VISIBLE);
            dday.setText("종료");
        }if (dday_count <= 3 && dday_count >= 0 ){
            if (dday_count==0){
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-Day");
            } else {
                dday.setVisibility(View.VISIBLE);
                dday.setText("D-"+dday_count);
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
                long diff = (today.getTime() - post.getDate().getTime()) / (60*1000);
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
//                            setResult(4);
//                            Toast.makeText(PostDetailActivity.this, "설문이 삭제되었습니다", Toast.LENGTH_SHORT).show();
//                            finish();

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
        customDialog.setMessage("설문을 삭제하더라도 설문 작성 가능 횟수는\n늘어나지 않습니다.\n설문을 삭제하겠습니까?");
        customDialog.setPositiveButton("삭제");
        customDialog.setNegativeButton("취소");
    }

    public void SurveyEndDialog(){
        customDialog = new CustomDialog(PostDetailActivity.this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partilayout.setVisibility(View.VISIBLE);
                authorlayout.setVisibility(View.GONE);
                surveyButton.setClickable(false);
                surveyButton.setBackgroundResource(R.drawable.not_round_gray_fill);
                surveyButton.setText("마감되었습니다");
                post.setDone(true);

                deadline.setText(new SimpleDateFormat("MM.dd").format(post.getDate()) + " - " + new SimpleDateFormat("MM.dd a H시", Locale.KOREA).format(post.getDeadline()));
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
        customDialog.setMessage("설문을 마감하면 더 이상 설문 참여를 받을 수 없으며, 참여 보상이 있는 경우 설문 참여자들에게 추첨을 통해 자동으로 지급됩니다.");
        customDialog.setPositiveButton("설문 마감");
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
        customDialog.setMessage("설문을 1회(24시간) 연장하면 1크레딧이\n" +"차감됩니다.\n"+"설문을 연장하겠습니까?");
        customDialog.setPositiveButton("설문 연장");
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
                            Toast.makeText(PostDetailActivity.this, "설문이 삭제되었습니다", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(PostDetailActivity.this, "설문기간 연장에 실패하였습니다", Toast.LENGTH_SHORT).show();
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

        deadline.setText(new SimpleDateFormat("MM.dd").format(post.getDate()) + " - " + new SimpleDateFormat("MM.dd a H시", Locale.KOREA).format(new_deadline));
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
        Toast.makeText(PostDetailActivity.this, "설문이 연장되었습니다", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onBackPressed() {
        if(reply_enter.getText().toString().length()>0){

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
        }
        else{
            finish();
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
                    detail_reply_Adapter = new ReplyListViewAdapter(PostDetailActivity.this, replyArrayList);
                    detail_reply_Adapter.setPost(post);
                    detail_reply_Adapter.notifyDataSetChanged();
                    detail_reply_listView.setAdapter(detail_reply_Adapter);

                    getPostDone = false;
                    mSwipeRefreshLayout.setRefreshing(false);
                    break;
                case REPLY:
                    detail_reply_Adapter = new ReplyListViewAdapter(PostDetailActivity.this, replyArrayList);
                    detail_reply_Adapter.setPost(post);
                    detail_reply_Adapter.notifyDataSetChanged();
                    detail_reply_listView.setAdapter(detail_reply_Adapter);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    loading.setVisibility(View.GONE);
                    postReplyDone = false;
                    newReply = null;
                    reply_enter.setText(null);
                    break;
                default:
                    break;
            }
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
                                        JSONObject reply = ja.getJSONObject(j);
                                        String reid = reply.getString("_id");
                                        String writer = reply.getString("writer");
                                        String contetn = reply.getString("content");
                                        Date datereply = null;
                                        try {
                                            datereply = fm.parse(reply.getString("date"));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        Boolean replyhide = reply.getBoolean("hide");
                                        JSONArray ua = (JSONArray)reply.get("reports");

                                        ArrayList<String> replyreports = new ArrayList<String>();
                                        for (int u = 0; u<ua.length(); u++){
                                            replyreports.add(ua.getString(u));
                                        }
                                        String writer_name = null;
                                        try {
                                            writer_name = reply.getString("writer_name");
                                        }catch (Exception e){
                                            writer_name = null;
                                        }
                                        Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide);
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
}
