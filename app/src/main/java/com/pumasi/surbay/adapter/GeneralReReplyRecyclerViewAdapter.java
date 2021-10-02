package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.General;
import com.pumasi.surbay.classfile.Post;
import com.pumasi.surbay.classfile.ReReply;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class GeneralReReplyRecyclerViewAdapter extends RecyclerView.Adapter<GeneralReReplyRecyclerViewAdapter.GeneralReReplyViewHolder> {
    private GeneralReReplyRecyclerViewAdapter.OnMenuClickListener bListener = null;

    private Tools tools = new Tools();
    private ArrayList<ReReply> reReplies = new ArrayList<ReReply>();
    private Context context;
    private LayoutInflater inflater;
    private General post;

    public GeneralReReplyRecyclerViewAdapter(ArrayList<ReReply> reReplies, Context ctx, General post) {
        this.reReplies = reReplies;
        this.context = ctx;
        this.post = post;
        inflater = LayoutInflater.from(ctx);
    }
    public interface OnMenuClickListener {
        void onMenuClick(View v, int position);
    }
    public void setOnItemClickListener(GeneralReReplyRecyclerViewAdapter.OnMenuClickListener listener) {
        this.bListener = listener ;
    }
    @NonNull
    @Override
    public GeneralReReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reply_reply_item, parent, false);
        GeneralReReplyViewHolder holder = new GeneralReReplyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralReReplyViewHolder holder, int position) {

        int size = reReplies == null ? 0 : reReplies.size();
        ReReply reReply = reReplies.get(position);

        if (size - 1 > position) {
            holder.ll_re_reply.setPadding(0, 0, 0, (int) context.getResources().getDisplayMetrics().density * 12);
        }
        String[] adminsList = {"SurBay_Admin", "SurBay_dev", "SurBay_dev2", "SurBay_des", "djrobort", "surbaying"};
        ArrayList<String> admins = new ArrayList<>(Arrays.asList(adminsList));
        if(reReply.getWriter().equals(post.getAuthor_userid())) {
            holder.tv_re_reply_name.setText("작성자");
            holder.tv_re_reply_name.setTextColor(context.getColor(R.color.teal_200));
        }else if((admins.contains(reReply.getWriter()))&&(reReply.getWriter_name()!=null)){
            holder.tv_re_reply_name.setText(reReply.getWriter_name());
            holder.tv_re_reply_name.setTextColor(context.getColor(R.color.teal_200));
        }else{
            holder.tv_re_reply_name.setText(reReply.getWriter_name());
            holder.tv_re_reply_name.setTextColor(context.getColor(R.color.text_black_70));
        }
        holder.tv_re_reply_date.setText(tools.convertTimeZone(context, reReply.getDate(), "MM.dd kk:mm"));
        if (reReply.getReports().size() > 4) {
            holder.tv_re_reply_text.setText("신고 누적으로 삭제된 대댓글입니다.");
        } else {
            holder.tv_re_reply_text.setText(reReply.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return reReplies == null ? 0 : reReplies.size();
    }

    public class GeneralReReplyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout ll_re_reply;
        private TextView tv_re_reply_name;
        private TextView tv_re_reply_date;
        private TextView tv_re_reply_text;
        private ImageView iv_re_reply_menu;
        public GeneralReReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_re_reply = itemView.findViewById(R.id.ll_re_reply);
            tv_re_reply_name = itemView.findViewById(R.id.tv_re_reply_name);
            tv_re_reply_date = itemView.findViewById(R.id.tv_re_reply_date);
            tv_re_reply_text = itemView.findViewById(R.id.tv_re_reply_text);
            iv_re_reply_menu = itemView.findViewById(R.id.iv_re_reply_menu);
            iv_re_reply_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (bListener != null) {
                            bListener.onMenuClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
