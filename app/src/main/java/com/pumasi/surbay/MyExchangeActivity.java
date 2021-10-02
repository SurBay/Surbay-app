package com.pumasi.surbay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.pumasi.surbay.adapter.MyExchangeRecyclerViewAdapter;
import com.pumasi.surbay.classfile.MyCoupon;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MyExchangeActivity extends AppCompatActivity {
    private Context context;
    Tools tools = new Tools();

    private int type = 0;
    private final int ON_CLICK_AVAILABLE = 0;
    private final int ON_CLICK_UNAVAILABLE = 1;

    private ArrayList<MyCoupon> myCoupons = new ArrayList<>();
    private ArrayList<MyCoupon> myAvailableCoupons = new ArrayList<>();
    private ArrayList<MyCoupon> myUnAvailableCoupons = new ArrayList<>();

    private ImageButton ib_user_exchange_query;
    private ImageButton btn_back;

    private Button btn_user_exchange_available;
    private Button btn_user_exchange_unavailable;
    private RecyclerView rv_user_exchange;
    private MyExchangeRecyclerViewAdapter myExchangeRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exchange);
        getSupportActionBar().hide();

        context = getApplicationContext();
        for (MyCoupon myCoupon : UserPersonalInfo.coupons) {
            if (!myCoupon.getUsed()) {
                myAvailableCoupons.add(myCoupon);
            } else {
                myUnAvailableCoupons.add(myCoupon);
            }
        }
        Filter(type);
        ib_user_exchange_query = findViewById(R.id.ib_user_exchange_query);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Log.d("userCoupons", "onCreate: " + UserPersonalInfo.coupons);
        rv_user_exchange = findViewById(R.id.rv_user_exchange);
        myExchangeRecyclerViewAdapter = new MyExchangeRecyclerViewAdapter(myCoupons, getApplicationContext());
        rv_user_exchange.setAdapter(myExchangeRecyclerViewAdapter);
        rv_user_exchange.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

        btn_user_exchange_available = findViewById(R.id.btn_user_exchange_available);
        btn_user_exchange_unavailable = findViewById(R.id.btn_user_exchange_unavailable);
        btn_user_exchange_available.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(ON_CLICK_AVAILABLE);
                myExchangeRecyclerViewAdapter.setItems(myAvailableCoupons);

                myExchangeRecyclerViewAdapter.notifyDataSetChanged();

            }
        });

        btn_user_exchange_unavailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelected(ON_CLICK_UNAVAILABLE);
                myExchangeRecyclerViewAdapter.setItems(myUnAvailableCoupons);

                myExchangeRecyclerViewAdapter.notifyDataSetChanged();


            }
        });
        myExchangeRecyclerViewAdapter.setOnItemClickListener(new MyExchangeRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MyCoupon myCoupon = (MyCoupon) myExchangeRecyclerViewAdapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), MyExchangeDetailActivity.class);
                intent.putExtra("myCoupon", (Parcelable) myExchangeRecyclerViewAdapter.getItem(position));
                intent.putExtra("date", tools.convertTimeZone(context, myCoupon.getDate(), "yyyy.MM.dd"));
                startActivity(intent);
            }
        });


    }

    private void setSelected(int num) {
        btn_user_exchange_available.setTextColor(getResources().getColor(R.color.BDBDBD));
        btn_user_exchange_unavailable.setTextColor(getResources().getColor(R.color.BDBDBD));
        btn_user_exchange_available.setBackgroundResource(R.drawable.round_border_gray_list);
        btn_user_exchange_unavailable.setBackgroundResource(R.drawable.round_border_gray_list);

        switch (num) {
            case ON_CLICK_AVAILABLE:
                btn_user_exchange_available.setTextColor(getResources().getColor(R.color.text_color3A));
                btn_user_exchange_available.setBackgroundResource(R.drawable.round_border_teal_list);
                break;
            case ON_CLICK_UNAVAILABLE:
                btn_user_exchange_unavailable.setTextColor(getResources().getColor(R.color.text_color3A));
                btn_user_exchange_unavailable.setBackgroundResource(R.drawable.round_border_teal_list);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + num);
        }
    }

    private void Filter(int type) {
        this.type = type;
        switch (type) {
            case ON_CLICK_AVAILABLE:
                myCoupons = myAvailableCoupons;
                break;
            case ON_CLICK_UNAVAILABLE:
                myCoupons = myUnAvailableCoupons;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);

        }


    }
}