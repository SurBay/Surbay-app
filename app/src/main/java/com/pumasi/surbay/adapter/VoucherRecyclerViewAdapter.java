package com.pumasi.surbay.adapter;

import com.pumasi.surbay.R;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VoucherRecyclerViewAdapter extends RecyclerView.Adapter<VoucherRecyclerViewAdapter.MyVoucherViewHolder> {
    LayoutInflater inflater;
    public VoucherRecyclerViewAdapter(Context ctx) {
        inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public MyVoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.my_voucher_item, parent, false);
        MyVoucherViewHolder holder = new MyVoucherViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyVoucherViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public class MyVoucherViewHolder extends RecyclerView.ViewHolder {
        public MyVoucherViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
