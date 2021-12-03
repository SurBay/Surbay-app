package com.pumasi.surbay.pages.boardpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.pages.homepage.NoticeImageDeatil;
import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.noticeImageAdapter;
import com.pumasi.surbay.classfile.BitmapTransfer;
import com.pumasi.surbay.classfile.CustomDialog;
import com.pumasi.surbay.classfile.Surveytip;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TipdetailActivity extends AppCompatActivity {
    static final int LIKED = 5;
    static final int DISLIKED = 4;
    int LIKE_CHANGE = 0;
    int ORIGIN_LIKE = 0;

    TextView author;
    TextView level;
    TextView title;
    TextView category;
    TextView content;
    TextView date;
    LinearLayout likesbutton;
    TextView likescount;

    Surveytip surveytip;
    ArrayList<String> likedlist;
    private AlertDialog dialog;

    boolean likedselected;
    private int position;
    ImageView back;
    private static final String TAG_TEXT = "text";
    private static final String TAG_IMAGE = "image";
    String[] text = {" 카카오톡으로 공유하기 "};
    int[] image = {R.drawable.kakaotalk};
    List<Map<String, Object>> dialogItemList;
    private RecyclerView imagesrecyclerview;
    private noticeImageAdapter imageAdapter;
    private ArrayList<Bitmap> image_bitmaps = new ArrayList<>();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tipdetail);

        this.getSupportActionBar().hide();

        back = findViewById(R.id.tip_detail_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ORIGIN_LIKE==LIKED && LIKE_CHANGE==DISLIKED) {
                    dislikepost();
                    surveytip.setLikes(surveytip.getLikes()-1);
                    surveytip.getLiked_users().remove(UserPersonalInfo.userID);
                }
                else if(ORIGIN_LIKE==DISLIKED && LIKE_CHANGE==LIKED){
                    likepost();
                    surveytip.setLikes(surveytip.getLikes()+1);
                    surveytip.getLiked_users().add(UserPersonalInfo.userID);
                }
                finish();
            }
        });

        author = findViewById(R.id.tip_detail_auther);
        title = findViewById(R.id.tip_detail_title);
        date = findViewById(R.id.tip_detail_date);
        content = findViewById(R.id.tip_detail_content);
        category = findViewById(R.id.tip_detail_category);

        Intent intent = getIntent();
        surveytip = intent.getParcelableExtra("post");
        position = intent.getIntExtra("position", 0);

        likesbutton = findViewById(R.id.tipdetail_likesbutton);
        likescount = findViewById(R.id.tipdetail_likes);

        imagesrecyclerview = findViewById(R.id.tip_images);

        dialogItemList = new ArrayList<>();

        for(int i=0;i<image.length;i++)
        {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put(TAG_IMAGE, image[i]);
            itemMap.put(TAG_TEXT, text[i]);

            dialogItemList.add(itemMap);
        }

        likedlist = new ArrayList<>(surveytip.getLiked_users());
        author.setText(surveytip.getAuthor() + " (Lv " + surveytip.getAuthor_lvl().toString() + ")");
        title.setText(surveytip.getTitle());
        category.setText(surveytip.getCategory());
        content.setText(surveytip.getContent());
        date.setText(new SimpleDateFormat("MM.dd").format(surveytip.getDate()));
        likescount.setText(surveytip.getLikes().toString());
        if (likedlist.contains(UserPersonalInfo.userID)){
            likedselected = true;
            ORIGIN_LIKE = LIKED;
        } else {
            likedselected = false;
            ORIGIN_LIKE = DISLIKED;
        }
        setLikesbutton();

        likesbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserPersonalInfo.userID.equals("nonMember")){
                    CustomDialog customDialog = new CustomDialog(TipdetailActivity.this, null);
                    customDialog.show();
                    customDialog.setMessage("비회원은 좋아요를 하실 수 없습니다");
                    customDialog.setNegativeButton("확인");
                    return;
                }
                if (likedselected){
                    LIKE_CHANGE = DISLIKED;
                    likescount.setText((Integer.valueOf(likescount.getText().toString())-1)+"");
                } else {
                    LIKE_CHANGE = LIKED;
                    likescount.setText((Integer.valueOf(likescount.getText().toString())+1)+"");
                }
                likedselected = !likedselected;
                setLikesbutton();
            }
        });

        imagesrecyclerview.setVisibility(View.GONE);
    }

    @SuppressLint("ResourceAsColor")
    public void setLikesbutton(){
        if (likedselected){
            likesbutton.setBackgroundResource(R.drawable.round_border_teal_list);
            likescount.setTextColor(R.color.teal_200);
        } else {
            likesbutton.setBackgroundResource(R.drawable.round_border_gray_list);
            likescount.setTextColor(R.color.gray2);
        }
    }


    public void likepost(){
        String requestURL = getString(R.string.server)+"/api/surveytips/like/"+surveytip.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID",UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);

                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }

    public void dislikepost(){
        String requestURL = getString(R.string.server)+"/api/surveytips/dislike/"+surveytip.getID();
        try{
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            JSONObject params = new JSONObject();
            params.put("userID",UserPersonalInfo.userID);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.PUT, requestURL, params, response -> {
                        Log.d("response is", ""+response);

                    }, error -> {
                        Log.d("exception", "volley error");
                        error.printStackTrace();
                    });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e){
            Log.d("exception", "failed posting");
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {
        if(ORIGIN_LIKE==LIKED && LIKE_CHANGE==DISLIKED){
            dislikepost();
            surveytip.setLikes(surveytip.getLikes()-1);
            surveytip.getLiked_users().remove(UserPersonalInfo.userID);
        }
        else if(ORIGIN_LIKE==DISLIKED && LIKE_CHANGE==LIKED){
            likepost();
            surveytip.setLikes(surveytip.getLikes()+1);
            surveytip.getLiked_users().add(UserPersonalInfo.userID);
        }
        finish();
    }

}