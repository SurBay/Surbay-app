package com.pumasi.surbay.pages.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.NoticeListAdapter;
import com.pumasi.surbay.classfile.Notice;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.tools.FirebaseLogging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class NoticeActivity extends AppCompatActivity {

    private Context context;

    ListView notice_listview;
    NoticeListAdapter notice_listAdapter;
    ArrayList<Notice> notice_list;

    ImageView back;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ValueHandler handler = new ValueHandler();
    private BackgroundThread refreshThread;

    private Boolean getNoticesDone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        getSupportActionBar().hide();
        context = getApplicationContext();

        new FirebaseLogging(context).LogScreen("notice_page", "공지사항");

        SharedPreferences sharedPreferences = getSharedPreferences("updateNotice", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("last_notice_id", MainActivity.NoticeArrayList.get(0).getID());
        editor.apply();
        back = findViewById(R.id.notice_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0);
                finish();
            }
        });

        notice_listview = findViewById(R.id.notice_list);

        notice_list = new ArrayList<>(MainActivity.NoticeArrayList);
        notice_listAdapter = new NoticeListAdapter(notice_list);
        notice_listview.setAdapter(notice_listAdapter);

        notice_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                Notice item = (Notice) notice_listAdapter.getItem(position);
                Intent intent = new Intent(NoticeActivity.this, NoticeDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.notice_swipe_container);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshThread = new BackgroundThread();
                refreshThread.start();
            }
        });
    }
    class BackgroundThread extends Thread {
        public void run() {
            try {
                getNotices();
            } catch (Exception e) {
                e.printStackTrace();
                getNoticesDone = true;
            }
            while(!(getNoticesDone)) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {}
            }
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    }

    class ValueHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("gotmessage", "goetmessage");
            notice_list = new ArrayList<>(MainActivity.NoticeArrayList);
            notice_listAdapter = new NoticeListAdapter(notice_list);
            notice_listview.setAdapter(notice_listAdapter);
            getNoticesDone = false;
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void getNotices() throws Exception{
        try{
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/notices";
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, requestURL, null, response -> {
                        try {
                            ArrayList<Notice> NoticeArrayList = new ArrayList<Notice>();
                            JSONArray resultArr = new JSONArray(response.toString());

                            for (int i = 0; i < resultArr.length(); i++) {
                                JSONObject post = resultArr.getJSONObject(i);
                                String id = post.getString("_id");
                                String title = post.getString("title");
                                String author = post.getString("author");
                                String content = post.getString("content");
                                SimpleDateFormat fm = new SimpleDateFormat(getString(R.string.date_format));
                                Date date = null;
                                try {
                                    date = fm.parse(post.getString("date"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                JSONArray images = (JSONArray)post.get("image_urls");
                                ArrayList<String> imagearray = new ArrayList<>();
                                if(images!=null) {
                                    imagearray = new ArrayList<String>();
                                    for (int j = 0; j < images.length(); j++) {
                                        imagearray.add(images.getString(j));
                                    }
                                }


                                Notice newNotice = new Notice(id, title, author, content, date);

                                if(images!=null){
                                    newNotice.setImages(imagearray);
                                }
                                NoticeArrayList.add(newNotice);
                            }
                            notice_list = NoticeArrayList;
                            getNoticesDone = true;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(0);
    }
}