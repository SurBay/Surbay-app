package com.pumasi.surbay;

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
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ContentDetailActivity extends AppCompatActivity {
    private Context context;
    private Content content;
    private TextView tv_content_indicator;
    private int current_page = 1;
    private SimpleImagePagerAdapter simpleImagePagerAdapter;
    private ContentDetailRecyclerViewAdapter contentDetailRecyclerViewAdapter;

    private ArrayList<String> imageRecycler = new ArrayList<>();

    private ImageButton ib_back;
    private TextView tv_content_detail_date;
    private TextView tv_content_detail_title;
    private ViewPager2 vp_content_detail;
    private RecyclerView rv_content_detail;
    private TextView tv_content_detail_content;

    private int like_state = 0;
    private int like_count = 0;
    private ImageButton ib_content_detail_like;
    private TextView tv_content_detail_like;
    private ImageButton ib_content_detail_comment;
    private TextView tv_content_detail_comment;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_detail);
        getSupportActionBar().hide();
        context = getApplicationContext();
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
        tv_content_detail_content = findViewById(R.id.tv_content_detail_title);

        tv_content_detail_date.setText(fm.format(content.getDate()));
        tv_content_detail_title.setText(content.getTitle());
        tv_content_detail_content.setText(content.getContent());

        Log.d("content", "onCreate: " + content.getDate() + content.getContent() + content.getAuthor());

        ib_content_detail_like = findViewById(R.id.ib_content_detail_like);
        tv_content_detail_like = findViewById(R.id.tv_content_detail_like);
        ib_content_detail_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLike(content.getId());
            }
        });
        tv_content_detail_like.setText(String.valueOf(content.getLikes()));
        for (String user : content.getLiked_users()) {
            Log.d("notinLikedUser", "onCreate: " + user);
            if (user.equals(UserPersonalInfo.email)) {
                Log.d("inLikedUser", "onCreate: " + "there is" + user);
                like_state = 1;
                like_count -= 1;
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
                startActivity(intent);
            }
        });
        tv_content_detail_comment.setText(String.valueOf(content.getComments().size()));
    }
    public void setLike(String content_object_id) {
        try {
            like_state = like_state == 0 ? 1 : 0;
            setLikeSelected(like_state);
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
                    } else if (message.equals("dislikes")) {
                        Toast.makeText(context, "좋아요를 취소하셨습니다.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "서버 통신 실패", Toast.LENGTH_SHORT).show();
                    like_state = like_state == 0 ? 1 : 0;
                    setLikeSelected(like_state);
                }
            }, error -> {
                        error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            Toast.makeText(context, "서버 통신 실패", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    public void setLikeSelected(int type) {
        if (type == 0) {
            like_count -= 1;
            ib_content_detail_like.setBackgroundResource(R.drawable.heart_empty);
        } else if (type == 1) {
            like_count += 1;
            ib_content_detail_like.setBackgroundResource(R.drawable.heart_filled);
        }
        tv_content_detail_like.setText(String.valueOf(like_count));
    }
}