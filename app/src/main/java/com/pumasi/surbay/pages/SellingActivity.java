package com.pumasi.surbay.pages;

import com.pumasi.surbay.R;
import com.pumasi.surbay.adapter.SellingRecyclerViewAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class SellingActivity extends AppCompatActivity {

    private Context context;
    private ImageButton btn_back;
    private RecyclerView rv_selling;
    private SellingRecyclerViewAdapter sellingRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);
        getSupportActionBar().hide();

        context = getApplicationContext();
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sellingRecyclerViewAdapter = new SellingRecyclerViewAdapter(context);
        rv_selling = findViewById(R.id.rv_selling);
        rv_selling.setAdapter(sellingRecyclerViewAdapter);
        rv_selling.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

    }
}