package com.pumasi.surbay.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.ContentDetailActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Content;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.MyCoupon;
import com.pumasi.surbay.classfile.Notification;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("SimpleDateFormat")
public class ServerTransport extends AppCompatActivity {

    SimpleDateFormat std_fm = new SimpleDateFormat("yyyy-MM-dd\'T\'kk:mm:ss.SSS");

    private Context context;
    public boolean getPersonalInfoDone = false;
    public Post result_research = null;
    public General result_vote = null;
    public Content result_content = null;

    private boolean isClickable = true;
    private final int RESEARCH = 0;
    private final int VOTE = 1;
    private final int CONTENT = 2;

    private final int GET_INTERNET_ERROR = 100;
    public final int DEFAULT_NETWORK_TRY = 20;
    private getHandler getHandler = new getHandler();

    public ServerTransport(Context context) {
        this.context = context;
    }

    private static void onErrorResponse(VolleyError error) {
        Log.d("boolean_done", "getPersonalInfo: error");
    }
    public void getPersonalInfo() {
        this.getPersonalInfoDone = false;
        String token = UserPersonalInfo.token;
        if (token == null) {
            return;
        }
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/personalinfo";
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject rawUserInfo = new JSONObject(response.toString());
                            JSONObject updatedUserInfo = rawUserInfo.getJSONObject("data");

                            UserPersonalInfo.userID = updatedUserInfo.getString("userID");
                            UserPersonalInfo.name = updatedUserInfo.getString("name");
                            UserPersonalInfo.email = updatedUserInfo.getString("email");
                            UserPersonalInfo.userPassword = updatedUserInfo.getString("userPassword");
                            UserPersonalInfo.points = updatedUserInfo.getInt("points");
                            UserPersonalInfo.level = updatedUserInfo.getInt("level");
                            UserPersonalInfo.gender = updatedUserInfo.getInt("gender");
                            UserPersonalInfo.yearBirth = updatedUserInfo.getInt("yearBirth");
                            Log.d("getUserInfo", "getPersonalInfo: " + UserPersonalInfo.userID + UserPersonalInfo.name + UserPersonalInfo.email + UserPersonalInfo.userPassword + UserPersonalInfo.points + UserPersonalInfo.level + UserPersonalInfo.gender + UserPersonalInfo.yearBirth);
                            try {
                                UserPersonalInfo.prize_check = updatedUserInfo.getInt("prize_check");
                            } catch (Exception e) {
                                UserPersonalInfo.prize_check = 0;
                            }
                            try {
                                UserPersonalInfo.notificationAllow = updatedUserInfo.getBoolean("notificationAllow");
                            } catch (Exception e) {
                                UserPersonalInfo.notificationAllow = false;
                            }
                            JSONArray my_postsJSONArray = updatedUserInfo.getJSONArray("my_posts");
                            JSONArray participationsJSONArray = updatedUserInfo.getJSONArray("participations");
                            JSONArray my_generalsJSONArray = updatedUserInfo.getJSONArray("my_generals");
                            JSONArray general_participationsJSONArray = updatedUserInfo.getJSONArray("general_participations");

                            ArrayList<String> my_posts = new ArrayList<>();
                            ArrayList<String> participations = new ArrayList<>();
                            ArrayList<String> my_generals = new ArrayList<>();
                            ArrayList<String> general_participations = new ArrayList<>();
                            for (int i = 0; i < my_postsJSONArray.length(); i++) {
                                my_posts.add(my_postsJSONArray.getString(i));
                            }
                            for (int i = 0; i < participationsJSONArray.length(); i++) {
                                participations.add(participationsJSONArray.getString(i));
                            }
                            for (int i = 0; i < my_generalsJSONArray.length(); i++) {
                                my_generals.add(my_generalsJSONArray.getString(i));
                            }
                            for (int i = 0; i < general_participationsJSONArray.length(); i++) {
                                general_participations.add(general_participationsJSONArray.getString(i));
                            }
                            UserPersonalInfo.my_posts = my_posts;
                            UserPersonalInfo.participations = participations;
                            UserPersonalInfo.my_generals = my_generals;
                            UserPersonalInfo.general_participations = general_participations;

                            JSONArray blocked_usersJSONArray = updatedUserInfo.getJSONArray("blocked_users");
                            JSONArray prizesJSONArray = updatedUserInfo.getJSONArray("prizes");

                            ArrayList<String> blocked_users = new ArrayList<>();
                            ArrayList<String> prizes = new ArrayList<>();
                            for (int i = 0; i < blocked_usersJSONArray.length(); i++) {
                                blocked_users.add(blocked_usersJSONArray.getString(i));
                            }
                            for (int i = 0; i < prizesJSONArray.length(); i++) {
                                prizes.add(prizesJSONArray.getString(i));
                            }
                            UserPersonalInfo.blocked_users = blocked_users;
                            UserPersonalInfo.prizes = prizes;

                            ArrayList<Notification> notifications = new ArrayList<>();
                            JSONArray notificationsJSON = updatedUserInfo.getJSONArray("notifications");
                            for (int i = 0; i < notificationsJSON.length(); i++) {
                                JSONObject notificationJSON = notificationsJSON.getJSONObject(i);
                                String title_notification = notificationJSON.getString("title");
                                String content_notification = notificationJSON.getString("content");
                                String post_id_notification = notificationJSON.getString("post_id");
                                Date date_notification = null;
                                try {
                                    date_notification = std_fm.parse(notificationJSON.getString("date"));
                                } catch (Exception e) {
                                    Log.d("here is the error", "getPersonalInfo: ");
                                    e.printStackTrace();
                                }
                                Log.d("case_s", "getPersonalInfo: " + date_notification);
                                int post_type_notification = notificationJSON.getInt("post_type");
                                notifications.add(new Notification(title_notification, content_notification, post_id_notification, date_notification, post_type_notification));
                            }
                            UserPersonalInfo.notifications = notifications;

                            ArrayList<MyCoupon> coupons = new ArrayList<>();
                            JSONArray couponsJSON = updatedUserInfo.getJSONArray("coupons");
                            for (int i = 0; i < couponsJSON.length(); i++) {
                                JSONObject couponJSON = couponsJSON.getJSONObject(i);
                                String id_coupon = couponJSON.getString("_id");
                                String coupon_id_coupon = couponJSON.getString("coupon_id");
                                boolean used_coupon = couponJSON.getBoolean("used");
                                Date used_date_coupon = null;
                                try {
                                    used_date_coupon = std_fm.parse(couponJSON.getString("used_date"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Date due_date_coupon = null;
                                try {
                                    due_date_coupon = std_fm.parse(couponJSON.getString("due_date"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                String date_coupon = couponJSON.getString("date");
                                ArrayList<String> image_urls_coupon = new ArrayList<>();
                                JSONArray image_urls_couponJSONArray = couponJSON.getJSONArray("image_urls");
                                for (int j = 0; j < image_urls_couponJSONArray.length(); j++) {
                                    image_urls_coupon.add(image_urls_couponJSONArray.getString(j));
                                }

                                String store_coupon = couponJSON.getString("store");
                                String menu_coupon = couponJSON.getString("menu");
                                String content_coupon = couponJSON.getString("content");
                                coupons.add(new MyCoupon(id_coupon, coupon_id_coupon, used_coupon, used_date_coupon, date_coupon, due_date_coupon, image_urls_coupon, store_coupon, menu_coupon, content_coupon));
                            }
                            UserPersonalInfo.coupons = coupons;
                            this.getPersonalInfoDone = true;
                        } catch (JSONException e) {}
                    }, ServerTransport::onErrorResponse) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }};
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Log.d("boolean_done", "getPersonalInfo: error");
            e.printStackTrace();
        }
    }
    public void getResearch(String post_object_id) {
        result_research = null;
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/posts/getpost/" + post_object_id;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject researchJSON = new JSONObject(response.toString());
                            String id = researchJSON.getString("_id");
                            String title = researchJSON.getString("title");
                            String author = researchJSON.getString("author");
                            Integer author_lvl = researchJSON.getInt("author_lvl");
                            String content = researchJSON.getString("content");
                            Integer participants = researchJSON.getInt("participants");
                            Integer goal_participants = researchJSON.getInt("goal_participants");
                            String url = researchJSON.getString("url");
                            Date date = null;
                            try {
                                date = std_fm.parse(researchJSON.getString("date"));
                            } catch (Exception e) {
                            }
                            Date deadline = null;
                            try {
                                deadline = std_fm.parse(researchJSON.getString("deadline"));
                            } catch (Exception e) {
                            }
                            Boolean with_prize = researchJSON.getBoolean("with_prize");
                            Integer est_time = researchJSON.getInt("est_time");
                            String target = researchJSON.getString("target");
                            Boolean done = researchJSON.getBoolean("done");
                            Boolean hide = researchJSON.getBoolean("hide");
                            Integer extended = researchJSON.getInt("extended");
                            String author_userid = researchJSON.getString("author_userid");
                            String prize = "none";
                            Integer num_prize = 0;
                            if (with_prize) {
                                prize = researchJSON.getString("prize");
                                num_prize = researchJSON.getInt("num_prize");
                            }
                            Integer pinned = 0;
                            try {
                                pinned = researchJSON.getInt("pinned");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Boolean annonymous = false;
                            try {
                                annonymous = researchJSON.getBoolean("annonymous");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String author_info = "";
                            try {
                                author_info = researchJSON.getString("author_info");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            JSONArray participants_useridsJSON = researchJSON.getJSONArray("participants_userids");
                            ArrayList<String> participants_userids = new ArrayList<>();
                            for (int i = 0; i < participants_useridsJSON.length(); i++) {
                                participants_userids.add(participants_useridsJSON.getString(i));
                            }
                            JSONArray reportsJSON = researchJSON.getJSONArray("reports");
                            ArrayList<String> reports = new ArrayList<>();
                            for (int i = 0; i < reportsJSON.length(); i++) {
                                reports.add(reportsJSON.getString(i));
                            }
                            Integer visit = 0;
                            try {
                                visit = researchJSON.getInt("visit");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Double almost = 0.;
                            try {
                                almost = researchJSON.getDouble("almost");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            JSONArray commentsJSON = researchJSON.getJSONArray("comments");
                            ArrayList<Reply> comments = new ArrayList<>();
                            for (int i = 0; i < commentsJSON.length(); i++) {
                                JSONObject comment = commentsJSON.getJSONObject(i);
                                String id_comment = comment.getString("_id");
                                String writer_comment = comment.getString("writer");
                                String content_comment = comment.getString("content");
                                Date date_comment = null;
                                try {
                                    date_comment = std_fm.parse(comment.getString("date"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Boolean hide_comment = comment.getBoolean("hide");
                                JSONArray reports_commentJSON = comment.getJSONArray("reports");
                                ArrayList<String> reports_comment = new ArrayList<>();
                                for (int j = 0; j < reports_commentJSON.length(); j++) {
                                    reports_comment.add(reports_commentJSON.getString(j));
                                }
                                String write_name_comment = "익명";
                                try {
                                    write_name_comment = comment.getString("writer_name");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                JSONArray reReplies_commentJSON = comment.getJSONArray("reply");
                                ArrayList<ReReply> reReplies_comment = new ArrayList<>();
                                for (int j = 0; j < reReplies_commentJSON.length(); j++) {
                                    JSONObject reReply = reReplies_commentJSON.getJSONObject(j);
                                    String id_reReply = reReply.getString("_id");
                                    JSONArray reports_reReplyJSON = reReply.getJSONArray("reports");
                                    ArrayList<String> reports_reReply = new ArrayList<>();
                                    for (int k = 0; k < reports_reReplyJSON.length(); k++) {
                                        reports_reReply.add(reports_reReplyJSON.getString(i));
                                    }
                                    JSONArray report_reasons_reReplyJSON = reReply.getJSONArray("report_reasons");
                                    ArrayList<String> report_reasons_reReply = new ArrayList<>();
                                    for (int k = 0; k < report_reasons_reReplyJSON.length(); k++) {
                                        report_reasons_reReply.add(report_reasons_reReplyJSON.getString(k));
                                    }
                                    Boolean hide_reReply = reReply.getBoolean("hide");
                                    String writer_reReply = reReply.getString("writer");
                                    String writer_name_reReply = "익명";
                                    try {
                                        writer_name_reReply = reReply.getString("writer_name");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String content_reReply = reReply.getString("content");
                                    Date date_reReply = null;
                                    try {
                                        date_reReply = std_fm.parse(reReply.getString("date"));
                                    } catch (Exception e) {
                                        date_reReply = null;
                                    }
                                    String replyID_reReply = reReply.getString("replyID");

                                    reReplies_comment.add(new ReReply(id_reReply, reports_reReply, report_reasons_reReply, hide_reReply, writer_reReply, writer_name_reReply, content_reReply, date_reReply, replyID_reReply));
                                }
                                comments.add(new Reply(id_comment, writer_comment, content_comment, date_comment, reports_comment, hide_comment, write_name_comment, reReplies_comment));
                            }
                            Post research = new Post(id, title, author, author_lvl, content, participants, goal_participants, url, date, deadline, with_prize, prize, est_time, target, num_prize, comments,
                                    done, extended, participants_userids, reports, hide, author_userid, pinned, annonymous, author_info, visit, almost);
                            result_research = research;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, ServerTransport::onErrorResponse);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getVote(String vote_object_id) {
        result_vote = null;
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/generals/getpost/" + vote_object_id;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject voteJSON = new JSONObject(response.toString());
                            String id = voteJSON.getString("_id");
                            String title = voteJSON.getString("title");
                            String author = voteJSON.getString("author");
                            Integer author_lvl = voteJSON.getInt("author_lvl");
                            String content = voteJSON.getString("content");
                            Date date = null;
                            try {
                                date = std_fm.parse(voteJSON.getString("date"));
                            } catch (ParseException e) {
                                date = null;
                            }
                            Date deadline = null;
                            try {
                                deadline = std_fm.parse(voteJSON.getString("deadline"));
                            } catch (ParseException e) {
                                deadline = null;
                            }
                            JSONArray commentsJSON = voteJSON.getJSONArray("comments");
                            ArrayList<Reply> comments = new ArrayList<>();
                            for (int i = 0; i < commentsJSON.length(); i++) {
                                JSONObject comment = commentsJSON.getJSONObject(i);
                                String id_comment = comment.getString("_id");
                                String writer_comment = comment.getString("writer");
                                String content_comment = comment.getString("content");
                                Date date_comment = null;
                                try {
                                    date_comment = std_fm.parse(comment.getString("date"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Boolean hide_comment = comment.getBoolean("hide");
                                JSONArray reports_commentJSON = comment.getJSONArray("reports");
                                ArrayList<String> reports_comment = new ArrayList<>();
                                for (int j = 0; j < reports_commentJSON.length(); j++) {
                                    reports_comment.add(reports_commentJSON.getString(j));
                                }
                                String write_name_comment = comment.getString("writer_name");
                                try {
                                    write_name_comment = comment.getString("writer_name");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                JSONArray reReplies_commentJSON = comment.getJSONArray("reply");
                                ArrayList<ReReply> reReplies_comment = new ArrayList<>();
                                for (int j = 0; j < reReplies_commentJSON.length(); j++) {
                                    JSONObject reReply = reReplies_commentJSON.getJSONObject(j);
                                    String id_reReply = reReply.getString("_id");
                                    JSONArray reports_reReplyJSON = reReply.getJSONArray("reports");
                                    ArrayList<String> reports_reReply = new ArrayList<>();
                                    for (int k = 0; k < reports_reReplyJSON.length(); k++) {
                                        reports_reReply.add(reports_reReplyJSON.getString(i));
                                    }
                                    JSONArray report_reasons_reReplyJSON = reReply.getJSONArray("report_reasons");
                                    ArrayList<String> report_reasons_reReply = new ArrayList<>();
                                    for (int k = 0; k < report_reasons_reReplyJSON.length(); k++) {
                                        report_reasons_reReply.add(report_reasons_reReplyJSON.getString(k));
                                    }
                                    Boolean hide_reReply = reReply.getBoolean("hide");
                                    String writer_reReply = reReply.getString("writer");
                                    String writer_name_reReply = "익명";
                                    try {
                                        writer_name_reReply = reReply.getString("writer_name");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    String content_reReply = reReply.getString("content");
                                    Date date_reReply = null;
                                    try {
                                        date_reReply = std_fm.parse(reReply.getString("date"));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String replyID_reReply = reReply.getString("replyID");

                                    reReplies_comment.add(new ReReply(id_reReply, reports_reReply, report_reasons_reReply, hide_reReply, writer_reReply, writer_name_reReply, content_reReply, date_reReply, replyID_reReply));
                                }
                                comments.add(new Reply(id_comment, writer_comment, content_comment, date_comment, reports_comment, hide_comment, write_name_comment, reReplies_comment));
                            }
                            Boolean done = voteJSON.getBoolean("done");
                            String author_userid = voteJSON.getString("author_userid");

                            JSONArray reportsJSON = voteJSON.getJSONArray("reports");
                            ArrayList<String> reports = new ArrayList<>();
                            for (int i = 0; i < reportsJSON.length(); i++) {
                                reports.add(reportsJSON.getString(i));
                            }
                            Boolean multi_response = voteJSON.getBoolean("multi_response");
                            Integer participants = voteJSON.getInt("participants");
                            JSONArray participants_useridsJSON = voteJSON.getJSONArray("participants_userids");
                            ArrayList<String> participants_userids = new ArrayList<>();
                            for (int i = 0; i < participants_useridsJSON.length(); i++) {
                                participants_userids.add(participants_useridsJSON.getString(i));
                            }
                            Boolean with_image = voteJSON.getBoolean("with_image");

                            JSONArray pollsJSON = voteJSON.getJSONArray("polls");
                            ArrayList<Poll> polls = new ArrayList<>();
                            for (int i = 0; i < pollsJSON.length(); i++) {
                                JSONObject pollJSON = pollsJSON.getJSONObject(i);
                                String id_poll = pollJSON.getString("_id");
                                String content_poll = pollJSON.getString("content");
                                JSONArray participants_userids_pollJSON = pollJSON.getJSONArray("participants_userids");
                                ArrayList<String> participants_userids_poll = new ArrayList<>();
                                for (int j = 0; j < participants_userids_pollJSON.length(); j++) {
                                    participants_userids_poll.add(participants_userids_pollJSON.getString(j));
                                }
                                String image_poll = null;
                                try {
                                    image_poll = pollJSON.getString("image");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                polls.add(new Poll(id_poll, content_poll, participants_userids_poll, image_poll));
                            }
                            JSONArray liked_usersJSON = voteJSON.getJSONArray("liked_users");
                            ArrayList<String> liked_users = new ArrayList<>();
                            for (int i = 0; i< liked_usersJSON.length(); i++) {
                                liked_users.add(liked_usersJSON.getString(i));
                            }
                            Integer likes = voteJSON.getInt("likes");
                            Boolean hide = voteJSON.getBoolean("hide");
                            Boolean special = voteJSON.getBoolean("special");
                            Integer visit = voteJSON.getInt("visit");

                            General vote = new General(id, title, author, author_lvl, content, date, deadline, comments, done, author_userid, reports, multi_response, participants, participants_userids, with_image, polls, liked_users, likes, hide, special, visit);
                            result_vote = vote;
                            Log.d("see_everything", "getVote: " + id+ title+ author+ author_lvl+ content+ date+ deadline+ comments+ done+ author_userid+ reports+ multi_response+ participants+ participants_userids+ with_image+ polls+ liked_users+ likes+ hide+ special+ visit);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, ServerTransport::onErrorResponse);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getContent(String content_object_id) {
        result_content = null;
        try {
            String requestURL = context.getResources().getString(R.string.server) + "/api/content/getcontent/" + content_object_id;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            JSONObject contentJSON = new JSONObject(response.toString());
                            String id = contentJSON.getString("_id");

                            JSONArray image_urlsJSON = contentJSON.getJSONArray("image_urls");
                            ArrayList<String> image_urls = new ArrayList<>();
                            for (int i = 0; i < image_urlsJSON.length(); i++) {
                                image_urls.add(image_urlsJSON.getString(i));
                            }
                            int likes = contentJSON.getInt("likes");
                            int visit = contentJSON.getInt("visit");
                            boolean hide = contentJSON.getBoolean("hide");
                            String title = contentJSON.getString("title");
                            String author = contentJSON.getString("author");
                            String content_ = contentJSON.getString("content");
                            Date date = null;
                            try {
                                date = std_fm.parse(contentJSON.getString("date"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            JSONArray commentsJSON = contentJSON.getJSONArray("comments");
                            ArrayList<ContentReply> comments = new ArrayList<>();
                            for (int i = 0; i < commentsJSON.length(); i++) {
                                JSONObject commentJSON = commentsJSON.getJSONObject(i);
                                String id_comment = commentJSON.getString("_id");
                                JSONArray replies_commentJSON = commentJSON.getJSONArray("reply");
                                ArrayList<ContentReReply> replies_comment = new ArrayList<>();
                                for (int j = 0; j < replies_commentJSON.length(); j++) {
                                    JSONObject replyJSON = replies_commentJSON.getJSONObject(j);
                                    String id_reply = replyJSON.getString("_id");
                                    JSONArray reports_replyJSON = replyJSON.getJSONArray("reports");
                                    ArrayList<String> reports_reply = new ArrayList<>();
                                    for (int k = 0; k < reports_replyJSON.length(); k++) {
                                        reports_reply.add(reports_replyJSON.getString(k));
                                    }
                                    Boolean hide_reply = replyJSON.getBoolean("hide");
                                    JSONArray report_reasons_replyJSON = replyJSON.getJSONArray("report_reasons");
                                    ArrayList<String> report_reasons_reply = new ArrayList<>();
                                    for (int k = 0; k < report_reasons_replyJSON.length(); k++) {
                                        report_reasons_reply.add(report_reasons_replyJSON.getString(k));
                                    }
                                    String writer_reply = replyJSON.getString("writer");
                                    String writer_name_reply = replyJSON.getString("writer_name");
                                    String replyID_reply = replyJSON.getString("replyID");
                                    String content_reply = replyJSON.getString("content");
                                    Date date_reply = null;
                                    try {
                                        date_reply = std_fm.parse(replyJSON.getString("date"));
                                    } catch (Exception e) {
                                        date_reply = null;
                                    }
                                    replies_comment.add(new ContentReReply(id_reply, reports_reply, hide_reply, report_reasons_reply, writer_reply, writer_name_reply, replyID_reply, content_reply, date_reply));
                                }
                                Boolean hide_comment = commentJSON.getBoolean("hide");
                                JSONArray reports_commentJSON = commentJSON.getJSONArray("reports");
                                ArrayList<String> reports_comment = new ArrayList<>();
                                for (int j = 0; j < reports_commentJSON.length(); j++) {
                                    reports_comment.add(reports_commentJSON.getString(j));
                                }
                                JSONArray report_reasons_commentJSON = commentJSON.getJSONArray("report_reasons");
                                ArrayList<String> report_reasons_comment = new ArrayList<>();
                                for (int j = 0; j < report_reasons_commentJSON.length(); j++) {
                                    report_reasons_comment.add(report_reasons_commentJSON.getString(j));
                                }
                                String write_comment = commentJSON.getString("writer");
                                String write_name_comment = commentJSON.getString("writer_name");
                                Date date_comment = null;
                                try {
                                    date_comment = std_fm.parse(commentJSON.getString("date"));
                                } catch (Exception e) {
                                    date_comment = null;
                                }
                                String content_comment = commentJSON.getString("content");
                                comments.add(new ContentReply(id_comment, replies_comment, hide_comment, reports_comment, report_reasons_comment, write_comment, write_name_comment, date_comment, content_comment));
                            }

                            JSONArray liked_usersJSON = contentJSON.getJSONArray("liked_users");
                            ArrayList<String> liked_users = new ArrayList<>();
                            for (int i = 0; i < liked_usersJSON.length(); i++) {
                                liked_users.add(liked_usersJSON.getString(i));
                            }
                            Content content = new Content(id, image_urls, likes, visit, hide, title, author, content_, date, comments, liked_users);
                            result_content = content;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, ServerTransport::onErrorResponse);
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void getExecute(String object_id, int type, int work) {
        if (isClickable) {
            isClickable = false;
            new getThread(object_id, type, work).start();
        }
    }
    public class getThread extends Thread {
        String object_id;
        int type;
        int work;
        public getThread(String object_id, int type, int work) {
            this.object_id = object_id;
            this.type = type;
            this.work = work;
        }
        @Override
        public void run() {
            int counter = 0;
            switch (type) {
                case RESEARCH:
                    getResearch(object_id);
                    while (result_research == null && counter != DEFAULT_NETWORK_TRY) {
                        try {
                            counter += 1;
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (counter != DEFAULT_NETWORK_TRY) {
                        Intent intent = new Intent(context, PostDetailActivity.class);
                        intent.putExtra("position", 0);
                        intent.putExtra("post", result_research);
                        intent.putExtra("work", work);
                        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        getHandler.sendEmptyMessage(GET_INTERNET_ERROR);
                    }
                    isClickable = true;
                    break;
                case VOTE:
                    getVote(object_id);
                    while (result_vote == null && counter != DEFAULT_NETWORK_TRY) {
                        try {
                            counter += 1;
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (counter != DEFAULT_NETWORK_TRY) {
                        Intent intent = new Intent(context, GeneralDetailActivity.class);
                        intent.putExtra("position", 0);
                        intent.putExtra("general", result_vote);
                        intent.putExtra("work", work);
                        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        getHandler.sendEmptyMessage(GET_INTERNET_ERROR);
                    }
                    isClickable = true;
                    break;
                case CONTENT:
                    getContent(object_id);
                    while (result_content == null && counter != DEFAULT_NETWORK_TRY) {
                        try {
                            counter += 1;
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (counter != DEFAULT_NETWORK_TRY) {
                        Intent intent = new Intent(context, ContentDetailActivity.class);
                        intent.putExtra("position", 0);
                        intent.putExtra("content", result_content);
                        intent.putExtra("work", work);
                        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    } else {
                        getHandler.sendEmptyMessage(GET_INTERNET_ERROR);
                    }
                    isClickable = true;
                    break;
            }
        }
    }
    public class getHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == GET_INTERNET_ERROR) {
                Toast.makeText(context, "데이터를 가져오는데 실패했습니다\n네트워크 상태를 확인하고 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
