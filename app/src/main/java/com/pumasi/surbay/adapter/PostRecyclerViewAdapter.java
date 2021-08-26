package com.pumasi.surbay.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;
import com.pumasi.surbay.pages.MainActivity;
import com.pumasi.surbay.pages.boardpage.BoardPost;
import com.pumasi.surbay.pages.boardpage.PostDetailActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PostRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private PostRecyclerViewAdapter.OnItemClickListener aListener = null;
    private LayoutInflater inflater;
    ArrayList<Post> boardPosts;

    public PostRecyclerViewAdapter(ArrayList<Post> boardPosts, Context ctx) {
        inflater = LayoutInflater.from(ctx);
        this.boardPosts = boardPosts;
    }


    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(PostRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener;
    }

    public Object getItem(int position) {
        return boardPosts.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.research_item, parent, false);
        if (viewType == VIEW_TYPE_ITEM) {
            MyPostViewHolder holder = new MyPostViewHolder(view);
            return holder;
        } else if (viewType == VIEW_TYPE_LOADING) {
            MyPostLoadingViewHolder holder = new MyPostLoadingViewHolder(view);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyPostViewHolder) {
            Post boardPost = boardPosts.get(position);
            ((MyPostViewHolder) holder).iv_research_item_check.setVisibility(View.INVISIBLE);
            ((MyPostViewHolder) holder).iv_research_item_check2.setVisibility(View.INVISIBLE);
            for (String user : boardPost.getParticipants_userids()) {
                if (user.equals(UserPersonalInfo.email)) {
                    ((MyPostViewHolder) holder).iv_research_item_check.setVisibility(View.VISIBLE);
                    ((MyPostViewHolder) holder).iv_research_item_check2.setVisibility(View.VISIBLE);
                    break;
                }
            }
            if (boardPost.getAnnonymous()) {
                ((MyPostViewHolder) holder).tv_research_item_author.setText("익명");
            } else {
                ((MyPostViewHolder) holder).tv_research_item_author.setText(boardPost.getAuthor());
            }
            ((MyPostViewHolder) holder).tv_research_item_title.setText(boardPost.getTitle());
            ((MyPostViewHolder) holder).tv_research_item_target.setText(boardPost.getTarget());
            ((MyPostViewHolder) holder).tv_research_item_participation.setText(boardPost.getParticipants().toString());
            ((MyPostViewHolder) holder).tv_research_item_goal.setText(boardPost.getGoal_participants().toString());

            Date date = new Date(System.currentTimeMillis());
            Log.d("time", "onBindViewHolder: " + boardPost.getDeadline().getTime());
            Log.d("sub", "onBindViewHolder: " + (boardPost.getDeadline().getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
            if (date.getTime() - boardPost.getDeadline().getTime() > 0) {
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("종료");
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#C4C4C4"));
            } else if (boardPost.getDeadline().getTime() - date.getTime() < 1000 * 60 * 60 * 24) {
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("D-DAY");
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#3AD1BF"));

            } else if (date.getTime() - boardPost.getDate().getTime() < 1000 * 60 * 60 * 24) {
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("NEW");
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#3AD1BF"));
            } else {
                long d_day = (boardPost.getDeadline().getTime() - date.getTime()) / (1000 * 60 * 60 * 24) + 1;
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("D-" + d_day);
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#3AD1BF"));

            }
            SimpleDateFormat fm_start = new SimpleDateFormat("MM.dd");
            SimpleDateFormat fm_end = new SimpleDateFormat("MM.dd HH:mm");
            String start = fm_start.format(boardPost.getDate());
            String end = fm_end.format(boardPost.getDeadline());

            ((MyPostViewHolder) holder).tv_research_item_start.setText(start);
            ((MyPostViewHolder) holder).tv_research_item_end.setText("~" + end);
        }
        else if (holder instanceof MyPostLoadingViewHolder) {
            ((MyPostLoadingViewHolder) holder).loadingPanel.setVisibility(View.VISIBLE);
            ((MyPostLoadingViewHolder) holder).cv_research_item.setVisibility(View.GONE);
        }

    }

    
    @Override
    public int getItemViewType(int position) {
        return boardPosts.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        if (boardPosts == null) {
            return 0;
        }
        return boardPosts.size();
    }

    public class MyPostViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_research_item_check;
        ImageView iv_research_item_check2;
        TextView tv_research_item_author;
        TextView tv_research_item_title;
        TextView tv_research_item_target;
        TextView tv_research_item_participation;
        TextView tv_research_item_goal;
        TextView tv_research_item_indicator;
        TextView tv_research_item_start;
        TextView tv_research_item_end;

        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_research_item_check = itemView.findViewById(R.id.iv_research_item_check);
            iv_research_item_check2 = itemView.findViewById(R.id.iv_research_item_check2);
            tv_research_item_author = itemView.findViewById(R.id.tv_research_item_author);
            tv_research_item_title = itemView.findViewById(R.id.tv_research_item_title);
            tv_research_item_target = itemView.findViewById(R.id.tv_research_item_target);
            tv_research_item_participation = itemView.findViewById(R.id.tv_research_item_participation);
            tv_research_item_goal = itemView.findViewById(R.id.tv_research_item_goal);
            tv_research_item_indicator = itemView.findViewById(R.id.tv_research_item_indicator);
            tv_research_item_start = itemView.findViewById(R.id.tv_research_item_start);
            tv_research_item_end = itemView.findViewById(R.id.tv_research_item_end);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }

    private class MyPostLoadingViewHolder extends RecyclerView.ViewHolder {
        private CardView cv_research_item;
        private RelativeLayout loadingPanel;

        public MyPostLoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            loadingPanel = itemView.findViewById(R.id.loadingPanel);
            cv_research_item = itemView.findViewById(R.id.cv_research_item);
        }
    }
}
