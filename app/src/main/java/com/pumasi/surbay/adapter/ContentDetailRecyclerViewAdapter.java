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

public class ContentDetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> imageRecycler = new ArrayList<>();
    private ContentDetailRecyclerViewAdapter.OnItemClickListener aListener= null;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(ContentDetailRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener;
    }
    public ContentDetailRecyclerViewAdapter(ArrayList<String> imageRecycler, Context ctx) {
        this.imageRecycler = imageRecycler;
        this.context = ctx;
        inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.content_detail_recycler_item, parent, false);
        StandardViewHolder holder = new StandardViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        StandardViewHolder standardViewHolder = (StandardViewHolder) holder;
        Glide.with(context).load(imageRecycler.get(position)).into(standardViewHolder.iv_content_detail_recycler_image);
    }

    @Override
    public int getItemCount() {
        return imageRecycler == null ? 0 : imageRecycler.size();
    }

    class StandardViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_content_detail_recycler_image;
        public StandardViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_content_detail_recycler_image = itemView.findViewById(R.id.iv_content_detail_recycler_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }
    }

}
