package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pumasi.surbay.BoardsSearchActivity.DO_SURVEY;
import static com.pumasi.surbay.adapter.HomeVotePagerAdapter.home_votes;

public class HomeVoteFragment extends Fragment {

    private int VOTE_ITEM_NUM;
    private Context context;
    private String vote_id;
    private View view;
    private CustomDialog customDialog;

    private LinearLayout ll_home_vote_item;

    private TextView tv_home_vote_item_title;
    private TextView tv_home_vote_item_votes;
    private TextView tv_home_vote_item_comments;
    private ImageView iv_home_vote_item_like;

    private FrameLayout fl_poll2;
    private TextView tv_content2_1;
    private TextView tv_content2_2;

    private FrameLayout fl_poll3;
    private TextView tv_content3_1;
    private TextView tv_content3_2;
    private TextView tv_content3_3;

    private FrameLayout fl_poll4;
    private TextView tv_content4_1;
    private TextView tv_content4_2;
    private TextView tv_content4_3;
    private TextView tv_content4_4;
    private TextView tv_content4_extra;

    public HomeVoteFragment(int num) {
        this.VOTE_ITEM_NUM = num;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int poll_size = home_votes.get(VOTE_ITEM_NUM).getPolls().size();
        vote_id = home_votes.get(VOTE_ITEM_NUM).getID();
        context = getActivity().getApplicationContext();
        view = inflater.inflate(R.layout.fragment_home_vote, container, false);
        ll_home_vote_item = view.findViewById(R.id.ll_home_vote_item);
        tv_home_vote_item_title = view.findViewById(R.id.tv_home_vote_item_title);
        tv_home_vote_item_votes = view.findViewById(R.id.tv_home_vote_item_votes);
        tv_home_vote_item_votes.setText("투표 " + home_votes.get(VOTE_ITEM_NUM).getParticipants() + "명");
        tv_home_vote_item_comments = view.findViewById(R.id.tv_home_vote_item_comments);
        tv_home_vote_item_comments.setText("댓글 " + home_votes.get(VOTE_ITEM_NUM).getComments().size() + "개");
        iv_home_vote_item_like = view.findViewById(R.id.iv_home_vote_item_like);

        for (String user : home_votes.get(VOTE_ITEM_NUM).getLiked_users()) {
            if (user.equals(UserPersonalInfo.email)) {
                iv_home_vote_item_like.setImageResource(R.drawable.heart_filled);
            }
        }
        if (poll_size == 2) {
            fl_poll2 = view.findViewById(R.id.fl_poll2);
            fl_poll2.setVisibility(View.VISIBLE);
            tv_content2_1 = view.findViewById(R.id.tv_content2_1);
            tv_content2_2 = view.findViewById(R.id.tv_content2_2);

            tv_content2_1.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(0).getContent());
            tv_content2_2.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(1).getContent());

        } else if (poll_size == 3) {
            fl_poll3 = view.findViewById(R.id.fl_poll3);
            fl_poll3.setVisibility(View.VISIBLE);
            tv_content3_1 = view.findViewById(R.id.tv_content3_1);
            tv_content3_2 = view.findViewById(R.id.tv_content3_2);
            tv_content3_3 = view.findViewById(R.id.tv_content3_3);

            tv_content3_1.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(0).getContent());
            tv_content3_2.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(1).getContent());
            tv_content3_3.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(2).getContent());

        } else {
            fl_poll4 = view.findViewById(R.id.fl_poll4);
            fl_poll4.setVisibility(View.VISIBLE);
            tv_content4_1 = view.findViewById(R.id.tv_content4_1);
            tv_content4_2 = view.findViewById(R.id.tv_content4_2);
            tv_content4_3 = view.findViewById(R.id.tv_content4_3);
            tv_content4_4 = view.findViewById(R.id.tv_content4_4);
            tv_content4_extra = view.findViewById(R.id.tv_content4_extra);

            tv_content4_1.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(0).getContent());
            tv_content4_2.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(1).getContent());
            tv_content4_3.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(2).getContent());
            tv_content4_4.setText(home_votes.get(VOTE_ITEM_NUM).getPolls().get(3).getContent());

            if (poll_size == 4) {
                tv_content4_extra.setVisibility(View.GONE);
            } else {
                tv_content4_extra.setText("+" + String.valueOf(poll_size - 4));
            }

        }

        tv_home_vote_item_title.setText("Q. " + home_votes.get(VOTE_ITEM_NUM).getTitle());
        tv_home_vote_item_comments.setText("댓글 " + String.valueOf(home_votes.get(VOTE_ITEM_NUM).getComments().size()) + "개");

        ll_home_vote_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 투표게시판을 이용할 수 없습니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    General newGeneral = home_votes.get(VOTE_ITEM_NUM);
                    getGeneral(newGeneral.getID(), VOTE_ITEM_NUM, 0);
                }

            }
        });
        return view;
    }
    private void getGeneral(String general_object_id, int position, int work) {
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/generals/getpost/" + general_object_id;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
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
                    if (work == 0) {
                        Intent intent = new Intent(context, GeneralDetailActivity.class);
                        intent.putExtra("general", newGeneral);
                        intent.putExtra("position", position);
                        startActivityForResult(intent, 0);
                    } else if (work == 1) {
                        tv_home_vote_item_votes.setText("투표 " + newGeneral.getParticipants() + "명");
                        tv_home_vote_item_comments.setText("댓글 " + newGeneral.getComments().size() + "개");
                        for (String user : newGeneral.getLiked_users()) {
                            if (user.equals(UserPersonalInfo.email)) {
                                iv_home_vote_item_like.setImageResource(R.drawable.heart_filled);
                            }
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 0) {
//            getGeneral(vote_id, resultCode, 1);
//        }
    }
}