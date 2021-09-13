package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.ContentDetailCommentsActivity;
import com.pumasi.surbay.R;
import com.pumasi.surbay.classfile.ContentReply;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class ContentReplyRecyclerViewAdapter extends RecyclerView.Adapter<ContentReplyRecyclerViewAdapter.ContentReplyViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<ContentReply> contentReplies = new ArrayList<>();
    private ContentReplyRecyclerViewAdapter.OnReplyClickListener aListener= null;
    private ContentReplyRecyclerViewAdapter.OnMenuClickListener bListener= null;
    private ContentReReplyRecyclerViewAdapter contentReReplyRecyclerViewAdapter;

    public ContentReplyRecyclerViewAdapter(ArrayList<ContentReply> contentReplies, Context ctx) {
        this.context = ctx;
        this.contentReplies = contentReplies;
        inflater = LayoutInflater.from(ctx);
    }
    public interface OnReplyClickListener {
        void onReplyClick(View v, int position) ;
    }
    public interface OnMenuClickListener {
        void onMenuClick(View v, int position);
    }
    public void setOnItemClickListener(ContentReplyRecyclerViewAdapter.OnReplyClickListener listener) {
        this.aListener = listener ;
    }
    public void setOnItemClickListener(ContentReplyRecyclerViewAdapter.OnMenuClickListener listener) {
        this.bListener = listener ;
    }

    public Object getItem(int position) {
        return contentReplies.get(position);
    }


    @NonNull
    @Override
    public ContentReplyRecyclerViewAdapter.ContentReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.detail_reply_list_item, parent, false);
        ContentReplyViewHolder holder = new ContentReplyViewHolder(view);
        return holder;
    }

    public void setItem(ArrayList<ContentReply> contentReplies) {
        this.contentReplies = contentReplies;
    }
    @Override
    public void onBindViewHolder(@NonNull ContentReplyRecyclerViewAdapter.ContentReplyViewHolder holder, int position) {
        SimpleDateFormat fm = new SimpleDateFormat("MM.dd hh:mm");
        ContentReply contentReply = contentReplies.get(position);
        holder.reply_name.setText(UserPersonalInfo.name);
        holder.reply_date.setText(fm.format(contentReply.getDate()));
        holder.reply_context.setText(contentReply.getContent());
        contentReReplyRecyclerViewAdapter = new ContentReReplyRecyclerViewAdapter(contentReply.getReply(), context);
        holder.rv_reply_reply.setAdapter(contentReReplyRecyclerViewAdapter);
        holder.rv_reply_reply.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL, false));
        contentReReplyRecyclerViewAdapter.setOnItemClickListener(new OnMenuClickListener() {
            @Override
            public void onMenuClick(View v, int position) {
                Toast.makeText(context, "대댓글 메뉴 clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return contentReplies == null ? 0 : contentReplies.size();
    }

    public class ContentReplyViewHolder extends RecyclerView.ViewHolder {
        private ImageView reply_reply;
        private ImageView reply_menu;
        private TextView reply_name;
        private TextView reply_date;
        private TextView reply_context;
        private RecyclerView rv_reply_reply;
        public ContentReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            reply_reply = itemView.findViewById(R.id.reply_reply);
            reply_name = itemView.findViewById(R.id.reply_name);
            reply_menu = itemView.findViewById(R.id.reply_menu);
            reply_date = itemView.findViewById(R.id.reply_date);
            reply_context = itemView.findViewById(R.id.reply_context);
            rv_reply_reply = itemView.findViewById(R.id.rv_reply_reply);

            reply_reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (aListener != null) {
                            aListener.onReplyClick(v, pos) ;
                        }
                    }
                }
            });
            reply_menu.setOnClickListener(new View.OnClickListener() {
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
