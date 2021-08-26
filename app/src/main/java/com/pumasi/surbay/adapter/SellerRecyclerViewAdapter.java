package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.classfile.Post;

import java.util.ArrayList;
import java.util.zip.Inflater;

import com.pumasi.surbay.R;


public class SellerRecyclerViewAdapter extends RecyclerView.Adapter<SellerRecyclerViewAdapter.MySellerViewHolder> {

    LayoutInflater inflater;
    public SellerRecyclerViewAdapter(Context ctx) {
        inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public MySellerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.seller_item, parent, false);
        MySellerViewHolder viewHolder = new MySellerViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MySellerViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class MySellerViewHolder extends RecyclerView.ViewHolder {
        public MySellerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
