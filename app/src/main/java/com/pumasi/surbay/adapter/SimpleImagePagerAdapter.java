package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pumasi.surbay.R;

import java.util.ArrayList;

public class SimpleImagePagerAdapter extends RecyclerView.Adapter<SimpleImagePagerAdapter.MySimpleImageViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> IMAGES = new ArrayList<>();

    public SimpleImagePagerAdapter(ArrayList<String> IMAGES, Context ctx) {
        this.context = ctx;
        this.IMAGES = IMAGES;
        this.inflater = LayoutInflater.from(ctx);

    }
    @NonNull
    @Override
    public SimpleImagePagerAdapter.MySimpleImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.simple_image, parent, false);
        MySimpleImageViewHolder holder = new MySimpleImageViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleImagePagerAdapter.MySimpleImageViewHolder holder, int position) {
        Glide.with(context).load(IMAGES.get(position)).into(holder.simple_image);
    }

    @Override
    public int getItemCount() {
        return IMAGES == null ? 0 : IMAGES.size();
    }

    public class MySimpleImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView simple_image;

        public MySimpleImageViewHolder(@NonNull View itemView) {
            super(itemView);
            simple_image = itemView.findViewById(R.id.simple_image);
        }
    }
}
