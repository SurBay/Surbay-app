package com.pumasi.surbay;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.pumasi.surbay.classfile.MyCoupon;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MyExchangeDetailActivity extends AppCompatActivity {

    private Context context;
    private String qrURL;
    private ImageButton ib_back;
    private ImageView iv_my_coupon_detail_qr;
    private TextView tv_my_coupon_detail_name;
    private TextView tv_my_coupon_detail_due;
    private TextView tv_my_coupon_detail_date;
    private TextView tv_my_coupon_detail_save;

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_exchange_detail);
        getSupportActionBar().hide();
        context = getApplicationContext();

        MyCoupon myCoupon = getIntent().getParcelableExtra("myCoupon");
        String date = new Tools().convertTimeZone(context, myCoupon.getDate(), "yyyy.MM.dd");

        ib_back = findViewById(R.id.ib_back);

        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_my_coupon_detail_name = findViewById(R.id.tv_my_coupon_detail_name);
        tv_my_coupon_detail_due = findViewById(R.id.tv_my_coupon_detail_due);
        tv_my_coupon_detail_date = findViewById(R.id.tv_my_coupon_detail_date);
        tv_my_coupon_detail_save = findViewById(R.id.tv_my_coupon_detail_save);
        tv_my_coupon_detail_name.setText(myCoupon.getMenu());

        tv_my_coupon_detail_date.setText(date);
        Log.d("Dateis?", "onCreate: " + date);

        iv_my_coupon_detail_qr = findViewById(R.id.iv_my_coupon_detail_qr);

        qrURL = "https://main.d18xbudboby48i.amplifyapp.com/?id=" + UserPersonalInfo.email + "&id=" + myCoupon.getId();
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(qrURL, BarcodeFormat.QR_CODE, 200, 200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            iv_my_coupon_detail_qr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }


    }
}