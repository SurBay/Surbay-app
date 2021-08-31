package com.pumasi.surbay.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.StoreCategory;

import java.util.ArrayList;

public class StoreCategoryRecyclerViewAdapter extends RecyclerView.Adapter<StoreCategoryRecyclerViewAdapter.MyStoreCategoryViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<StoreCategory> boardStoreCategories;
    private StoreCategoryRecyclerViewAdapter.OnItemClickListener aListener = null;
    public StoreCategoryRecyclerViewAdapter(ArrayList<StoreCategory> boardStoreCategories, Context ctx) {
        this.boardStoreCategories = boardStoreCategories;
        inflater = LayoutInflater.from(ctx);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }
    public void setOnItemClickListener(StoreCategoryRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener;
    }
    public Object getItem(int position) {
        return boardStoreCategories.get(position);
    }
    @NonNull
    @Override
    public MyStoreCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.store_category_item, parent, false);
        MyStoreCategoryViewHolder holder = new MyStoreCategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyStoreCategoryViewHolder holder, int position) {
        StoreCategory storeCategory = boardStoreCategories.get(position);
        holder.btn_seller_category_item.setText(storeCategory.getCategory());
    }

    @Override
    public int getItemCount() {
        return boardStoreCategories == null ? 0 : boardStoreCategories.size();
    }

    public class MyStoreCategoryViewHolder extends RecyclerView.ViewHolder {
        private final Button btn_seller_category_item;
        public MyStoreCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            btn_seller_category_item = itemView.findViewById(R.id.btn_seller_category_item);
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
