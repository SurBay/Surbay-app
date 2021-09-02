package com.pumasi.surbay.pages;

import com.pumasi.surbay.R;
import com.pumasi.surbay.SellingDetailActivity;
import com.pumasi.surbay.adapter.RecyclerViewDdayAdapter;
import com.pumasi.surbay.adapter.SellingRecyclerViewAdapter;
import com.pumasi.surbay.classfile.Coupon;
import com.pumasi.surbay.classfile.Store;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class SellingActivity extends AppCompatActivity {

    private Context context;
    private ImageButton btn_back;
    private RecyclerView rv_selling;
    private SellingRecyclerViewAdapter sellingRecyclerViewAdapter;

    private TextView tv_coupon_selling_head;
    private ArrayList<Coupon> selling = new ArrayList<Coupon>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling);
        getSupportActionBar().hide();

        Store store = getIntent().getParcelableExtra("store");
        selling = store.getCoupons_list();
        context = getApplicationContext();

        tv_coupon_selling_head = findViewById(R.id.tv_coupon_selling_head);
        btn_back = findViewById(R.id.btn_back);
        tv_coupon_selling_head.setText(store.getName());
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sellingRecyclerViewAdapter = new SellingRecyclerViewAdapter(selling, context);
        rv_selling = findViewById(R.id.rv_selling);
        rv_selling.setAdapter(sellingRecyclerViewAdapter);
        rv_selling.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        sellingRecyclerViewAdapter.setOnItemClickListener(new RecyclerViewDdayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Coupon coupon = (Coupon) sellingRecyclerViewAdapter.getItem(position);
                Intent intent = new Intent(context, SellingDetailActivity.class);
                intent.putExtra("coupon", coupon);
                startActivity(intent);
            }
        });
    }
}