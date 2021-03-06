package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.PhotoViewAttacher;
import com.pumasi.surbay.R;

import java.util.ArrayList;
import java.util.Objects;

public class ViewPagerAdapter extends PagerAdapter {

    // Context object
    Context context;

    // Array of images
    ArrayList<String> images = new ArrayList<>();

    // Layout Inflater
    LayoutInflater mLayoutInflater;
    public ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;

    PhotoViewAttacher mAttacher;

//    mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


    // Viewpager Constructor
    public ViewPagerAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;

        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {



        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.viewpager_image, container, false);

        // referencing the image view from the item.xml file
        PhotoView imageView = (PhotoView) itemView.findViewById(R.id.imageview);

        Glide.with(context).load(images.get(position)).into(imageView);
        Objects.requireNonNull(container).addView(itemView);


        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }



}