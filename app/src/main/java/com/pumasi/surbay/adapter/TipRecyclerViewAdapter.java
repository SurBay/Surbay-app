package com.pumasi.surbay.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.Surveytip;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.zip.Inflater;

public class TipRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private ArrayList<Surveytip> boardTips = new ArrayList<Surveytip>();
    private LayoutInflater inflater;

    private TipRecyclerViewAdapter.OnItemClickListener aListener = null;


    public TipRecyclerViewAdapter(ArrayList<Surveytip> surveyTipArrayList, Context ctx) {
        this.boardTips = surveyTipArrayList;
        this.context = ctx;
        inflater = LayoutInflater.from(ctx);
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(TipRecyclerViewAdapter.OnItemClickListener onItemClickListener) {
        this.aListener = onItemClickListener;
    }
    public Object getItem(int position) {
        return boardTips.get(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.non_survey_list_item, parent, false);
        MyTipViewHolder holder = new MyTipViewHolder(view);
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Surveytip tip = boardTips.get(position);
        if (holder instanceof MyTipViewHolder) {
            ((MyTipViewHolder) holder).tv_tip_item_category.setText(tip.getCategory());
            ((MyTipViewHolder) holder).tv_tip_item_title.setText(tip.getTitle());
            SimpleDateFormat fm = new SimpleDateFormat("MM.dd");
            ((MyTipViewHolder) holder).tv_tip_item_date.setText(fm.format(tip.getDate()));
            ((MyTipViewHolder) holder).tv_tip_item_author.setText(tip.getAuthor());
            ((MyTipViewHolder) holder).tv_tip_item_likes.setText(tip.getLikes().toString());
            String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
            ArrayList<String> admins = new ArrayList<>(Arrays.asList(adminsList));
            if(admins.contains(tip.getAuthor_userid())){
                ((MyTipViewHolder) holder).iv_tip_item_profile.setImageResource(R.drawable.surbay_profile);
            }else{
                ((MyTipViewHolder) holder).iv_tip_item_profile.setImageResource(R.drawable.surbay_character_lv1);
            }
        }

    }

    @Override
    public int getItemCount() {
        return boardTips == null ? 0 : boardTips.size();
    }

    private class MyTipViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_tip_item_category;
        private TextView tv_tip_item_title;
        private TextView tv_tip_item_date;
        private TextView tv_tip_item_author;
        private TextView tv_tip_item_likes;
        private ImageView iv_tip_item_profile;

        public MyTipViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_tip_item_category = itemView.findViewById(R.id.tv_tip_item_category);
            tv_tip_item_title = itemView.findViewById(R.id.tv_tip_item_title);
            tv_tip_item_date = itemView.findViewById(R.id.tv_tip_item_date);
            tv_tip_item_author = itemView.findViewById(R.id.tv_tip_item_author);
            tv_tip_item_likes = itemView.findViewById(R.id.tv_tip_item_likes);
            iv_tip_item_profile = itemView.findViewById(R.id.iv_tip_item_profile);

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
