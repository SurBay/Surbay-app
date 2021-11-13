package com.pumasi.surbay.pages.homepage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.noticeImageAdapter;
import com.pumasi.surbay.classfile.BitmapTransfer;
import com.pumasi.surbay.classfile.Notice;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NoticeDetailActivity extends AppCompatActivity {
    TextView author;
    TextView date;
    TextView title;
    TextView content;;

    ImageView back;
    Notice post;
    private RecyclerView imagesrecyclerview;
    private noticeImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        this.getSupportActionBar().hide();

        back = findViewById(R.id.noticedetail_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        author = findViewById(R.id.notice_detail_auther);
        title = findViewById(R.id.notice_detail_title);
        date = findViewById(R.id.notice_detail_date);
        content = findViewById(R.id.notice_detail_content);
        imagesrecyclerview = findViewById(R.id.notice_images);



        Intent intent = getIntent();

        post = (Notice)intent.getParcelableExtra("post");


        imagesrecyclerview.setVisibility(View.GONE);
        ArrayList<String> notice_images = post.getImages();
        if (notice_images.size() > 0) {
            imageAdapter = new noticeImageAdapter(NoticeDetailActivity.this, notice_images);
            imagesrecyclerview.setAdapter(imageAdapter);
            imagesrecyclerview.setLayoutManager(new LinearLayoutManager(NoticeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            imagesrecyclerview.setVisibility(View.VISIBLE);
            imageAdapter.setOnItemClickListener(new noticeImageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    Intent intent = new Intent(NoticeDetailActivity.this, NoticeImageDeatil.class);
                    intent.putExtra("notice_images", notice_images);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
        }

        author.setText(post.getAuthor());
        date.setText(new SimpleDateFormat("MM.dd").format(post.getDate()));
        title.setText(post.getTitle());
        content.setText(post.getContent());
    }

}