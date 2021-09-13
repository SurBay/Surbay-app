package com.pumasi.surbay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ContentReplyRecyclerViewAdapter;
import com.pumasi.surbay.adapter.ReplyRecyclerViewAdapter;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.util.ArrayList;

public class ContentDetailCommentsActivity extends AppCompatActivity {

    private Context context;
    private String content_id;
    private boolean is_reply = false;
    private String reply_id;
    private ArrayList<ContentReply> comments;
    private ContentReplyRecyclerViewAdapter contentReplyRecyclerViewAdapter;

    private ImageButton ib_back;
    private RecyclerView rv_content_detail_comment;
    public ImageView iv_content_detail_comment_reply;
    private EditText et_content_detail_comment_reply;
    private ImageButton ib_content_detail_comment_reply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail_comments);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        context = getApplicationContext();
        content_id = getIntent().getStringExtra("content_id");
        comments = getIntent().getParcelableArrayListExtra("comments");


        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rv_content_detail_comment = findViewById(R.id.rv_content_detail_comment);
        contentReplyRecyclerViewAdapter = new ContentReplyRecyclerViewAdapter(comments, context);
        contentReplyRecyclerViewAdapter.setOnItemClickListener(new ContentReplyRecyclerViewAdapter.OnReplyClickListener() {
            @Override
            public void onReplyClick(View v, int position) {
                ContentReply contentReply = (ContentReply) contentReplyRecyclerViewAdapter.getItem(position);
                reply_id = contentReply.getId();
                is_reply = true;
                setReplyVisible(true);
            }
        });
        contentReplyRecyclerViewAdapter.setOnItemClickListener(new ContentReplyRecyclerViewAdapter.OnMenuClickListener() {
            @Override
            public void onMenuClick(View v, int position) {
                Toast.makeText(context, "menu 선택", Toast.LENGTH_SHORT).show();
            }
        });
        rv_content_detail_comment.setAdapter(contentReplyRecyclerViewAdapter);
        rv_content_detail_comment.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));

        iv_content_detail_comment_reply = findViewById(R.id.iv_content_detail_comment_reply);
        et_content_detail_comment_reply = findViewById(R.id.et_content_detail_comment_reply);
        ib_content_detail_comment_reply = findViewById(R.id.ib_content_detail_comment_reply);
        ib_content_detail_comment_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reply = et_content_detail_comment_reply.getText().toString();
                et_content_detail_comment_reply.getText().clear();
                if (is_reply) {
                    postContentReReply(reply_id, reply);
                } else {
                    postContentReply(reply);
                }
            }
        });
    }

    public void postContentReply(String content) {
        try {
            String requestURL = getResources().getString(R.string.server) + "/api/content/postcomment";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("content_object_id", content_id);
            params.put("content", content);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {

            }, error -> {
                        error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {

        }
    }

    public void postContentReReply(String content_comment_object_id, String content) {
        try {
            String requestURL = getResources().getString(R.string.server) + "/api/content/postreply";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("contentcomment_object_id", content_comment_object_id);
            params.put("content", content);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {

            }, error -> {
                        error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void setReplyVisible(boolean visible) {
        Toast.makeText(context, "안녕", Toast.LENGTH_SHORT).show();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_content_detail_comment_reply.getLayoutParams();
        if (!visible) {
            params.width = 0;
            params.height = 0;
        } else {
            params.width = (int) (context.getResources().getDisplayMetrics().density * 23);
            params.height = (int) (context.getResources().getDisplayMetrics().density * 23);
        }
        iv_content_detail_comment_reply.setLayoutParams(params);
    }
}