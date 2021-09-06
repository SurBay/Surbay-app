package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pumasi.surbay.classfile.Post;

import java.util.ArrayList;
import java.util.zip.Inflater;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Store;


public class SellerRecyclerViewAdapter extends RecyclerView.Adapter<SellerRecyclerViewAdapter.MySellerViewHolder> {

    private Context context;
    LayoutInflater inflater;
    private ArrayList<Store> boardStores = new ArrayList<Store>();
    private SellerRecyclerViewAdapter.OnItemClickListener aListener = null;

    public SellerRecyclerViewAdapter(ArrayList<Store> boardStores, Context ctx) {
        this.boardStores = boardStores;
        this.context = ctx;
        inflater = LayoutInflater.from(ctx);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(SellerRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener;
    }
    public Object getItem(int position) {
        return boardStores.get(position);
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
        Store store = boardStores.get(position);
        holder.tv_coupon_seller_item_name.setText(store.getName());
        if (!(store.getImg_urls() == null || store.getImg_urls().size() == 0)) {
            Glide.with(context).load(store.getImg_urls().get(0)).into(holder.iv_coupon_seller_item_show);
        }

    }

    @Override
    public int getItemCount() {
        return boardStores == null ? 0 : boardStores.size();
    }

    public class MySellerViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_coupon_seller_item_name;
        private final ImageView iv_coupon_seller_item_show;

        public MySellerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_coupon_seller_item_name = itemView.findViewById(R.id.tv_coupon_seller_item_name);
            iv_coupon_seller_item_show = itemView.findViewById(R.id.iv_coupon_seller_item_show);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
