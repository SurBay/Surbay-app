package com.pumasi.surbay.adapter;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Coupon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SellingRecyclerViewAdapter extends RecyclerView.Adapter<SellingRecyclerViewAdapter.MySellingHolder> {

    LayoutInflater inflater;
    ArrayList<Coupon> selling;
    public SellingRecyclerViewAdapter(ArrayList<Coupon> selling, Context ctx) {
        this.selling = selling;
        inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public SellingRecyclerViewAdapter.MySellingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.selling_item, parent, false);
        MySellingHolder holder = new MySellingHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SellingRecyclerViewAdapter.MySellingHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return selling == null ? 0 : selling.size();
    }

    public class MySellingHolder extends RecyclerView.ViewHolder {
        public MySellingHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
