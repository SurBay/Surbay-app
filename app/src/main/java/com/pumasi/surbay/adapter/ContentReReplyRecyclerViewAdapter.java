package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.ContentReReply;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ContentReReplyRecyclerViewAdapter extends RecyclerView.Adapter<ContentReReplyRecyclerViewAdapter.ContentReReplyViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ContentReReply> contentReReplies = new ArrayList<>();
    private ContentReplyRecyclerViewAdapter.OnMenuClickListener bListener= null;

    public ContentReReplyRecyclerViewAdapter(ArrayList<ContentReReply> contentReReplies, Context ctx) {
        this.context = ctx;
        this.contentReReplies = contentReReplies;
        inflater = LayoutInflater.from(ctx);
    }

    public interface OnMenuClickListener {
        void onMenuClick(View v, int position);
    }
    public void setOnItemClickListener(ContentReplyRecyclerViewAdapter.OnMenuClickListener listener) {
        this.bListener = listener ;
    }

    public Object getItem(int position) {
        return contentReReplies.get(position);
    }
    @NonNull
    @Override
    public ContentReReplyRecyclerViewAdapter.ContentReReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reply_reply_item, parent, false);
        ContentReReplyViewHolder holder = new ContentReReplyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ContentReReplyRecyclerViewAdapter.ContentReReplyViewHolder holder, int position) {
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd hh.mm");
        ContentReReply contentReReply = contentReReplies.get(position);
        holder.tv_re_reply_name.setText(contentReReply.getWriter_name());
        holder.tv_re_reply_date.setText(fm.format(contentReReply.getDate()));
        holder.tv_re_reply_text.setText(contentReReply.getContent());
    }

    @Override
    public int getItemCount() {
        return contentReReplies == null ? 0 : contentReReplies.size();
    }

    public class ContentReReplyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_re_reply_menu;

        private TextView tv_re_reply_name;
        private TextView tv_re_reply_date;
        private TextView tv_re_reply_text;
        public ContentReReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_re_reply_menu = itemView.findViewById(R.id.iv_re_reply_menu);
            tv_re_reply_name = itemView.findViewById(R.id.tv_re_reply_name);
            tv_re_reply_date = itemView.findViewById(R.id.tv_re_reply_date);
            tv_re_reply_text = itemView.findViewById(R.id.tv_re_reply_text);

            iv_re_reply_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (bListener != null) {
                            bListener.onMenuClick(v, pos); ;
                        }
                    }
                }
            });
        }
    }
}
