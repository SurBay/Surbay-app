package com.pumasi.surbay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.pumasi.surbay.pages.boardpage.BoardSurveyTip;

public class SurveyTipContainer extends AppCompatActivity {
    static FragmentStateAdapter adapter;
    private ImageButton btn_back;
    private ImageButton btn_query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_tip_container);
        getSupportActionBar().hide();

        ViewPager2 vp_survey_tip = findViewById(R.id.vp_survey_tip);
        btn_back = findViewById(R.id.btn_back);
        btn_query = findViewById(R.id.btn_query);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BoardsSearchActivity.class);
                intent.putExtra("pos", 2);
                startActivity(intent);
            }
        });
        vp_survey_tip.setAdapter(adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new BoardSurveyTip();
            }

            @Override
            public int getItemCount() {
                return 1;
            }
        });

        adapter.notifyDataSetChanged();

    }

}