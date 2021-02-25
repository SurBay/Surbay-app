package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;

import java.util.ArrayList;

public class GiftImageAdapter extends RecyclerView.Adapter<GiftImageAdapter.MyViewHolder>{
    private static GiftImageAdapter.OnItemClickListener mListener = null ;

    private LayoutInflater inflater;
    private ArrayList<Uri> imageModelArrayList;
    private ArrayList<Bitmap> imageBitmapList;

    public GiftImageAdapter(Context ctx, ArrayList<Uri> imageModelArrayList){
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
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
    public void setOnItemClickListener(GiftImageAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }

    public void removeItem(int position){
        imageModelArrayList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItem(Uri uri){
        imageModelArrayList.add(uri);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public GiftImageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.postimage_listitem, parent, false);
        GiftImageAdapter.MyViewHolder holder = new GiftImageAdapter.MyViewHolder(view);


        return holder;

    }

    @Override
    public void onBindViewHolder(@NonNull GiftImageAdapter.MyViewHolder holder, int position) {
//        Log.d("bmlistis", imageBitmapList+"");
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

