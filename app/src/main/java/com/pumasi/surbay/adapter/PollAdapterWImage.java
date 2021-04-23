package com.pumasi.surbay.adapter;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.GeneralDetailActivity;
import com.pumasi.surbay.GeneralImageDetail;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.BitmapTransfer;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.util.ArrayList;
import java.util.Collections;

public class PollAdapterWImage extends RecyclerView.Adapter<PollAdapterWImage.MyViewHolder> {
    private static OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private ArrayList<Poll> imageModelArrayList;
    public ArrayList<Integer> bitArray;
    private Boolean multi_response;
    ArrayList<Bitmap> images = GeneralDetailActivity.images;
    Context ctx;


    public PollAdapterWImage(Context ctx, ArrayList<Poll> imageModelArrayList, ArrayList<Integer> bitArray, Boolean multi_response){
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
        this.bitArray = bitArray;
        this.multi_response = multi_response;
        this.ctx = ctx;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public PollAdapterWImage.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.polldetail_recycler_item_wimage, parent, false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(PollAdapterWImage.MyViewHolder holder, int position) {
        holder.content.setText(imageModelArrayList.get(position).getContent());
        if(bitArray.get(position)==1){
            holder.content.setBackgroundResource(R.drawable.poll_selected);
        }else{
            holder.content.setBackgroundResource(R.drawable.poll_notselected);
        }
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitArray.get(position)==0){
//                    TextView content = v.findViewById(R.id.content);
                    holder.content.setBackgroundResource(R.drawable.poll_selected);
                    if(multi_response==false){
                        bitArray = new ArrayList<Integer>(Collections.nCopies(getItemCount(), 0));
                        for(int i=0;i<imageModelArrayList.size();i++){
                            if(((Poll) getItem(i)).getParticipants_userids().contains(UserPersonalInfo.userID))
                                ((Poll) getItem(i)).pullParticipant(UserPersonalInfo.userID);
                        }
                    }
                    bitArray.set(position, 1);
                    ((Poll) getItem(position)).pushParticipant(UserPersonalInfo.userID);
                    notifyDataSetChanged();
//                    Log.d("selected", ""+bitArray);
                }
                else{
//                    TextView content = v.findViewById(R.id.content);
                    holder.content.setBackgroundResource(R.drawable.poll_notselected);
                    bitArray.set(position, 0);
                    ((Poll) getItem(position)).pullParticipant(UserPersonalInfo.userID);
                    notifyDataSetChanged();
                    Log.d("unselected", ""+((Poll) getItem(position)).getParticipants_userids());

                }
            }
        });
        if(images.get(position)!=null){
            holder.imageView.setImageBitmap(images.get(position));
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BitmapTransfer.setBitmap(images.get(position));
                    Intent intent = new Intent(ctx, GeneralImageDetail.class);
                    ctx.startActivity(intent);
                }
            });
        }else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_image_24);
        }

    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        Button content;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            content = (Button) itemView.findViewById(R.id.content);
            imageView = (ImageView) itemView.findViewById(R.id.image);

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

