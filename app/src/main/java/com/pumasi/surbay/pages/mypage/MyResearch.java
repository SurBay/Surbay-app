package com.pumasi.surbay.pages.mypage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.PostRecyclerViewAdapter;
import com.pumasi.surbay.pages.MainActivity;

public class MyResearch extends AppCompatActivity {

    private TextView tv_user_research_head;
    private ImageButton ib_back;
    private ImageButton ib_user_research_query;
    private RecyclerView rv_user_research;
    private int type;
    private PostRecyclerViewAdapter postRecyclerViewAdapter;

    private final int UPLOADED = 0;
    private final int PARTICIPATED = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_research);
        getSupportActionBar().hide();
        tv_user_research_head = findViewById(R.id.tv_user_research_head);
        ib_back = findViewById(R.id.ib_back);
        ib_user_research_query = findViewById(R.id.ib_user_research_query);
        rv_user_research = findViewById(R.id.rv_user_research);
        type = getIntent().getIntExtra("type", UPLOADED);
        if (type == UPLOADED) {
            tv_user_research_head.setText("업로드한 리서치");
        } else if (type == PARTICIPATED) {
            tv_user_research_head.setText("참여한 리서치");
        }
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        postRecyclerViewAdapter = new PostRecyclerViewAdapter(MainActivity.postArrayList, getApplicationContext());
        rv_user_research.setAdapter(postRecyclerViewAdapter);
        rv_user_research.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
    }
}