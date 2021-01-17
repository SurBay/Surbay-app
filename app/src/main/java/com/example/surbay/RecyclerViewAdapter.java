package com.example.surbay;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private OnItemClickListener mListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드


    private LayoutInflater inflater;
    private ArrayList<Post> imageModelArrayList;
    public RecyclerViewAdapter(Context ctx, ArrayList<Post> imageModelArrayList){
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.MyViewHolder holder, int position) {

        holder.title.setText(imageModelArrayList.get(position).getTitle());
        holder.content.setText(imageModelArrayList.get(position).getContent());
        holder.author.setText(imageModelArrayList.get(position).getAuthor());

        SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
        holder.date.setText(fm.format(imageModelArrayList.get(position).getDate()));
        holder.deadline.setText(fm.format(imageModelArrayList.get(position).getDeadline()));


    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView content;
        TextView author;
        TextView date;
        TextView deadline;

        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            content = (TextView) itemView.findViewById(R.id.content);
            author = (TextView) itemView.findViewById(R.id.author);
            date = (TextView) itemView.findViewById(R.id.date);
            deadline = (TextView) itemView.findViewById(R.id.deadline);

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