package com.pumasi.surbay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pumasi.surbay.R;
import com.pumasi.surbay.Tools;
import com.pumasi.surbay.classfile.MessageContent;
import com.pumasi.surbay.classfile.MyMessage;
import com.pumasi.surbay.classfile.UserPersonalInfo;

import java.util.ArrayList;

public class MyChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int VIEW_MY_CHAT = 0;
    private final static int VIEW_COUNTER_CHAT = 1;


    private View view;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<MessageContent> messageContents = new ArrayList<>();

    public MyChatRecyclerViewAdapter(ArrayList<MessageContent> messageContents, Context ctx) {
        this.context = ctx;
        this.messageContents = messageContents;
        inflater = LayoutInflater.from(ctx);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_MY_CHAT) {
            view = inflater.inflate(R.layout.my_note_chat_myself, parent, false);
            MyChatViewHolder holder = new MyChatViewHolder(view);
            return holder;
        } else if (viewType == VIEW_COUNTER_CHAT) {
            view = inflater.inflate(R.layout.my_note_chat_counterpart, parent, false);
            CounterChatViewHolder holder = new CounterChatViewHolder(view);
            return holder;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageContent messageContent = messageContents.get(position);
        if (holder instanceof MyChatViewHolder) {
            ((MyChatViewHolder) holder).tv_my_note_myself_chat.setText(messageContent.getContent());
            ((MyChatViewHolder) holder).tv_my_note_myself_date.setText(new Tools().convertTimeZone(context, messageContent.getDate(), "MM.dd hh:mm"));
        } else if (holder instanceof CounterChatViewHolder) {
            ((CounterChatViewHolder) holder).tv_my_note_counter_chat.setText(messageContent.getContent());
            ((CounterChatViewHolder) holder).tv_my_note_counter_date.setText(new Tools().convertTimeZone(context, messageContent.getDate(), "MM.dd hh:mm"));
        }

    }

    @Override
    public int getItemCount() {
        return messageContents == null ? 0 : messageContents.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messageContents.get(position).getWriter().equals(UserPersonalInfo.email) ? 0 : 1;
    }

    public void setItem(ArrayList<MessageContent> messageContents) {
        this.messageContents = messageContents;
    }

    class MyChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_my_note_myself_chat;
        private TextView tv_my_note_myself_date;

        public MyChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_my_note_myself_chat = itemView.findViewById(R.id.tv_my_note_myself_chat);
            tv_my_note_myself_date = itemView.findViewById(R.id.tv_my_note_myself_date);

        }
    }

    class CounterChatViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_my_note_counter_chat;
        private TextView tv_my_note_counter_date;

        public CounterChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_my_note_counter_chat = itemView.findViewById(R.id.tv_my_note_counter_chat);
            tv_my_note_counter_date = itemView.findViewById(R.id.tv_my_note_counter_date);

        }
    }
}
