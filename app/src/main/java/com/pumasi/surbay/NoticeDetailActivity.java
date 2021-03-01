package com.pumasi.surbay;

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

import com.pumasi.surbay.adapter.GiftImageAdapter2;
import com.pumasi.surbay.adapter.noticeImageAdapter;
import com.pumasi.surbay.classfile.BitmapTransfer;
import com.pumasi.surbay.classfile.Notice;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
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
    private ArrayList<Bitmap> image_bitmaps = new ArrayList<>();

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

        if(notice_images.size()>0) {
            ArrayList<Uri> image_uris = new ArrayList<>();
            final Handler handler = new Handler(){
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    imageAdapter = new noticeImageAdapter(NoticeDetailActivity.this, image_bitmaps);
                    imagesrecyclerview.setAdapter(imageAdapter);
                    imagesrecyclerview.setLayoutManager(new LinearLayoutManager(NoticeDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    imagesrecyclerview.setVisibility(View.VISIBLE);
                    imageAdapter.setOnItemClickListener(new noticeImageAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Intent intent = new Intent(NoticeDetailActivity.this, NoticeImageDeatil.class);
                            BitmapTransfer.setBitmap(image_bitmaps.get(position));
                            startActivityForResult(intent, 20);
                        }
                    });


                }
            };
            for (int i = 0; i < notice_images.size(); i++) {
                int finalI = i;
                new Thread() {
                    Message msg;
                    public void run() {
                        try {

                            Log.d("start", "bitmap no." + finalI);
                            String uri = notice_images.get(finalI);
                            URL url = new
                                    URL(uri);
                            URLConnection conn = url.openConnection();
                            conn.connect();
                            BufferedInputStream bis = new
                                    BufferedInputStream(conn.getInputStream());

                            Bitmap bm = BitmapFactory.decodeStream(bis);
                            image_bitmaps.add(bm);
                            bis.close();
                            msg = handler.obtainMessage();
                            handler.sendMessage(msg);
                        } catch (IOException e) {
                        }
                    }
                }.start();
            }

        }
        author.setText(post.getAuthor());
        date.setText(new SimpleDateFormat("MM.dd").format(post.getDate()));
        title.setText(post.getTitle());
        content.setText(post.getContent());
    }
}