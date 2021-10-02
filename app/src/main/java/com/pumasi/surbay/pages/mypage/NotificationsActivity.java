package com.pumasi.surbay.pages.mypage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.NotificationsAdapter;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView notificationsRecyclerView;
    NotificationsAdapter notificationsAdapter;
    private Context context;
    private ImageButton ib_back;
    private ArrayList<Notification> notifications = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().hide();

        notifications.addAll(UserPersonalInfo.notifications);
        Collections.reverse(notifications);
        Log.d("notifications", "onCreate: " + UserPersonalInfo.notifications);
        context = getApplicationContext();
        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        notificationsRecyclerView = findViewById(R.id.notification_recyclerview);
        notificationsAdapter = new NotificationsAdapter(NotificationsActivity.this, notifications);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(NotificationsActivity.this, RecyclerView.VERTICAL, false));
        notificationsRecyclerView.setAdapter(notificationsAdapter);
        notificationsAdapter.setOnItemClickListener(new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Notification notification = (Notification)notificationsAdapter.getItem(position);
                if(notification.getPost_type()==0){
                    getPost(notification.getPost_id(), 0);

                }else if(notification.getPost_type()==1){
                    getGeneral(notification.getPost_id(), 0);
                } else {
                    Intent intent = new Intent(context, IGotGifts.class);
                    startActivity(intent);
                }
            }
        });


    }
    public void getPost(String post_object_id, int position) {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/posts/getpost/" + post_object_id;
            RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.mContext);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    JSONObject post = new JSONObject(response.toString());
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
                    Log.d("newPost", "getInfinityPosts: " + newPost);
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("post", newPost);
                    intent.putExtra("position", position);
                    startActivity(intent);

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
    private void getGeneral(String general_object_id, int position) {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals/getpost/" + general_object_id;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.GET, requestURL, null, response -> {
                try {
                    JSONObject general = new JSONObject(response.toString());
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

                        Intent intent = new Intent(context, GeneralDetailActivity.class);
                        intent.putExtra("general", newGeneral);
                        intent.putExtra("position", position);
                        startActivity(intent);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
