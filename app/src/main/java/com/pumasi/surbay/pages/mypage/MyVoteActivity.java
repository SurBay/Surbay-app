package com.pumasi.surbay.pages.mypage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.VoteRecyclerViewAdapter;
import com.pumasi.surbay.pages.MainActivity;

public class MyVoteActivity extends AppCompatActivity {

    private TextView tv_user_vote_head;
    private ImageButton ib_back;
    private ImageButton ib_user_vote_query;
    private RecyclerView rv_user_vote;
    private int type;
    private VoteRecyclerViewAdapter voteRecyclerViewAdapter;
    private final int UPLOADED = 0;
    private final int PARTICIPATED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vote);
        getSupportActionBar().hide();
        tv_user_vote_head = findViewById(R.id.tv_user_vote_head);
        ib_back = findViewById(R.id.ib_back);
        ib_user_vote_query = findViewById(R.id.ib_user_vote_query);
        rv_user_vote = findViewById(R.id.rv_user_vote);
        type = getIntent().getIntExtra("type", UPLOADED);
        if (type == UPLOADED) {
            tv_user_vote_head.setText("업로드한 투표");
        } else if (type == PARTICIPATED) {
            tv_user_vote_head.setText("참여한 투표");
        }


        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        voteRecyclerViewAdapter = new VoteRecyclerViewAdapter(MainActivity.generalArrayList, getApplicationContext());
        rv_user_vote.setAdapter(voteRecyclerViewAdapter);
        rv_user_vote.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

    }
}