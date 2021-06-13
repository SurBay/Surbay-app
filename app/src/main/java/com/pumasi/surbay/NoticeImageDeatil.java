package com.pumasi.surbay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pumasi.surbay.adapter.ViewPagerAdapter;
import com.pumasi.surbay.classfile.BitmapTransfer;
import com.pumasi.surbay.classfile.CustomViewPager;

public class NoticeImageDeatil extends AppCompatActivity {
    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    CustomViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    Integer position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_viewpager);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", -1);

        Log.d("bitmapssizeare", ""+BitmapTransfer.getBitmap_list().size());


        viewPager = (CustomViewPager)findViewById(R.id.viewpager);
        viewPagerAdapter = new ViewPagerAdapter(NoticeImageDeatil.this, BitmapTransfer.getBitmap_list());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(position);

//        imageView = findViewById(R.id.imageview);
//        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
//
//        imageView.setImageBitmap(BitmapTransfer.getBitmap());


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    public boolean onTouchEvent(MotionEvent motionEvent) {
//        //변수로 선언해 놓은 ScaleGestureDetector
//        viewPagerAdapter.
//        return true;
//    }
//    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        @Override
//        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
//            // ScaleGestureDetector에서 factor를 받아 변수로 선언한 factor에 넣고
//            mScaleFactor *= scaleGestureDetector.getScaleFactor();
//
//            // 최대 10배, 최소 10배 줌 한계 설정
//            mScaleFactor = Math.max(0.1f,
//                    Math.min(mScaleFactor, 10.0f));
//
//            // 이미지뷰 스케일에 적용
//            sa.setScaleX(mScaleFactor);
//            imageView.setScaleY(mScaleFactor);
//            return true;
//        }
//    }
}
