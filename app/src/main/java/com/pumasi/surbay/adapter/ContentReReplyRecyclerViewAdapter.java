package com.pumasi.surbay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.ContentReReply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ContentReReplyRecyclerViewAdapter extends RecyclerView.Adapter<ContentReReplyRecyclerViewAdapter.ContentReReplyViewHolder> {
    private Tools tools = new Tools();
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
        ContentReReply contentReReply = contentReReplies.get(position);
        int size = contentReReplies == null ? 0 : contentReReplies.size();

        holder.tv_re_reply_name.setText(contentReReply.getWriter_name());
        holder.tv_re_reply_date.setText(tools.convertTimeZone(context, contentReReply.getDate(), "MM.dd kk:mm"));
        if (UserPersonalInfo.blocked_users.contains(contentReReply.getWriter())) {
            holder.tv_re_reply_text.setText("(차단한 유저의 댓글입니다.)");
        }
        else if (contentReReply.getReports().size() > 4) {
            holder.tv_re_reply_text.setText("(신고가 누적된 댓글입니다.)");
        } else {
            holder.tv_re_reply_text.setText(contentReReply.getContent());
        }
        if (size - 1 > position) {
            holder.ll_re_reply.setPadding(0, 0, 0, (int) context.getResources().getDisplayMetrics().density * 12);
        }
        Log.d("rereply", "onBindViewHolder: 중간 대댓글");
    }

    @Override
    public int getItemCount() {
        return contentReReplies == null ? 0 : contentReReplies.size();
    }

    public class ContentReReplyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_re_reply;
        private ImageView iv_re_reply_menu;

        private TextView tv_re_reply_name;
        private TextView tv_re_reply_date;
        private TextView tv_re_reply_text;
        public ContentReReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_re_reply = itemView.findViewById(R.id.ll_re_reply);
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
