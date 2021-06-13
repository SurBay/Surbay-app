package com.pumasi.surbay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class ProblemActivity extends AppCompatActivity {

    ImageView iv_problem_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem);
        getSupportActionBar().hide();

        TextView tv_problem_content = findViewById(R.id.tv_problem_content);
        iv_problem_back = findViewById(R.id.iv_problem_back);

        iv_problem_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_problem_content.setText(R.string.problem_solve);
    }
}