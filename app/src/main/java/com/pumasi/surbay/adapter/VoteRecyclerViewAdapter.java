package com.pumasi.surbay.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class VoteRecyclerViewAdapter extends RecyclerView.Adapter<VoteRecyclerViewAdapter.MyVoteViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<General> boardVotes;

    public VoteRecyclerViewAdapter(ArrayList<General> boardVotes, Context ctx) {
        this.boardVotes = boardVotes;
        inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public MyVoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vote_item, parent, false);
        MyVoteViewHolder holder = new MyVoteViewHolder(view);
        return holder;
    }

    @Override
        public void onBindViewHolder(@NonNull MyVoteViewHolder holder, int position) {
            General general = boardVotes.get(position);

            holder.tv_vote_item_title.setText("Q." + general.getTitle());
            Date date = new Date(System.currentTimeMillis());
            if (date.getTime() - general.getDeadline().getTime() > 0) {
                holder.tv_vote_item_progress.setText("종료");
                holder.tv_vote_item_progress.setTextColor(Color.parseColor("#C4C4C4"));
            } else {
                holder.tv_vote_item_progress.setText("진행중");
                holder.tv_vote_item_progress.setTextColor(Color.parseColor("#50D3DD"));
            }
            Log.d("get_time", "onBindViewHolder: " + date.getTime() + ", " + general.getDeadline().getTime());
            holder.tv_vote_item_votes_comments.setText("투표 " + String.valueOf(general.getParticipants()) + "개 | " + "댓글 " + String.valueOf(general.getComments().size()) + "개");
            Log.d("liked", "onBindViewHolder: " + general.getLiked_users() + ", " + UserPersonalInfo.email + ", " + general.getTitle());
            holder.iv_vote_item_like.setImageResource(R.drawable.heart_empty);
            if (general.getLikes() == 0) {
                holder.iv_vote_item_like.setImageResource(R.drawable.heart_empty);
                } else {
                    for (String user : general.getLiked_users()) {
                    Log.d("liked", "onBindViewHolder: " + user);
                        if (UserPersonalInfo.email.equals(user)) {
                            holder.iv_vote_item_like.setImageResource(R.drawable.heart_filled);
                            break;
                    }
                }
            }

            holder.tv_vote_item_more.setText(general.getLikes().toString());

            if (boardVotes.get(position).getPolls().size() == 2) {
                holder.fl_vote_poll2.setVisibility(View.VISIBLE);
                holder.fl_vote_poll3.setVisibility(View.GONE);
                holder.fl_vote_poll4.setVisibility(View.GONE);
                holder.tv_vote_content2_1.setText(general.getPolls().get(0).getContent());
                holder.tv_vote_content2_2.setText(general.getPolls().get(1).getContent());
            } else if (boardVotes.get(position).getPolls().size() == 3) {
                holder.fl_vote_poll2.setVisibility(View.GONE);
                holder.fl_vote_poll3.setVisibility(View.VISIBLE);
                holder.fl_vote_poll4.setVisibility(View.GONE);
                holder.tv_vote_content3_1.setText(general.getPolls().get(0).getContent());
                holder.tv_vote_content3_2.setText(general.getPolls().get(1).getContent());
                holder.tv_vote_content3_3.setText(general.getPolls().get(2).getContent());
            } else {
                holder.fl_vote_poll2.setVisibility(View.GONE);
                holder.fl_vote_poll3.setVisibility(View.GONE);
                holder.fl_vote_poll4.setVisibility(View.VISIBLE);
                holder.tv_vote_content4_1.setText(general.getPolls().get(0).getContent());
                holder.tv_vote_content4_1.setText(general.getPolls().get(1).getContent());
                holder.tv_vote_content4_1.setText(general.getPolls().get(2).getContent());
                holder.tv_vote_content4_1.setText(general.getPolls().get(3).getContent());
                if (general.getPolls().size() == 4) {
                    holder.tv_vote_content4_extra.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_vote_content4_extra.setText("+" + String.valueOf(general.getPolls().size() - 4));
                }
            }
        }

    @Override
    public int getItemCount() {
        return boardVotes.size();
    }

    public class MyVoteViewHolder extends RecyclerView.ViewHolder {

        FrameLayout fl_vote_poll2;
        FrameLayout fl_vote_poll3;
        FrameLayout fl_vote_poll4;

        TextView tv_vote_item_title;
        TextView tv_vote_item_progress;
        TextView tv_vote_item_votes_comments;
        ImageView iv_vote_item_like;
        TextView tv_vote_item_more;

        TextView tv_vote_content2_1;
        TextView tv_vote_content2_2;
        TextView tv_vote_content3_1;
        TextView tv_vote_content3_2;
        TextView tv_vote_content3_3;
        TextView tv_vote_content4_1;
        TextView tv_vote_content4_2;
        TextView tv_vote_content4_3;
        TextView tv_vote_content4_4;
        TextView tv_vote_content4_extra;


        public MyVoteViewHolder(@NonNull View itemView) {
            super(itemView);
            fl_vote_poll2 = itemView.findViewById(R.id.fl_vote_poll2);
            fl_vote_poll3 = itemView.findViewById(R.id.fl_vote_poll3);
            fl_vote_poll4 = itemView.findViewById(R.id.fl_vote_poll4);

            tv_vote_item_title = itemView.findViewById(R.id.tv_vote_item_title);
            tv_vote_item_progress = itemView.findViewById(R.id.tv_vote_item_progress);
            tv_vote_item_votes_comments = itemView.findViewById(R.id.tv_vote_item_votes_comments);
            iv_vote_item_like = itemView.findViewById(R.id.iv_vote_item_like);
            tv_vote_item_more = itemView.findViewById(R.id.tv_vote_item_more);

            tv_vote_content2_1 = itemView.findViewById(R.id.tv_vote_content2_1);
            tv_vote_content2_2 = itemView.findViewById(R.id.tv_vote_content2_2);
            tv_vote_content3_1 = itemView.findViewById(R.id.tv_vote_content3_1);
            tv_vote_content3_2 = itemView.findViewById(R.id.tv_vote_content3_2);
            tv_vote_content3_3 = itemView.findViewById(R.id.tv_vote_content3_3);
            tv_vote_content4_1 = itemView.findViewById(R.id.tv_vote_content4_1);
            tv_vote_content4_2 = itemView.findViewById(R.id.tv_vote_content4_2);
            tv_vote_content4_3 = itemView.findViewById(R.id.tv_vote_content4_3);
            tv_vote_content4_4 = itemView.findViewById(R.id.tv_vote_content4_4);
            tv_vote_content4_extra = itemView.findViewById(R.id.tv_vote_content4_extra);


        }
    }
}
