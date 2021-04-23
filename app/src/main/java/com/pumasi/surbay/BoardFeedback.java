package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.FeedbackListViewAdapter;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.PostNonSurvey;
import com.pumasi.surbay.classfile.Reply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class BoardFeedback extends Fragment {
    static final int WRITE_NEWPOST = 1;
    static final int LIKE_SURVEY = 2;
    static final int NEWPOST = 1;

    private FeedbackListViewAdapter listViewAdapter;
    private ListView listView;
    private View view;
    TextView writeButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<PostNonSurvey> list;
    private boolean getFeedbacksDone = false;
    private BackgroundThread refreshThread;
    private refreshHandler handler = new refreshHandler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_feedback,container,false);

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        writeButton = view.findViewById(R.id.writeButton);
        writeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserPersonalInfo.userID.equals("nonMember")){
                    CustomDialog customDialog = new CustomDialog(getActivity(), null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 건의/의견 작성하실 수 없습니다");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), FeedbackWrite.class);
                intent.putExtra("purpose", WRITE_NEWPOST);
                startActivityForResult(intent, WRITE_NEWPOST);
            }
        });
        listView = view.findViewById(R.id.list);

        list = MainActivity.feedbackArrayList;
        Comparator<PostNonSurvey> cmpNew = new Comparator<PostNonSurvey>() {
            @Override
            public int compare(PostNonSurvey o1, PostNonSurvey o2) {
                int ret;
                Date date1 = o1.getDate();
                Date date2 = o2.getDate();
                int compare = date1.compareTo(date2);
                if (compare > 0)
                    ret = -1; //date2<date1
                else if (compare == 0)
                    ret = 0;
                else
                    ret = 1;
                return ret;
            }
        };
        Collections.sort(list, cmpNew);
        listViewAdapter = new FeedbackListViewAdapter(list);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                PostNonSurvey item = (PostNonSurvey) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), Feedbackdetail.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                intent.putExtra("position", position);
            }
        });
        mSwipeRefreshLayout = view.findViewById(R.id.refresh_boards3);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshThread = new BackgroundThread();
                refreshThread.start();
            }
        });
        return view;
    }
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState){
        listView = view.findViewById(R.id.list);
        listViewAdapter = new FeedbackListViewAdapter(MainActivity.feedbackArrayList);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                PostNonSurvey item = (PostNonSurvey) listViewAdapter.getItem(position);
                Intent intent = new Intent(((AppCompatActivity) getActivity()).getApplicationContext(), Feedbackdetail.class);
                intent.putExtra("post", item);
                intent.putParcelableArrayListExtra("reply", item.getComments());
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
    class BackgroundThread extends Thread {
        public void run() {
            getFeedbacks();
            while(!getFeedbacksDone) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    }

    private class refreshHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            list = MainActivity.feedbackArrayList;
            Comparator<PostNonSurvey> cmpNew = new Comparator<PostNonSurvey>() {
                @Override
                public int compare(PostNonSurvey o1, PostNonSurvey o2) {
                    int ret;
                    Date date1 = o1.getDate();
                    Date date2 = o2.getDate();
                    int compare = date1.compareTo(date2);
                    if (compare > 0)
                        ret = -1; //date2<date1
                    else if (compare == 0)
                        ret = 0;
                    else
                        ret = 1;
                    return ret;
                }
            };
            Collections.sort(list, cmpNew);

            listViewAdapter = new FeedbackListViewAdapter(list);
            listView.setAdapter(listViewAdapter);

            mSwipeRefreshLayout.setRefreshing(false);
            getFeedbacksDone = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case WRITE_NEWPOST:
                switch (resultCode) {
                    case NEWPOST:
                        try {
                            list = MainActivity.feedbackArrayList;
                            refreshListview();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return;
                    default:
                        return;
                }
            default:
                return;
        }
    }

    public void refreshListview(){
        listViewAdapter.notifyDataSetChanged();
        listView.setAdapter(listViewAdapter);
    }
    private void getFeedbacks(){
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/feedbacks";
            ArrayList<Post> list = new ArrayList<>();
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {

                        try {
                            ArrayList<PostNonSurvey> feedbackArrayList = new ArrayList<PostNonSurvey>();
                            JSONArray resultArr = new JSONArray(response.toString());

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                Integer author_lvl = post.getInt("author_lvl");
                                String content = post.getString("content");

                                SimpleDateFormat fm = new SimpleDateFormat(getActivity().getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                Integer category = post.getInt("category");

                                ArrayList<Reply> comments = new ArrayList<>();
                                JSONArray ja = (JSONArray)post.get("comments");
                                for (int j = 0; j<ja.length(); j++){
                                    JSONObject reply = ja.getJSONObject(j);
                                    String reid = reply.getString("_id");
                                    String writer = reply.getString("writer");
                                    String contetn = reply.getString("content");
                                    Date datereply = null;
                                    try {
                                        datereply = fm.parse(post.getString("date"));
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    Boolean replyhide = post.getBoolean("hide");
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
                                    comments.add(re);
                                }

                                String author_userid = post.getString("author_userid");


                                PostNonSurvey newFeedback = new PostNonSurvey(id, title, author, author_lvl, content, date, category, comments);
                                newFeedback.setAuthor_userid(author_userid);
                                feedbackArrayList.add(newFeedback);
                            }
                            MainActivity.feedbackArrayList = feedbackArrayList;
                            getFeedbacksDone = true;

                        } catch (JSONException e) {
                            Log.d("exception", "JSON error");
                            e.printStackTrace();
                        }
                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(20*1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonArrayRequest);
        } catch (Exception e){
            Log.d("exception", "failed getting response");
            e.printStackTrace();
        }
    }
}