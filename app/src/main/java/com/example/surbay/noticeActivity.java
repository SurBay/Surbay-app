package com.example.surbay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.surbay.adapter.NoticeListAdapter;
import com.example.surbay.classfile.PostNonSurvey;

import java.util.ArrayList;
import java.util.Date;

public class noticeActivity extends AppCompatActivity {
    ListView notice_listview;
    NoticeListAdapter notice_listAdapter;
    ArrayList<PostNonSurvey> notice_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        this.getSupportActionBar().hide();

        notice_list = new ArrayList<>();
        Date date = new Date();
        notice_list.add(new PostNonSurvey(null, "하이","하이",1,"하이",date));

        notice_listview = findViewById(R.id.notice_list);

        notice_listAdapter = new NoticeListAdapter(notice_list);
        notice_listview.setAdapter(notice_listAdapter);

        notice_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //클릭시 글 확대 기능 추가 예정
                PostNonSurvey item = (PostNonSurvey) notice_listAdapter.getItem(position);
                Intent intent = new Intent(noticeActivity.this, NoticeDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}