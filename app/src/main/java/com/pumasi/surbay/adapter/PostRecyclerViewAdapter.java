package com.pumasi.surbay.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Tools tools = new Tools();
    private Context context;
    private PostRecyclerViewAdapter.OnItemClickListener aListener = null;
    private LayoutInflater inflater;
    ArrayList<Post> boardPosts;

    public PostRecyclerViewAdapter(ArrayList<Post> boardPosts, Context ctx) {
        inflater = LayoutInflater.from(ctx);
        this.boardPosts = boardPosts;
        this.context = ctx;
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
    public void setItem(ArrayList<Post> boardPosts) {
        this.boardPosts = boardPosts;
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

    @SuppressLint({"SetTextI18n", "SimpleDateFormat"})
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyPostViewHolder) {

            String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
            Post boardPost = boardPosts.get(position);
            boolean isAdmin = false;
            for (String admin : adminsList) {
                if (boardPost.getAuthor_userid().equals(admin)) {
                    ((MyPostViewHolder) holder).iv_research_item_character.setImageResource(R.drawable.surbay_logo_transparent);
                    isAdmin = true;
                    break;
                }
            }
            if (!isAdmin) {
                if (boardPost.getAuthor_lvl() <= 1) {
                    ((MyPostViewHolder) holder).iv_research_item_character.setImageResource(R.drawable.lv1_trans);
                } else if (boardPost.getAuthor_lvl() <= 2) {
                    ((MyPostViewHolder) holder).iv_research_item_character.setImageResource(R.drawable.lv2_trans);
                } else if (boardPost.getAuthor_lvl() <= 3) {
                    ((MyPostViewHolder) holder).iv_research_item_character.setImageResource(R.drawable.lv3_trans);
                } else {
                    ((MyPostViewHolder) holder).iv_research_item_character.setImageResource(R.drawable.lv4_trans);
                }
            }
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
            if (boardPost.isWith_prize()) {
                ((MyPostViewHolder) holder).iv_research_item_gift.setVisibility(View.VISIBLE);
            } else {
                ((MyPostViewHolder) holder).iv_research_item_gift.setVisibility(View.GONE);
            }
            ((MyPostViewHolder) holder).tv_research_item_title.setText(boardPost.getTitle());
            ((MyPostViewHolder) holder).tv_research_item_target.setText(boardPost.getTarget());
            if (boardPost.getParticipants() > 999) {
                ((MyPostViewHolder) holder).tv_research_item_participation.setText("999+");
            } else {
                ((MyPostViewHolder) holder).tv_research_item_participation.setText(boardPost.getParticipants().toString());
            }
            if (boardPost.getGoal_participants() > 999) {
                ((MyPostViewHolder) holder).tv_research_item_goal.setText("999+");
            } else {
                ((MyPostViewHolder) holder).tv_research_item_goal.setText(boardPost.getGoal_participants().toString());
            }

            Date date = new Date(System.currentTimeMillis());
            Log.d("time_show", new SimpleDateFormat(context.getResources().getString(R.string.date_format)).format(date));
            if (tools.toUTC(date.getTime()) - boardPost.getDeadline().getTime() > 0) {
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("종료");
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#C4C4C4"));
            } else if (tools.dayCompare(tools.toLocal(boardPost.getDeadline().getTime()), date.getTime()) == 0) {
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("D-DAY");
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#3AD1BF"));
            } else if (tools.toUTC(date.getTime()) - boardPost.getDate().getTime() < tools.time_day) {
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("NEW");
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#3AD1BF"));
            } else {
                ((MyPostViewHolder) holder).tv_research_item_indicator.setText("D-" + tools.dayCompare(tools.toLocal(boardPost.getDeadline().getTime()), date.getTime()));
                ((MyPostViewHolder) holder).tv_research_item_indicator.setTextColor(Color.parseColor("#3AD1BF"));

            }

            ((MyPostViewHolder) holder).tv_research_item_start.setText(tools.convertTimeZone(context, boardPost.getDate(), "MM.dd"));
            ((MyPostViewHolder) holder).tv_research_item_end.setText(tools.convertTimeZone(context, boardPost.getDeadline(), "~ MM.dd HH:mm"));
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
        return boardPosts.size();
    }

    public class MyPostViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_research_item_character;
        private ImageView iv_research_item_check;
        private ImageView iv_research_item_check2;
        private TextView tv_research_item_author;
        private TextView tv_research_item_title;
        private TextView tv_research_item_target;
        private ImageView iv_research_item_gift;
        private TextView tv_research_item_participation;
        private TextView tv_research_item_goal;
        private TextView tv_research_item_indicator;
        private TextView tv_research_item_start;
        private TextView tv_research_item_end;

        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_research_item_character = itemView.findViewById(R.id.iv_research_item_character);
            iv_research_item_check = itemView.findViewById(R.id.iv_research_item_check);
            iv_research_item_check2 = itemView.findViewById(R.id.iv_research_item_check2);
            tv_research_item_author = itemView.findViewById(R.id.tv_research_item_author);
            tv_research_item_title = itemView.findViewById(R.id.tv_research_item_title);
            tv_research_item_target = itemView.findViewById(R.id.tv_research_item_target);
            iv_research_item_gift = itemView.findViewById(R.id.iv_research_item_gift);
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
