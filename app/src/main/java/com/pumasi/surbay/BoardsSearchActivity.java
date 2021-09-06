package com.pumasi.surbay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.pumasi.surbay.adapter.GeneralListViewAdapter;
import com.pumasi.surbay.adapter.PostListViewAdapter;
import com.pumasi.surbay.adapter.PostRecyclerViewAdapter;
import com.pumasi.surbay.adapter.SurveyTipListViewAdapter;
import com.pumasi.surbay.adapter.TipRecyclerViewAdapter;
import com.pumasi.surbay.adapter.VoteRecyclerViewAdapter;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;
import com.pumasi.surbay.pages.boardpage.TipdetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class BoardsSearchActivity extends AppCompatActivity {
    static final int LIKED = 5;
    static final int DISLIKED = 4;

    public final int RESEARCH_SELECT = 0;
    public final int VOTE_SELECT = 1;
    public final int TIP_SELECT = 2;


    static final int DO_SURVEY = 2;
    static final int DONE = 1;
    static final int LIKE_SURVEY = 3;

    final String[] spinner_context = {"리서치", "투표", "설문 Tip"};
    RecyclerView rv_search;
    EditText search_editview;
    ImageButton search_delete;
    Spinner search_spinner;
    ImageButton back;
    private RelativeLayout loadingPanel;

    private ImageButton ib_search;
    static ArrayList<Post> searchPosts = new ArrayList<Post>();
    static ArrayList<General> searchVotes = new ArrayList<General>();
    static ArrayList<Surveytip> searchTips = new ArrayList<Surveytip>();

    private PostRecyclerViewAdapter search_ResearchAdapter;
    private VoteRecyclerViewAdapter search_VoteAdapter;
    private TipRecyclerViewAdapter search_TipAdapter;

    int selecting_spinner;
    int selected_spinner;
    private boolean getDone = true;
    private String search_keyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boards_search);

        this.getSupportActionBar().hide();

        loadingPanel = findViewById(R.id.loadingPanel);

        search_editview = (EditText)findViewById(R.id.search_edittext);
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
                switch (position) {
                    case RESEARCH_SELECT:
                        Log.d("search", "onItemSelected: Research Selected");
                        search_ResearchAdapter = new PostRecyclerViewAdapter(searchPosts, getApplicationContext());
                        rv_search.setAdapter(search_ResearchAdapter);
                        rv_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        search_ResearchAdapter.setOnItemClickListener(new PostRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {

                            }
                        });
                        break;
                    case VOTE_SELECT:
                        Log.d("search", "onItemSelected: Votes Selected");
                        search_VoteAdapter = new VoteRecyclerViewAdapter(searchVotes, getApplicationContext());
                        rv_search.setAdapter(search_VoteAdapter);
                        rv_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        search_VoteAdapter.setOnItemClickListener(new VoteRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {

                            }
                        });
                        break;
                    case TIP_SELECT:
                        Log.d("search", "onItemSelected: Tips Selected");
                        search_TipAdapter = new TipRecyclerViewAdapter(searchTips, getApplicationContext());
                        rv_search.setAdapter(search_TipAdapter);
                        rv_search.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
                        search_TipAdapter.setOnItemClickListener(new TipRecyclerViewAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(View v, int position) {
                                Surveytip surveytip = (Surveytip) search_TipAdapter.getItem(position);
                                Intent intent = new Intent(getApplicationContext(), TipdetailActivity.class);
                                intent.putExtra("post", surveytip);
                                intent.putExtra("position", position);
                                startActivityForResult(intent, LIKE_SURVEY);
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
                search_editview.getText().clear();
                search_delete.setVisibility(View.INVISIBLE);
            }
        });

        search_editview.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("search", "afterTextChanged: textChanged");
                selected_spinner = selecting_spinner;
                search_keyword = search_editview.getText().toString();
