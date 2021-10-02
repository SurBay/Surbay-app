package com.pumasi.surbay.pages.mypage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.MypageRenewalFragment;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.VoteRecyclerViewAdapter;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyVoteActivity extends AppCompatActivity {

    private Context context;
    private TextView tv_user_vote_head;
    private ImageButton ib_back;
    private ImageButton ib_user_vote_query;
    private RecyclerView rv_user_vote;
    private int type;
    private VoteRecyclerViewAdapter voteRecyclerViewAdapter;
    private ArrayList<General> myVotes = new ArrayList<General>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vote);
        context = getApplicationContext();
        getSupportActionBar().hide();
        tv_user_vote_head = findViewById(R.id.tv_user_vote_head);
        ib_back = findViewById(R.id.ib_back);
        ib_user_vote_query = findViewById(R.id.ib_user_vote_query);
        rv_user_vote = findViewById(R.id.rv_user_vote);
        type = getIntent().getIntExtra("type", MypageRenewalFragment.UPLOADED_VOTE);
        if (type == MypageRenewalFragment.UPLOADED_VOTE) tv_user_vote_head.setText("업로드한 투표");
        else if (type == MypageRenewalFragment.PARTICIPATED_VOTE) tv_user_vote_head.setText("참여한 투표");

        voteRecyclerViewAdapter = new VoteRecyclerViewAdapter(myVotes, getApplicationContext());
        rv_user_vote.setAdapter(voteRecyclerViewAdapter);
        rv_user_vote.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        getMyVotes();

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
    @Override
    public void onBackPressed() {
        finish();
    }

    public void getMyVotes() {
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/user/myparticipate";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("type", type);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            CustomJsonArrayRequest customJsonArrayRequest = new CustomJsonArrayRequest(
                    Request.Method.PUT, requestURL, params, response -> {
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
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat fm = new SimpleDateFormat(context.getResources().getString(R.string.date_format));
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
                        myVotes.add(newGeneral);
                    }
                    voteRecyclerViewAdapter.setItem(myVotes);
                    voteRecyclerViewAdapter.notifyDataSetChanged();

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