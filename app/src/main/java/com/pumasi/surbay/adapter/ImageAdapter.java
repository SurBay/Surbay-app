package com.pumasi.surbay.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.MyViewHolder>{
    private static ImageAdapter.OnItemClickListener mListener = null ;

    private LayoutInflater inflater;
    private ArrayList<Uri> imageModelArrayList;

    public ImageAdapter(Context ctx, ArrayList<Uri> imageModelArrayList){
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(ImageAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void removeItem(int position){
        imageModelArrayList.remove(position);
        notifyItemRemoved(position);
    }


    @NonNull
    @Override
    public ImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.image_listitem, parent, false);
        ImageAdapter.MyViewHolder holder = new ImageAdapter.MyViewHolder(view);


        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.MyViewHolder holder, int position) {
        holder.image.setImageURI(imageModelArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

            image = (ImageView)itemView.findViewById(R.id.imageitemview);

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
