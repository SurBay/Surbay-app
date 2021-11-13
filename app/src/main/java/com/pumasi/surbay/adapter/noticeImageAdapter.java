package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pumasi.surbay.R;

import java.util.ArrayList;

public class noticeImageAdapter extends RecyclerView.Adapter<noticeImageAdapter.MyViewHolder>{
    private static noticeImageAdapter.OnItemClickListener mListener = null ;

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<String> imageURLs = new ArrayList<>();


    public noticeImageAdapter(Context ctx, ArrayList<String> imageURLs){
        this.context = ctx;
        inflater = LayoutInflater.from(ctx);
        this.imageURLs = imageURLs;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(noticeImageAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @NonNull
    @Override
    public noticeImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.notice_image_item, parent, false);
        noticeImageAdapter.MyViewHolder holder = new noticeImageAdapter.MyViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull noticeImageAdapter.MyViewHolder holder, int position) {
        Glide.with(context).load(imageURLs.get(position)).into(holder.image);
    }

    @Override
    public int getItemCount() {
        return imageURLs.size();
    }

    public Object getItem(int position) {
        return imageURLs.get(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.giftimageitemview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }
    }
}

