package com.pumasi.surbay.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.BoardPost;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;

import java.util.ArrayList;


public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.MyPostViewHolder> {

    private PostRecyclerViewAdapter.OnItemClickListener aListener = null;
    private LayoutInflater inflater;
    ArrayList<Post> boardPosts;

    public PostRecyclerViewAdapter(ArrayList<Post> boardPosts, Context ctx) {
        inflater = LayoutInflater.from(ctx);
        this.boardPosts = boardPosts;
    }




    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(PostRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener ;
    }
    public Object getItem(int position) {
        return boardPosts.get(position);
    }
    @NonNull
    @Override
    public MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.research_item, parent, false);
        MyPostViewHolder holder = new MyPostViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyPostViewHolder holder, int position) {
        holder.tv_research_item_author.setText(boardPosts.get(position).getAuthor());
        holder.tv_research_item_title.setText(boardPosts.get(position).getTitle());
        holder.tv_research_item_target.setText(boardPosts.get(position).getTarget());
        holder.tv_research_item_participation.setText(boardPosts.get(position).getParticipants().toString());
        holder.tv_research_item_goal.setText(boardPosts.get(position).getGoal_participants().toString());

    }

    @Override
    public int getItemCount() {
        return boardPosts.size();
    }

    public class MyPostViewHolder extends RecyclerView.ViewHolder {
        TextView tv_research_item_author;
        TextView tv_research_item_title;
        TextView tv_research_item_target;
        TextView tv_research_item_participation;
        TextView tv_research_item_goal;
        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_research_item_author = itemView.findViewById(R.id.tv_research_item_author);
            tv_research_item_title = itemView.findViewById(R.id.tv_research_item_title);
            tv_research_item_target = itemView.findViewById(R.id.tv_research_item_target);
            tv_research_item_participation = itemView.findViewById(R.id.tv_research_item_participation);
            tv_research_item_goal = itemView.findViewById(R.id.tv_research_item_goal);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }
    }


}
