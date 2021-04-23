package com.pumasi.surbay.adapter;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Poll;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;

public class PollDoneAdapter extends RecyclerView.Adapter<PollDoneAdapter.MyViewHolder> {
    private static OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private ArrayList<Poll> imageModelArrayList;
    public ArrayList<Integer> bitArray;
    private Boolean multi_response;
    private Integer tot_votes = 0;
    private Context ctx;

    public PollDoneAdapter(Context ctx, ArrayList<Poll> imageModelArrayList, ArrayList<Integer> bitArray, Boolean multi_response){
        inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.imageModelArrayList = imageModelArrayList;
        this.bitArray = bitArray;
        this.multi_response = multi_response;
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
    public PollDoneAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.poll_done_recycler_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(PollDoneAdapter.MyViewHolder holder, int position) {

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

        public MyViewHolder(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.progress_bar);
            content = itemView.findViewById(R.id.content);
            votes = itemView.findViewById(R.id.votes);


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

