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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

import kotlin.reflect.KVisibility;

public class VoteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private VoteRecyclerViewAdapter.OnItemClickListener aListener = null;

    private LayoutInflater inflater;
    private ArrayList<General> boardVotes;

    public VoteRecyclerViewAdapter(ArrayList<General> boardVotes, Context ctx) {
        this.boardVotes = boardVotes;
        inflater = LayoutInflater.from(ctx);
    }
    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(VoteRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener;
    }
    public Object getItem(int position) {
        return boardVotes.get(position);
    }
    public void setItem(ArrayList<General> boardVotes) {
        this.boardVotes = boardVotes;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vote_item, parent, false);

        if (viewType == VIEW_TYPE_ITEM) {
            VoteRecyclerViewAdapter.MyVoteViewHolder holder = new VoteRecyclerViewAdapter.MyVoteViewHolder(view);
            return holder;
        } else if (viewType == VIEW_TYPE_LOADING) {
            VoteRecyclerViewAdapter.MyVoteLoadingViewHolder holder = new VoteRecyclerViewAdapter.MyVoteLoadingViewHolder(view);
            return holder;
        }
        return null;
    }


    @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyVoteViewHolder) {
            General general = boardVotes.get(position);
            ((MyVoteViewHolder) holder).tv_vote_item_title.setText("Q. " + general.getTitle());
            Date date = new Date(System.currentTimeMillis());
            if (date.getTime() - general.getDeadline().getTime() > 0) {
                ((MyVoteViewHolder) holder).tv_vote_item_progress.setText("종료");
                ((MyVoteViewHolder) holder).tv_vote_item_progress.setTextColor(Color.parseColor("#C4C4C4"));
            } else {
                ((MyVoteViewHolder) holder).tv_vote_item_progress.setText("진행중");
                ((MyVoteViewHolder) holder).tv_vote_item_progress.setTextColor(Color.parseColor("#50D3DD"));
            }
            Log.d("get_time", "onBindViewHolder: " + date.getTime() + ", " + general.getDeadline().getTime());
            ((MyVoteViewHolder) holder).tv_vote_item_votes_comments.setText("투표 " + String.valueOf(general.getParticipants()) + "개 | " + "댓글 " + String.valueOf(general.getComments().size()) + "개");
            Log.d("liked", "onBindViewHolder: " + general.getLiked_users() + ", " + UserPersonalInfo.email + ", " + general.getTitle());
            ((MyVoteViewHolder) holder).iv_vote_item_like.setImageResource(R.drawable.heart_empty);
            if (general.getLikes() == 0) {
                ((MyVoteViewHolder) holder).iv_vote_item_like.setImageResource(R.drawable.heart_empty);
            } else {
                for (String user : general.getLiked_users()) {
                    Log.d("liked", "onBindViewHolder: " + user);
                    if (UserPersonalInfo.email.equals(user)) {
                        ((MyVoteViewHolder) holder).iv_vote_item_like.setImageResource(R.drawable.heart_filled);
                        break;
                    }
                }
            }

            ((MyVoteViewHolder) holder).tv_vote_item_more.setText(general.getLikes().toString());

            if (boardVotes.get(position).getPolls().size() == 2) {
                ((MyVoteViewHolder) holder).fl_vote_poll2.setVisibility(View.VISIBLE);
                ((MyVoteViewHolder) holder).fl_vote_poll3.setVisibility(View.GONE);
                ((MyVoteViewHolder) holder).fl_vote_poll4.setVisibility(View.GONE);
                ((MyVoteViewHolder) holder).tv_vote_content2_1.setText(general.getPolls().get(0).getContent());
                ((MyVoteViewHolder) holder).tv_vote_content2_2.setText(general.getPolls().get(1).getContent());
            } else if (boardVotes.get(position).getPolls().size() == 3) {
                ((MyVoteViewHolder) holder).fl_vote_poll2.setVisibility(View.GONE);
                ((MyVoteViewHolder) holder).fl_vote_poll3.setVisibility(View.VISIBLE);
                ((MyVoteViewHolder) holder).fl_vote_poll4.setVisibility(View.GONE);
                ((MyVoteViewHolder) holder).tv_vote_content3_1.setText(general.getPolls().get(0).getContent());
                ((MyVoteViewHolder) holder).tv_vote_content3_2.setText(general.getPolls().get(1).getContent());
                ((MyVoteViewHolder) holder).tv_vote_content3_3.setText(general.getPolls().get(2).getContent());
            } else {
                ((MyVoteViewHolder) holder).fl_vote_poll2.setVisibility(View.GONE);
                ((MyVoteViewHolder) holder).fl_vote_poll3.setVisibility(View.GONE);
                ((MyVoteViewHolder) holder).fl_vote_poll4.setVisibility(View.VISIBLE);
                ((MyVoteViewHolder) holder).tv_vote_content4_1.setText(general.getPolls().get(0).getContent());
                ((MyVoteViewHolder) holder).tv_vote_content4_1.setText(general.getPolls().get(1).getContent());
                ((MyVoteViewHolder) holder).tv_vote_content4_1.setText(general.getPolls().get(2).getContent());
                ((MyVoteViewHolder) holder).tv_vote_content4_1.setText(general.getPolls().get(3).getContent());
                if (general.getPolls().size() == 4) {
                    ((MyVoteViewHolder) holder).tv_vote_content4_extra.setVisibility(View.INVISIBLE);
                } else {
                    ((MyVoteViewHolder) holder).tv_vote_content4_extra.setText("+" + String.valueOf(general.getPolls().size() - 4));
                }
            }
        } else if (holder instanceof MyVoteLoadingViewHolder) {
            ((MyVoteLoadingViewHolder) holder).cv_vote_item.setVisibility(View.GONE);
            ((MyVoteLoadingViewHolder) holder).loadingPanel.setVisibility(View.VISIBLE);
        }

        }
    @Override
    public int getItemViewType(int position) {
        return boardVotes.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
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

    public class MyVoteLoadingViewHolder extends RecyclerView.ViewHolder {

        private CardView cv_vote_item;
        private RelativeLayout loadingPanel;
        public MyVoteLoadingViewHolder(@NonNull View itemView) {
            super(itemView);

            cv_vote_item = itemView.findViewById(R.id.cv_vote_item);
            loadingPanel = itemView.findViewById(R.id.loadingPanel);
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
}
