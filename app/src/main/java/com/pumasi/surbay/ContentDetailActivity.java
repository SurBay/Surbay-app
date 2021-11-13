package com.pumasi.surbay;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.adapter.ContentDetailRecyclerViewAdapter;
import com.pumasi.surbay.adapter.SimpleImagePagerAdapter;
import com.pumasi.surbay.classfile.Content;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ContentDetailActivity extends AppCompatActivity {
    private Context context;
    private Content content;
    private TextView tv_content_indicator;
    private int current_page = 1;
    private SimpleImagePagerAdapter simpleImagePagerAdapter;
    private ContentDetailRecyclerViewAdapter contentDetailRecyclerViewAdapter;
    private CustomDialog customDialog;

    private ArrayList<String> imageRecycler = new ArrayList<>();

    private ImageButton ib_back;
    private TextView tv_content_detail_date;
    private TextView tv_content_detail_title;
    private ViewPager2 vp_content_detail;
    private RecyclerView rv_content_detail;
    private TextView tv_content_detail_content;

    private boolean like_state = false;
    private int like_count = 0;
    private ImageButton ib_content_detail_like;
    private TextView tv_content_detail_like;
    private ImageButton ib_content_detail_comment;
    private TextView tv_content_detail_comment;
    private int comment_counts = 0;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);
        getSupportActionBar().hide();
        context = getApplicationContext();

        setView();

    }
    public void setView() {
        content = getIntent().getParcelableExtra("content");
        like_count = content.getLikes();
        imageRecycler = content.getImage_urls();


        vp_content_detail = findViewById(R.id.vp_content_detail);
        simpleImagePagerAdapter = new SimpleImagePagerAdapter(imageRecycler, context);
        vp_content_detail.setAdapter(simpleImagePagerAdapter);
        tv_content_indicator = findViewById(R.id.tv_content_indicator);
        tv_content_indicator.setText(current_page + "/" + simpleImagePagerAdapter.getItemCount());
        vp_content_detail.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                current_page = position + 1;
                tv_content_indicator.setText(current_page + "/" + simpleImagePagerAdapter.getItemCount());
            }
        });
        rv_content_detail = findViewById(R.id.rv_content_detail);
        contentDetailRecyclerViewAdapter = new ContentDetailRecyclerViewAdapter(imageRecycler, context);
        rv_content_detail.setAdapter(contentDetailRecyclerViewAdapter);
        rv_content_detail.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));

        contentDetailRecyclerViewAdapter.setOnItemClickListener(new ContentDetailRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                vp_content_detail.setCurrentItem(position, true);
            }
        });
        ib_back = findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
        tv_content_detail_date = findViewById(R.id.tv_content_detail_date);
        tv_content_detail_title = findViewById(R.id.tv_content_detail_title);
        tv_content_detail_content = findViewById(R.id.tv_content_detail_content);

        tv_content_detail_date.setText(fm.format(content.getDate()));
        tv_content_detail_title.setText(content.getTitle());
        tv_content_detail_content.setText(content.getContent());

        ib_content_detail_like = findViewById(R.id.ib_content_detail_like);
        tv_content_detail_like = findViewById(R.id.tv_content_detail_like);
        ib_content_detail_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPersonalInfo.userID.equals("nonMember")) {
                    customDialog = new CustomDialog(ContentDetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 좋아요를 누를 수 없습니다.");
                    customDialog.setNegativeButton("확인");
                } else {
                    setLike(content.getId());
                }
            }
        });
        tv_content_detail_like.setText(String.valueOf(content.getLikes()));
        for (String user : content.getLiked_users()) {
            Log.d("notinLikedUser", "onCreate: " + user);
            if (user.equals(UserPersonalInfo.email)) {
                Log.d("inLikedUser", "onCreate: " + "there is" + user);
                like_state = true;
                setLikeSelected(like_state);
                break;
            }
        }
        ib_content_detail_comment = findViewById(R.id.ib_content_detail_comment);
        tv_content_detail_comment = findViewById(R.id.tv_content_detail_comment);
        ib_content_detail_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ContentDetailCommentsActivity.class);
                intent.putExtra("content_id", content.getId());
                intent.putExtra("comments", content.getComments());
                startActivityForResult(intent, 0);
            }
        });
        for (ContentReply contentReply : content.getComments()) {
            comment_counts++;
            comment_counts += contentReply.getReply().size();
        }
        tv_content_detail_comment.setText(String.valueOf(comment_counts));
    }

    public void setLike(String content_object_id) {
        try {
            String requestURL = getResources().getString(R.string.server) + "/api/content/likecontent/";
            JSONObject params = new JSONObject();
            params.put("userID", UserPersonalInfo.email);
            params.put("content_object_id", content_object_id);
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.PUT, requestURL, params, response -> {
                try {
                    String message = new JSONObject(response.toString()).getString("message");
                    if (message.equals("likes")) {
                        Toast.makeText(context, "좋아요를 누르셨습니다.", Toast.LENGTH_SHORT).show();
                        like_count += 1;
                    } else if (message.equals("dislikes")) {
                        Toast.makeText(context, "좋아요를 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                        like_count -= 1;
                    }
                    like_state = !like_state;
                    setLikeSelected(like_state);
                } catch (JSONException e) {
                    Toast.makeText(context, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                    like_state = !like_state;
                    setLikeSelected(like_state);
                }
            }, error -> { error.printStackTrace();
                like_state = !like_state;
                setLikeSelected(like_state);
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(context, "서버 통신 실패", Toast.LENGTH_SHORT).show();
            like_state = !like_state;
            setLikeSelected(like_state);
        }
    }
    public void setLikeSelected(boolean type) {
        if (!type) {
            ib_content_detail_like.setBackgroundResource(R.drawable.heart_empty);
        } else if (type) {
            ib_content_detail_like.setBackgroundResource(R.drawable.heart_filled);
        }
        tv_content_detail_like.setText(String.valueOf(like_count));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0) {
            tv_content_detail_comment.setText(String.valueOf(resultCode));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        int work = getIntent().getIntExtra("work", 0);
        if (work == 0) {

        } else if (work == 1) {
            this.overridePendingTransition(R.anim.enter, R.anim._null);
        }
    }
}