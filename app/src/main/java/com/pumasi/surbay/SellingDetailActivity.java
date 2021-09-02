package com.pumasi.surbay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.pumasi.surbay.classfile.Coupon;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class SellingDetailActivity extends AppCompatActivity {

    private ImageButton ib_back;
    private TextView tv_coupon_selling_detail_name;
    private TextView tv_coupon_selling_detail_due;
    private TextView tv_coupon_selling_detail_price;
    private TextView tv_coupon_selling_detail_count;
    private TextView tv_coupon_selling_detail_total_price;
    private ImageButton ib_coupon_selling_detail_increase;
    private ImageButton ib_coupon_selling_detail_decrease;
    private Button btn_coupon_selling_detail_exchange;

    private int count = 1;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selling_detail);
        getSupportActionBar().hide();

        Coupon coupon = getIntent().getParcelableExtra("coupon");
        context = getApplicationContext();
        ib_back = findViewById(R.id.ib_back);
        tv_coupon_selling_detail_name = findViewById(R.id.tv_coupon_selling_detail_name);
        tv_coupon_selling_detail_due = findViewById(R.id.tv_coupon_selling_detail_due);
        tv_coupon_selling_detail_price = findViewById(R.id.tv_coupon_selling_detail_price);
        tv_coupon_selling_detail_count = findViewById(R.id.tv_coupon_selling_detail_count);
        tv_coupon_selling_detail_total_price = findViewById(R.id.tv_coupon_selling_detail_total_price);
        ib_coupon_selling_detail_increase = findViewById(R.id.ib_coupon_selling_detail_increase);
        ib_coupon_selling_detail_decrease = findViewById(R.id.ib_coupon_selling_detail_decrease);
        ib_coupon_selling_detail_increase.setEnabled(true);
        ib_coupon_selling_detail_decrease.setEnabled(false);
        btn_coupon_selling_detail_exchange = findViewById(R.id.btn_coupon_selling_detail_exchange);
        tv_coupon_selling_detail_name.setText(coupon.getStore() + " " + coupon.getMenu());
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
        String date = "";
        try {
            date = fm.format(coupon.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_coupon_selling_detail_due.setText(date);
        tv_coupon_selling_detail_price.setText(coupon.getCost() + " 크레딧");
        tv_coupon_selling_detail_count.setText("수량 "  + count + "개");
        tv_coupon_selling_detail_total_price.setText(coupon.getCost() * count + " 크레딧");

        ib_coupon_selling_detail_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count += 1;
                tv_coupon_selling_detail_count.setText("수량 "  + count + "개");
                tv_coupon_selling_detail_total_price.setText(coupon.getCost() * count + " 크레딧");
                if (count > 1) {
                    ib_coupon_selling_detail_decrease.setEnabled(true);
                }
            }
        });

        ib_coupon_selling_detail_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count -= 1;
                tv_coupon_selling_detail_count.setText("수량 "  + count + "개");
                tv_coupon_selling_detail_total_price.setText(coupon.getCost() * count + " 크레딧");
                if (count == 1) {
                    ib_coupon_selling_detail_decrease.setEnabled(false);
                }
            }
        });
        btn_coupon_selling_detail_exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    buyCoupon(coupon.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void buyCoupon(String couponId) throws JSONException {
        JSONObject params = new JSONObject();
        params.put("userID", UserPersonalInfo.userID);
        params.put("num", count);
        try {
            String requestURL = "http://ec2-3-35-152-40.ap-northeast-2.compute.amazonaws.com/api/coupon/buy/:coupon_id=" + couponId;
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST, requestURL, params, response -> {
            }, error -> {
                    error.printStackTrace();
            });
            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}