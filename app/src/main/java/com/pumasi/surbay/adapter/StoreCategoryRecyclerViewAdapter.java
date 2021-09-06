package com.pumasi.surbay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.StoreCategory;

import java.util.ArrayList;

public class StoreCategoryRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private final int VIEW_TYPE_NORMAL = 0;
    private final int VIEW_TYPE_CLICKED = 1;
    private LayoutInflater inflater;
    public ArrayList<Integer> clicked = new ArrayList<Integer>();
    public ArrayList<StoreCategory> boardStoreCategories = new ArrayList<StoreCategory>();
    private StoreCategoryRecyclerViewAdapter.OnItemClickListener aListener = null;

    public StoreCategoryRecyclerViewAdapter(ArrayList<StoreCategory> boardStoreCategories, ArrayList<Integer> clicked, Context ctx) {
        this.boardStoreCategories = boardStoreCategories;
        this.clicked = clicked;
        this.context = ctx;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.store_category_item, parent, false);
        if (viewType == VIEW_TYPE_NORMAL) {
            MyStoreCategoryViewHolder holder = new MyStoreCategoryViewHolder(view);
            return holder;
        } else if (viewType == VIEW_TYPE_CLICKED) {
            MyStoreCategoryViewHolderClicked holder = new MyStoreCategoryViewHolderClicked(view);
            return holder;
        }
        return null;
    }

    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StoreCategory storeCategory = boardStoreCategories.get(position);
        if (holder instanceof MyStoreCategoryViewHolder) {
            ((MyStoreCategoryViewHolder) holder).btn_seller_category_item.setText(storeCategory.getCategory());
        }
        else if (holder instanceof MyStoreCategoryViewHolderClicked) {
            ((MyStoreCategoryViewHolderClicked) holder).btn_seller_category_item2.setText(storeCategory.getCategory());
            ((MyStoreCategoryViewHolderClicked) holder).btn_seller_category_item2.setBackgroundResource(R.drawable.round_border_teal_list);
            ((MyStoreCategoryViewHolderClicked) holder).btn_seller_category_item2.setTextColor(context.getResources().getColor(R.color.teal_200));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return clicked.get(position);
//        return boardStoreCategories.get(position).getType() == null ? 0 : boardStoreCategories.get(position).getType();
    }


    @Override
    public int getItemCount() {
        return boardStoreCategories.size();
    }

    public class MyStoreCategoryViewHolder extends RecyclerView.ViewHolder {
        private Button btn_seller_category_item;
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

    private class MyStoreCategoryViewHolderClicked extends RecyclerView.ViewHolder {
        private Button btn_seller_category_item2;
        public MyStoreCategoryViewHolderClicked(@NonNull View itemView) {
            super(itemView);
            btn_seller_category_item2 = itemView.findViewById(R.id.btn_seller_category_item);
        }
    }
}
