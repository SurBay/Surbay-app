package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Post;

import java.util.ArrayList;

public class RecyclerViewGoalAdapter extends RecyclerView.Adapter<RecyclerViewGoalAdapter.MyViewGoalHolder> {
    private RecyclerViewGoalAdapter.OnItemClickListener bListener = null ;

    // OnItemClickListener 리스너 객체 참조를 어댑터에 전달하는 메서드

    private LayoutInflater inflater;
    private ArrayList<Post> imageModelArrayList;


    public RecyclerViewGoalAdapter(Context ctx, ArrayList<Post> imageModelArrayList){
        inflater = LayoutInflater.from(ctx);
        this.imageModelArrayList = imageModelArrayList;

    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(RecyclerViewGoalAdapter.OnItemClickListener listener) {
        this.bListener = listener ;
    }

    @Override
    public RecyclerViewGoalAdapter.MyViewGoalHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.recycler_item, parent, false);
        RecyclerViewGoalAdapter.MyViewGoalHolder holder = new RecyclerViewGoalAdapter.MyViewGoalHolder(view);


        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewGoalAdapter.MyViewGoalHolder holder, int position) {


        holder.title.setText(imageModelArrayList.get(position).getTitle());
        holder.content.setText(imageModelArrayList.get(position).getContent());
        holder.participate.setText((100 * imageModelArrayList.get(position).getParticipants() / imageModelArrayList.get(position).getGoal_participants()) + "%");

    }

    @Override
    public int getItemCount() {
        return imageModelArrayList.size();
    }

    public Object getItem(int position) {
        return imageModelArrayList.get(position);
    }

    class MyViewGoalHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView content;
        TextView participate;

        public MyViewGoalHolder(View itemView) {
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
                        if (bListener != null) {
                            bListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

    }

}
