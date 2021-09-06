package com.pumasi.surbay.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReReplyRecyclerViewAdapter extends RecyclerView.Adapter<ReReplyRecyclerViewAdapter.MyReReplyViewHolder> {

    public ReReplyRecyclerViewAdapter() {

    }
    @NonNull
    @Override
    public MyReReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MyReReplyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyReReplyViewHolder extends RecyclerView.ViewHolder {
        public MyReReplyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
