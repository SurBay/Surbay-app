package com.pumasi.surbay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.pumasi.surbay.adapter.VoucherRecyclerViewAdapter;

public class MyExchangeActivity extends AppCompatActivity {

    private ImageButton ib_user_exchange_query;
    private ImageButton btn_back;

    private RecyclerView rv_user_exchange;
    private VoucherRecyclerViewAdapter voucherRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exchange);
        getSupportActionBar().hide();
        ib_user_exchange_query = findViewById(R.id.ib_user_exchange_query);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rv_user_exchange = findViewById(R.id.rv_user_exchange);
        voucherRecyclerViewAdapter = new VoucherRecyclerViewAdapter(getApplicationContext());
        rv_user_exchange.setAdapter(voucherRecyclerViewAdapter);

        rv_user_exchange.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

    }
}