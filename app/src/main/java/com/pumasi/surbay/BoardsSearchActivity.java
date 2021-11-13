package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ContentRecyclerViewAdapter;
import com.pumasi.surbay.adapter.PostRecyclerViewAdapter;
import com.pumasi.surbay.adapter.VoteRecyclerViewAdapter;
import com.pumasi.surbay.classfile.Content;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;
import com.pumasi.surbay.pages.signup.LoginActivity;
import com.pumasi.surbay.tools.ServerTransport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class BoardsSearchActivity extends AppCompatActivity {

    private Context context;

    static final int LIKED = 5;
    static final int DISLIKED = 4;

    public final int RESEARCH_SELECT = 0;
    public final int VOTE_SELECT = 1;
    public final int CONTENT_SELECT = 2;


    static final int DO_SURVEY = 2;
    static final int DONE = 1;
    static final int LIKE_SURVEY = 3;

    private CustomDialog customDialog;
    final String[] spinner_context = {"리서치", "투표", "콘텐츠"};
    RecyclerView rv_search;
    EditText et_search;
    ImageButton search_delete;
    Spinner search_spinner;
    ImageButton back;
    private RelativeLayout loadingPanel;

    private ImageButton ib_search;
    static ArrayList<Post> searchPosts = new ArrayList<Post>();
    static ArrayList<General> searchVotes = new ArrayList<General>();
    static ArrayList<Content> searchContents = new ArrayList<Content>();

    private PostRecyclerViewAdapter search_ResearchAdapter;
    private VoteRecyclerViewAdapter search_VoteAdapter;
    private ContentRecyclerViewAdapter search_ContentAdapter;

    int selecting_spinner;
    int selected_spinner;
    private boolean getDone = true;
    private String search_keyword = "";

    private ServerTransport st;
    private boolean item_clickable = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards_search);
        this.getSupportActionBar().hide();
        context = getApplicationContext();
        st = new ServerTransport(context);

        Date date = new Date(System.currentTimeMillis());

        loadingPanel = findViewById(R.id.loadingPanel);

        et_search = (EditText)findViewById(R.id.search_edittext);
        search_delete = (ImageButton)findViewById(R.id.boards_delete_button);
        search_spinner = (Spinner)findViewById(R.id.search_spinner);
        rv_search = (RecyclerView) findViewById(R.id.rv_search);
        ib_search = findViewById(R.id.ib_search);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.simple_spinner_item_search, spinner_context);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        search_spinner.setAdapter(adapter);

        Intent intent = getIntent();
        selected_spinner = intent.getIntExtra("pos", 0);
        search_spinner.setSelection(selected_spinner);

        rv_search.setAdapter(search_ResearchAdapter);

        back = findViewById(R.id.go_back_search);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        search_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selecting_spinner = position;
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) rv_search.getLayoutParams();
                et_search.getText().clear();
                clear();

                switch (position) {
                    case RESEARCH_SELECT:
                        Log.d("search", "onItemSelected: Research Selected");
                        layoutParams.setMargins(0,0,0,0);
                        search_ResearchAdapter = new PostRecyclerViewAdapter(searchPosts, getApplicationContext());
                        rv_search.setAdapter(search_ResearchAdapter);
                        rv_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        search_ResearchAdapter.setOnItemClickListener(new PostRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Post post = (Post) search_ResearchAdapter.getItem(position);
                                st.getExecute(post.getID(), 0, 0);
                            }
                        });
                        break;
                    case VOTE_SELECT:
                        Log.d("search", "onItemSelected: Votes Selected");
                        layoutParams.setMargins(0,0,0,0);
                        search_VoteAdapter = new VoteRecyclerViewAdapter(searchVotes, getApplicationContext());
                        rv_search.setAdapter(search_VoteAdapter);
                        rv_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        search_VoteAdapter.setOnItemClickListener(new VoteRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                if (UserPersonalInfo.userID.equals("nonMember")) {
                                    customDialog = new CustomDialog(BoardsSearchActivity.this, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(BoardsSearchActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        }
                                    });
                                    customDialog.show();
                                    customDialog.setMessage("투표게시판을 이용하기 위해서는 \n로그인이 필요합니다.");
                                    customDialog.setPositiveButton("로그인 하기");
                                    customDialog.setNegativeButton("닫기");
                                } else {
                                    General general = (General) search_VoteAdapter.getItem(position);
                                    st.getExecute(general.getID(), 1, 0);
                                }

                            }
                        });
                        break;
                    case CONTENT_SELECT:
                        layoutParams.setMargins((int) (MainActivity.screen_width_px / 45.6666666667),0, (int) (MainActivity.screen_width_px / 45.6666666667),0);
                        search_ContentAdapter = new ContentRecyclerViewAdapter(searchContents, getApplicationContext());
                        rv_search.setAdapter(search_ContentAdapter);
                        rv_search.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2, RecyclerView.VERTICAL, false));
                        search_ContentAdapter.setOnItemClickListener(new ContentRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Content content = (Content) search_ContentAdapter.getItem(position);
                                st.getExecute(content.getId(), 2, 0);
                            }
                        });
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                search_spinner.setSelection(0);
                selecting_spinner = 0;
            }
        });



        search_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_search.getText().clear();
                search_delete.setVisibility(View.INVISIBLE);
            }
        });

        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("search", "afterTextChanged: textChanged");
                selected_spinner = selecting_spinner;
                search_keyword = et_search.getText().toString();
            }
        });
        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search_keyword.length() != 0) {
                    do_search(search_keyword);
                }
            }
        });

        et_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    do_search(search_keyword);
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
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


    private void do_search(String search_keyword) {
        setClickable(false);
        setLoading(true);
        switch (selected_spinner){
            case RESEARCH_SELECT:
                searchPosts.clear();
                search_ResearchAdapter.notifyDataSetChanged();
                getSearchResearch(search_keyword);
                break;
            case VOTE_SELECT:
                searchVotes.clear();
                search_VoteAdapter.notifyDataSetChanged();
                getSearchVote(search_keyword);
                break;
            case CONTENT_SELECT:
                searchContents.clear();
                search_ContentAdapter.notifyDataSetChanged();
                getSearchContent(search_keyword);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void getSearchResearch (String searchQuery) {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/search";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("content", searchQuery);
            params.put("type", RESEARCH_SELECT);
            CustomJsonArrayRequest customJsonArrayRequest= new CustomJsonArrayRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                try {
//                    searchPosts = new ArrayList<Post>();
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
                        Integer visit = post.getInt("visit");
                        Double almost = post.getDouble("almost");

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
                            author_info = "";
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
                            getDone = true;

                        }
                        Post newPost = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, num_prize, comments, done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info, visit, almost);
                        searchPosts.add(newPost);
                        Log.d("어?", "getRandomPosts: " + newPost);
                    }
                    getDone = true;
                    search_ResearchAdapter.notifyDataSetChanged();
                    setLoading(false);
                    setClickable(true);



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                        error.printStackTrace();
                getDone = true;
            });
            customJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(customJsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
            getDone = true;

        }
    }
    private void getSearchVote(String searchQuery) {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/search";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("content", searchQuery);
            params.put("type", VOTE_SELECT);
            CustomJsonArrayRequest customJsonArrayRequest = new CustomJsonArrayRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                try {
                    JSONArray resultArr = new JSONArray(response.toString());
                    for (int i = 0; i < resultArr.length(); i++) {
                        JSONObject general = resultArr.getJSONObject(i);
                        String id = general.getString("_id");
                        String title = general.getString("title");
                        String author = general.getString("author");
                        Integer author_lvl = general.getInt("author_lvl");
                        String content = general.getString("content");
                        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");
                        Date date = null;
                        try {
                            date = fm.parse(general.getString("date"));
                        } catch (ParseException e) {
                            e.printStackTrace();
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
                        searchVotes.add(newGeneral);
                    }
                    search_VoteAdapter.notifyDataSetChanged();
                    setLoading(false);
                    setClickable(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                        error.printStackTrace();
            });
            customJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(customJsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void getSearchContent(String searchQuery) {
        Log.d("searchcheck", "getSearchContent:");
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/search";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("content", searchQuery);
            params.put("type", CONTENT_SELECT);
            CustomJsonArrayRequest customJsonArrayRequest = new CustomJsonArrayRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                try {
                    JSONArray responseContents = new JSONArray(response.toString());
                    for (int i = 0; i < responseContents.length(); i++) {
                        JSONObject responseContent = (JSONObject) responseContents.get(i);
                        String id = responseContent.getString("_id");
                        ArrayList<String> image_urls = new ArrayList<>();
                        JSONArray ja = responseContent.getJSONArray("image_urls");
                        for (int j = 0; j < ja.length(); j++) {
                            image_urls.add(ja.getString(j));
                        }
                        int likes = responseContent.getInt("likes");
                        int visit = responseContent.getInt("visit");
                        boolean hide = responseContent.getBoolean("hide");
                        String title = responseContent.getString("title");
                        String author = responseContent.getString("author");
                        String content = responseContent.getString("content");
                        SimpleDateFormat fm = new SimpleDateFormat(getResources().getString(R.string.date_format));
                        Date date = null;
                        try {
                            date = fm.parse(responseContent.getString("date"));
                        } catch (ParseException e) {
                            date = null;
                        }
                        ArrayList<String> liked_users = new ArrayList<>();
                        JSONArray ja2 = responseContent.getJSONArray("liked_users");;
                        for (int j = 0; j < ja2.length(); j++) {
                            liked_users.add(ja.getString(j));
                        }
                        ArrayList<ContentReply> comments = new ArrayList<>();
                        JSONArray responseComments = responseContent.getJSONArray("comments");
                        for (int j = 0; j < responseComments.length(); j++) {
                            JSONObject responseComment = (JSONObject) responseComments.get(j);
                            String _id = responseComment.getString("_id");
                            ArrayList<ContentReReply> contentReplies = new ArrayList<>();
                            JSONArray responseReplies = (JSONArray) responseComment.get("reply");
                            for (int k = 0; k < responseReplies.length(); k++) {
                                JSONObject responseReply = (JSONObject) responseReplies.get(k);
                                String __id = responseReply.getString("_id");
                                ArrayList<String> __reports = new ArrayList<>();
                                JSONArray ua = responseReply.getJSONArray("reports");
                                for (int u = 0; u < ua.length(); u++) {
                                    __reports.add(ua.getString(u));
                                }
                                boolean __hide = responseReply.getBoolean("hide");
                                ArrayList<String> __report_reasons = new ArrayList<>();
                                JSONArray ua2 = responseReply.getJSONArray("report_reasons");
                                for (int u = 0; u < ua2.length(); u++) {
                                    __report_reasons.add(ua.getString(u));
                                }
                                String __writer = responseReply.getString("writer");
                                String __writer_name = responseReply.getString("writer_name");
                                String __replyID = responseReply.getString("replyID");
                                String __content = responseReply.getString("content");
                                Date __date = null;
                                try {
                                    __date = fm.parse(responseReply.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                contentReplies.add(new ContentReReply(__id, __reports, __hide, __report_reasons, __writer, __writer_name, __replyID, __content, __date));

                            }
                            boolean _hide = responseComment.getBoolean("hide");
                            ArrayList<String> _report_reasons = new ArrayList<>();
                            JSONArray ka = responseComment.getJSONArray("report_reasons");
                            for (int k = 0; k < ka.length(); k++) {
                                _report_reasons.add(ka.getString(k));
                            }
                            ArrayList<String> _reports = new ArrayList<>();
                            JSONArray ka2 = responseComment.getJSONArray("reports");
                            for (int k = 0; k < ka2.length(); k++) {
                                _reports.add(ka2.getString(k));
                            }
                            String _writer = responseComment.getString("writer");
                            String _writer_name = responseComment.getString("writer_name");
                            Date _date = null;
                            try {
                                _date = fm.parse(responseComment.getString("date"));
                            } catch (ParseException e) {
                                _date = null;
                            }
                            String _content = responseComment.getString("content");

                            comments.add(new ContentReply(_id, contentReplies, _hide, _reports, _report_reasons, _writer, _writer_name, _date, _content));

                        }
                        searchContents.add(new Content(id, image_urls, likes, visit, hide, title, author, content, date, comments, liked_users));

                    }
                    search_ContentAdapter.notifyDataSetChanged();
                    setLoading(false);
                    setClickable(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                error.printStackTrace();
            });
            customJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(customJsonArrayRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void setClickable(boolean clickable) {
        ib_search.setEnabled(clickable);
    }
    private void setLoading(boolean show) {
        if (show) {
            rv_search.setVisibility(View.GONE);
            loadingPanel.setVisibility(View.VISIBLE);
        } else if (!show) {
            rv_search.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility(View.GONE);
        }
    }
    private class CustomJsonArrayRequest extends JsonRequest<JSONArray> {

        /**
         * Creates a new request.
         * @param method the HTTP method to use
         * @param url URL to fetch the JSON from
         * @param jsonRequest A {@link JSONObject} to post with the request. Null is allowed and
         *   indicates no parameters will be posted along with request.
         * @param listener Listener to receive the JSON response
         * @param errorListener Error listener, or null to ignore errors.
         */
        public CustomJsonArrayRequest(int method, String url, JSONObject jsonRequest,
                                      Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
            super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                    errorListener);
        }

        @Override
        protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data,
                        HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                return Response.success(new JSONArray(jsonString),
                        HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }
    }

    private void clear() {
        searchPosts.clear();
        searchVotes.clear();
        searchContents.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clear();
    }


}
