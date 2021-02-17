package com.pumasi.surbay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.pumasi.surbay.adapter.NoticeListAdapter;
import com.pumasi.surbay.classfile.Notice;

import java.util.ArrayList;

public class noticeActivity extends AppCompatActivity {
    ListView notice_listview;
    NoticeListAdapter notice_listAdapter;
    ArrayList<Notice> notice_list;

    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        this.getSupportActionBar().hide();


        back = findViewById(R.id.notice_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                Intent intent = new Intent(noticeActivity.this, NoticeDetailActivity.class);
                intent.putExtra("post", item);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }
}