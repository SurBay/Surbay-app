package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.HomeResearchPagerAdapter;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.pumasi.surbay.BoardsSearchActivity.DO_SURVEY;
import static com.pumasi.surbay.adapter.HomeResearchPagerAdapter.home_research;


public class HomeResearchFragment extends Fragment {
    private Context context;
    private int RESEARCH_ITEM_NUM;
    private View view;

    private Tools tools = new Tools();
    private LinearLayout ll_home_research_item1;
    private LinearLayout ll_home_research_item2;

    private TextView tv_name1;
    private TextView tv_name2;
    private TextView tv_title1;
    private TextView tv_title2;
    private TextView tv_target1;
    private TextView tv_target2;
    private ImageView iv_gift1;
    private ImageView iv_gift2;
    private TextView tv_day1;
    private TextView tv_day2;

    public HomeResearchFragment(int num) {
        this.RESEARCH_ITEM_NUM = num;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        boolean single = (HomeResearchPagerAdapter.HOME_RESEARCH_COUNT - RESEARCH_ITEM_NUM * 2 == 1);

        view = inflater.inflate(R.layout.fragment_home_research, container, false);
        ll_home_research_item1 = view.findViewById(R.id.ll_home_research_item1);
        ll_home_research_item2 = view.findViewById(R.id.ll_home_research_item2);

        tv_name1 = view.findViewById(R.id.tv_name1);
        tv_title1 = view.findViewById(R.id.tv_title1);
        tv_target1 = view.findViewById(R.id.tv_target1);
        iv_gift1 = view.findViewById(R.id.iv_gift1);
        tv_day1 = view.findViewById(R.id.tv_day1);

        Post home_item = home_research.get(RESEARCH_ITEM_NUM * 2);

        if (home_item.getAnnonymous()) {
            tv_name1.setText("익명");
        } else {
            tv_name1.setText(home_item.getAuthor().toString());
        }
        tv_title1.setText(home_item.getTitle().toString());
        tv_target1.setText(home_item.getTarget().toString());

        if (home_item.getNum_prize() == 0) {
            iv_gift1.setVisibility(View.INVISIBLE);
        }
        Date date = new Date(System.currentTimeMillis());
        Log.d("time checking", "onCreateView: " + new SimpleDateFormat("dd.HH.mm").format(date) + ", deadline: " + new SimpleDateFormat("dd.HH.mm").format(home_item.getDeadline()));
        if (tools.toUTC(date.getTime()) - home_item.getDeadline().getTime() > 0) {
            tv_day1.setText("종료");
            tv_day1.setTextColor(Color.parseColor("#C4C4C4"));
        } else if (home_item.getDeadline().getTime() - date.getTime() < 1000 * 60 * 60 * 24) {
            tv_day1.setText("D-DAY");
        } else if (date.getTime() - home_item.getDate().getTime() < 1000 * 60 * 60 * 24) {
            tv_day1.setText("NEW");
        } else {
            long d_day = (home_item.getDeadline().getTime() - date.getTime()) / (1000 * 60 * 60 * 24) + 1;
            tv_day1.setText("D-" + d_day);
        }
        ll_home_research_item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post research = home_item;
                getPost(research.getID(), 2 * RESEARCH_ITEM_NUM);
            }
        });

        if (single) {
            ll_home_research_item2.setVisibility(View.INVISIBLE);
        } else {
            tv_name2 = view.findViewById(R.id.tv_name2);
            tv_title2 = view.findViewById(R.id.tv_title2);
            tv_target2 = view.findViewById(R.id.tv_target2);
            iv_gift2 = view.findViewById(R.id.iv_gift2);
            tv_day2 = view.findViewById(R.id.tv_day2);

            Post home_item2 = home_research.get(RESEARCH_ITEM_NUM * 2 + 1);

            if (home_item2.getAnnonymous()) {
                tv_name2.setText("익명");
            } else {
                tv_name2.setText(home_item2.getAuthor().toString());
            }
            tv_title2.setText(home_item2.getTitle().toString());
            tv_target2.setText(home_item2.getTarget().toString());

            if (home_item2.getNum_prize() == 0) {
                iv_gift2.setVisibility(View.INVISIBLE);
            }
            if (date.getTime() - home_item2.getDeadline().getTime() > 0) {
                tv_day2.setText("종료");
                tv_day2.setTextColor(Color.parseColor("#C4C4C4"));
            } else if (tools.dayCompare(tools.toLocal(home_item2.getDeadline().getTime()), date.getTime()) == 0) {
                tv_day2.setText("D-DAY");
            } else if (date.getTime() - home_item2.getDate().getTime() < tools.time_day) {
                tv_day2.setText("NEW");
            } else {
                tv_day2.setText("D-" + tools.dayCompare(tools.toLocal(home_item2.getDeadline().getTime()), date.getTime()));
            }
            ll_home_research_item2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post research = home_item2;
                    getPost(research.getID(), 2 * RESEARCH_ITEM_NUM + 1);
                }
            });
        }
        return view;
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

}