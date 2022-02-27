package com.pumasi.surbay.adapter;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.pages.boardpage.GeneralDetailActivity;
import com.pumasi.surbay.pages.boardpage.GeneralImageDetail;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.BitmapTransfer;
import com.pumasi.surbay.classfile.Poll;

import java.util.ArrayList;
import java.util.Collections;

public class PollDoneAdapterWImage extends RecyclerView.Adapter<PollDoneAdapterWImage.MyViewHolder> {
    private static OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private ArrayList<Poll> imageModelArrayList;
    public ArrayList<Integer> bitArray;
    private Boolean multi_response;
    private Integer tot_votes = 0;
    private Context ctx;
    private ArrayList<Bitmap> images = GeneralDetailActivity.images;

    public PollDoneAdapterWImage(Context ctx, ArrayList<Poll> imageModelArrayList, ArrayList<Integer> bitArray, Boolean multi_response){
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.imageModelArrayList = imageModelArrayList;
        Log.d("imagemodel", "PollDoneAdapterWImage: " + imageModelArrayList);
        this.bitArray = bitArray;
        this.multi_response = multi_response;
        Log.d("bitarray", "PollDoneAdapterWImage: " + bitArray);
        for (int i=0;i<imageModelArrayList.size();i++){
            tot_votes+=imageModelArrayList.get(i).getParticipants_userids().size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public PollDoneAdapterWImage.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.poll_done_recycler_item_wimage, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(PollDoneAdapterWImage.MyViewHolder holder, int position) {

        holder.votes.setText(imageModelArrayList.get(position).getParticipants_userids().size()+ "표");
        if(bitArray.get(position)==1){
            holder.progressBar.setBackgroundResource(R.drawable.poll_done_selected);
            holder.progressBar.setProgressDrawable(ctx.getResources().getDrawable(R.drawable.poll_done_progressbar_selected));
            holder.content.setText("V "+imageModelArrayList.get(position).getContent());
        }else{
            holder.progressBar.setBackgroundResource(R.drawable.poll_done_notselected);
            holder.progressBar.setProgressDrawable(ctx.getResources().getDrawable(R.drawable.poll_done_progressbar_notselected));
            holder.content.setText(imageModelArrayList.get(position).getContent());
        }
        holder.progressBar.setProgress(Math.round((float)imageModelArrayList.get(position).getParticipants_userids().size()/(float)tot_votes * 100));
        Log.d("progressis", ""+Math.round((float)imageModelArrayList.get(position).getParticipants_userids().size()/(float)tot_votes * 100));
        Log.d("whodidit", ""+imageModelArrayList.get(position).getParticipants_userids());
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
        }
        else
            holder.imageView.setImageResource(R.drawable.ic_baseline_image_24);


    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        ProgressBar progressBar;
        TextView content;
        TextView votes;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progress_bar);
            content = itemView.findViewById(R.id.content);
            votes = itemView.findViewById(R.id.votes);
            imageView = itemView.findViewById(R.id.image);


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

