package com.example.surbay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.surbay.classfile.PostNonSurvey;

import java.text.SimpleDateFormat;

public class NoticeDetailActivity extends AppCompatActivity {
    TextView author;
    TextView date;
    TextView title;
    TextView content;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_detail);

        this.getSupportActionBar().hide();

        author = findViewById(R.id.notice_detail_auther);
        title = findViewById(R.id.notice_detail_title);
        date = findViewById(R.id.notice_detail_date);
        content = findViewById(R.id.notice_detail_content);

        Intent intent = getIntent();

        PostNonSurvey post = intent.getParcelableExtra("post");

        author.setText(post.getAuthor());
        date.setText(new SimpleDateFormat("MM.dd").format(post.getDate()));
        title.setText(post.getTitle());
        content.setText(post.getContent());
    }
}