//                searchPosts.clear();
//                search_ResearchAdapter.notifyDataSetChanged();
//                if (search_keyword.length() > 0 ){
//
//                    search_delete.setVisibility(View.VISIBLE);
//                    if (getDone) {
//                        getDone = false;
//                        do_search(search_keyword);
//                    }
//                };
            }
        });
        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    do_search(search_keyword);
//                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(search_editview.getWindowToken(), 0);
            }
        });

//        rv_search.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                //클릭시 글 확대 기능 추가 예정
//                if (selected_spinner == SURBAY_SELECT){
//                    Post item = (Post)search_SurveyAdapter.getItem(position);
//                    Log.d("search", item.toString());
//                    Intent intent = new Intent(BoardsSearchActivity.this, PostDetailActivity.class);
//                    intent.putExtra("post", item);
//                    intent.putExtra("position", position);
//                    Log.d("search", item.toString());
//                    startActivityForResult(intent, DO_SURVEY);
//                }
//            }
//        });
    }
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(getCurrentFocus()!=null)imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
//        return true;
//    }

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
            case TIP_SELECT:
                searchTips.clear();
                search_TipAdapter.notifyDataSetChanged();
                for (Surveytip tip : MainActivity.surveytipArrayList){
                    if ((tip.getContent().contains(search_keyword)) || (tip.getTitle().contains(search_keyword))){
                        searchTips.add(tip);
                    }
                    search_TipAdapter.notifyDataSetChanged();
                    setClickable(true);
                    setLoading(false);
                }
//
//                search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        General item =  (General) search_GeneralAdapter.getItem(position);
//                        Log.d("search", item.toString());
//                        Intent intent = new Intent(BoardsSearchActivity.this, GeneralDetailActivity.class);
//                        intent.putExtra("general", item);
//                        intent.putExtra("position", position);
//                        Log.d("search", item.toString());
//                        startActivity(intent);
//                    }
//                });
//                break;
//            case TIP_SELECT:
//                search_list_tip.clear();
//                search_TipAdapter = new SurveyTipListViewAdapter(search_list_tip);
//                search_listview.setAdapter(search_TipAdapter);
//
//
//                search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        if (selected_spinner == TIP_SELECT){
//                            Surveytip item = (Surveytip)search_TipAdapter.getItem(position);
//                            Log.d("search", item.toString());
//                            Intent intent = new Intent(BoardsSearchActivity.this, TipdetailActivity.class);
//                            intent.putExtra("post", item);
//                            intent.putExtra("position", position);
//                            Log.d("search", item.toString());
//                            startActivityForResult(intent, LIKE_SURVEY);
//                        }
//                    }
//                });
//                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case DO_SURVEY:
//                Log.d("result code is", ""+resultCode);
//                switch (resultCode){
//                    case(DONE):
//                        int position = data.getIntExtra("position", -1);
//                        int newParticipants = data.getIntExtra("participants", -1);
//                        search_SurveyAdapter.updateParticipants(position, newParticipants);
//                        Post item = (Post) search_SurveyAdapter.getItem(position);
//
//                        search_listview.setAdapter(search_SurveyAdapter);
//                        Log.d("new participant", "new is: " + item.getParticipants());
//                        return;
//                    default:
//                        return;
//                }
//            case LIKE_SURVEY:
//                int pos = data.getIntExtra("position", -1);
//                Surveytip surveytip = data.getParcelableExtra("surveyTip");
//                search_list_tip.set(pos, surveytip);
//                MainActivity.surveytipArrayList.set(pos, surveytip);
//                try {
//                    MainActivity.getSurveytips();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                search_TipAdapter = new SurveyTipListViewAdapter(search_list_tip);
//                search_TipAdapter.notifyDataSetChanged();
//                search_listview.setAdapter(search_TipAdapter);
//                break;
//            default:
//                return;
//        }

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
                                    Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide, writer_name);
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
                                    Reply re = new Reply(reid, writer, contetn, datereply,replyreports,replyhide, writer_name);
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
}
