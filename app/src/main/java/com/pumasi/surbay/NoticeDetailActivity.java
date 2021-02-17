package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.classfile.Notice;

import java.text.SimpleDateFormat;

public class NoticeDetailActivity extends AppCompatActivity {
    TextView author;
    TextView date;
    TextView title;
    TextView content;;

    ImageView back;
    Notice post;
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

        Intent intent = getIntent();

        post = (Notice)intent.getParcelableExtra("post");

        author.setText(post.getAuthor());
        date.setText(new SimpleDateFormat("MM.dd").format(post.getDate()));
        title.setText(post.getTitle());
        content.setText(post.getContent());
    }
}