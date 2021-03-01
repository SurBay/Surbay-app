package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;

import java.util.ArrayList;

public class GiftImageAdapter2 extends RecyclerView.Adapter<GiftImageAdapter2.MyViewHolder>{
    private static GiftImageAdapter2.OnItemClickListener mListener = null ;

    private LayoutInflater inflater;
    private ArrayList<Bitmap> imageBitmapList;

    public GiftImageAdapter2(Context ctx, ArrayList<Bitmap> imageBitmapList){
        inflater = LayoutInflater.from(ctx);
        this.imageBitmapList = imageBitmapList;
    }

    public void setBitmapList(ArrayList<Bitmap> bmList){
        this.imageBitmapList = bmList;
    }
    public ArrayList<Bitmap> getBitmapList(){
        return imageBitmapList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(GiftImageAdapter2.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void removeItem(int position){
        imageBitmapList.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public GiftImageAdapter2.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.postimage_listitem, parent, false);
        GiftImageAdapter2.MyViewHolder holder = new GiftImageAdapter2.MyViewHolder(view);


        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull GiftImageAdapter2.MyViewHolder holder, int position) {
//        Log.d("bmlistis", imageBitmapList+"");
        holder.image.setImageBitmap(imageBitmapList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageBitmapList.size();
    }

    public Object getItem(int position) {
        return imageBitmapList.get(position);
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

