package com.example.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.surbay.R;
import com.example.surbay.classfile.Notice;
import com.example.surbay.classfile.Post;

import java.util.ArrayList;

public class RecyclerViewNoticeAdapter extends RecyclerView.Adapter<RecyclerViewNoticeAdapter.NoticeViewHolder> {
    private static RecyclerViewNoticeAdapter.OnItemClickListener cListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private ArrayList<Notice> imageModelArrayList;


    public RecyclerViewNoticeAdapter(Context ctx, ArrayList<Notice> imageModelArrayList){
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(RecyclerViewNoticeAdapter.OnItemClickListener listener) {
        this.cListener = listener ;
    }

    @Override
    public RecyclerViewNoticeAdapter.NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        RecyclerViewNoticeAdapter.NoticeViewHolder holder = new RecyclerViewNoticeAdapter.NoticeViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewNoticeAdapter.NoticeViewHolder holder, int position) {


        holder.title.setText(imageModelArrayList.get(position).getTitle());
        holder.content.setText(imageModelArrayList.get(position).getContent());
        holder.participate.setVisibility(View.GONE);


    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    static class NoticeViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView content;
        TextView participate;

        public NoticeViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            participate = (TextView) itemView.findViewById(R.id.recyc_participants);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (cListener != null) {
                            cListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

    }

}
