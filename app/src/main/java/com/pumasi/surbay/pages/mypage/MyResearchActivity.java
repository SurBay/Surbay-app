package com.pumasi.surbay.pages.mypage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.pumasi.surbay.adapter.PostRecyclerViewAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyResearchActivity extends AppCompatActivity {
    private Context context;
    private TextView tv_user_research_head;
    private ImageButton ib_back;
    private ImageButton ib_user_research_query;
    private RecyclerView rv_user_research;
    private int type;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;
    private ArrayList<Post> myPosts = new ArrayList<Post>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_research);
        getSupportActionBar().hide();
        context = getApplicationContext();
        tv_user_research_head = findViewById(R.id.tv_user_research_head);
        ib_back = findViewById(R.id.ib_back);
        ib_user_research_query = findViewById(R.id.ib_user_research_query);

        rv_user_research = findViewById(R.id.rv_user_research);
        type = getIntent().getIntExtra("type", MypageRenewalFragment.UPLOADED_RESEARCH);
        if (type == MypageRenewalFragment.UPLOADED_RESEARCH) tv_user_research_head.setText("업로드한 리서치");
        else if (type == MypageRenewalFragment.PARTICIPATED_RESEARCH) tv_user_research_head.setText("참여한 리서치");
        postRecyclerViewAdapter = new PostRecyclerViewAdapter(myPosts, getApplicationContext());
        rv_user_research.setAdapter(postRecyclerViewAdapter);
        rv_user_research.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        getMyResearches();
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

    private void getMyResearches() {
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
                                                String content_ = reReply.getString("content");
                                                Date date_ = fm.parse(reReply.getString("date"));
                                                String replyID_ = reReply.getString("replyID");

                                                ReReply newReReply = new ReReply(id_, reports_, report_reasons_, hide_, writer_, content_, date_, replyID_);
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
                        myPosts.add(newPost);
                    }
                    postRecyclerViewAdapter.setItem(myPosts);
                    postRecyclerViewAdapter.notifyDataSetChanged();


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