package com.pumasi.surbay.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.ReReply;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ReReplyRecyclerViewAdapter extends RecyclerView.Adapter<ReReplyRecyclerViewAdapter.MyReReplyViewHolder> {

    private ArrayList<ReReply> reReplies = new ArrayList<ReReply>();
    private Context context;
    private LayoutInflater inflater;
    public ReReplyRecyclerViewAdapter(ArrayList<ReReply> reReplies, Context ctx) {
        this.reReplies = reReplies;
        this.context = ctx;
        inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public MyReReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reply_reply_item, parent, false);
        MyReReplyViewHolder holder = new MyReReplyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyReReplyViewHolder holder, int position) {
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd hh:mm");

        int size = reReplies == null ? 0 : reReplies.size();
        ReReply reReply = reReplies.get(position);

        if (size - 1 > position) {
            holder.ll_re_reply.setPadding(0, 0, 0, (int) context.getResources().getDisplayMetrics().density * 12);
        }
        holder.tv_re_reply_name.setText(reReply.getWriter());
        holder.tv_re_reply_date.setText(fm.format(reReply.getDate()));
        holder.tv_re_reply_text.setText(reReply.getContent());

        holder.iv_re_reply_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return reReplies == null ? 0 : reReplies.size();
    }

    public class MyReReplyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_re_reply;
        private TextView tv_re_reply_name;
        private TextView tv_re_reply_date;
        private TextView tv_re_reply_text;
        private ImageView iv_re_reply_menu;
        public MyReReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_re_reply = itemView.findViewById(R.id.ll_re_reply);
            tv_re_reply_name = itemView.findViewById(R.id.tv_re_reply_name);
            tv_re_reply_date = itemView.findViewById(R.id.tv_re_reply_date);
            tv_re_reply_text = itemView.findViewById(R.id.tv_re_reply_text);
            iv_re_reply_menu = itemView.findViewById(R.id.iv_re_reply_menu);

        }
    }
}
